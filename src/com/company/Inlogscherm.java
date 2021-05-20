package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Inlogscherm extends JFrame implements ActionListener {
    private JTextField tfGebruikersnaam;
    private JPasswordField pfWachtwoord;
    private JButton bInloggen;
    private JLabel lblMsg;
    private Connection c;
    private ResultSet rsMedewerker;
    private JComboBox cbInlogAls;


    public Inlogscherm() throws SQLException {
        setLayout(new FlowLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Inloggen");
        setSize(450, 150);

        c = DBC.getInstance().getConnection();
        System.out.println(c);

        JPanel content = new JPanel(new GridLayout(0,2));
        content.setPreferredSize(new Dimension(420,100));
        content.add(new JLabel("Inlognaam: "));
        content.add(tfGebruikersnaam = new JTextField());
        content.add(new JLabel("Wachtwoord"));
        content.add(pfWachtwoord = new JPasswordField());
        content.add(lblMsg = new JLabel(""));
        lblMsg.setForeground(Color.red);
        lblMsg.setFont(new Font("default", Font.PLAIN, 10));
        content.add(bInloggen = new JButton("Inloggen"));
        bInloggen.addActionListener(this);
        content.add(new JPanel());

        String[] accountTypes = {"Magazijnmedewerker", "Bezorger"};
        cbInlogAls = new JComboBox(accountTypes);
        cbInlogAls.setSelectedIndex(0);
        cbInlogAls.addActionListener(this);
        content.add(cbInlogAls);

        add(content);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String accountype = (String) cbInlogAls.getSelectedItem();
        System.out.println(accountype);
        if (e.getSource() == bInloggen){
            try {
                if (c == null || c.isClosed()){
                    c = DBC.getInstance().getConnection();
                }
                if (checkCredentials(tfGebruikersnaam.getText(), pfWachtwoord.getPassword(), accountype)){
                    if (accountype.equals("Magazijnmedewerker")){
                        Scherm scherm = new Scherm(rsMedewerker);
                    }else{
                        BezorgerMainScherm scherm = new BezorgerMainScherm();
                    }
                    setVisible(false);
                }else{
                    lblMsg.setText("Onjuiste gebruikersnaam en/of wachtwoord");
                }
            } catch (SQLException | NullPointerException throwables) {
                lblMsg.setText("Geen verbinding!");
                throwables.printStackTrace();
            }
        }
    }

    private boolean checkCredentials(String gebruikersnaam, char[] wachtwoord, String functie) throws SQLException {
        PreparedStatement s = c.prepareStatement("select * from medewerker where gebruikersnaam = ? and wachtwoord = ? " +
                "and functie = ?");
        s.setString(1, gebruikersnaam);
        s.setString(2, String.valueOf(wachtwoord));
        s.setString(3, functie);
        rsMedewerker = s.executeQuery();

        return rsMedewerker.next();
    }
}
