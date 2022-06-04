package ru.bgpu.client;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class Client {
    public static void main(String[] args) throws UnknownHostException, SocketException {

        try {
            DatagramSocket mcSocket = new DatagramSocket();
            InetAddress ipGroup = InetAddress.getByName("228.5.6.7");

            String msg = "client connect";
            DatagramPacket testConnect = new DatagramPacket(
                    msg.getBytes(),
                    msg.length(),
                    ipGroup,
                    2121);
            mcSocket.send(testConnect);

            DatagramPacket packFromServer = new DatagramPacket(
                    new byte[1024],
                    1024,
                    ipGroup,
                    2121
            );
            mcSocket.receive(packFromServer);
            System.out.println("Waiting server massage");
            String rec = new String(
                    packFromServer.getData(),
                    packFromServer.getOffset(),
                    packFromServer.getLength()
            );
            System.out.println("[Multicast Receiver] Received: " + rec);

            Gson gson = new Gson();
            ServerInfo serverInfo = gson.fromJson(rec, ServerInfo.class);
            InetAddress serverIp = InetAddress.getByName(serverInfo.getHost());

            Socket clientSocket = new Socket(serverIp, serverInfo.getPort());
            InputStream in = clientSocket.getInputStream();
            int fileSize = in.read();
            byte[] list = new byte[fileSize];
            in.read(list,0,fileSize);
            String filesList = new String(list, "utf-8").trim();
            System.out.println(filesList);

            FileInfo filesListTest = gson.fromJson(filesList, FileInfo.class);
            System.out.println(filesListTest.getNames()[1]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
