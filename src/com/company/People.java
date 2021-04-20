package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class People extends JFrame implements ActionListener{
    private Connection c = DBC.getInstance().getConnection();
    private JTextField tfCustomer;
    private JButton bZoek, bDoorvoeren, bAnnuleren;
    private JPanel north, west, south, east;
    private CustomerPanel p;

    People() throws SQLException {
        setSize(400, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Klantbeheer");

        add(north = new JPanel(new FlowLayout(FlowLayout.LEFT)), BorderLayout.NORTH);
        north.add(new JLabel("Klantnummer:"));
        north.add(tfCustomer = new JTextField(5));
        north.add(bZoek = new JButton("Zoeken"));
        bZoek.addActionListener(this);

        add(south = new JPanel(new FlowLayout(FlowLayout.CENTER)), BorderLayout.SOUTH);
        south.add(bDoorvoeren = new JButton("Doorvoeren"));
        south.add(bAnnuleren = new JButton("Annuleren"));
        bDoorvoeren.addActionListener(this);
        bAnnuleren.addActionListener(this);

        //opvulling
        add(west = new JPanel(), BorderLayout.WEST);
        add(east = new JPanel(), BorderLayout.EAST);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==bZoek){
            int cID = Integer.parseInt(tfCustomer.getText());
            try {
                if (p != null){
                    remove(p);
                }
                add(p = new CustomerPanel(c, getPersoonsgegevens(cID)), BorderLayout.CENTER );
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (e.getSource()==bDoorvoeren){
            try {
                voerDoor();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        revalidate();
        repaint();
    }

    private ResultSet getPersoonsgegevens(int customerID) throws SQLException {
        PreparedStatement stmt = c.prepareStatement("select * from customers where customerid = ?");
        stmt.setInt(1, customerID);

        return stmt.executeQuery();
    }

    private void voerDoor() throws SQLException {
        PreparedStatement stmt = c.prepareStatement("update customers set customername = ?, phonenumber = ?, faxnumber = ?, PostalAddressLine2 = ?," +
                "DeliveryAddressLine2 = ?, DeliveryPostalCode = ?, PostalAddressLine1 = ? where customerid = ?");
        stmt.setString(1, p.getCustomerName());
        stmt.setString(2, p.getPhone());
        stmt.setString(3, p.getFax());
        stmt.setString(4, p.getPlaats());
        stmt.setString(5, p.getStraaths());
        stmt.setString(6, p.getPostcode());
        stmt.setString(7, p.getPobox());
        stmt.setInt(8, p.getCustomerID());

        stmt.executeUpdate();
        stmt.close();
    }

    //Oud spul ter ondersteuning
    /*public int[] getCustomerArray() throws SQLException {
        int[] idArray = new int[countCustomers()];

        PreparedStatement s = c.prepareStatement("Select customerid from customers where customerid > ?");
        s.setInt(1, 601);
        ResultSet r = s.executeQuery();

        for (int i = 0; i < countCustomers(); i++) {
            r.next();
            idArray[i] = Integer.parseInt( r.getString("customerid"));
        }
        return idArray;
    }

    public int countCustomers() throws SQLException {
        PreparedStatement stmt = c.prepareStatement("select count(customerid) from customers where customerid > ?");
        stmt.setInt(1, 601);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        return rs.getInt(1);
    }*/
}
