package org.openmole.web

import _root_.akka.actor.ActorSystem
import org.scalatra._
import scalate.ScalateSupport
import servlet.{ FileItem, FileUploadSupport }
import java.io.{ PrintStream, File, InputStream }
import javax.servlet.annotation.MultipartConfig

import org.openmole.core.serializer._
import org.openmole.core.model.mole.{ IPartialMoleExecution, IMoleExecution, ExecutionContext }
import org.openmole.core.model.data.{ Context, Prototype, Variable }
import com.thoughtworks.xstream.mapper.CannotResolveClassException
import concurrent.Future

import slick.driver.H2Driver.simple._
import slick.jdbc.meta.MTable

import org.openmole.misc.tools.io.FromString

import org.openmole.misc.eventdispatcher.EventDispatcher

import Database.threadLocalSession

import javax.sql.rowset.serial.SerialClob
import reflect.ClassTag
import json.JacksonJsonSupport
import org.json4s._
import org.json4s.JsonDSL._
import scala.None
import org.json4s.{ Formats, DefaultFormats }
import org.openmole.core.implementation.validation.Validation
import org.openmole.core.implementation.validation.DataflowProblem.{ MissingSourceInput, MissingInput, WrongType }
import scala.io.Source
import org.openmole.core.model.mole.IMoleExecution.JobStatusChanged
import org.openmole.misc.eventdispatcher.{ Event, EventListener }

