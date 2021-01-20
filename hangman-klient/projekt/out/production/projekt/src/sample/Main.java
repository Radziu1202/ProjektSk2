package sample;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    /*
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/game.fxml"));
        primaryStage.setTitle("Hangman");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 500, 450));
        primaryStage.show();
    }
    */


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/connect.fxml"));
        Parent root = loader.load();

        primaryStage.setOnHiding( event -> {
            if(Client.getClient().isConnected())
                Client.getClient().getSender().sendSignOutMessage();
        } );

        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }


    public static void main(String[] args) {
        System.setProperty("line.separator", "\n"); // Unix line separator
        //Client.getClient().connectToServer("192.168.0.20", 1337);
        launch(args);
    }
}
