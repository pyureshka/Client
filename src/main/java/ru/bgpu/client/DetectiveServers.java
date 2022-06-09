package ru.bgpu.client;

import com.google.gson.Gson;
import ru.bgpu.client.dto.ServerInfoDto;

import java.io.IOException;
import java.net.*;

public class DetectiveServers{

    private Controller controller;
    private Boolean run = true;
    private InetAddress ipGroup;
    private DatagramSocket mcSocket;
    public DetectiveServers(Controller controller) throws SocketException, UnknownHostException {
        this.controller = controller;
        mcSocket = new DatagramSocket();
        ipGroup = InetAddress.getByName("228.5.6.7");
        new Thread(this::serversInfo).start();
    }

    public void sendMsgToServers() throws IOException {
        InetAddress ipGroup = InetAddress.getByName("228.5.6.7");
        String msg = "client connect";
        DatagramPacket testConnect = new DatagramPacket(
                msg.getBytes(),
                msg.length(),
                ipGroup,
                2121
        );
        mcSocket.send(testConnect);
    }

    public void serversInfo()  {

        DatagramPacket packFromServer = new DatagramPacket(
                new byte[1024],
                1024,
                ipGroup,
                2121
        );
        while (run) {
            try {
                mcSocket.receive(packFromServer);

                String rec = new String(
                        packFromServer.getData(),
                        packFromServer.getOffset(),
                        packFromServer.getLength()
                );
                Gson gson = new Gson();
                ServerInfoDto serverInfo = gson.fromJson(rec, ServerInfoDto.class);
                controller.findServer(serverInfo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Boolean getRun() {
        return run;
    }

    public void setRun(Boolean run) {
        this.run = run;
    }


}
