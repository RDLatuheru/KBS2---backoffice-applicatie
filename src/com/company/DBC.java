package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBC {
    private static DBC instance;
    private Connection con;

    private DBC(){
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost/nerdygadgets", "root", "");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return con;
    }

    public static DBC getInstance() throws SQLException {
        if (instance == null){
            instance = new DBC();
        }else if (instance.getConnection().isClosed()){
            instance = new DBC();
        }
        return instance;
    }
}
