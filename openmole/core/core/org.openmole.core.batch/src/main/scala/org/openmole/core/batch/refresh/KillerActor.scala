/*
 * Copyright (C) 2012 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.batch.refresh

import akka.actor.{ Actor, ActorRef }
import org.openmole.core.implementation.tools.objectToSomeObjectConverter
import org.openmole.core.model.execution.ExecutionState
import org.openmole.misc.tools.service.Logger
import org.openmole.misc.workspace.{ ConfigurationLocation, Workspace }
import org.openmole.core.batch.environment.BatchEnvironment

object KillerActor extends Logger {
  val maxKillAttempts = new ConfigurationLocation("JobManager", "MaxKillAttempts")
  Workspace += (maxKillAttempts, "3")
}

import KillerActor.Log._
import KillerActor._

class KillerActor(jobManager: ActorRef) extends Actor {
  def receive = withRunFinalization {
    case msg @ KillBatchJob(bj, killAttempts) ⇒
      if (killAttempts < Workspace.preferenceAsInt(maxKillAttempts) && bj.state != ExecutionState.KILLED) {
        try bj.jobService.tryWithToken {
          case Some(t) ⇒ {
            bj.kill(t)
            println(s"Attempted to kill ${bj} ${killAttempts} time(s)")
            // job state will keep on getting refreshed by Refresh actor if necessary
            // just plan a later new attempt that will eventually end the process
            jobManager ! Delay(KillBatchJob(bj, killAttempts - 1), bj.jobService.environment.minUpdateInterval)
          }
          case None ⇒
            jobManager ! Delay(msg, Workspace.preferenceAsDuration(BatchEnvironment.NoTokenForServiceRetryInterval))
        } catch {
          case e: Throwable ⇒ logger.log(FINE, "Could not kill job.", e)
        }
      }
  }
}
