package org.beru.coltecnuspazserver.ui.model;

public class SessionDatas {
    private String name;
    private String pass;
    private String host;
    private int port;

    public SessionDatas(String name, String pass, String host, int port) {
        this.name = name;
        this.pass = pass;
        this.host = host;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
