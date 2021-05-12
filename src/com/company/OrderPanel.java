package com.company;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderPanel extends JPanel {
    Connection c;
    int oID;
    //ResultSet rsO, rsOl, rsR, rsRl; //Order, OrderLijn, Retourorder, RetourorderLijn
    JLabel lblOrderID, lblKlantID, lblOrderDatum, lblOrderLijn, lblStockItemID;
    JTextField tfAantal;
    JPanel pOrderContent, pOrder, pOrderlijn, retourorderLijn;


    public OrderPanel(Connection connection, int orderID) throws SQLException {
        //Content panel
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setPreferredSize(new Dimension(950, 800));

        //Databasegegevens
        c = connection;
        oID = orderID;

        //Content
        add(pOrderContent = new JPanel(new FlowLayout(FlowLayout.CENTER)));
        pOrderContent.setPreferredSize(new Dimension(925, 200 + (fetchBestellingLijnCount(oID) * 50)));
        pOrderContent.setBorder(new TitledBorder("Order"));
        pOrderContent.add(pOrder = generateOrder(getOrder(oID)));
        pOrderContent.add(Box.createRigidArea(new Dimension(1370,30)));
        pOrderContent.add(pOrderlijn = generateBestellingLijn(oID));
        pOrderContent.add(Box.createRigidArea(new Dimension(1370,30)));
        add(Box.createRigidArea(new Dimension(1370,30)));
        if (hasRetourOrder(oID)){
            int h = fetchRetourOrderCount(oID);
            add(retourorderLijn = new JPanel(new FlowLayout(FlowLayout.CENTER)));
            retourorderLijn.setPreferredSize(new Dimension(925, h * 90));
            retourorderLijn.setBorder(new TitledBorder("Geretourneerde artikelen"));
            retourorderLijn.add(generateRetourbestellingLijn());
        }
        revalidate();
        repaint();
    }

    private JPanel generateRetourbestellingLijn() throws SQLException {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(900, fetchRetourOrderCount(oID) * 55));

        ResultSet r = fetchRetourOrder(oID);
        while (r.next()){
            int retourbestellingLijnID = r.getInt(1);
            JPanel a = new JPanel(new GridLayout(0, 3));
            a.setBorder(new EtchedBorder());

            a.add(new JLabel("Artikel: "+r.getInt(9)));
            a.add(new JLabel(r.getString(12)));
            boolean b = false;
            if (r.getByte(4) == 1){
                b = true;
            }
            String s;
            if (b){
                s = "Afgehandeld";
            }else{
                s = "Afhandelen";
            }
            JCheckBox cbAfgehandeld = new JCheckBox(s, b);
            cbAfgehandeld.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    try {
                        updateRetourorder(1, retourbestellingLijnID);
                        cbAfgehandeld.setText("Afgehandeld");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }else if (e.getStateChange() == ItemEvent.DESELECTED){
                    try {
                        updateRetourorder(0, retourbestellingLijnID);
                        cbAfgehandeld.setText("Afhandelen");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
            a.add(cbAfgehandeld);
            p.add(a);
        }
        return p;
    }

    private void updateRetourorder(int bit, int id) throws SQLException {
        PreparedStatement s = c.prepareStatement("update retourbestellinglijn set afgehandeld = ? where retourbestellinglijnid = ?");
        s.setInt(1, bit);
        s.setByte(2, (byte) id);

        s.executeUpdate();
    }

    private ResultSet fetchRetourOrder(int orderID) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * FROM retourbestellinglijn rbl " +
                "join retourbestelling rb on rbl.retourID = rb.retourID " +
                "join bestellinglijn bl on rbl.BestellingLijnID = bl.BestellingLijnID " +
                "join stockitems s on bl.stockitemid = s.stockitemid " +
                "where bl.BestellingID = ?");
        s.setInt(1, orderID);

        return s.executeQuery();
    }

    //Panel generators
    private JPanel generateBestellingLijn(int id) throws SQLException {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(900, fetchBestellingLijnCount(id) * 50));
        p.setBorder(new TitledBorder("Artikelen"));

        ResultSet r = fetchBestellingLijn(id);
        while (r.next()){
            JPanel a = new JPanel(new GridLayout(0, 2));
            a.setBorder(new EtchedBorder());

            a.add(new JLabel("Artikel: "+r.getInt(1)));
            a.add(new JLabel(r.getString(3)));
            a.add(new JLabel("Aantal:"));
            a.add(new JLabel(String.valueOf(r.getInt(2))));

            p.add(a);
        }

        return p;
    }

    private JPanel generateOrder(ResultSet rs) throws SQLException {
        JPanel p = new JPanel(new GridLayout(0,2));
        p.setPreferredSize(new Dimension(900, 125));
        p.setBorder(new TitledBorder("Bestelgegevens"));


        rs.next();
        p.add(new JLabel("Klant ID:"));
        p.add(lblOrderID = new JLabel(String.valueOf(rs.getInt(2))));
        p.add(new JLabel("Order ID:"));
        p.add(lblKlantID = new JLabel(String.valueOf(rs.getInt(1))));
        p.add(new JLabel("Besteldatum: "));
        p.add(lblOrderDatum = new JLabel(String.valueOf(rs.getDate(4))));

        return p;
    }

    //Database functies

    private int fetchRetourOrderCount(int orderID) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * FROM retourbestellinglijn rbl " +
                "join retourbestelling rb on rbl.retourID = rb.retourID " +
                "join bestellinglijn bl on rbl.BestellingLijnID = bl.BestellingLijnID " +
                "join stockitems s on bl.stockitemid = s.stockitemid " +
                "where bl.BestellingID = ?");
        s.setInt(1, orderID);
        ResultSet r = s.executeQuery();
        r.next();

        return r.getInt(1);
    }

    private boolean hasRetourOrder(int orderID) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * FROM retourbestellinglijn rbl " +
                "join retourbestelling rb on rbl.retourID = rb.retourID " +
                "join bestellinglijn bl on rbl.BestellingLijnID = bl.BestellingLijnID " +
                "where bl.BestellingID = ?");
        s.setInt(1, orderID);
        ResultSet r = s.executeQuery();
        if (!r.next()){
            return false;
        }
        return true;
    }

    private ResultSet getOrder(int oID) throws SQLException {
        PreparedStatement s = c.prepareStatement("select * from bestelling where bestellingid = ?");
        s.setInt(1, oID);

        return s.executeQuery();
    }

    private ResultSet fetchBestellingLijn(int orderid) throws SQLException {
        PreparedStatement stmt = c.prepareStatement("select b.stockitemid, b.aantal, s.stockitemname from bestellinglijn b " +
                "join stockitems s on b.stockitemid = s.stockitemid where bestellingid = ?");
        stmt.setInt(1, orderid);

        return stmt.executeQuery();
    }

    private int fetchBestellingLijnCount(int orderID) throws SQLException {
        PreparedStatement stmt = c.prepareStatement("select count(*) from bestellinglijn where bestellingid = ?");
        stmt.setInt(1, orderID);
        ResultSet r = stmt.executeQuery();
        r.next();

        return r.getInt(1);

    }
}
