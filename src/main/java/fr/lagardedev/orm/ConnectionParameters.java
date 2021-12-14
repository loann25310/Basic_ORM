package fr.lagardedev.orm;

public class ConnectionParameters {

    private String hostname;
    private int port;
    private String username;
    private String password;
    private String schema;

    public ConnectionParameters(String hostname, int port, String username, String password, String schema){
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.schema = schema;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    protected String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    protected String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
