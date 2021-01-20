package view;

import javafx.application.Platform;
import sample.Client;
import sample.Pair;
import sample.ViewManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {

    private static final int MAX_WRONG_GUESSES = 7;

    private String rndWord;
    private List<Character> enteredChars;


    private int wrongGuesses;
    private boolean wrongGuess;

   public GameController() {

        enteredChars = new ArrayList<>();

        wrongGuesses = 0;
        rndWord=Client.getClient().getCurrentWord();
        System.out.println(rndWord);
    }

    String getCurrentWord() {
        String currentWord = "";
        for (char c : rndWord.toCharArray()) {
            if (enteredChars.contains(c)) {
                currentWord += c + " ";

            } else {
                currentWord += "_ ";

            }
        }
        return currentWord;
    }

    String getMissingChars() {
        String missingChars = "";
        for (char c : rndWord.toCharArray()) {
            if (enteredChars.contains(c)) {
                missingChars += "  ";
            } else {
                missingChars += c + " ";
            }
        }
        return missingChars;
    }




    void reset() {
        System.out.println("reset start ");
        wrongGuesses = 0;
        enteredChars.clear();
        System.out.println("after clear");
    }




    public int getWrongGuesses() {
        return wrongGuesses;
    }

    public void setWrongGuess(boolean wrongGuess) {
        this.wrongGuess = wrongGuess;
    }

    public boolean isWrongGuess() {
        return wrongGuess;
    }


    boolean addChar(char ch) {
        if ((!enteredChars.stream().anyMatch(i -> i.equals(ch)))) {
            enteredChars.add(ch);
            wrongGuess=false;
            if (!rndWord.contains(String.valueOf(ch))) {
                System.out.println("a");
                Client.getClient().getSender().sendMadeMistakeMessage(Client.getClient().getUser().getLogin());
                ViewManager.guiController.getGameController().setWrongGuess(true);
                ViewManager.guiController.getGameController().setWrongGuesses( ViewManager.guiController.getGameController().getWrongGuesses()+1);
                ViewManager.guiController.keepDrawing();
                wrongGuess = true;
                // wrongGuesses++;

                //Client.getClient().getSender().sendLetterMessage(ch);
            }
            else {
                Client.getClient().getSender().sendGuessedLetter(Client.getClient().getUser().getLogin());
            }


        }
        return wrongGuess;
    }

    List<Character> getEnteredChars() {
        return Collections.unmodifiableList(enteredChars);
    }



    boolean isGameOver() {
        return wrongGuesses >= MAX_WRONG_GUESSES;
    }

    boolean isGameWon() {
        for (char c : rndWord.toCharArray()) {
            if (!enteredChars.contains(c)) {
                return false;
            }
        }
        Client.getClient().getSender().sendNextWordPls(Client.getClient().getUser().getLogin());
        return true;
    }

    public void setRndWord(String rndWord) {
        this.rndWord = rndWord;
    }

    public void setWrongGuesses(int wrongGuesses) {
        this.wrongGuesses = wrongGuesses;
    }
}

