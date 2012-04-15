/*
 * Copyright (C) 2012 reuillon
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

package org.openmole.ui.console

import java.util.concurrent.atomic.AtomicInteger
import org.openmole.core.batch.environment.AuthenticationMethod
import org.openmole.core.batch.environment.BatchEnvironment
import org.openmole.core.implementation.execution.local.LocalExecutionEnvironment
import org.openmole.core.implementation.validation.Validation
import org.openmole.core.model.execution.ExecutionState
import org.openmole.core.model.job.State
import org.openmole.core.model.mole.IMole
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.core.model.transition.IAggregationTransition
import org.openmole.core.model.transition.IExplorationTransition
import org.openmole.misc.workspace.Workspace

class Command {

  def print(environment: LocalExecutionEnvironment) = {
    println("Queued jobs: " + environment.nbJobInQueue)
    println("Number of threads: " + environment.nbThreads)
  }
  
  def print(environment: BatchEnvironment) = {
    
    val accounting = new Array[AtomicInteger](ExecutionState.values.size)
    val executionJobRegistry = environment.jobRegistry

    for (state <- ExecutionState.values) {
      accounting(state.id) = new AtomicInteger
    }

    for (executionJob <- executionJobRegistry.allExecutionJobs) {
      accounting(executionJob.state.id).incrementAndGet
    }

    for (state <- ExecutionState.values) {
      System.out.println(state.toString + ": " + accounting(state.id))
    }
      
  }
  
  def structure(mole: IMole) = {
    mole.capsules.zipWithIndex.foreach { 
      case(c, i) => 
        println(i + " " + c + " (" + c.outputTransitions.map{
            t =>
            (t match {
                case _: IExplorationTransition => "< "
                case _: IAggregationTransition => "> "
                case _ => "- "
              }) + t.end.capsule.toString
          }.foldLeft("") {
            (acc, c) => if(acc.isEmpty) c else acc + ", " + c
          } + ")")
    }
  }
  
  def print(moleExecution: IMoleExecution) = {
    val toDisplay = new Array[AtomicInteger](State.values.size)
    for (state <- State.values) toDisplay(state.id) = new AtomicInteger
    for (job <- moleExecution.moleJobs) toDisplay(job.state.id).incrementAndGet
    for (state <- State.values) System.out.println(state.toString + ": " + toDisplay(state.id))
  }
  
  def verify(mole: IMole) = Validation(mole).foreach(println)  
  
  def encrypt(s: String) = Workspace.encrypt(s)
  
  
  def auth(method: Class[_]) {
          Workspace.persistentList(method).foreach {
        case (i, m) => println(i + ": " + m)
      }
  }
  
  def auth = new {
    def update(index: Int, auth: AuthenticationMethod) =  
      Workspace.instance.persistentList(auth.method.asInstanceOf[Class[AuthenticationMethod]]).update(index, auth)
  }
  
  
}