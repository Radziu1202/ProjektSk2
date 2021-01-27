package sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Sender {
    private PrintWriter writer;
    private char sep;


    public Sender(Socket socket) throws IOException {
        System.out.println(socket.getInetAddress());
       // socket.setKeepAlive();
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.sep = '|';
    }

    public void sendSignInMessage(String login, String password){
        writer.println("101" + this.sep + login + this.sep + password);
        System.out.println("sending");
    }

    public void sendSignOutMessage(){
        writer.println("102" + this.sep);
    }

    public void sendSignUpMessage(String nick, String login, String password){

        writer.println("103" + this.sep + nick + this.sep + login + this.sep + password);
        System.out.println("signup");
    }

    public void sendLetterMessage(char letter){
        writer.println("105" + this.sep + letter);
    } //tu pewnie jeszcze nick

    public void sendWant2PlayMessage(String login){
        writer.println("106" + this.sep + login);
    }

    public void sendStartGame(String login){writer.println("109"+this.sep+login);}
    public void sendNextWordPls(String login){
        writer.println("107" + this.sep + login);
    }

    public void sendGuessedLetter(String login){
        writer.println("104" + this.sep + login);
    }

    public void sendLostGameMessage(String login){writer.println("108"+this.sep+login);}


    public void sendMadeMistakeMessage(String login){writer.println("105"+this.sep+login);}

    private String transformToMessage(String code, String[] message){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s%s", code, this.sep));

        for(int i = 0; i  < message.length; i++){
            stringBuilder.append(message[i]);
            if(i < message.length - 1 )
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

}
