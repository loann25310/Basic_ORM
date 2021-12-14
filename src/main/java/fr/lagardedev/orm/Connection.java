package fr.lagardedev.orm;

import java.sql.*;
import java.util.Properties;

public class Connection {

    private final java.sql.Connection conn;
    private ConnectionParameters parameters;

    public Connection(ConnectionParameters parameters) throws SQLException {
        this.parameters = parameters;

        String url = "jdbc:postgresql://"+parameters.getHostname()+":"+parameters.getPort()+"/"+parameters.getSchema();

        Properties params = new Properties();
        params.setProperty("user", parameters.getUsername());
        params.setProperty("password", parameters.getPassword());

        conn = DriverManager.getConnection(url, params);
    }

    public java.sql.Connection getConn() {
        return conn;
    }

    public ConnectionParameters getParameters() {
        return parameters;
    }
}
