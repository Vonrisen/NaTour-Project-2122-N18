package org.natour.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlDB {

public static Connection getConnection() {
    try {

        String URL = "jdbc:mysql://natour-db.c8r5sn2iekjw.eu-central-1.rds.amazonaws.com/natour";
        Properties info = new Properties( );
        info.put( "user", "admin" );
        info.put( "password", "cinamidea2022" );
        return DriverManager.getConnection(URL, info);
    } catch (SQLException ex) {
        return null;
    }
}
}
