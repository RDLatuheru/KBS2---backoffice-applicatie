package com.company;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ArtikelScherm extends JPanel implements ActionListener, ListSelectionListener {
    private JList<Artikel> lijstArtikel;
    private Artikel[] artikelen;
    private JScrollPane scrollPaneArtikelen;
    private DefaultListModel<Artikel> model;

    private JPanel north, west, south, east, center;
    private JTextField tfFindItem;
    private JButton bZoek;
    private Connection c = DBC.getInstance().getConnection();
    private ArtikelPanel artikelPanel;

    public ArtikelScherm() throws SQLException {
        setLayout(new BorderLayout());

        //NORTH - controls
        add(north = new JPanel(new FlowLayout(FlowLayout.LEFT)), BorderLayout.NORTH);
        north.setBorder(new EtchedBorder());
        north.add(new JLabel("Artikel naam/nummer:"));
        north.add(tfFindItem = new JTextField(5));
        north.add(bZoek = new JButton("Zoeken"));
        bZoek.addActionListener(this);

        //EAST - low stock items
        add(east = new JPanel(), BorderLayout.EAST);
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.setBorder(new EtchedBorder());
        JLabel lblLageVoorraad = new JLabel("Lage voorraad");
        lblLageVoorraad.setFont(new Font("Default", Font.BOLD, 15));
        east.add(lblLageVoorraad);
        east.add(generateArtikelScrollPane());
        east.add(Box.createRigidArea(new Dimension(0, 500)));

        //CENTER - content
        add(center = new JPanel(new FlowLayout()), BorderLayout.CENTER);
        center.setBorder(new EtchedBorder());

        add(south = new JPanel(), BorderLayout.SOUTH);
        JButton b = new JButton();
        south.add(b);
        b.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==bZoek){
            try {
                int aID = Integer.parseInt(tfFindItem.getText());
                artikelPanel = new ArtikelPanel(c, aID);
            } catch (NumberFormatException | SQLException error) {
                try {
                    ArtikelDialoog artikelDialoog = new ArtikelDialoog(c, tfFindItem.getText());
                    artikelPanel = new ArtikelPanel(c, artikelDialoog.getaID());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                error.printStackTrace();
            }
            if (artikelPanel != null){
                center.removeAll();
            }
            center.add(artikelPanel);
        }
        repaint();
        revalidate();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (lijstArtikel.getSelectedValue().voorraad > 49){
            model.removeElementAt(lijstArtikel.getSelectedIndex());
        }
        if (artikelPanel != null){
            center.removeAll();
        }
        try {
            artikelPanel = new ArtikelPanel(c, lijstArtikel.getSelectedValue().stockitemID);
            lijstArtikel.getSelectedValue().voorraad = artikelPanel.getVoorraad();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        center.add(artikelPanel);
        revalidate();
        repaint();
    }

    private JScrollPane generateArtikelScrollPane() throws SQLException {
        model = new DefaultListModel<>();
        artikelen = new Artikel[fetchCountAll()];
        ResultSet r = fetchAll();
        int i = 0;
        while (r.next()){
            artikelen[i] = new Artikel(r.getInt(1), r.getInt(2), r.getInt(3), r.getInt(4));
            i++;
        }
        lijstArtikel = new JList<>(artikelen);
        lijstArtikel.setModel(model);
        for (Artikel artikel : artikelen) {
            model.addElement(artikel);
        }
        lijstArtikel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lijstArtikel.getSelectionModel().addListSelectionListener(this);
        scrollPaneArtikelen = new JScrollPane(lijstArtikel);
        scrollPaneArtikelen.setPreferredSize(new Dimension(230, 250));

        return scrollPaneArtikelen;
    }

    public int fetchCountAll() throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT count(*) FROM stockitemholdings where quantityonhand < ?");
        s.setInt(1, 50);
        ResultSet r = s.executeQuery();
        r.next();

        return r.getInt(1);
    }

    public ResultSet fetchAll() throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT stockitemid, quantityonhand, targetstocklevel, reorderlevel " +
                "FROM stockitemholdings where quantityonhand < ?");
        s.setInt(1, 50);

        return s.executeQuery();
    }
}
