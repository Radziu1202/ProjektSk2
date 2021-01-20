package view;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javafx.fxml.FXMLLoader;
import sample.Client;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sample.ViewManager;

public class ConnectController {

    @FXML
    private TextField fieldServerIP;
    @FXML
    private TextField fieldPort;
    @FXML
    private Label labelInfo;
    @FXML
    private Button buttonConnect;

    public void connect(){
        Client.getClient().setPort(Integer.valueOf(fieldPort.getText()));
        Client.getClient().setServerIP(fieldServerIP.getText());
        labelInfo.setText("Connecting to server...");

        CompletableFuture.runAsync(() -> Client.getClient().connectToServer(
                fieldServerIP.getText(), Integer.valueOf(fieldPort.getText())))
                .handle((res, ex) -> {
                    if (Client.getClient().isConnected())
                        showLogInWindow();
                    else
                        Platform.runLater(() -> labelInfo.setText("Can't reach server"));
                    return res;
                });
    }

    private void showLogInWindow(){
        FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        try {
            ViewManager.loggInRoot = logInLoader.load();
            ViewManager.logInController = logInLoader.getController();

            labelInfo.getScene().getWindow().setHeight(630);
            labelInfo.getScene().setRoot(ViewManager.loggInRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
