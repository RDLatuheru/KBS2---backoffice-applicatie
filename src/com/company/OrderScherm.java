package com.company;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class OrderScherm extends JFrame implements ActionListener, ListSelectionListener {
    JScrollPane scrollPane;
    JList<Backorder> lijstBackorder;
    Connection c = DBC.getInstance().getConnection();
    Backorder[] backorders;
    BackorderPanel backorderPanel;
    ResultSet r;
    JButton bRefresh;

    //Initialiseer scherm
    OrderScherm() throws SQLException {
        setSize(600, 600);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setTitle("Orderoverzicht");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        backorders = new Backorder[fetchCountAll()];
        r = fetchAll();
        fillBackorderArray();

        DefaultListModel<Backorder> model = new DefaultListModel<>();
        lijstBackorder = new JList<>(backorders);
        lijstBackorder.setModel(model);
        for (int i = 0; i < backorders.length; i++) {
            model.addElement(backorders[i]);
        }
        lijstBackorder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lijstBackorder.getSelectionModel().addListSelectionListener(e -> {
            try {
                if (backorderPanel != null){
                    remove(backorderPanel);
                }
                backorderPanel = new BackorderPanel(c, fetchOrder(lijstBackorder.getSelectedValue().orderID));
                add(backorderPanel);
                revalidate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        scrollPane = new JScrollPane(lijstBackorder);
        scrollPane.setPreferredSize(new Dimension(230, 500));
        add(scrollPane);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==bRefresh){
            System.out.println("Succes");
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    public ResultSet fetchAll() throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet r = stmt.executeQuery("SELECT * FROM orders o join orders bo on o.OrderID = bo.BackorderOrderID");

        return r;
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
        //vullen
        int i = 0;
        while (r.next()){
            backorders[i] = new Backorder(r.getInt(1), r.getInt(2), r.getString(7), r.getString(8));
            i++;
        }
    }
}
