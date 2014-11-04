package org.openmole.gui.plugin.task.groovy

import org.openmole.gui.bootstrap.osgi._
import org.openmole.gui.plugin.task.groovy.ext.GroovyTaskData
import org.openmole.gui.plugin.task.groovy.server.GroovyTaskFactory
import org.openmole.gui.plugin.task.groovy.client.GroovyTaskFactoryUI
import org.openmole.gui.server.factory._

/*
 * Copyright (C) 01/10/14 // mathieu.leclaire@openmole.org
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

class Activator extends OSGiActivator with ServerOSGiActivator {
  val data = new GroovyTaskData
  val dataClass = data.getClass()
  override def factories = Seq((dataClass, new GroovyTaskFactory(data)))
  override def factoriesUI = Seq((dataClass, new GroovyTaskFactoryUI))
}
