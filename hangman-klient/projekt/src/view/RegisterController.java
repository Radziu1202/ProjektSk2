package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import sample.Client;
import sample.ViewManager;


import java.util.concurrent.CompletableFuture;

public class RegisterController {

    @FXML
    private TextField textFieldNick;
    @FXML
    private TextField textFieldLogin;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private PasswordField textFieldRepeatPassword;
    @FXML
    private Label labelInfo;
    @FXML
    private Button buttonSignUpReg;
    @FXML
    private Button buttonBack;


    public void signUp(){
        if(!Client.getClient().isConnected()){
            CompletableFuture.runAsync(() -> Client.getClient().connectToServer(
                    Client.getClient().getServerIP(), Client.getClient().getPort()))
                    .handle((res, ex) -> {
                        if (Client.getClient().isConnected())
                            sendSingUpMes();
                        else
                            Platform.runLater(() -> labelInfo.setText("Can't reach server"));
                        return res;
                    });
        }else
            sendSingUpMes();
    }

    private void sendSingUpMes(){
        if(textFieldPassword.getText().equals(textFieldRepeatPassword.getText())){
            Client.getClient().getSender().sendSignUpMessage(textFieldNick.getText(), textFieldLogin.getText(), textFieldPassword.getText());
            Platform.runLater(()->labelInfo.setText("Account created !"));
        }
        else
            Platform.runLater(()->labelInfo.setText("Passwords do not match "));
    }

    public void backToLoggIn(){
        labelInfo.setText("");
        Scene scene = textFieldLogin.getScene();
        scene.setRoot(ViewManager.loggInRoot);
    }
}
