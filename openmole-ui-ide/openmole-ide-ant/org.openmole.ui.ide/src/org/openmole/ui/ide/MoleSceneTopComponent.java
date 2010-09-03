/*
 *  Copyright (C) 2010 mathieu
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Affero GNU General Public License as published by
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
package org.openmole.ui.ide;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.spi.palette.PaletteController;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openmole.ui.ide.commons.ApplicationCustomize;
import org.openmole.ui.ide.control.task.TaskSettingTabManager;
import org.openmole.ui.ide.dialog.PrototypeManagementPanel;
import org.openmole.ui.ide.workflow.implementation.MoleScene;
import org.openmole.ui.ide.palette.PaletteSupport;
import org.openmole.ui.ide.workflow.action.ManagePrototypeAction;
import org.openmole.ui.ide.workflow.action.EnableTaskDetailedView;
import org.openmole.ui.ide.workflow.action.MoveOrDrawTransitionAction;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.openmole.ui.ide//MoleSceneTopComponent//EN",
autostore = false)
public final class MoleSceneTopComponent extends TopComponent {

    private static MoleSceneTopComponent instance;
    private PaletteController palette;
    private JComponent myView;
    private PrototypeManagementPanel prototypeManagement;
    private JToolBar toolBar = new JToolBar("SSSE");
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "MoleSceneTopComponentTopComponent";

    public MoleSceneTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(MoleSceneTopComponent.class, "CTL_MoleSceneTopComponentTopComponent"));
        setToolTipText(NbBundle.getMessage(MoleSceneTopComponent.class, "HINT_MoleSceneTopComponentTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

        //FIXME un meilleur endroit pour les inits??
        // TableMapping.getInstance().initialize();
        // Preferences.getInstance().initialize();
        TaskSettingTabManager.getInstance().setTabbedPane(jTabbedPane1);


        MoleScene scene = new MoleScene();
        myView = scene.createView();

        moleSceneScrollPane.setViewportView(myView);
        jTabbedPane1.add("Workflow", moleSceneScrollPane);
        palette = PaletteSupport.createPalette();
       // refreshPalette();
        associateLookup(Lookups.fixed(new Object[]{palette}));
//        associateLookup(Lookups.proxy(new Lookup.Provider() {
//
//            @Override
//            public Lookup getLookup() {
//                return Lookups.fixed(new Object[]{palette});
//            }
//        }));
        
        prototypeManagement = new PrototypeManagementPanel();
        prototypeManagement.setVisible(true);

        JToggleButton moveButton = new JToggleButton(new ImageIcon(ApplicationCustomize.IMAGE_TRANSITIONS));
        moveButton.addActionListener(new MoveOrDrawTransitionAction());
        moveButton.setSelected(false);

        JToggleButton detailedViewButton = new JToggleButton("Detailed view");
        detailedViewButton.addActionListener(new EnableTaskDetailedView(scene));

        JButton newPrototypeButton = new JButton("Prototypes");
        newPrototypeButton.addActionListener(new ManagePrototypeAction(prototypeManagement,this));
        toolBar.add(moveButton);
        toolBar.add(detailedViewButton);
        toolBar.add(new JToolBar.Separator());
        toolBar.add(newPrototypeButton);
        add(toolBar, java.awt.BorderLayout.NORTH);
        add(jTabbedPane1, java.awt.BorderLayout.CENTER);

       

        //   associateLookup(Lookups.fixed(new Object[]{new PropertySupport()}));
    }

    public void refreshPalette() {
        System.out.println("-- refreshPalette");
//        palette = PaletteSupport.createPalette();
//        associateLookup(Lookups.proxy(new Lookup.Provider() {
//        
//            @Override
//            public Lookup getLookup() {
//                return palette.getRoot();
//            }
//        }));
//       // associateLookup(Lookups.fixed(new Object[]{palette}));
//        repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        moleSceneScrollPane = new javax.swing.JScrollPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();

        setLayout(new java.awt.BorderLayout());

        moleSceneScrollPane.setViewportView(jTabbedPane1);

        add(moleSceneScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JScrollPane moleSceneScrollPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized MoleSceneTopComponent getDefault() {
        if (instance == null) {
            instance = new MoleSceneTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the MoleSceneTopComponentTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized MoleSceneTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(MoleSceneTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof MoleSceneTopComponent) {
            return (MoleSceneTopComponent) win;
        }
        Logger.getLogger(MoleSceneTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }
}
