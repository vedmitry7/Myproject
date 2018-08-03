package app.mycity.mycity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class SocketTestActivity extends AppCompatActivity {

    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);



        {
            try {
                mSocket = IO.socket("http://192.168.0.104:8000");
            } catch (URISyntaxException e) {}
        }

        mSocket.connect();
        JSONObject obj = new JSONObject();
        try {
            obj.put("hash", "845737b121e003577aeda064e71fa57cc5a272e8");
            obj.put("ts", "15312274567891");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("auth", obj);
        mSocket.on("check_users", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("TAG21", String.valueOf(args[0]));
            }
        });
        mSocket.emit("check_users", obj);


/*        mSocket.on("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("TAG21", "login");
            }
        });
        mSocket.emit("add user", "Vasia");*/
        Log.d("TAG21", "alright");


    }
}
