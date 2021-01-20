package view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sample.Client;
import sample.ResponseHandler;
import sample.User;
import sample.ViewManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    public TextField numberOfPLayers;
    @FXML
    private TextArea textAreaDescription;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button Start;
    @FXML
    private ListView<User> listViewUsers;
    @FXML
    private Label labelUserName;
    @FXML
    private Label labelDescription;

    Stage stage = new Stage();

    private ObservableList<User> observableListUsers;

    public MenuController(){
        observableListUsers = FXCollections.observableArrayList();
      //  observableListUsers.addAll(Client.getClient().getUser().getFriends());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Change description when ENTER pressed
        textAreaDescription.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                String description = textAreaDescription.getText();
                Platform.runLater(() -> textAreaDescription.getParent().requestFocus());
                ke.consume();
            }
        });
        numberOfPLayers.setEditable(false);
        numberOfPLayers.setText("liczba graczy: ");
        // Set local user nick and description
        labelUserName.setText(Client.getClient().getUser().getNickname());

        listViewUsers.setItems(observableListUsers);
     //   listViewUsers.setCellFactory(observableListUsers -> new UserCellController() );
       // createShowConversationListener();
    }


    public Button getStart() {
        return Start;
    }


    public void connectionLost(){
        labelDescription.getStyleClass().add("labelConLost");
        labelDescription.setText("Connection lost - try to sign in later");
    }

    public void showGame(){
        Platform.runLater(() -> newGame());
    }
    public void showGameNewWord(){
        Platform.runLater(() -> newWord());
    }

    public void sendWant2Play(){
        Client.getClient().getSender().sendWant2PlayMessage(Client.getClient().getUser().getLogin());
    }
    public void newGame() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("zaczynam nowy obraz");
                //buttonAdd.getScene().getWindow().setWidth(500);
               // stage.setScene(buttonAdd.getScene());
                stage = (Stage) getStart().getScene().getWindow();
                stage.setWidth(700);
                stage.setHeight(800);
                FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
                try {
                    ViewManager.guiRoot = gameLoader.load();
                    ViewManager.guiController = gameLoader.getController();
                    stage.setScene(new Scene(ViewManager.guiRoot));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setOnHiding( event -> {Client.getClient().getSender().sendSignOutMessage();} );
            }
        });

    }

    public void newWord() {
        Platform.runLater(()->{
            System.out.println("zaczynam nowy obraz");
            stage.setWidth(700);
            stage.setHeight(800);
            FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
            try {
                ViewManager.guiRoot = gameLoader.load();
                ViewManager.guiController = gameLoader.getController();
                ViewManager.guiController.updateScores();
                ViewManager.guiController.updateOpponents();
                stage.setScene(new Scene(ViewManager.guiRoot));

            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setOnHiding( event -> {Client.getClient().getSender().sendSignOutMessage();} );
        });

    }


    public void updatePlayers(){
        Platform.runLater(() ->{
            numberOfPLayers.setText("liczba graczy: " + Client.getClient().getNumberOfPlayers());
        });
    }

    public void LetsStart(ActionEvent actionEvent) {
        Client.getClient().getSender().sendStartGame(Client.getClient().getUser().getLogin());

    }
}
