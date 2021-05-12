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

        add(content);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bInloggen){
            try {
                if (c == null || c.isClosed()){
                    c = DBC.getInstance().getConnection();
                }
                if (checkCredentials(tfGebruikersnaam.getText(), pfWachtwoord.getPassword())){
                    Scherm scherm = new Scherm(rsMedewerker);
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

    private boolean checkCredentials(String gebruikersnaam, char[] wachtwoord) throws SQLException {
        PreparedStatement s = c.prepareStatement("select * from medewerker where gebruikersnaam = ? and wachtwoord = ?");
        s.setString(1, gebruikersnaam);
        s.setString(2, String.valueOf(wachtwoord));
        rsMedewerker = s.executeQuery();

        return rsMedewerker.next();
    }
}
