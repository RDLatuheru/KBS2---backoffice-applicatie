package com.company;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerPanel extends JTabbedPane {
    private Connection c;
    private ResultSet r;
    private JLabel id, succesMsg;
    private JTextField naam, achternaam, email, telefoon, plaats, straat, postcode, huisnummer;
    private LayoutManager grid = new GridLayout(0,2);
    private JPanel pPersoonsgegevens, pAdresgegevens, pBestellingen;
    private JCheckBox cbActief;

    CustomerPanel(Connection connection, ResultSet resultSet) throws SQLException {
        c = connection;
        r = resultSet;
        r.next();

        pPersoonsgegevens = new JPanel();
        pPersoonsgegevens.setLayout(new FlowLayout(FlowLayout.LEFT));

        int isActief = r.getInt(10);
        boolean b = false;
        if (isActief == 1){
            b = true;
        }

        pPersoonsgegevens.add(Box.createRigidArea(new Dimension(1370,40)));
        pPersoonsgegevens.add(generatePeroonsgegevens());
        pPersoonsgegevens.add(cbActief = new JCheckBox("Actief", b));
        pPersoonsgegevens.add(Box.createRigidArea(new Dimension(1370,40)));
        pPersoonsgegevens.add(generateAdresgegevens());
        pPersoonsgegevens.add(Box.createRigidArea(new Dimension(1370,40)));
        pPersoonsgegevens.add(succesMsg = new JLabel());

        addTab("Basisgegevens", pPersoonsgegevens);
        //addTab("Afleveradres", generateAdresgegevens());
        //setVisible(true);

        System.out.println(pPersoonsgegevens.getWidth());
    }

    private JPanel generatePeroonsgegevens() throws SQLException {
        JPanel p = new JPanel(new GridLayout(0,2));
        p.setBorder(new TitledBorder("Persoonsgegevens"));
        p.setPreferredSize(new Dimension(750, 200));

        p.add(new JLabel("ID:"));
        p.add(id = new JLabel(r.getString(1)));
        p.add(new JLabel("Naam:"));
        p.add(naam = new JTextField(r.getString(2)));
        p.add(new JLabel("Achternaam:"));
        p.add(achternaam = new JTextField(r.getString(3)));
        p.add(new JLabel("E-mailadres:"));
        p.add(email = new JTextField(r.getString(4)));
        p.add(new JLabel("Telefoonnummer:"));
        p.add(telefoon = new JTextField(r.getString(5)));

        return p;
    }

    private JPanel generateAdresgegevens () throws SQLException {
        JPanel p = new JPanel(new GridLayout(0, 2));
        p.setBorder(new TitledBorder("Standaard afleveradres"));
        p.setPreferredSize(new Dimension(750, 160));

        p.add(new JLabel("Straat:"));
        p.add(straat = new JTextField(r.getString(6)));
        p.add(new JLabel("Huisnummer:"));
        p.add(huisnummer = new JTextField(r.getString(7)));
        p.add(new JLabel("Postcode:"));
        p.add(postcode = new JTextField(r.getString(8)));
        p.add(new JLabel("Plaats:"));
        p.add(plaats = new JTextField(r.getString(9)));

        return p;
    }

    //SETTERS
    public void setMsg() {
        succesMsg.setOpaque(true);
        succesMsg.setBackground(Color.green);
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        succesMsg.setText("<html><body>Wijzigingen succesvol doorgevoerd<br>"+" Laatste wijziging: "+sdf.format(LocalDateTime.now())+"<body><html>");
    }

    //GETTERS
    public int getId() {
        return Integer.parseInt(id.getText());
    }

    public String getNaam() {
        return naam.getText();
    }

    public String getAchternaam(){
        return achternaam.getText();
    }

    public String getEmail() {
        return email.getText();
    }

    public String getHuisnummer() {
        return huisnummer.getText();
    }

    public String getTelefoon() {
        return telefoon.getText();
    }

    public String getPlaats() {
        return plaats.getText();
    }

    public String getStraat() {
        return straat.getText();
    }

    public String getPostcode() {
        return postcode.getText();
    }

    public int getCbActief() {
        if (cbActief.isSelected()){
            return 1;
        }
        return 0;
    }
}
