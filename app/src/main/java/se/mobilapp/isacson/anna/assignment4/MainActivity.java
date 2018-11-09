package se.mobilapp.isacson.anna.assignment4;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private Button send;
    private Button connect;
    private Button disconnect;
    private EditText message;
    private String convoMessage;
    private TextView textView;
    private Handler handler = new Handler();
    private ServerConnection serverConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    void setupUI() {
        send = (Button) findViewById(R.id.send);
        connect = (Button) findViewById(R.id.connect);
        disconnect = (Button) findViewById(R.id.disconnect);
        textView = (TextView) findViewById(R.id.textView);

        //calls when the send text message button has been pressed.
        send.setOnClickListener(this);
        send.setEnabled(false);
        //calls when the connect button has been pressed.
        connect.setOnClickListener(this);
        //calls when the disconnect button has been pressed.
        disconnect.setOnClickListener(this);
        disconnect.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if(v == send) {
            message = (EditText) findViewById(R.id.textInput);
            convoMessage = message.getText().toString();
            sendToServer(convoMessage);
            log("Me: "+ convoMessage);
            message.setText("");
        }
        if(v == connect) {
            connect.setEnabled(false);
            disconnect.setEnabled(true);
            send.setEnabled(true);
            new ServerConnectionTask().execute();
        }
        if(v == disconnect) {
            disconnect.setEnabled(false);
            disconnectToServer();
        }
    }

    public void log(String msg) {
        final String line = msg + "\n";
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(textView.getText() + line);
                textView.invalidate();
            }
        });
    }

    void sendToServer(String msg) {
        final String lineFromUser = msg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverConnection.sendToServer(lineFromUser);
            }
        }).start();
    }

    void disconnectToServer() {
        serverConnection.disconnectToServer();
    }

    public void disconnected() {
        serverConnection = null;
        connect.setEnabled(true);
        send.setEnabled(false);
    }

    private class ServerConnectionTask extends AsyncTask<Void, Void, ServerConnection>{

        @Override
        protected ServerConnection doInBackground(Void... _) {
            String host = getString(R.string.HOST);
            int port = Integer.valueOf(getString(R.string.PORT));
                serverConnection = new ServerConnection(MainActivity.this, host, port);
                serverConnection.connect();
            return serverConnection;
        }

        @Override
        protected void onPostExecute(ServerConnection serverConnection) {
            MainActivity.this.serverConnection = serverConnection;

            new Thread(serverConnection).start();
        }
    }
}
