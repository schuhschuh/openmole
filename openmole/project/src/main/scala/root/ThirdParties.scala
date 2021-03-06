package root

import sbt._
import org.openmole.buildsystem.OMKeys._
import sbt.Keys._

object ThirdParties extends Defaults {

  lazy val dir = file("third-parties")

  lazy val openmoleTar = OsgiProject("org.openmole.tool.tar", imports = Seq("*")) settings (bundleType := Set("core")) dependsOn (openmoleFile)
  lazy val openmoleFile = OsgiProject("org.openmole.tool.file", imports = Seq("*")) settings (bundleType := Set("core")) dependsOn (openmoleLock, openmoleThread)
  lazy val openmoleLock = OsgiProject("org.openmole.tool.lock", imports = Seq("*")) settings (bundleType := Set("core"))
  lazy val openmoleThread = OsgiProject("org.openmole.tool.thread", imports = Seq("*")) settings (bundleType := Set("core"))
  lazy val openmoleHash = OsgiProject("org.openmole.tool.hash", imports = Seq("*")) settings (bundleType := Set("core")) dependsOn (openmoleFile, openmoleStream)
  lazy val openmoleStream = OsgiProject("org.openmole.tool.stream", imports = Seq("*")) settings (bundleType := Set("core"))
  lazy val openmoleData = OsgiProject("org.openmole.tool.data", imports = Seq("*")) settings (bundleType := Set("core")) settings (
    libraryDependencies += Libraries.scalaLang
  )

  lazy val toolxitBibtexMacros = OsgiProject("toolxit.bibtex.macros", "toolxit.bibtex/macros") settings (
    libraryDependencies += Libraries.scalaLang
  )

  lazy val toolxitBibtex = OsgiProject("toolxit.bibtex.core", "toolxit.bibtex/core", exports = Seq("toolxit.bibtex.*", "freemarker.*")) dependsOn (toolxitBibtexMacros) settings (
    libraryDependencies ++= Seq(
      Libraries.scalaLang,
      "org.freemarker" % "freemarker" % "2.3.19",
      Libraries.slf4j
    )
  )

}
