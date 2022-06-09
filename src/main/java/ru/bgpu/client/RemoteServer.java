package ru.bgpu.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.bgpu.client.dto.ServerInfoDto;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class RemoteServer{

    private int port;
    private String host;
    private String name;

    private Socket clientSocket = null;

    String[] fileNames;
    public RemoteServer() throws IOException {}

    public RemoteServer (ServerInfoDto dto) throws IOException {
        this.name = dto.getName();
        this.host = dto.getHost();
        this.port = dto.getPort();
        this.clientSocket = new Socket(this.host, this.port);
    }

    public List<String> fileNames () throws IOException {
        InputStream in = clientSocket.getInputStream();
        int fileSize = in.read();
        byte[] list = new byte[fileSize];
        in.read(list,0,fileSize);
        String filesListJson = new String(list, "utf-8").trim();

        Gson gson = new Gson();
        List<String> filesList= gson.fromJson(filesListJson,  new TypeToken<List<String>>(){}.getType());
        return filesList;
    }

    public void sendFileName (String fileName) {

    }

    @Override
    public String toString() {
        return name;
    }
}
