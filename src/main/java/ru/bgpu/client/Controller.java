package ru.bgpu.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.bgpu.client.dto.FileInfoDto;
import ru.bgpu.client.dto.ServerInfoDto;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private DetectiveServers detectiveServers;
    private RemoteServer fileChoose;
    private FileChooser fileChooser;
    @FXML public ListView<RemoteServer> listServers;
    @FXML public ListView<FileInfoDto> listFiles;
    @FXML public Label sizeFile;


    boolean run = true;

    public void save(ActionEvent actionEvent) throws IOException {
        fileChoose = listServers.getSelectionModel().getSelectedItem();
        fileChoose.sendFile(listFiles.getSelectionModel().getSelectedItem().getName());
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
        listFiles.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, old, item) -> {
                        sizeFile.setText("Size: " + item.getSize() + " Kb");
                }
        );
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
