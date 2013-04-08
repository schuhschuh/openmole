package root.libraries

import root.Defaults
import sbt._
import sbt.Keys._

package object apache extends Defaults {
  val dir = file("libraries") / "apache"

  lazy val all = Project("libraries-apache", dir) aggregate(pool, config, math, exec)

  lazy val pool = OsgiProject("org.apache.commons.pool") settings
    (libraryDependencies += "commons-pool" % "commons-pool" % "1.5.4")

  lazy val config = OsgiProject("org.apache.commons.configuration", privatePackages = Seq("org.apache.commons.*")) settings
    (libraryDependencies += "commons-configuration" % "commons-configuration" % "1.6")

  lazy val math = OsgiProject("org.apache.commons.math", exports = Seq("org.apache.commons.math3.*")) settings
    (libraryDependencies += "org.apache.commons" % "commons-math3" % "3.0")

  lazy val exec = OsgiProject("org.apache.commons.exec") settings
    (libraryDependencies += "org.apache.commons" % "commons-exec" % "1.1")

}