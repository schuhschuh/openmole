/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CSVSamplingPanelUI.java
 *
 * Created on 27 avr. 2011, 15:02:06
 */
package org.openmole.ide.plugin.sampling.csv;

import javax.swing.JFileChooser;
import org.openmole.ide.core.properties.PanelUI;
import org.openmole.ide.core.properties.PanelUIData;

/**
 *
 * @author mathieu
 */
public class CSVSamplingPanelUI extends PanelUI {

    /** Creates new form CSVSamplingPanelUI */
    public CSVSamplingPanelUI() {
        initComponents();
    }

    public PanelUIData saveContent() {
        CSVSamplingPanelUIData panelData = new CSVSamplingPanelUIData();
        System.out.println("in saveContent CSVSamplingPanelUI " + csvPathFileTextField);
        System.out.println("in saveContent CSVSamplingPanelUI " + csvPathFileTextField.getText());
        panelData.csvFilePath_$eq(csvPathFileTextField.getText());
        return panelData;
    }

    public void loadContent(PanelUIData pud) {
        CSVSamplingPanelUIData panelData = (CSVSamplingPanelUIData) pud;
        System.out.println("in loadContent CSVSamplingPanelUI " + csvPathFileTextField);
        csvPathFileTextField.setText(panelData.csvFilePath());
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        csvPathFileTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(CSVSamplingPanelUI.class, "CSVSamplingPanelUI.jLabel1.text")); // NOI18N

        csvPathFileTextField.setText(org.openide.util.NbBundle.getMessage(CSVSamplingPanelUI.class, "CSVSamplingPanelUI.csvPathFileTextField.text")); // NOI18N
        csvPathFileTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csvPathFileTextFieldActionPerformed(evt);
            }
        });

        browseButton.setText(org.openide.util.NbBundle.getMessage(CSVSamplingPanelUI.class, "CSVSamplingPanelUI.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(csvPathFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(csvPathFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {browseButton, csvPathFileTextField, jLabel1});

    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(this);
        csvPathFileTextField.setText(fc.getSelectedFile().getPath());

        //  for(PrototypeUI p : PrototypesUI().get)
    }//GEN-LAST:event_browseButtonActionPerformed

    private void csvPathFileTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csvPathFileTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_csvPathFileTextFieldActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField csvPathFileTextField;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
