package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BackorderPanel extends JPanel implements ActionListener {
    Connection c;
    ResultSet r;
    JLabel ldOrderID, lCustomerID, lOrderDate;
    JTextField tfSalesID, tfPickedID, tfExpected;
    JButton bDoorvoeren;

    public BackorderPanel(Connection c, ResultSet r) throws SQLException {
        this.c = c;
        this.r = r;
        r.next();

        setLayout(new GridLayout(0,2));
        setPreferredSize(new Dimension(310, 300));
        setBackground(Color.WHITE );

        add(new JLabel("Order ID: "));
        add(ldOrderID = new JLabel(String.valueOf(r.getInt(1))));
        add(new JLabel("Klant ID: "));
        add(lCustomerID = new JLabel(String.valueOf(r.getInt(2))));
        add(new JLabel("Order datum: "));
        add(lOrderDate = new JLabel(String.valueOf(r.getDate(7))));
        add(new JLabel("Verwachte verzenddatum: "));
        add(tfExpected = new JTextField(String.valueOf(r.getDate(8))));
        add(new JLabel("Orderpicker: "));
        add(tfPickedID = new JTextField(String.valueOf(r.getInt(4))));
        add(new JLabel("Verkoper ID: "));
        add(tfSalesID = new JTextField(String.valueOf(r.getInt(3))));
        add(new JLabel());

        add(bDoorvoeren = new JButton("Doorvoeren"));
        bDoorvoeren.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==bDoorvoeren){
            try {
                updateBackorder();
                getParent().revalidate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void updateBackorder() throws SQLException {
        PreparedStatement s = c.prepareStatement("update orders set salespersonpersonid = ?, pickedbypersonid = ?, expecteddeliverydate = ? where orderid = ?");
        s.setInt(1, Integer.parseInt(tfSalesID.getText()));
        s.setInt(2, Integer.parseInt(tfPickedID.getText()));
        s.setDate(3, (Date.valueOf(tfExpected.getText())));
        s.setInt(4, r.getInt(1));
        s.executeUpdate();
    }
}
