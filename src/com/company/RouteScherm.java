package com.company;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class RouteScherm extends JPanel implements ActionListener {
    private JScrollPane scrollPane;
    private JList<routeOrders> lijstRouteOrders;
    private routeOrders[] backorders;

    private Connection c = DBC.getInstance().getConnection();
    private BackorderPanel retourorderPanel;
    private JPanel north, west, south, east, center;
    private JButton bereken = new JButton("bereken route");


    //Initialiseer scherm
    RouteScherm() throws SQLException {
        setLayout(new BorderLayout());

        //EAST - return orders
        add(east = new JPanel(), BorderLayout.EAST);
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.setBorder(new EtchedBorder());
        JLabel lblRetourorder = new JLabel("Openstaande backorders");
        lblRetourorder.setFont(new Font("Default", Font.BOLD, 15));
        east.add(lblRetourorder);
        east.add(generateReturns());
        east.add(Box.createRigidArea(new Dimension(0, 500)));

        //CENTER - content
        add(center = new JPanel(new FlowLayout()), BorderLayout.CENTER);
        center.setBorder(new EtchedBorder());
        bereken.addActionListener(this);
        center.add(bereken);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == bereken){
            System.out.println("route REKENEN");
            RouteBerekening algoritme = new RouteBerekening(Arrays.asList(backorders));
            try {
                // geeft elke routeOrder een lat en lon op basis van straat naam
                algoritme.GetLatLons();
                algoritme.findNeighrest(Arrays.asList(backorders));
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        }
        revalidate();
        repaint();
    }

    private JScrollPane generateReturns() throws SQLException {
        DefaultListModel<routeOrders> model = new DefaultListModel<>();
        backorders = new routeOrders[fetchCountAll()];
        fillBackorderArray();
        lijstRouteOrders = new JList<>(backorders);
        lijstRouteOrders.setModel(model);
        for (int i = 0; i < backorders.length; i++) {
            model.addElement(backorders[i]);
        }
        lijstRouteOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lijstRouteOrders.getSelectionModel().addListSelectionListener(e -> {
        });
        scrollPane = new JScrollPane(lijstRouteOrders);
        scrollPane.setPreferredSize(new Dimension(230, 250));

        return scrollPane;
    }

    public ResultSet fetchAll() throws SQLException {
        Statement stmt = c.createStatement();

        return stmt.executeQuery("select b.BestellingID ,k.naam, k.plaats, k.straat, k.huisnummer from bestelling b inner join klant k on b.klantID = k.klantID");
    }

    public int fetchCountAll() throws SQLException {
        //initialiseren
        Statement stmt = c.createStatement();
        ResultSet r = stmt.executeQuery("SELECT count(*) FROM bestelling");
        r.next();

        return r.getInt(1);
    }

    public void fillBackorderArray () throws SQLException {
        ResultSet r = fetchAll();
        //vullen
        int i = 0;
        while (r.next()){
            backorders[i] = new routeOrders(r.getInt(1), r.getString(2), r.getString(3), r.getString(4), r.getInt(5));
            i++;
        }
    }
}
