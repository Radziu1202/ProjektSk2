package view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import controls.LetterTextField;
import sample.Client;
import sample.Pair;

import java.io.File;
import java.net.URL;
import java.util.*;

public class GUIController implements Initializable {

    private static final Font fontLarge = Font.font("Droid Sans", FontWeight.BOLD, 35);
    private static final Font fontWord = Font.font("Courier New", FontWeight.BOLD, 20);

    public ImageView rywal1jpg;
    public TextField rywal1name;
    public ImageView rywal2jpg;
    public TextField rywal2name;
    public ImageView rywal3jpg;
    public TextField rywal3name;
    public List<Image> images;
    public Button nextOpponentButton;

    @FXML
    BorderPane bdPane;
    @FXML
    private LetterTextField txtInput;
    @FXML
    private TextField txtEntered;
    @FXML
    private Canvas canvas;

    @FXML
    private TextArea testField;
    private GraphicsContext gc;
    private GameController gameController;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        URL mistake0 = ClassLoader.getSystemResource("0mistakes.png");
        URL mistake1 = ClassLoader.getSystemResource("1mistake.png");
        URL mistakes2 = ClassLoader.getSystemResource("2mistakes.png");
        URL mistakes3 = ClassLoader.getSystemResource("3mistakes.png");
        URL mistakes4 = ClassLoader.getSystemResource("4mistakes.png");
        URL mistakes5 = ClassLoader.getSystemResource("5mistakes.png");
        URL mistakes6 = ClassLoader.getSystemResource("6mistakes.png");
        URL mistakes7 = ClassLoader.getSystemResource("7mistakes.png");

        //File mistake0=new File("src/jpgs/0mistakes.png");
       // File mistake1=new File("src/jpgs/1mistake.png");
       // File mistakes2=new File("src/jpgs/2mistakes.png");
       // File mistakes3=new File("src/jpgs/3mistakes.png");
       // File mistakes4=new File("src/jpgs/4mistakes.png");
       // File mistakes5=new File("src/jpgs/5mistakes.png");
       // File mistakes6=new File("src/jpgs/6mistakes.png");
       // File mistakes7=new File("src/jpgs/7mistakes.png");

        Image image0 = new Image(mistake0.toString());
        Image image1 = new Image(mistake1.toString());
        Image image2 = new Image(mistakes2.toString());
        Image image3 = new Image(mistakes3.toString());
        Image image4 = new Image(mistakes4.toString());
        Image image5 = new Image(mistakes5.toString());
        Image image6 = new Image(mistakes6.toString());
        Image image7 = new Image(mistakes7.toString());


        images= new ArrayList<>();
        images.add(image0);
        images.add(image1);
        images.add(image2);
        images.add(image3);
        images.add(image4);
        images.add(image5);
        images.add(image6);
        images.add(image7);

