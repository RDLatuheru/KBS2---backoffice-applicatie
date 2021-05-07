package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class People extends JPanel implements ActionListener{
    private Connection c = DBC.getInstance().getConnection();
    private JTextField tfCustomer;
    private JButton bZoek, bDoorvoeren, bAnnuleren;
    private JPanel north, west, south, east, center;
    private CustomerPanel p;
    private JLabel resultaat, succesMsg;
    private boolean contentOpen;

    People() throws SQLException {
        setPreferredSize(new Dimension(400, 400));
        setLayout(new BorderLayout());

        add(north = new JPanel(new FlowLayout(FlowLayout.LEFT)), BorderLayout.NORTH);
        north.add(new JLabel("Klantnummer:"));
        north.add(tfCustomer = new JTextField(5));
        north.add(bZoek = new JButton("Zoeken"));
        north.add(resultaat = new JLabel());
        bZoek.addActionListener(this);

        add(south = new JPanel(new FlowLayout(FlowLayout.RIGHT)), BorderLayout.SOUTH);
        south.setPreferredSize(new Dimension(0, 100));
        south.add(succesMsg = new JLabel());
        south.add(bDoorvoeren = new JButton("Doorvoeren"));
        south.add(bAnnuleren = new JButton("Annuleren"));
        bDoorvoeren.addActionListener(this);
        bAnnuleren.addActionListener(this);


        //opvulling
        add(west = new JPanel(), BorderLayout.WEST);
        add(east = new JPanel(), BorderLayout.EAST);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==bZoek){
            try {
                int kID = Integer.parseInt(tfCustomer.getText());
                if (p != null){
                    remove(p);
                }
                add(p = new CustomerPanel(c, kID), BorderLayout.CENTER );
                contentOpen = true;
                resultaat.setText("Resultaat:");
            } catch (SQLException | NumberFormatException error) {
                resultaat.setText("Geen resultaat! ");
                error.printStackTrace();
            }
        }
        if (e.getSource()==bDoorvoeren && contentOpen){
            try {
                voerDoor();
                setMsg();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (e.getSource()==bAnnuleren && contentOpen){
            contentOpen = false;
            remove(p);
        }
        revalidate();
        repaint();
    }

    private void voerDoor() throws SQLException {
        PreparedStatement stmt = c.prepareStatement("update klant set naam = ?, achternaam = ?, email = ?, telefoon = ?," +
                "straat = ?, huisnummer = ?, postcode = ?, plaats = ?, actief = ? where klantid  = ?");
        stmt.setString(1, p.getNaam());
        stmt.setString(2, p.getAchternaam());
        stmt.setString(3, p.getEmail());
        stmt.setString(4, p.getTelefoon());
        stmt.setString(5, p.getStraat());
        stmt.setString(6, p.getHuisnummer());
        stmt.setString(7, p.getPostcode());
        stmt.setString(8, p.getPlaats());
        stmt.setByte(9, (byte) p.getCbActief());
        stmt.setInt(10, p.getId());

        stmt.executeUpdate();
        stmt.close();
    }

    public void setMsg() {
        succesMsg.setOpaque(true);
        succesMsg.setBackground(Color.green);
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        succesMsg.setText("<html><body>Wijzigingen succesvol doorgevoerd<br>"+" Laatste wijziging: "+sdf.format(LocalDateTime.now())+"<body><html>");
    }
}
