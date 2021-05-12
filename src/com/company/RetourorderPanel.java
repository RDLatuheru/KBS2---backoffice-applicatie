package com.company;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RetourorderPanel extends JPanel {
    private Connection c;
    int retourbestellingLijnID;

    public RetourorderPanel(int id, Connection connection, int height) throws SQLException {
        c = connection;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(900, height * 55));

        ResultSet r = fetchRetourOrder(id);
        while (r.next()){
            retourbestellingLijnID = r.getInt(1);
            JPanel a = new JPanel(new GridLayout(0, 3));
            a.setBorder(new EtchedBorder());

            a.add(new JLabel("Artikel: "+r.getInt(9)));
            a.add(new JLabel(r.getString(12)));
            boolean b = false;
            if (r.getByte(4) == 1){
                b = true;
            }
            JCheckBox cbAfgehandeld = new JCheckBox("Afgehandeld", b);
            cbAfgehandeld.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED){
                        try {
                            updateRetourorder(1, retourbestellingLijnID);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }else if (e.getStateChange() == ItemEvent.DESELECTED){
                        try {
                            updateRetourorder(0, retourbestellingLijnID);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
            });
            a.add(cbAfgehandeld);
            add(a);
        }
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
}
