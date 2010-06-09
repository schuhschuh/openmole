/*
 *  Copyright (C) 2010 Mathieu Leclaire <mathieu.leclaire@openmole.fr>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ui.ide.workflow.implementation;

import java.util.Collection;
import org.openmole.core.model.task.IGenericTask;
import org.openmole.ui.ide.control.TableType.Name;

/**
 *
 * @author Mathieu Leclaire <mathieu.leclaire@openmole.fr>
 */
public class MoleTaskModeulUI  <T extends IGenericTask> extends GenericTaskModelUI<T> {

    @Override
    public Collection<Name> getFields() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFields() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
