package se.mobilapp.isacson.anna.assignment4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection implements Runnable{
    private String lineFromServer;
    private Socket clientSocket;
    private MainActivity mainActivity;
    private String host;
    private int port;
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private boolean disconnected = false;

    public ServerConnection(MainActivity mainActivity, String host, int port) {
        this.mainActivity = mainActivity;
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try {
            mainActivity.log("Connecting to host: " + host);
            clientSocket = new Socket(host, port);
            mainActivity.log("Connected.");
                fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                toServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void disconnectToServer() {
        disconnected = true;
        mainActivity.disconnected();
    }

    void sendToServer(String msg) {
        toServer.println(msg);
    }

    @Override
    public void run() {
        try{
            while(!disconnected) {
                lineFromServer = fromServer.readLine();
                mainActivity.log(lineFromServer);
            }
            toServer.close();
            fromServer.close();
            clientSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
            disconnected = true;
        }
    }
}
