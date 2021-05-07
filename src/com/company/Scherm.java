package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Scherm extends JFrame implements ActionListener {
    private JButton bKlant, bOrder ;
    private JPanel pMenuWrapper, pMenuButtons, pMenuAccount, pContentWrapper, pContent, pOverviewWrapper, pOverview;

    Scherm() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Beginscherm");

        //WEST
        //Menu - wrapper
        add(pMenuWrapper = new JPanel(new FlowLayout()), BorderLayout.WEST);
        pMenuWrapper.setPreferredSize(new Dimension(250, 0));
        pMenuWrapper.add(pMenuAccount = new JPanel());
        pMenuWrapper.add(pMenuButtons = new JPanel());

        //Menu - account/welkom panel
        pMenuAccount.setPreferredSize(new Dimension(200, 300));
        pMenuAccount.setBackground(Color.WHITE);

        //Menu - naigatie panel
        GridLayout gridLayout = new GridLayout(8,1);
        //gridLayout.setVgap(10);
        pMenuButtons.setLayout(gridLayout);
        pMenuButtons.setBackground(Color.white);
        pMenuButtons.setPreferredSize(new Dimension(200, 650));

        //Menu - navigatie panel -> buttons
        pMenuButtons.add(bKlant = new JButton("Klantbeheer"));
        bKlant.addActionListener(this);
        pMenuButtons.add(bOrder = new JButton("Orderbeheer"));
        bOrder.addActionListener(this);

        //CENTER
        //Content - wrapper
        add(pContentWrapper = new JPanel(new BorderLayout()), BorderLayout.CENTER);
        pContentWrapper.add(pContent = new JPanel(), BorderLayout.CENTER);

        //Content - content panel

        //EAST
        JPanel ePanel = new JPanel();
        ePanel.setPreferredSize(new Dimension(300,0));
        add(ePanel, BorderLayout.EAST);

        pack();
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pContentWrapper.remove(0);
        if (e.getSource()== bKlant){
            try {
                People p = new People();
                pContentWrapper.add(p);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (e.getSource()==bOrder){
            try {
                OrderScherm o = new OrderScherm();
                pContentWrapper.add(o, BorderLayout.CENTER);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        System.out.println(pContentWrapper.getWidth());
        revalidate();
        repaint();
    }
}
