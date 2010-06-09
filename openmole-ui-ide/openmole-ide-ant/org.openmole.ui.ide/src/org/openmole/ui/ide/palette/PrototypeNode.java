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

package org.openmole.ui.ide.palette;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.Lookups;
import org.openmole.ui.ide.commons.ApplicationCustomize;

/**
 *
 * @author Mathieu Leclaire <mathieu.leclaire@openmole.fr>
 */
public class PrototypeNode extends AbstractNode {
    private Class prototypeClass;

    public PrototypeNode(DataFlavor key,
                         Class prototypeClass) {
        super(Children.LEAF, Lookups.fixed(new Object[]{key}));
        this.prototypeClass = prototypeClass;
        setName(prototypeClass.getSimpleName());
      //  setIconBaseWithExtension(Preferences.getInstance().getModelSettings(Preferences.getInstance().getModelClass(coreTask)).getThumbImagePath());
    }

     //DND start
    @Override
    public Transferable drag() throws IOException {
        ExTransferable retValue = ExTransferable.create( super.drag() );
        //add the 'data' into the Transferable
        retValue.put( new ExTransferable.Single(ApplicationCustomize.PROTOTYPE_DATA_FLAVOR) {
            @Override
            protected Object getData() throws IOException, UnsupportedFlavorException
            {return prototypeClass;}

        });
        return retValue;
    }
    //DND end
}
