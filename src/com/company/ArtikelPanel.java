package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArtikelPanel extends JPanel implements ActionListener {
    private Connection c;
    private int aID;
    private JPanel south, center;
    private JLabel lblID, lblNaam, lblPrijs, lblTarget, lblVoorraad, lblDoorvoeren;
    private JButton bDoorvoeren;
    private JTextField tfVoorraad;
    private ResultSet r;
    private int voorraad;

    public ArtikelPanel(Connection connection, int artikelnummer) throws SQLException {
        c = connection;
        aID = artikelnummer;
        setLayout(new BorderLayout());
        r = fetchOrder();
        r.next();
        voorraad = r.getInt(4);

        JPanel left = new JPanel(new GridLayout(0,2));
        add(left);
        add(new JPanel());

        left.add(new JLabel("ID:"));
        left.add(lblID = new JLabel(String.valueOf(r.getInt(1))));
        left.add(new JLabel("Naam:"));
        left.add(lblNaam = new JLabel(r.getString(2)));
        left.add(new JLabel("Prijs:"));
        left.add(lblPrijs = new JLabel(String.valueOf(r.getFloat(3))));
        left.add(new JLabel("Voorraad:"));
        left.add(lblVoorraad = new JLabel(voorraad + " / " + (r.getInt(5)+50)));

        add(center = new JPanel(new GridLayout(0,2)), BorderLayout.CENTER);
        center.setPreferredSize(new Dimension(900, 300));
        center.setBorder(new TitledBorder("Artikel"));
        center.add(left);
        center.add(new JPanel());

        add(south = new JPanel(new FlowLayout()), BorderLayout.SOUTH);
        south.add(lblDoorvoeren = new JLabel());
        south.add(new JLabel("Aantal:"));
        south.add(tfVoorraad = new JTextField(2));
        south.add(bDoorvoeren = new JButton("Voorraad toevoegen"));

        bDoorvoeren.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==bDoorvoeren && !tfVoorraad.getText().equals("")){
            try {
                toevoegenVoorraad();
                setMsg();
                voorraad = voorraad+Integer.parseInt(tfVoorraad.getText());
                lblVoorraad.setText(voorraad + " / " + (r.getInt(5)+50));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void toevoegenVoorraad() throws SQLException {
        try{
            int i = Integer.parseInt(tfVoorraad.getText())+voorraad;
            System.out.println(i);
            PreparedStatement s = c.prepareStatement("update stockitemholdings set quantityonhand = ? where stockitemid = ?");
            s.setInt(1, i);
            s.setInt(2, aID);
            s.executeUpdate();
        }catch (NumberFormatException e){
            e.printStackTrace();
            System.out.println("NEIN!");
        }
    }

    public ResultSet fetchOrder() throws SQLException {
        PreparedStatement s = c.prepareStatement("select si.stockitemid, si.stockitemname, si.recommendedretailprice, sih.quantityonhand, sih.targetstocklevel " +
                "from stockitems si " +
                "join stockitemholdings sih on si.stockitemid = sih.stockitemid " +
                "where si.stockitemid = ?");
        s.setInt(1, aID);

        return s.executeQuery();
    }

    public void setMsg() {
        lblDoorvoeren.setOpaque(true);
        lblDoorvoeren.setBackground(Color.green);
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        lblDoorvoeren.setText("<html><body>Wijzigingen succesvol doorgevoerd<br>"+" Laatste wijziging: "+sdf.format(LocalDateTime.now())+"<body><html>");
    }

    public int getVoorraad() throws SQLException {
        return voorraad;
    }
}
