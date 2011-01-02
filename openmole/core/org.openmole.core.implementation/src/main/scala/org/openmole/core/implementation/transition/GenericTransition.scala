/*
 * Copyright (C) 2010 reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
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

package org.openmole.core.implementation.transition

import java.util.logging.Logger
import org.openmole.commons.exception.InternalProcessingError
import org.openmole.core.implementation.data.Context
import org.openmole.core.implementation.data.Variable
import org.openmole.core.implementation.tools.CloningService
import org.openmole.core.implementation.tools.ContextAggregator
import org.openmole.core.model.capsule.IGenericCapsule
import org.openmole.core.model.data.IContext
import org.openmole.core.model.job.ITicket
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.core.model.mole.ISubMoleExecution
import org.openmole.core.model.task.IGenericTask
import org.openmole.core.model.tools.IContextBuffer
import org.openmole.core.model.transition.ICondition
import org.openmole.core.model.transition.IGenericTransition
import org.openmole.core.model.transition.ISlot
import org.openmole.core.implementation.tools.ToCloneFinder.variablesToClone
import scala.collection.mutable.ListBuffer

abstract class GenericTransition(val start: IGenericCapsule, val end: ISlot, val condition: ICondition, val filtered: Set[String]) extends IGenericTransition {

  plugStart 
  end += this

  def nextTaskReady(ticket: ITicket, execution: IMoleExecution): Boolean = {
    val registry = execution.localCommunication.transitionRegistry

    for (t <- end.transitions) {
      if (!registry.isRegistred(t, ticket)) return false
    }
    return true
  }

  protected def submitNextJobsIfReady(global: IContext, context: IContextBuffer, ticket: ITicket, moleExecution: IMoleExecution, subMole: ISubMoleExecution) = synchronized {
    val registry = moleExecution.localCommunication.transitionRegistry
    registry.register(this, ticket, context)

    if (nextTaskReady(ticket, moleExecution)) {
      val combinaison = new ListBuffer[IContext]

      for (t <- end.transitions) combinaison += {
        (registry.remove(t, ticket) match {
            case None => throw new InternalProcessingError("BUG Context not registred for transtion")
            case Some(c) => c
          }).toContext
      }
            
      val itDc = end.capsule.inputDataChannels
      for (dataChannel <- itDc) {
        combinaison += dataChannel.consums(ticket, moleExecution)
      }

      val newTicket =  if (end.capsule.intputSlots.size <= 1) ticket else {
        moleExecution.nextTicket(ticket.parent match {
            case None => throw new InternalProcessingError("BUG should never reach root ticket")
            case Some(t) => t
          })
      } 

      val endTask = end.capsule.task match {
        case None => throw new InternalProcessingError("End task capsule of the transition is no assigned")
        case Some(t) => t
      }  
  
      val newContext = ContextAggregator.aggregate(endTask.inputs, false, combinaison)

      moleExecution.submit(end.capsule,  global, newContext, newTicket, subMole)
    }
  }

  override def perform(global: IContext, context: IContext, ticket: ITicket, toClone: Set[String], scheduler: IMoleExecution, subMole: ISubMoleExecution) = {
    if (isConditionTrue(global, context)) {
      /*-- Remove filtred --*/
      for(name <- filtered) context -= name
      performImpl(global, context, ticket, toClone, scheduler, subMole);
    }
  }


  override def isConditionTrue(global: IContext, context: IContext): Boolean = {
    condition.evaluate(global, context)
  }

  protected def performImpl(global: IContext, context: IContext, ticket: ITicket, toClone: Set[String], scheduler: IMoleExecution, subMole: ISubMoleExecution) 
  protected def plugStart
}
