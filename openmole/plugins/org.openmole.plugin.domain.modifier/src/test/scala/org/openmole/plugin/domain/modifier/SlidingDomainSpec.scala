/*
 * Copyright (C) 21/12/12 Romain Reuillon
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

package org.openmole.plugin.domain.modifier

import org.openmole.core.workflow.data._
import org.openmole.core.workflow.domain._
import org.openmole.core.workflow.data._

import org.scalatest._

import scala.util.Random

class SlidingDomainSpec extends FlatSpec with Matchers {

  "SlidingDomain" should "change the values of a domain to array" in {
    implicit val rng = new Random(42)

    val r1 = (1 to 10)

    val d1 = new Domain[Int] with Discrete[Int] {
      override def iterator(context: Context)(implicit rng: Random) = r1.iterator
    }

    val md = SlidingDomain(d1, 2, 1).iterator(Context.empty)

    md.toList.size should equal(9)
  }

}
