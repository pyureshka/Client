package ru.bgpu.client;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    private BooleanProperty load = new SimpleBooleanProperty(false);
    @FXML public ListView<RemoteServer> listServers;
    @FXML public ListView<FileInfoDto> listFiles;

    @FXML public Button save;
    @FXML public Button update;
    @FXML public Label info;


    boolean run = true;

    public void save(ActionEvent actionEvent) throws IOException {
        fileChoose = listServers.getSelectionModel().getSelectedItem();
        load.set(true);
        fileChoose.sendFile(listFiles.getSelectionModel().getSelectedItem().getName(),
                (int) listFiles.getSelectionModel().getSelectedItem().getSize());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        save.disableProperty().bind(load);
        update.disableProperty().bind(load);
        listFiles.disableProperty().bind(load);
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
                    if(item != null) {
                        info.setText("Size: " + Math.round(item.getSize() / 1024) + " Kb");
                    } else {
                        info.setText("--");
                    }
                }
        );
    }

    public void updateServersList(ActionEvent actionEvent) {
        Platform.runLater(()->{
            listServers.getItems().clear();
        });
        try {
            detectiveServers.sendMsgToServers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void findServer(ServerInfoDto dto) {
        Platform.runLater(()->{
            try {
                listServers.getItems().add(new RemoteServer(dto, this));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void progressView(int fileSize, float progress, int speed, int check) {
        Platform.runLater(()-> {
            if(check == 0) {
                info.setText(progress / 1024 + " Kb / " + fileSize / 1024 + " Kb - " + speed / 1024 + " Kb/s");
            } else if (check == 1) {
                info.setText("success!");
                load.set(false);
            } else {
                info.setText("error");
                load.set(false);
            }
        });
    }
}
