package com.company;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Scherm extends JFrame implements ActionListener {
    private MenuButton bKlant, bOrder, bArtikel,bRoute, isSelected;
    private JPanel pMenuWrapper, pMenuButtons, pMenuAccount, pContentWrapper, pContent, pOverviewWrapper, pOverview;

    Scherm(ResultSet resultSet) throws SQLException {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Beginscherm");

        isSelected = null;

        //WEST
        //Menu - wrapper
        add(pMenuWrapper = new JPanel(), BorderLayout.WEST);
        pMenuWrapper.setLayout(new BoxLayout(pMenuWrapper, BoxLayout.Y_AXIS));
        pMenuWrapper.setBorder(new EtchedBorder());
        pMenuWrapper.setPreferredSize(new Dimension(250, 0));
        pMenuWrapper.add(pMenuAccount = new JPanel());
        pMenuWrapper.add(pMenuButtons = new JPanel());

        //Menu - account/welkom panel
        pMenuAccount.setPreferredSize(new Dimension(0, 75));
        pMenuAccount.setLayout(new GridLayout(6, 0));
        JLabel lblWelkom = new JLabel("Welkom, ");
        lblWelkom.setFont(new Font("default", Font.PLAIN, 25));
        pMenuAccount.add(lblWelkom);
        JLabel lblMedewerker = new JLabel(resultSet.getString(2)+" "+resultSet.getString(3));
        lblMedewerker.setFont(new Font("default", Font.BOLD, 15));
        pMenuAccount.add(lblMedewerker);
        pMenuAccount.add(new JLabel());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
        java.util.Date d = new java.util.Date();
        pMenuAccount.add(new JLabel(LocalDate.now().getDayOfWeek().name()+", "+sdf.format(d)));

        //Menu - naigatie panel
        pMenuButtons.setLayout(new GridLayout(8,1));
        pMenuButtons.setPreferredSize(new Dimension(0, 650));

        //Menu - navigatie panel -> buttons
        pMenuButtons.add(bKlant = new MenuButton("Klantbeheer"));
        bKlant.addActionListener(this);
        pMenuButtons.add(bOrder = new MenuButton("Orderbeheer"));
        bOrder.addActionListener(this);
        pMenuButtons.add(bArtikel = new MenuButton("Vooraadbeheer"));
        bArtikel.addActionListener(this);
        pMenuButtons.add(bRoute = new MenuButton("Route Bepaling"));
        bRoute.addActionListener(this);

        //CENTER
        //Content - wrapper
        add(pContentWrapper = new JPanel(new BorderLayout()), BorderLayout.CENTER);
        //pContentWrapper.add(pContent = new JPanel(), BorderLayout.CENTER);

        //Content - content panel

        //EAST
        JPanel ePanel = new JPanel();
        ePanel.setPreferredSize(new Dimension(300,0));
        add(ePanel, BorderLayout.EAST);
        ePanel.setBorder(new EtchedBorder());

        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isSelected == null){
            isSelected = (MenuButton) e.getSource();
            System.out.println(1);
        }else if (e.getSource() == isSelected){
            pContentWrapper.removeAll();
            isSelected.setSelected(false);
            isSelected = null;
            System.out.println(2);
        }else if (e.getSource() != isSelected){
            System.out.println(3);
            isSelected.setSelected(false);
            pContentWrapper.removeAll();
            isSelected = (MenuButton) e.getSource();
        }
        if (isSelected == bKlant){
            try {
                KlantScherm p = new KlantScherm();
                pContentWrapper.add(p);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (isSelected==bOrder){
            try {
                OrderScherm o = new OrderScherm();
                pContentWrapper.add(o);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (isSelected==bArtikel){
            try {
                ArtikelScherm a = new ArtikelScherm();
                pContentWrapper.add(a);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (isSelected==bRoute){
            System.out.println("route scherm");
            RouteScherm r = null;
            try {
                r = new RouteScherm();
                pContentWrapper.add(r);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        revalidate();
        repaint();
    }
}
