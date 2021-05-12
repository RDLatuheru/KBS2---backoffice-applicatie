package com.company;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerPanel extends JTabbedPane {
    private Connection c;
    private ResultSet pg, bg, bl; //Persoonsgegevens, Bestellingsgegevens, BestellingLijn
    private JLabel id, succesMsg;
    private JTextField naam, achternaam, email, telefoon, plaats, straat, postcode, huisnummer;
    private JPanel pPersoonsgegevens, pBestellingen, pBestelling;
    private JCheckBox cbActief;
    private int klantID;
    private JTable t;

    CustomerPanel(Connection connection, int kID) throws SQLException {
        c = connection;
        klantID = kID;
        pg = fetchKlant(klantID);
        pg.next();
        bg = fetchKlantBestellingen(klantID);

        int isActief = pg.getInt(10);
        boolean b = false;
        if (isActief == 1){
            b = true;
        }

        //Panel persoonsgegevens
        pPersoonsgegevens = new JPanel();
        pPersoonsgegevens.setLayout(new FlowLayout(FlowLayout.LEFT));
        pPersoonsgegevens.add(Box.createRigidArea(new Dimension(1370,40)));
        pPersoonsgegevens.add(generatePeroonsgegevens());
        pPersoonsgegevens.add(cbActief = new JCheckBox("Actief", b));
        pPersoonsgegevens.add(Box.createRigidArea(new Dimension(1370,40)));
        pPersoonsgegevens.add(generateAdresgegevens());
        pPersoonsgegevens.add(Box.createRigidArea(new Dimension(1370,40)));

        //Panel bestellingen
        pBestellingen = new JPanel();
        pBestellingen.setLayout(new FlowLayout(FlowLayout.CENTER));
        pBestellingen.add(Box.createRigidArea(new Dimension(1370,40)));
        pBestellingen.add(generateBestellingen());

        //Tabs
        addTab("Basisgegevens", pPersoonsgegevens);
        addTab("Bestellingen", pBestellingen);

        ListSelectionModel m = t.getSelectionModel();
        m.addListSelectionListener(e -> {
            if (pBestelling != null){
                pBestellingen.remove(pBestelling);
            }
            int i = (int) t.getModel().getValueAt(t.getSelectedRow(), 0);
            System.out.println(i);
            try {
                pBestelling = generateBestellingLijn(i);
                pBestellingen.add(pBestelling);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            repaint();
            revalidate();
        });
    }

    private JPanel generateBestellingLijn(int id) throws SQLException {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(750, 300));

        ResultSet r = fetchOrder(id);
        while (r.next()){
            JPanel a = new JPanel(new GridLayout(0, 2));
            a.setBorder(new EtchedBorder());

            a.add(new JLabel("Artikel:"));
            a.add(new JLabel(r.getInt(1) +": "+r.getString(3)));
            a.add(new JLabel("Aantal:"));
            a.add(new JLabel(String.valueOf(r.getInt(2))));

            p.add(a);
        }

        return p;
    }

    private JScrollPane generateBestellingen() throws SQLException {
        String[] columns = {"BestellingID", "Bestellingdatum"};
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(columns);

        while (bg.next()){
            m.addRow(new Object[]{bg.getInt(1), bg.getString(3)});
        }

        t = new JTable(m);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane p = new JScrollPane(t);
        p.setPreferredSize(new Dimension(750, 200));

        return p;
    }

    private JPanel generatePeroonsgegevens() throws SQLException {
        JPanel p = new JPanel(new GridLayout(0,2));
        p.setBorder(new TitledBorder("Persoonsgegevens"));
        p.setPreferredSize(new Dimension(750, 200));

        p.add(new JLabel("ID:"));
        p.add(id = new JLabel(pg.getString(1)));
        p.add(new JLabel("Naam:"));
        p.add(naam = new JTextField(pg.getString(2)));
        p.add(new JLabel("Achternaam:"));
        p.add(achternaam = new JTextField(pg.getString(3)));
        p.add(new JLabel("E-mailadres:"));
        p.add(email = new JTextField(pg.getString(4)));
        p.add(new JLabel("Telefoonnummer:"));
        p.add(telefoon = new JTextField(pg.getString(5)));

        return p;
    }

    private JPanel generateAdresgegevens () throws SQLException {
        JPanel p = new JPanel(new GridLayout(0, 2));
        p.setBorder(new TitledBorder("Standaard afleveradres"));
        p.setPreferredSize(new Dimension(750, 160));

        p.add(new JLabel("Straat:"));
        p.add(straat = new JTextField(pg.getString(6)));
        p.add(new JLabel("Huisnummer:"));
        p.add(huisnummer = new JTextField(pg.getString(7)));
        p.add(new JLabel("Postcode:"));
        p.add(postcode = new JTextField(pg.getString(8)));
        p.add(new JLabel("Plaats:"));
        p.add(plaats = new JTextField(pg.getString(9)));

        return p;
    }

    //RESULTSETS
    private ResultSet fetchOrder(int orderid) throws SQLException {
        PreparedStatement stmt = c.prepareStatement("select b.stockitemid, b.aantal, s.stockitemname from bestellinglijn b " +
                "join stockitems s on b.stockitemid = s.stockitemid where bestellingid = ?");
        stmt.setInt(1, orderid);

        return stmt.executeQuery();
    }

    private ResultSet fetchKlant(int klantid) throws SQLException {
        PreparedStatement stmt = c.prepareStatement("select * from klant where klantid = ?");
        stmt.setInt(1, klantid);

        return stmt.executeQuery();
    }

    private ResultSet fetchKlantBestellingen(int klantID) throws SQLException {
        PreparedStatement stmt = c.prepareStatement("select * from bestelling where klantid = ?");
        stmt.setInt(1, klantID);

        return stmt.executeQuery();
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
