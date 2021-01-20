package view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Client;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.LocalUser;
import sample.User;
import sample.ViewManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class LoginController {
    @FXML
    private TextField textFieldLogin;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button buttonSignIn;
    @FXML
    private Button buttonSignUp;
    @FXML
    private Label labelInfo;

    Stage stage;

    public void singIn() {
        labelInfo.setText("Conecting...");

        if(!Client.getClient().isConnected()){
            CompletableFuture.runAsync(() -> Client.getClient().connectToServer(Client.getClient().getServerIP(), Client.getClient().getPort()))
                    .handle((res, ex) -> {
                        if (Client.getClient().isConnected())
                            Client.getClient().getSender().sendSignInMessage(textFieldLogin.getText(), passwordField.getText());
                        else
                            Platform.runLater(() -> labelInfo.setText("Can't reach server"));
                        return res;
                    });
        }else
            Client.getClient().getSender().sendSignInMessage(textFieldLogin.getText(), passwordField.getText());
    }

    public void signUp(){
        Scene scene = textFieldLogin.getScene();

        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
        Parent registerRoot = null;
        try {
            registerRoot = registerLoader.load();
            scene.setRoot(registerRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        labelInfo.setText("");
    }

    public void accessGranted(String nickname) {
        Client.getClient().user = new LocalUser(nickname, textFieldLogin.getText(), true );
        Platform.runLater(() -> showMenuWindow());
    }

    public void accessDenied(){
        Platform.runLater(() ->{
            labelInfo.setText("Wrong username / password or account is in use");
            textFieldLogin.clear();
            passwordField.clear();
        });
    }


    public void showMenuWindow(){
        stage = (Stage) textFieldLogin.getScene().getWindow();
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/view/Menu.fxml"));
        try {
            ViewManager.menuRoot = menuLoader.load();
            ViewManager.menuController = menuLoader.getController();
            stage.setScene(new Scene(ViewManager.menuRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setOnHiding( event -> {Client.getClient().getSender().sendSignOutMessage();} );
    }

    public void accessMenu(){
        Platform.runLater(() -> goBacktoMenu());
    }

    public void goBacktoMenu(){

        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/view/Menu.fxml"));
        try {

            ViewManager.menuRoot = menuLoader.load();
            ViewManager.menuController = menuLoader.getController();
            ViewManager.menuController.getStart().setDisable(false);
            ViewManager.menuController.numberOfPLayers.setText("liczba graczy: " + Client.getClient().getNumberOfPlayers());
            stage.setWidth(400);
            stage.setHeight(600);
            stage.setScene(new Scene(ViewManager.menuRoot));

        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setOnHiding( event -> {Client.getClient().getSender().sendSignOutMessage();} );
    }

}
