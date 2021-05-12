package com.company;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtikelDialoog extends JDialog implements ListSelectionListener, ActionListener {
    private int aID;
    private String z;

    private DefaultListModel<Artikel> model;
    private Artikel[] artikelen;
    private JList<Artikel> lijstArtikel;
    private JScrollPane scrollPaneArtikelen;
    private Connection c;
    private JButton bOpen;

    public ArtikelDialoog(Connection connection, String zoekTerm) throws SQLException {
        setSize(500, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Zoekresultaten");
        setModal(true);

        c = connection;
        z = zoekTerm;

        model = new DefaultListModel<>();
        artikelen = new Artikel[fetchCountAll()];
        ResultSet r = fetchAll();
        int i = 0;
        while (r.next()){
            artikelen[i] = new Artikel(r.getInt(1), r.getString(2), r.getInt(3));
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

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(bOpen = new JButton("Open artikel"));
        bOpen.addActionListener(this);

        add(south, BorderLayout.SOUTH);
        add(scrollPaneArtikelen, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        aID = lijstArtikel.getSelectedValue().stockitemID;
        System.out.println(aID);
    }

    public ResultSet fetchAll() throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT si.stockitemid, si.stockitemname, sih.quantityonhand FROM stockitems si " +
                "join stockitemholdings sih on si.stockitemid = sih.stockitemid " +
                "where stockitemname like ?");
        s.setString(1, "%"+z+"%");

        return s.executeQuery();
    }

    public int fetchCountAll() throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT count(*) FROM stockitems where stockitemname like ?");
        s.setString(1, "%"+z+"%");
        ResultSet r = s.executeQuery();
        r.next();

        return r.getInt(1);
    }

    public int getaID() {
        return aID;
    }
}
