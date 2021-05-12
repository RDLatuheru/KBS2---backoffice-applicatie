package com.company;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OrderScherm extends JPanel implements ActionListener {
    private JScrollPane scrollPane;
    private JList<Order> lijstBackorder;
    private Order[] backorders;

    private Connection c = DBC.getInstance().getConnection();
    private BackorderPanel retourorderPanel;
    private JButton bRefresh, bZoek;
    private JPanel north, west, south, east, center;
    private JTextField tfFindOrder;
    private OrderPanel orderPanel;
    private JLabel resultaat;

    //Initialiseer scherm
    OrderScherm() throws SQLException {
        setLayout(new BorderLayout());

        //NORTH - search controls
        add(north = new JPanel(new FlowLayout(FlowLayout.LEFT)), BorderLayout.NORTH);
        north.setBorder(new EtchedBorder());
        north.add(new JLabel("Ordernummer:"));
        north.add(tfFindOrder = new JTextField(5));
        north.add(bZoek = new JButton("Zoeken"));
        north.add(resultaat = new JLabel(""));
        bZoek.addActionListener(this);

        //EAST - return orders
        add(east = new JPanel(), BorderLayout.EAST);
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.setBorder(new EtchedBorder());
        JLabel lblRetourorder = new JLabel("Openstaande backorders");
        lblRetourorder.setFont(new Font("Default", Font.BOLD, 15));
        east.add(lblRetourorder);
        east.add(generateReturns());
        east.add(Box.createRigidArea(new Dimension(0, 500)));

        //SOUTH - TODO
        add(south = new JPanel(), BorderLayout.SOUTH);
        south.setBorder(new EtchedBorder());

        //CENTER - content
        add(center = new JPanel(new FlowLayout()), BorderLayout.CENTER);
        center.setBorder(new EtchedBorder());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==bZoek){
            try {
                int oID = Integer.parseInt(tfFindOrder.getText());
                if (orderPanel != null || retourorderPanel != null){
                    center.removeAll();
                }
                center.add(orderPanel = new OrderPanel(c, oID), BorderLayout.CENTER);
                resultaat.setText("Resultaat:");
            } catch (NumberFormatException | SQLException error) {
                error.printStackTrace();
                resultaat.setText("Geen resultaat!");
            }
        }
        if (e.getSource()==bRefresh){
            System.out.println("Succes");
        }
        revalidate();
        repaint();
    }

    private JScrollPane generateReturns() throws SQLException {
        DefaultListModel<Order> model = new DefaultListModel<>();
        backorders = new Order[fetchCountAll()];
        fillBackorderArray();
        lijstBackorder = new JList<>(backorders);
        lijstBackorder.setModel(model);
        for (int i = 0; i < backorders.length; i++) {
            model.addElement(backorders[i]);
        }
        lijstBackorder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lijstBackorder.getSelectionModel().addListSelectionListener(e -> {
            try {
                if (retourorderPanel != null || orderPanel != null){
                    center.removeAll();
                    revalidate();
                    repaint();
                }
                retourorderPanel = new BackorderPanel(c, fetchOrder(lijstBackorder.getSelectedValue().orderID));
                center.add(retourorderPanel);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        scrollPane = new JScrollPane(lijstBackorder);
        scrollPane.setPreferredSize(new Dimension(230, 250));

        return scrollPane;
    }

    public ResultSet fetchAll() throws SQLException {
        Statement stmt = c.createStatement();

        return stmt.executeQuery("SELECT * FROM orders o join orders bo on o.OrderID = bo.BackorderOrderID");
    }

    public int fetchCountAll() throws SQLException {
        //initialiseren
        Statement stmt = c.createStatement();
        ResultSet r = stmt.executeQuery("SELECT count(*) FROM orders o join orders bo on o.OrderID = bo.BackorderOrderID");
        r.next();

        return r.getInt(1);
    }

    public ResultSet fetchOrder(int orderID) throws SQLException {
        PreparedStatement s = c.prepareStatement("select * from orders where orderid = ?");
        s.setInt(1, orderID);

        return s.executeQuery();
    }

    public void fillBackorderArray () throws SQLException {
        ResultSet r = fetchAll();
        //vullen
        int i = 0;
        while (r.next()){
            backorders[i] = new Order(r.getInt(1), r.getInt(2), r.getString(7), r.getString(8));
            i++;
        }
    }
}
