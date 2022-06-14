package ru.bgpu.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private DataInputStream dIn;
    private Controller controller;

    public RemoteServer(ServerInfoDto dto, Controller controller) throws IOException {
        this.name = dto.getName();
        this.host = dto.getHost();
        this.port = dto.getPort();
        this.controller = controller;
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
        return filesList;
    }

    public void sendFile(String fileName, int fileSize) throws IOException {
        this.clientSocket = new Socket(this.host, this.port);
        bufOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        String command = "SendFile\n";
        bufOut.write(command);
        String name = fileName + "\n";
        bufOut.write(name);
        bufOut.flush();
        try {
            dIn = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int index = fileName.lastIndexOf(".");
        String ext = fileName.substring(index + 1);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(ext, "*." + ext));
        fileChooser.setInitialFileName(fileName);
        File file = fileChooser.showSaveDialog(null);

        new Thread(() -> {
            if (file != null) {
                try {
                    FileOutputStream fOut = new FileOutputStream(file);
                    int check = 0;

                    int size = fileSize;
                    byte[] buffer = new byte[1024];
                    int bytes = 0;

                    long time = System.currentTimeMillis()-500;
                    int periodSize = 0;

                    while (size > 0 && (bytes = dIn.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {

                        if(System.currentTimeMillis() - time > 500){
                            controller.progressView(fileSize, fileSize-size, periodSize, check);
                            periodSize =0;
                            time = System.currentTimeMillis();
                        }
                        fOut.write(buffer, 0, bytes);
                        periodSize+=bytes;
                        size -= bytes;
                    }
                    //
                    controller.progressView(0, 0, 0, 1);
                } catch (IOException e) {
                    //
                    controller.progressView(0, 0, 0, 2);
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public String toString() {
        return name;
    }
}
