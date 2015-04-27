package org.openmole.gui.client.core.files

import org.openmole.gui.client.core.Post
import org.openmole.gui.shared._
import org.openmole.gui.misc.js.Forms._
import org.scalajs.dom.html.{ Input, UList }
import scalatags.JsDom.all._
import scalatags.JsDom.{ TypedTag, tags ⇒ tags }
import org.openmole.gui.misc.js.{ Forms ⇒ bs }
import org.openmole.gui.misc.js.JsRxTags._
import org.openmole.gui.misc.utils.Utils._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import TreeNode._
import autowire._
import rx._

/*
 * Copyright (C) 16/04/15 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

object TreeNodePanel {

  def apply(path: String): TreeNodePanel = apply(DirNode(path))

  def apply(dirNode: DirNode): TreeNodePanel = new TreeNodePanel(dirNode)

  def computeAllSons(dn: DirNode): Unit = {
    sons(dn).foreach { sons ⇒
      dn.sons() = sons
      dn.sons().foreach { tn ⇒
        tn match {
          case (d: DirNode) ⇒
            computeAllSons(d)
          case _ ⇒
        }
      }
    }
  }

  def sons(dirNode: DirNode) = Post[Api].listFiles(dirNode).call()

}

import TreeNodePanel._

class TreeNodePanel(_dirNode: DirNode) {

  val rootNode: Var[DirNode] = Var(_dirNode)
  val addDirState: Var[Boolean] = Var(false)
  val selectedNode: Var[Option[TreeNode]] = Var(None)
  val dirNodeLine: Var[Seq[DirNode]] = Var(Seq(rootNode()))

  computeAllSons(_dirNode)

  val addRootDirButton =
    bs.glyphButton(" Add", btn_success, glyph_folder_close, () ⇒ {
      rootDirInput.value = ""
      addDirState() = !addDirState()
    })(`type` := "submit")

  val rootDirInput: Input = bs.input("")(
    placeholder := "Folder name",
    width := "130px",
    autofocus
  ).render

  val view = tags.div(
    Rx {
      val toDraw = dirNodeLine().drop(1)
      val dirNodeLineSize = toDraw.size
      buttonGroup()(
        glyphButton(" Home", btn_primary, glyph_home, goToDirAction(dirNodeLine().head)),
        if (dirNodeLineSize > 2) goToDirButton(toDraw(dirNodeLineSize - 3), Some("...")),
        toDraw.drop(dirNodeLineSize - 2).takeRight(2).map { dn ⇒ goToDirButton(dn) }
      )
    },
    Rx {
      bs.form()(
        inputGroup(navbar_left)(
          inputGroupButton(addRootDirButton),
          if (addDirState()) rootDirInput else tags.span()
        ),
        onsubmit := { () ⇒
          {
            val newDirName = rootDirInput.value
            Post[Api].addRootDirectory(newDirName).call().foreach { b ⇒
              if (b) rootNode().sons() = rootNode().sons() :+ DirNode(newDirName, Var(rootNode().canonicalPath() + "/" + newDirName))
              addDirState() = !addDirState()
              reComputeAllSons(rootNode())
            }
          }
        })
    },
    tags.div(`class` := "tree")(
      Rx {
        drawTree(dirNodeLine().last.sons())
      }
    )
  )

  def goToDirButton(dn: DirNode, name: Option[String] = None) = bs.button(name.getOrElse(dn.name()), btn_default)(onclick := { () ⇒
    goToDirAction(dn)()
  })

  def goToDirAction(dn: DirNode): () ⇒ Unit = () ⇒ {
    dirNodeLine() = dirNodeLine().zipWithIndex.filter(_._1 == dn).headOption.map {
      case (dn, index) ⇒ dirNodeLine().take(index + 1)
    }.getOrElse(dirNodeLine())
    drawTree(dirNodeLine().last.sons())
  }

  def drawTree(tns: Seq[TreeNode]): TypedTag[UList] = tags.ul(
    for (tn ← tns.sorted(TreeNodeOrdering)) yield {
      drawNode(tn)
    }
  )

  def drawNode(node: TreeNode) = node match {
    case fn: FileNode ⇒ clickableElement(fn, "file", () ⇒ {
      println(fn.name() + " display the file")
    })
    case dn: DirNode ⇒ clickableElement(dn, "dir", () ⇒ {
      dirNodeLine() = dirNodeLine() :+ dn
    }
    )
  }

  def reComputeAllSons(dn: DirNode) = {
    dn.selected() = false
    selectedNode() = None
    computeAllSons(dn)
  }

  def clickableElement(tn: TreeNode,
                       classType: String,
                       todo: () ⇒ Unit) = tags.li(
    tags.span(
      cursor := "pointer",
      if (tn.selected()) {
        backgroundColor := "green"
      },
      onclick := {
        () ⇒
          selectedNode().map {
            _.selected() = false
          }
          selectedNode() = Some(tn)
          tn.selected() = !tn.selected()
          todo()
      }, `class` := classType)(
        tags.i(`class` := {
          tn.hasSons match {
            case true  ⇒ "glyphicon glyphicon-plus-sign"
            case false ⇒ ""
          }
        }),
        tags.i(tn.name())
      ),
    if (tn.selected())
      tags.div(
      (tn match {
        case dn: DirNode ⇒
          Seq(tags.span(glyphSpan(glyph_trash, () ⇒ trashNode(dn)),
            glyphSpan(glyph_folder_close, () ⇒
              Post[Api].addDirectory(dn, "TOTO").call().foreach {
                b ⇒
                  if (b) reComputeAllSons(dn)
              }),
            glyphSpan(glyph_file, () ⇒
              Post[Api].addFile(dn, "TOTO.scala").call().foreach {
                b ⇒
                  if (b) reComputeAllSons(dn)
              })))
        case fn: FileNode ⇒
          Seq(glyphSpan(glyph_trash, () ⇒ trashNode(fn)))

      }) :+ glyphSpan(glyph_edit, () ⇒ println("edit")): _*)
  )

  def trashNode(treeNode: TreeNode) =
    Post[Api].deleteFile(treeNode).call().foreach { _ ⇒
      reComputeAllSons(rootNode())
    }

}