package sample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class Client {
    public LocalUser user;
    private boolean connected;
    private String serverIP;
    private int port;
    private static Client client;
    private Sender sender;
    private Receiver receiver;
    private Socket clientSocket;
    private String numberOfPlayers="0";
    private String currentWord="";
    private int currentView=0;
    private List<Pair<String,Integer>> scores;
    private static final Logger LOG = Logger.getLogger(ResponseHandler.class.getName());

    private Client() {
        this.clientSocket = new Socket();
        this.connected = false;
    }

    public void connectToServer(String  serverIP, int port) {
        try {
            scores=new ArrayList<>();
            clientSocket.connect(new InetSocketAddress(serverIP, port), 5000);
            System.out.println("server ip " + serverIP);
            System.out.println("port  " + port);
            this.sender = new Sender(this.clientSocket);
            this.receiver = new Receiver(this.clientSocket);
            Thread thread = new Thread(receiver);
            thread.start();
            connected = true;
        } catch (IOException e) {
            LOG.info("Server unreachable");
            connected = false;
            clientSocket = null;
            clientSocket = new Socket();
        }
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getPort() {
        return port;
    }

    public static synchronized Client getClient() {
        if(client == null){
            synchronized (Client.class){
                if(client == null)
                    client = new Client();
            }
        }
        return client;
    }

    public int getCurrentView() {
        return currentView;
    }

    public void setCurrentView(int currentView) {
        this.currentView = currentView;
    }

    public boolean isConnected() {
        return connected;
    }

    public Sender getSender() {
        return sender;
    }

    public LocalUser getUser() {
        return user;
    }

    public String getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(String numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }


    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public List<Pair<String, Integer>> getScores() {
        return scores;
    }

    public void setScores(List<Pair<String, Integer>> scores) {
        this.scores = scores;
    }
}
