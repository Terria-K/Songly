/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.teuria.songly;

import com.teuria.songly.models.Playlist;

/**
 *
 * @author teuria
 */
public class PlaylistItem extends javax.swing.JPanel {

    private Playlist playlist;
    private PlaylistClickEvent clickEvent;
    
    public PlaylistItem(Playlist playlist, PlaylistClickEvent clickEvent) {
        initComponents();
        this.playlist = playlist;
        this.playlistTitle.setText(playlist.getTitle());
        this.clickEvent = clickEvent;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playlistTitle = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(32767, 64));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        playlistTitle.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        playlistTitle.setIcon(jiconfont.swing.IconFontSwing.buildIcon(jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons.PLAYLIST_PLAY, 24, new java.awt.Color(255, 255, 255)));
        playlistTitle.setText("Name");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playlistTitle)
                .addContainerGap(707, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(playlistTitle)
                .addGap(21, 21, 21))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        clickEvent.run(playlist);
    }//GEN-LAST:event_formMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel playlistTitle;
    // End of variables declaration//GEN-END:variables
}

interface PlaylistClickEvent {
    void run(Playlist playlist);
}