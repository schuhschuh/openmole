/*
 * Copyright (C) 2011 Mathieu leclaire <mathieu.leclaire at openmole.org>
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

package org.openmole.ide.core.workflow.implementation.paint

import java.awt.Point
import org.netbeans.api.visual.anchor.Anchor
import org.openmole.ide.core.commons.Constants
import org.openmole.ide.core.control.MoleScenesManager
import org.openmole.ide.core.workflow.implementation.CapsuleViewUI

class OSlotAnchor(relatedWidget: CapsuleViewUI) extends SlotAnchor(relatedWidget) {

  val x= Constants.TASK_CONTAINER_WIDTH + 10
  val y= Constants.TASK_TITLE_HEIGHT + 22
  
  override def compute(entry: Anchor.Entry)= {
    var detailedEffect= 0
    if (MoleScenesManager.detailedView)
      detailedEffect= Constants.EXPANDED_TASK_CONTAINER_WIDTH -Constants.TASK_CONTAINER_WIDTH
    new Result(relatedWidget.convertLocalToScene(new Point(x + detailedEffect, y)), Anchor.Direction.RIGHT)
  }
}