/*
 * Copyright (C) 2014 Romain Reuillon
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

package org.openmole.plugin.method.evolution

import fr.iscpif.mgo._
import org.openmole.core.workflow.data.PrototypeType

import scala.util.Random

object CMAES {

  def apply(
    termination: GATermination { type G >: CMAES#G; type P >: CMAES#P; type F >: CMAES#F },
    inputs: Inputs,
    objectives: Objectives) = {
    val (_inputs, _objectives) = (inputs, objectives)
    new CMAES {
      val inputs = _inputs
      val objectives = _objectives

      val stateType = termination.stateType
      val populationType = PrototypeType[Population[G, P, F]]
      val individualType = PrototypeType[Individual[G, P, F]]
      val aType = PrototypeType[A]
      val fType = PrototypeType[F]
      val gType = PrototypeType[G]

      val genomeSize = inputs.size

      //val mu = _mu
      type STATE = termination.STATE
      def initialState: STATE = termination.initialState
      def terminated(population: Population[G, P, F], terminationState: STATE)(implicit rng: Random): (Boolean, STATE) = termination.terminated(population, terminationState)
    }
  }
}

trait CMAES extends GAAlgorithm
  with KeepOffspringElitism
  with GAGenomeWithRandomValue
  with MaxAggregation
  with CMAESBreeding
  with CMAESArchive
  with ClampedGenome