        Platform.runLater(() -> {
            gameController = new GameController();
            gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.setLineWidth(5);

            txtInput.textProperty().addListener((observable, oldValue, newValue) -> {
                if ("".equalsIgnoreCase(newValue)) {
                    return;
                }

                char ch = newValue.charAt(0);

            /*if (gameController.addChar(ch)) {
                // Update graphic
                int wrongGuesses = gameController.getWrongGuesses();
                drawHangman(wrongGuesses);

            }
*/
                gameController.addChar(ch);
                txtInput.clear();
                updateEnteredChars();
                updateWord();
                checkGameOver();
            });
            updateWord();
        });
    }


    public void keepDrawing(){
        Platform.runLater(()-> {
            int wrongGuesses = gameController.getWrongGuesses();
            drawHangman(wrongGuesses);
        });
    }

    private void updateWord() {
        Platform.runLater(()-> {
            gc.setFill(Color.WHITE);
            gc.fillRect(70,canvas.getHeight() - 100,200,canvas.getHeight() - 180);
            gc.setFont(fontWord);
            gc.setFill(Color.BLACK);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(gameController.getCurrentWord(), 70, canvas.getHeight() - 60);
        });
    }

    private void drawCorrectWord() {
        Platform.runLater(()-> {
            gc.setFont(fontWord);
            gc.setFill(Color.GREEN);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(gameController.getMissingChars(), 70, canvas.getHeight() - 60);
        });
    }

    private void checkGameOver() {
        if (gameController.isGameWon()) {
            // Return game won message
            disableGame();
            drawWon();

        } else if (gameController.isGameOver()) {
            // Return game over message
            Client.getClient().getSender().sendLostGameMessage(Client.getClient().getUser().getLogin());
            disableGame();
            drawGameOver();
            drawCorrectWord();
        }
    }

    @FXML
    private void handleNewGame(final ActionEvent event)
    {
        resetGame();
    }

    private void disableGame() {
        txtInput.setDisable(true);
    }

    private void resetGame() {
        System.out.println("before reset");
        gameController.reset();
        System.out.println("1");
        txtEntered.clear();
        txtInput.setDisable(false);
        System.out.println("2");
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        System.out.println("3");
        updateWord();
        System.out.println("4");
    }

    private void drawWon() {
        Platform.runLater(()-> {
            gc.setFont(fontLarge);
            gc.setFill(Color.GREEN);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("You Won!", Math.round(canvas.getWidth() / 2), Math.round(canvas.getHeight()) / 2);
        });
    }

    private void drawGameOver() {
        Platform.runLater(()-> {
            gc.setFont(fontLarge);
            gc.setFill(Color.RED);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("Game Over!", Math.round(canvas.getWidth() / 2), Math.round(canvas.getHeight()) / 2);
        });
    }

    private void drawHangman(int wrongGuesses) {
        Platform.runLater(()-> {
            switch (wrongGuesses) {
                case 1:
                    drawVerticalGallows();
                    break;
                case 2:
                    drawHorizontalGallows();
                    break;
                case 3:
                    drawHead();
                    break;
                case 4:
                    drawBody();
                    break;
                case 5:
                    drawLegs();
                    break;
                case 6:
                    drawArms();
                    break;
                case 7:
                    drawFace();
                    break;
            }
        });
    }

    private void drawFace() {
        gc.beginPath();
        gc.moveTo(135, 75);
        gc.lineTo(145, 85);
        gc.moveTo(145, 75);
        gc.lineTo(135, 85);
        gc.moveTo(165, 75);
        gc.lineTo(155, 85);
        gc.moveTo(155, 75);
        gc.lineTo(165, 85);
        gc.stroke();
        gc.strokeArc(135, 95, 30, 30, 50, 80, ArcType.OPEN);
    }

    private void drawArms() {
        gc.beginPath();
        gc.moveTo(120,140);
        gc.lineTo(180,140);
        gc.stroke();
    }

    private void drawLegs() {
        gc.beginPath();
        gc.moveTo(150,180);
        gc.lineTo(120, 220);
        gc.moveTo(150,180);
        gc.lineTo(180, 220);
        gc.stroke();
    }

    private void drawBody() {
        gc.beginPath();
        gc.moveTo(150,110);
        gc.lineTo(150,180);
        gc.stroke();
    }

    private void drawHead() {
        gc.strokeOval(125, 60, 50, 50);
    }

    private void drawHorizontalGallows() {
        gc.beginPath();
        gc.moveTo(31, 60);
        gc.lineTo(60, 31);
        gc.moveTo(30, 30);
        gc.lineTo(150, 30);
        gc.lineTo(150, 60);
        gc.stroke();
    }

    private void drawVerticalGallows() {
        gc.beginPath();
        gc.moveTo(30,30);
        gc.lineTo(30, 300);
        gc.moveTo(0, 300);
        gc.lineTo(60,300);
        gc.stroke();
    }

    private void updateEnteredChars() {
        StringBuilder enteredFormatted = new StringBuilder();
        gameController.getEnteredChars().stream().sorted().forEach(i -> enteredFormatted.append(i).append(","));
        txtEntered.setText(enteredFormatted.toString().substring(0, enteredFormatted.length() - 1));
    }

    public void updateScores(){
        String wyniki="";
        for (Pair pair : Client.getClient().getScores() ){ //zmien na GyiContoller.getscores
            wyniki+=(pair.getLogin()+" "+ Integer.toString( pair.getNumber())+ "pkt\n");

        }
        testField.setText(wyniki);
    }



    public void updateOpponents(){
        if (Client.getClient().getUser().getOpponontes().size() >3){
            nextOpponentButton.setDisable(false);
        }

        int x= Client.getClient().getCurrentView();
        if (Client.getClient().getUser().getOpponontes().size()>0){
            rywal3jpg.setImage(images.get(Client.getClient().getUser().getOpponontes().get(x).getNumber()));
            rywal3name.setText(Client.getClient().getUser().getOpponontes().get(x).getLogin());
            if (Client.getClient().getUser().getOpponontes().size()>1) {
                rywal2jpg.setImage(images.get(Client.getClient().getUser().getOpponontes().get(x+1).getNumber()));
                rywal2name.setText(Client.getClient().getUser().getOpponontes().get(x+1).getLogin());
                if (Client.getClient().getUser().getOpponontes().size()>2){
                    rywal1jpg.setImage(images.get(Client.getClient().getUser().getOpponontes().get(x+2).getNumber()));
                    rywal1name.setText(Client.getClient().getUser().getOpponontes().get(x+2).getLogin());
                }
            }
        }


    }

    public void nextOpponent(){
        System.out.println(Client.getClient().getCurrentView()+" curr");
        System.out.println(Client.getClient().getUser().getOpponontes().size()+" size opp");
        if (Client.getClient().getCurrentView()==Client.getClient().getUser().getOpponontes().size()-3){
            Client.getClient().setCurrentView(0);
        }
        else{
            Client.getClient().setCurrentView(Client.getClient().getCurrentView()+1);

        }

        updateOpponents();
    }


    static void compare(List<Pair<String,Integer>> words)
    {

        Collections.sort(words, Comparator.comparing(p -> -p.getNumber()));

    }


    public GameController getGameController() {
        return gameController;
    }



}