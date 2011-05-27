/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SettingsFrame.java
 *
 * Created on Oct 10, 2009, 11:30:34 AM
 */
package info.semanticsoftware.semassist.client.openoffice.utils;

import info.semanticsoftware.semassist.csal.ClientUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author nick
 */
public class SettingsFrame extends javax.swing.JFrame
{

    private static final String HIGHLIGHT_ON = "Disable Annotation Highlighting";
    private static final String HIGHLIGHT_OFF = "Enable Annotation Highlighting";
    private static String CURRENT_HIGHLIGHT = HIGHLIGHT_OFF;
    private static boolean mDefaultServerSelect = true;
    private static String mCustomServerPort;
    private static String mCustomServerHost;


    /** Creates new form SettingsFrame */
    public SettingsFrame()
    {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings( "unchecked" )
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jCheckBox1 = new javax.swing.JCheckBox(null,null,UNOUtils.getCURRENT_HIGHLIGHT());
        jCheckBox2 = new JCheckBox("Filter Empty Features", UNOUtils.isEmptyFeatureFilter());
        jCheckBox3 = new JCheckBox("Show Annotation Content", UNOUtils.isShowAnnotationContent());
        jRadioButtonDefault = new javax.swing.JRadioButton( null, null, mDefaultServerSelect);
        jRadioButtonCustom = new javax.swing.JRadioButton(null, null, !mDefaultServerSelect);
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jDefaultHostField = new javax.swing.JTextField(ClientUtils.defaultServerHost());
        jDefaultPortField = new javax.swing.JTextField(ClientUtils.defaultServerPort());
        jCustomHostField = new javax.swing.JTextField(mCustomServerHost);
        jCustomPortField = new javax.swing.JTextField(mCustomServerPort);
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Global Settings");
        setAlwaysOnTop(true);

        jCheckBox1.setText("Annotation Highlighting");

        jRadioButtonDefault.setText("Default");
        jRadioButtonDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDefaultActionPerformed(evt);
            }
        });

        jRadioButtonCustom.setText("Custom");
        jRadioButtonCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCustomActionPerformed(evt);
            }
        });

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener( new ActionListener()
            {

                public void actionPerformed( ActionEvent e )
                {
                    dispose();
                    setVisible(false);
                }

            } );

            jLabel1.setText("Server Settings");

            jLabel2.setText("Host:");

            jLabel3.setText("Port:");

            jLabel4.setText("Host:");

            jLabel5.setText("Port:");

            jDefaultHostField.setEditable(false);

            jDefaultPortField.setEditable(false);

            jCustomHostField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    jCustomHostFieldKeyReleased(evt);
                }
            });

            jCustomPortField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    jCustomPortFieldKeyReleased(evt);
                }
            });

            jLabel6.setText("Annotation Settings");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRadioButtonDefault)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel2))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jDefaultHostField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jDefaultPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRadioButtonCustom)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel5)
                                            .addGap(16, 16, 16))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel4)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jCustomHostField, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jCustomPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGap(66, 66, 66))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jCheckBox1))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jCheckBox2))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jCheckBox3))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(197, 197, 197)
                            .addComponent(jLabel6))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(207, 207, 207)
                            .addComponent(jLabel1))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap(338, Short.MAX_VALUE)
                            .addComponent(cancelButton)
                            .addGap(18, 18, 18)
                            .addComponent(okButton)))
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jLabel6)
                    .addGap(18, 18, 18)
                    .addComponent(jCheckBox1)
                    .addGap(18, 18, 18)
                    .addComponent(jCheckBox2)
                    .addGap(18, 18, 18)
                    .addComponent(jCheckBox3)
                    .addGap(27, 27, 27)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(jLabel1)
                    .addGap(12, 12, 12)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButtonDefault)
                        .addComponent(jRadioButtonCustom))
                    .addGap(19, 19, 19)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jCustomHostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jDefaultHostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(26, 26, 26)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jDefaultPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCustomPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel3))
                    .addGap(59, 59, 59)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(okButton)
                        .addComponent(cancelButton))
                    .addContainerGap())
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        // for checkbox
        updateCheckBoxStatus();
        updateRadioButtonsStatus();
        updateServerInfo();

        dispose();
        setVisible( false );
    }//GEN-LAST:event_okButtonActionPerformed

    private void jRadioButtonDefaultActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonDefaultActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonDefaultActionPerformed
        //De-select the custom radio button
        jRadioButtonCustom.setSelected( false );
    }//GEN-LAST:event_jRadioButtonDefaultActionPerformed

    private void jRadioButtonCustomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonCustomActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonCustomActionPerformed
        //De-select the default radio button
        jRadioButtonDefault.setSelected( false );
    }//GEN-LAST:event_jRadioButtonCustomActionPerformed

    private void jCustomHostFieldKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_jCustomHostFieldKeyReleased
    {//GEN-HEADEREND:event_jCustomHostFieldKeyReleased
        mCustomServerHost = jCustomHostField.getText();
    }//GEN-LAST:event_jCustomHostFieldKeyReleased

    private void jCustomPortFieldKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_jCustomPortFieldKeyReleased
    {//GEN-HEADEREND:event_jCustomPortFieldKeyReleased
        mCustomServerPort = jCustomPortField.getText();
    }//GEN-LAST:event_jCustomPortFieldKeyReleased

    private void updateCheckBoxStatus()
    {
        boolean status;

        if( jCheckBox1.isSelected() )
        {
            System.out.println( "------ Highlight Enabled" );
            UNOUtils.enableHighlighting();
        }
        else
        {
            System.out.println( "------ Highlight Disabled" );
            UNOUtils.disableHighlighting();
        }

        // Empty feature filtering option.
        status = jCheckBox2.isSelected();
        System.out.println( "------ Filter Empty Features " + (status ? "Enabled" : "Disabled") );
        UNOUtils.setEmptyFeatureFilter( status );

        // Show annotation content option.
        status = jCheckBox3.isSelected();
        System.out.println( "------ Show Annotation Content " + (status ? "Enabled" : "Disabled") );
        UNOUtils.setShowAnnotationContent( status );
    }

    private void updateRadioButtonsStatus()
    {
        if( jRadioButtonDefault.isSelected() )
        {
            mDefaultServerSelect = true;
        }
        else
        {
            mDefaultServerSelect = false;
        }
    }

    private void updateServerInfo()
    {
        if( jRadioButtonDefault.isSelected() )
        {
            UNOUtils.setServerHost( jDefaultHostField.getText() );
            UNOUtils.setServerPort( jDefaultPortField.getText() );
        }
        else
        {
             UNOUtils.setServerHost( mCustomServerHost );
             UNOUtils.setServerPort( mCustomServerPort );
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JTextField jCustomHostField;
    private javax.swing.JTextField jCustomPortField;
    private javax.swing.JTextField jDefaultHostField;
    private javax.swing.JTextField jDefaultPortField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JRadioButton jRadioButtonCustom;
    private javax.swing.JRadioButton jRadioButtonDefault;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

}
