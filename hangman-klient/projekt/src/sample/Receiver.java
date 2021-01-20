package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Pattern;

public class Receiver implements Runnable {

    private BufferedReader reader;
    //private InputStream reader;
    private volatile boolean running;

    public Receiver(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //this.reader=socket.getInputStream();
        this.running = true;
    }


    @Override
    public void run() {
        String message;
        while(running){
            try {
                message = reader.readLine();
                if(message != null){
                    System.out.println("---> Recived: " + message);
                    ResponseHandler responseHandler  = new ResponseHandler(
                            message.split(Pattern.quote("|")), Client.getClient().getUser()
                    );
                    responseHandler.handleResponse();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
