package ru.bgpu.client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import ru.bgpu.client.dto.ServerInfoDto;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private DetectiveServers detectiveServers;
    @FXML public ListView<RemoteServer> listServers;
    @FXML public ListView<String> listFiles;


    boolean run = true;

    public void save(ActionEvent actionEvent) {
//        listServers.getItems().add("hello");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            detectiveServers = new DetectiveServers(this);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        listServers.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, old, item) -> {
                    Platform.runLater(() -> {
                        listFiles.getItems().clear();
                        try {
                            listFiles.getItems().addAll(item.fileNames());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
        );
        listFiles.getSelectionModel().getSelectedItems();
    }

    public void updateServersList(ActionEvent actionEvent) {
        try {
            detectiveServers.sendMsgToServers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void findServer(ServerInfoDto dto) {
        Platform.runLater(()->{
            try {
                listServers.getItems().add(new RemoteServer(dto));
                System.out.println("!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
