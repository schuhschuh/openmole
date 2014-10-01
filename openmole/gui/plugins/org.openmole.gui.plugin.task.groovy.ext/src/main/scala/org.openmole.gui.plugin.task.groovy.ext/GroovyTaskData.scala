package org.openmole.gui.plugin.task.groovy.ext
/*
 * Copyright (C) 25/09/14 // mathieu.leclaire@openmole.org
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

import org.openmole.gui.ext.data.{TaskData, PrototypeData}

class GroovyTaskData(val name: String = "",
                     val code: String = "",
                     val libs: Seq[String] = Seq(),
                     val inputs: Seq[PrototypeData] = Seq(),
                     val outputs: Seq[PrototypeData] = Seq(),
                     val inputParameters: Map[PrototypeData, String] = Map()) extends TaskData