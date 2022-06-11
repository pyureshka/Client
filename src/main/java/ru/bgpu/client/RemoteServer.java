package ru.bgpu.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.bgpu.client.dto.FileInfoDto;
import ru.bgpu.client.dto.ServerInfoDto;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class RemoteServer {
    private int port;
    private String host;
    private String name;
    private Socket clientSocket = null;
    private Gson gson;
    private InputStream in;
    private BufferedWriter bufOut;

    public RemoteServer(ServerInfoDto dto) throws IOException {
        this.name = dto.getName();
        this.host = dto.getHost();
        this.port = dto.getPort();

    }

    public List<FileInfoDto> fileNames() throws IOException {
        this.clientSocket = new Socket(this.host, this.port);
        bufOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        String command = "FilesList\n";
        bufOut.write(command);
        bufOut.flush();

        in = clientSocket.getInputStream();
        int fileSize = in.read();
        byte[] list = new byte[fileSize];
        in.read(list,0,fileSize);
        String filesListJson = new String(list, "utf-8").trim();
        gson = new Gson();
        List<FileInfoDto> filesList = gson.fromJson(filesListJson, new TypeToken<List<FileInfoDto>>(){}.getType());
        in.close();
        bufOut.close();
        return filesList;
    }

    public void sendFile(String fileName) throws IOException {
        this.clientSocket = new Socket(this.host, this.port);
        bufOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        String command = "SendFile\n";
        bufOut.write(command);
        String name = fileName+"\n";
        bufOut.write(name);
        bufOut.flush();
    }

    @Override
    public String toString() {
        return name;
    }
}
