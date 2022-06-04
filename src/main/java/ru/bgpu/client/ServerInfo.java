package ru.bgpu.client;

public class ServerInfo {
    public int port;
    public String host;
    public String name;

    public ServerInfo(int port, String host, String name){
        this.name = name;
        this.host = host;
        this.port = port;
    };
    public ServerInfo(int port, String host){
        this.name = null;
        this.host = host;
        this.port = port;
    };

    public ServerInfo(){};

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }
}
