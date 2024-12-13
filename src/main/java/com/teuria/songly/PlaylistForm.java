/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.teuria.songly;

import com.teuria.songly.models.Music;
import com.teuria.songly.models.Playlist;
import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

/**
 *
 * @author teuria
 */
public class PlaylistForm extends javax.swing.JDialog {
    private JCheckBox[] checkboxes;
    /**
     * Creates new form PlaylistForm
     */
    public PlaylistForm(JFrame parent, Music music) {
        super(parent, "", Dialog.ModalityType.DOCUMENT_MODAL);
        setSize(360, 240);
        setLocationByPlatform(true);
        initComponents();

        Collection<Playlist> playlists = Database.getPlaylists();
        checkboxes = new JCheckBox[playlists.size()];
        
        // this is like for loop, but we do it kinda manually
        int i = 0;
        for (Playlist playlist : playlists) {
            String title = playlist.getTitle();
            
            JCheckBox checkBox = new JCheckBox(title);
            checkBox.setSelected(playlist.getSongs().contains(music));
            checkboxes[i] = checkBox;
            checkPanel.add(checkBox);
            // I don't like ++
            i += 1;
        }
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                for (JCheckBox checkBox : checkboxes) {
                    String title = checkBox.getText();
                    Playlist playlist = Database.getPlaylist(title);
                    if (checkBox.isSelected()) {
                        playlist.addMusic(music);
                    } else {
                        playlist.removeMusic(music);
                    }
                }
                
                Database.save();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        checkPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add to playlist");

        checkPanel.setLayout(new javax.swing.BoxLayout(checkPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(checkPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel checkPanel;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
