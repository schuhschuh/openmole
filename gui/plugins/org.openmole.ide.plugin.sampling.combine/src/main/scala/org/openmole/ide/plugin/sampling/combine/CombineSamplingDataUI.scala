/*
 * Copyright (C) 2011 <mathieu.Mathieu Leclaire at openmole.org>
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
package org.openmole.ide.plugin.sampling.combine

import org.openmole.ide.core.model.data.{ IFactorDataUI, IDomainDataUI, ISamplingDataUI }
import org.openmole.core.model.sampling.{ DiscreteFactor, Factor, Sampling }
import org.openmole.plugin.sampling.combine.CombineSampling
import org.openmole.ide.core.model.dataproxy.IPrototypeDataProxyUI
import org.openmole.core.model.data.Prototype
import org.openmole.misc.exception.UserBadDataError
import org.openmole.ide.misc.widget.{ URL, Helper }
import org.openmole.core.model.domain.{ Discrete, Domain }

class CombineSamplingDataUI extends ISamplingDataUI {
  def coreObject(factors: List[IFactorDataUI], samplings: List[Sampling]) =
    new CombineSampling(
      (factors.map(f ⇒ f.prototype match {
        case Some(p: IPrototypeDataProxyUI) ⇒
          DiscreteFactor(Factor(p.dataUI.coreObject.asInstanceOf[Prototype[Any]],
            f.domain.dataUI.coreObject.asInstanceOf[Domain[Any] with Discrete[Any]]))
        case _ ⇒ throw new UserBadDataError("No Prototype has been associated to the Factor")
      }) ::: samplings): _*)

  def buildPanelUI = new GenericCombineSamplingPanelUI(this) {
    override val help = new Helper(List(new URL(i18n.getString("completePermalinkText"),
      i18n.getString("combinePermalink"))))
  }

  def imagePath = "img/combineSampling.png"

  def fatImagePath = "img/combineSampling_fat.png"

  def isAcceptable(domain: IDomainDataUI) = false

  def isAcceptable(sampling: ISamplingDataUI) = true

  def preview = "Combine"

  def coreClass = classOf[CombineSampling]
}