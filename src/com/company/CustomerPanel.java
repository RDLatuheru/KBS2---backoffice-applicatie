package com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerPanel extends JTabbedPane {
    Connection c;
    ResultSet r;
    JLabel customerID;
    JTextField customerName, phone, fax; //persoonlijke gegevens
    JTextField plaats, straaths, postcode, pobox; //adresgegevens
    LayoutManager grid = new GridLayout(0,2);

    CustomerPanel(Connection connection, ResultSet resultSet) throws SQLException {
        c = connection;
        r = resultSet;
        r.next();

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(350, 200));

        JPanel p = new JPanel(grid){
            public Dimension getPrefferedSize(){
                Dimension d = super.getPreferredSize();
                d.width += 100;
                return d;
            }
        };

        addTab("Persoonsgegevens", pPeroonsgegevens(p));
        addTab("Afleveradres", pAdresgegevens(new JPanel(grid)));

        setVisible(true);
    }

    private JPanel pPeroonsgegevens(JPanel p) throws SQLException {
        p.add(new JLabel("ID: "));
        p.add(customerID = new JLabel(r.getString(1)));
        p.add(new JLabel("Naam: "));
        p.add(customerName = new JTextField(5));
        p.add(new JLabel("Telefoon: "));
        p.add(phone = new JTextField(5));
        p.add(new JLabel("Fax: "));
        p.add(fax = new JTextField(5));
        customerName.setText(r.getString(2));
        phone.setText(r.getString(17));
        fax.setText(r.getString(18));

        return p;
    }

    private JPanel pAdresgegevens (JPanel p) throws SQLException {
        p.add(new JLabel("Plaats: "));
        p.add(plaats = new JTextField(5));
        p.add(new JLabel("Straat & huisnummer: "));
        p.add(straaths = new JTextField(5));
        p.add(new JLabel("Postcode: "));
        p.add(postcode = new JTextField(5));
        p.add(new JLabel("PO-box: "));
        p.add(pobox = new JTextField(5));

        plaats.setText(r.getString(27));
        straaths.setText(r.getString(23));
        postcode.setText(r.getString(24));
        pobox.setText(r.getString(26));

        return p;
    }

    //GETTERS
    public int getCustomerID() {
        return Integer.parseInt(customerID.getText());
    }

    public String getCustomerName() {
        return customerName.getText();
    }

    public String getPhone() {
        return phone.getText();
    }

    public String getFax() {
        return fax.getText();
    }

    public String getPlaats() {
        return plaats.getText();
    }

    public String getStraaths() {
        return straaths.getText();
    }

    public String getPostcode() {
        return postcode.getText();
    }

    public String getPobox() {
        return pobox.getText();
    }
}
