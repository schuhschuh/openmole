/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.plugin.environment.pbs

import org.openmole.ide.plugin.environment.tools.RequirementDataUI
import org.openmole.plugin.environment.pbs.PBSEnvironment
import org.openmole.core.batch.environment.BatchEnvironment
import org.openmole.ide.core.model.data.IEnvironmentDataUI

class PBSEnvironmentDataUI(val name: String = "",
                           val login: String = "",
                           val host: String = "",
                           val dir: String = "",
                           val queue: String = "",
                           val openMOLEMemory: Int = BatchEnvironment.defaultRuntimeMemory)
    //                           val requirements: RequirementDataUI = new RequirementDataUI) 
    extends IEnvironmentDataUI { ui ⇒

  def coreObject = new PBSEnvironment(login,
    host,
    path = Some(dir),
    // requirements.toMap,
    openMOLEMemory = Some(ui.openMOLEMemory),
    queue = { if (queue.isEmpty) None else Some(queue) })

  def coreClass = classOf[PBSEnvironment]

  def imagePath = "img/pbs.png"

  override def fatImagePath = "img/pbs_fat.png"

  def buildPanelUI = new PBSEnvironmentPanelUI(this)
}