@MultipartConfig(maxFileSize = 3145728 /*max file size of 3 MiB*/ ) //research scala multipart config
class MoleRunner(val system: ActorSystem, protected val dbPassword: String /*TODO: is this safe??*/ ) extends ScalatraServlet with SlickSupport with ScalateSupport
    with FileUploadSupport with FlashMapSupport with FutureSupport with JacksonJsonSupport with MoleHandling with Authentication {

  protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal

  /*val cachedMoles = new DataHandler[String, IMoleExecution](system)
  val moleStats = new DataHandler[String, Stats.Stats](system)

  val listener: EventListener[IMoleExecution] = new JobEventListener(moleStats)*/

  get("/index.html") {
    contentType = "text/html"

    new AsyncResult() {
      val is = Future {
        ssp("/index.ssp")
      }
    }
  }

  get("/") {
    new AsyncResult() {
      val is = Future { redirect(url("index.html")) }
    }
  }

  get("/createMole") {
    contentType = "text/html"
    new AsyncResult() {
      val is = Future {
        requireAuth(cookies get "apiKey") {
          ssp("/createMole", "body" -> "Please upload a serialized mole execution below!")
        } {

          ssp("/authenticate", "goTo" -> "/createMole")
        }
      }
    }
  }

  post("/createMole") {

    val data = fileParams.get("file")

    val inS = data.map(_.getInputStream)

    val csv = fileParams.get("csv")

    val molePack = params get "pack" match {
      case Some("on") ⇒ true
      case _          ⇒ false
    }

    val cnS = csv.map(_.getInputStream)

    val encapsulate = params get "encapsulate" match {
      case Some("on") ⇒ true
      case _          ⇒ false
    }

    //TODO: make sure this is released

    new AsyncResult {
      val is = Future {
        requireAuth(cookies get "apiKey" orElse (request.headers get "apiKey")) {
          contentType = "text/html"

          createMole(inS, cnS, encapsulate, pack = molePack) match {
            case Left(error) ⇒ ssp("/createMole", "body" -> "Please upload a serialized mole execution below!", "errors" -> List(error))
            case _           ⇒ redirect(url("execs"))
          }
        } {
          halt(status = 401, headers = Map("WWW-Authenticate" -> "apiKey"))
        }
      }
    }
  }

  post("/getApiKey") {
    contentType = "text/plain"
    new AsyncResult() {
      val is = Future {
        logger.info("received apiKey request")
        try {
          request.headers get "pass" map issueKey foreach (cookies("apiKey") = _)
          cookies get "apiKey" foreach (k ⇒ logger.info(s"created api key: $k"))
          ""
        }
        catch {
          case e: InvalidPasswordException ⇒ "Invalid password entered"
        }
      }
    }
  }

  post("/json/getApiKey") {
    contentType = formats("json")

    new AsyncResult {
      val is = Future {
        try {
          request.headers get "pass" map issueKey map (render("apiKey", _)) getOrElse render("error", "no password sent with request")
        }
        catch {
          case e: InvalidPasswordException ⇒ render(("error", e.getMessage) ~ ("stackTrace", e.getStackTrace.map(e ⇒ s"\tat$e").reduceLeft((prev, next) ⇒ s"$prev\n$next")))
        }
      }

    }
  }

  post("/xml/getApiKey") {
    contentType = "application/xml"

    new AsyncResult() {
      val is = Future {
        try {
          request.headers get "pass" map issueKey map (k ⇒ <apiKey>{ k }</apiKey>) getOrElse <error>"no password sent with request"</error>
        }
        catch {
          case e: InvalidPasswordException ⇒ <error><message>{ e.getMessage }</message><stackTrace>{ e.getStackTrace.map(e ⇒ s"\tat $e").reduceLeft((prev, next) ⇒ s"$prev\n$next") }</stackTrace></error>
        }
      }
    }
  }

  post("/xml/createMole") {
    contentType = "application/xml"

    println(request.headers get "apiKey")

    requireAuth(request.headers get "apiKey") {
      println("starting the create operation")
      val encapsulate = params get "encapsulate" match {
        case Some("on") ⇒ true
        case _          ⇒ false
      }

      val molePack = params get "pack" match {
        case Some("on") ⇒ true
        case _          ⇒ false
      }

      println("encapsulate and pack parsed")

      val res = createMole(fileParams get "file" map (_.getInputStream), fileParams get "csv" map (_.getInputStream), encapsulate, molePack)

      println("mole created")

      println(res)

      res match {
        case Left(error) ⇒ <error>{ error }</error>
        case Right(exec) ⇒ <moleID>{ exec.id }</moleID>
      }
    } {
      <error>"This service requires a password"</error>
    }
  }

  post("/json/createMole") {
    contentType = formats("json")

    requireAuth(request.headers get "apiKey") {
      val encapsulate = params get "encapsulate" match {
        case Some("on") ⇒ true
        case _          ⇒ false
      }

      val molePack = params get "pack" match {
        case Some("on") ⇒ true
        case _          ⇒ false
      }

      val res = createMole(fileParams get "file" map (_.getInputStream), fileParams get "csv" map (_.getInputStream), encapsulate, pack = molePack)

      res match {
        case Left(error) ⇒ Xml.toJson(<error>{ error }</error>)
        case Right(exec) ⇒ Xml.toJson(<moleID>{ exec.id }</moleID>)
      }
    } {
      Xml.toJson(<error>"This service requires a password"</error>)
    }
  }

  get("/execs") {
    new AsyncResult() {
      contentType = "text/html"

      val is = Future {
        ssp("/loadedExecutions", "ids" -> getMoleKeys)
      }
    }
  }

  get("/execs/:id") {
    new AsyncResult() {
      val is = Future {
        contentType = "text/html"

        val pRams = params("id")

        val pageData = getMoleStats(pRams) + ("Encapsulated" -> isEncapsulated(pRams)) + ("id" -> pRams) + ("status" -> getStatus(pRams))

        ssp("/executionData", pageData.toSeq: _*)
      }
    }
  }

  get("/json/execs/:id") {
    contentType = formats("json")

    val pRams = params("id")

    val stats = getMoleStats(pRams)
    val r = getStatus(pRams)

    render(("status", r) ~
      ("stats", stats.toSeq))
  }

  get("/json/execs") {
    contentType = formats("json")

    render(("execIds", getMoleKeys))
  }

  get("/start/:id") {
    contentType = "text/html"

    new AsyncResult() {
      val is = Future {

        startMole(params("id"))

        redirect("/execs/" + params("id"))
      }
    }
  }

  get("/data/:id/data.tar") {
    contentType = "application/octet-stream"
    getMoleResult(params("id"))
  }

  get("/xml/execs") {
    contentType = "application/xml"

    <mole-execs>
      { for (key ← getMoleKeys) yield <moleID>{ key }</moleID> }
    </mole-execs>
  }

  get("/xml/execs/:id") {
    contentType = "application/xml"

    val pRams = params("id")

    val stats = getMoleStats(pRams)
    val r = getStatus(pRams)

    <status current={ r }>
      {
        for (stat ← stats.keys) <stat id={ stat }>{ stats(stat) }</stat>
      }
    </status>

  }

  /*get("/xml/start/:id") {
    contentType = "text/html"

    new AsyncResult() {
      val is = Future {
        val exec = cachedMoles.get(params("id"))
        println(exec)

        val res = exec map { x ⇒ x.start; "started" }

        <exec-result> { res.getOrElse("id didn't exist") } </exec-result>
      }
    }
  }*/

  get("/json/start/:id") {
    contentType = formats("json")

    val exec = getMole(params("id"))

    render(("id", exec map (_.id) getOrElse "none") ~
      ("execResult", exec map { e ⇒ e.start; getStatus(params("id")) } getOrElse "id didn't exist"))
  }

  get("/xml/start/:id") {
    contentType = "application/xml"

    val exec = getMole(params("id"))

    <moleID status={ exec map { e ⇒ e.start; getStatus(params("id")) } getOrElse ("id doesn't exist") }>{ params("id") }</moleID>
  }

  get("/json/remove/:id") {
    contentType = formats("json")

    requireAuth(request.headers get "apiKey") {
      val exec = deleteMole(params("id"))

      render(("id", exec map (_.id) getOrElse "none") ~
        ("status", "deleted"))
    } {
      render(("error", "This service requires a password"))
    }
  }

  get("/remove/:id") {
    contentType = "text/html"
    new AsyncResult() {
      val is = Future {
        requireAuth(cookies get "apiKey") {
          deleteMole(params("id"))
          redirect("/execs")
          "" //TODO not good practice but necessary to get ssp("/authenticate"...) working
        } {
          ssp("/authenticate", "goTo" -> s"/remove/${params("id")}")
        }
      }
    }
  }

  get("/xml/remove/:id") {
    contentType = "application/xml"

    new AsyncResult() {
      val is = Future {
        requireAuth(request.headers get "apiKey") {
          val exec = deleteMole(params("id"))

          <moleID status={ if (exec.isDefined) "deleted" else "id doesn't exist" }>{ params("id") }</moleID>
        } {
          <error>"This service requires a password"</error>
        }
      }
    }
  }

  options("/createMole") {}

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
    resourceNotFound()
  }
}
