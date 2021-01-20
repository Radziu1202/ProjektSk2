package sample;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.GameController;
import view.MenuController;

import javax.swing.text.View;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseHandler {
    private String[] response;
    private LocalUser localUser;
    private static final Logger LOG = Logger.getLogger(ResponseHandler.class.getName());

    public ResponseHandler(String[] response, LocalUser localUser) {
        this.response = response;
        this.localUser = localUser;
    }


    public void handleResponse() {
        switch (Integer.valueOf(response[0])) {
            case 400:
                catchOpponent();
                break;
            case 401:
                catchSignInResp();
                break;
            case 402:
                catchSignOutResp();
                break;
            case 403:
                catchSignUpResp();
                break;
            case 405:
                checkLetter();
                break;
            case 406:
                 mayIPlayResp();
                 break;
            case 407:
                wePlayinBois();
                break;
            case 408:
                newWord();
                break;
            case 409:
                catchNumberOfPlayers();
                break;
            case 410:
                catchScore();
                break;

            case 600:
                catchServerOffline();
        }
    }

    // response: 400|0 or 400|1
    private void catchOpponent(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - opponent message");
        else{
            LOG.log(Level.INFO, "Server: success - opponent message");
            response = response[1].split("@", 2);
            Pair<String,Integer> LogAndMistakes = new Pair<String,Integer>(response[0], Integer.parseInt(response[1]));
            int counter=0;
            System.out.println("a");
            for (int i=0; i < Client.getClient().getUser().getOpponontes().size();i++){
                if (Client.getClient().getUser().getOpponontes().get(i).getLogin().equals(LogAndMistakes.getLogin())){
                    System.out.println("b");
                    Client.getClient().getUser().getOpponontes().set(i,LogAndMistakes);
                    counter+=1;
                    System.out.println("podmieniam oponenta");
                }
            }
            if (counter==0){
                System.out.println("c");
                Client.getClient().getUser().getOpponontes().add(LogAndMistakes);
                System.out.println("dodaje oponenta");
            }
            ViewManager.guiController.updateOpponents();
        }
    }



    // response: 401|0 or 401|nick|description|nick1,login1,description1,logged_in1,nick2,login2,description2,logged_in2...
    private void catchSignInResp(){
        if(response[1].equals("0")){
            ViewManager.logInController.accessDenied();
            LOG.log(Level.INFO, "Server: fail - sign in");
        }
        else {
            ViewManager.logInController.accessGranted(response[1]);
            LOG.log(Level.INFO, "Server: success - sign in");
        }
    }

    // response: 402|0 lub 402|1
    private void catchSignOutResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - sign out");
        else
            LOG.log(Level.INFO, "Server: success - sign out");
    }

    // response: 403|0 lub 403|1
    private void catchSignUpResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - sign up");
        else
            LOG.log(Level.INFO, "Server: success - sign up");
    }

    // response: 404|0 lub 404|nick1,login1,nick2,login2...


    // response: 405|0 lub 405|nick|login|description|logged_in
    private void checkLetter(){
        if(response[1].equals("wrong")){
            LOG.log(Level.INFO, "Server: fail - zła litera");
            ViewManager.guiController.getGameController().setWrongGuess(true);
            ViewManager.guiController.getGameController().setWrongGuesses( ViewManager.guiController.getGameController().getWrongGuesses()+1);
            ViewManager.guiController.keepDrawing();
        }
        else {
            ViewManager.guiController.getGameController().setWrongGuess(false);
            LOG.log(Level.INFO, "Server: success - dobra litera");

            ViewManager.guiController.keepDrawing();

        }
    }
    // response: 406|0 lub 404|yes
    private void mayIPlayResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - cannot play");
        else {
            LOG.log(Level.INFO, "Server: success - play");
            System.out.println(Client.getClient().getUser().getLogin()+"info o graczach");
            Client.getClient().setNumberOfPlayers(response[1]);
            if (Integer.parseInt(response[1]) >=2){
                ViewManager.menuController.getStart().setDisable(false);
            }
            else{ViewManager.menuController.getStart().setDisable(true);}
            ViewManager.menuController.updatePlayers();

        }
    }

    // response: 407|0 lub 407|zaczynamy
    private void wePlayinBois() {
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - cannot play yet");
        else {
            Client.getClient().getUser().setPlayin(true);
            LOG.log(Level.INFO, "Server: success - Game is about to begin");
            Client.getClient().setCurrentWord(response[1]);
            Client.getClient().getUser().getOpponontes().clear();
            Client.getClient().getScores().clear();
            ViewManager.menuController.showGame();

        }
    }

    // response: 408|0 lub 408|ID_conv
    private void newWord(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - new word error");
        else{
            System.out.println("odebrałem nowe slowo "+response[1]);
            ViewManager.guiController.getGameController().setRndWord(response[1]);
            Client.getClient().setCurrentWord(response[1]);
            Client.getClient().getUser().getOpponontes().clear();
            Client.getClient().setCurrentView(0);
            System.out.println("czyszcze liste przeciwnikow");
            ViewManager.menuController.showGameNewWord();


        }
    }

    //  create new conversation when you have only string logins, not objects


    // response: 409|0 lub 409|1
    private void catchNumberOfPlayers() {
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: info - Someone else is still playing");
        else{
            LOG.log(Level.INFO, "Server: info - end of game, go back to menu");
            ViewManager.logInController.accessMenu();
        }
    }

    // response: 410|0 lub 410|1
    private void catchDeleteFriendResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - delete friend");
        else
            LOG.log(Level.INFO, "Server: success - delete friend");
    }


    private void catchScore(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - nie dostarczono wyniku");
        else {
            LOG.log(Level.INFO, "Server: success - dostarczono wynik");

            response = response[1].split("@", 2);
            Pair<String, Integer> LogAndScore = new Pair<>(response[0], Integer.parseInt(response[1]));
            int counter=0;
            for (int i=0; i < Client.getClient().getScores().size();i++){
                if (Client.getClient().getScores().get(i).getLogin().equals(LogAndScore.getLogin())){
                    Client.getClient().getScores().set(i,LogAndScore);
                    counter+=1;
                    System.out.println("podmieniam");
                }
            }
            if (counter==0){
                Client.getClient().getScores().add(LogAndScore);
                System.out.println("dodaje");
            }
            ViewManager.guiController.updateScores();
        }
    }

    // response: 500|ID_conv|login,data,godzina,wiadomosc


    // response: 501|login|description|logged_in



    // response: 502|login|nick|description|logged_in|ID_conv



    // response: 600|FIN
    private void catchServerOffline(){
        Client.getClient().setConnected(false);
        Platform.runLater(()->ViewManager.menuController.connectionLost());
        LOG.log(Level.INFO, "Server: offline");
    }


    // Read message in format "login,data,godzina,wiadomosc"

}
