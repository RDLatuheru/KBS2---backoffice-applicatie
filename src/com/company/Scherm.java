package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Scherm extends JFrame implements ActionListener {
    private JButton bKlant, bOrder;

    Scherm() {
        setSize(500, 100);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Beginscherm");

        add(bKlant = new JButton("Klantgegevens wijzigen"));
        bKlant.addActionListener(this);
        add(bOrder = new JButton("Open orderoverzicht"));
        bOrder.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()== bKlant){
            try {
                People p = new People();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (e.getSource()==bOrder){
            try {
                OrderScherm o = new OrderScherm();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
