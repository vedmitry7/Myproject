package app.mycity.mycity.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.OkHttpClientFactory;
import app.mycity.mycity.api.model.Message;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.SendMessageResponse;
import app.mycity.mycity.views.adapters.ChatRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chatRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.chatEditText)
    EditText editText;

    @BindView(R.id.chatProfileImage)
    CircleImageView imageView;

    @BindView(R.id.chatName)
    TextView nameText;


    ChatRecyclerAdapter adapter;

    RealmList<Message> messages = new RealmList<>();

    private Realm mRealm;

    Callback callback;
    Request request;

    RealmResults<Message> results;

    long userId = 1;
    public static String imageUrl;

    private Socket mSocket;

    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String history = "" + args[0];
            Log.d("TAG21", "History - " + args[0]);
            chatResponse2(history);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();


        {
            try {
                mSocket = IO.socket("http://192.168.0.104:8000");
            } catch (URISyntaxException e) {}
        }

        mSocket.connect();


        JSONObject obj = new JSONObject();

        long l = 15342461596308L;
        try {
            obj.put("hash", SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));
            if(SharedManager.getProperty("ts")!=null){
                obj.put("ts", Long.parseLong(SharedManager.getProperty("ts")));
                Log.d("TAG21", "TS AUTH - " + SharedManager.getProperty("ts"));
            }
            else{
                obj.put("ts", l);
                Log.d("TAG21", "TS AUTH old - " + l);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("auth", obj);

        mSocket.on("history", listener);

        userId = getIntent().getLongExtra("user_id", 0);
        imageUrl = getIntent().getStringExtra("image");

        String name =  getIntent().getStringExtra("name");

        if(name!=null)
        nameText.setText(name);

        Picasso.get().load(imageUrl).into(imageView);

        for (int i = 0; i < 0; i++) {
            Message message = mRealm.createObject(Message.class, getNextKey());
            //   message.setId(i);
            if ((i & 1) == 0) {
                message.setOut(0);
                message.setText("Чье то сообщение " + (i + 1));
            } else {
                message.setOut(1);
                message.setText("Мое сообщение " + (i + 1));
            }
            message.setTime(Calendar.getInstance().getTimeInMillis());
            message.setUser(1);
        }
        //possible error
        updateList();

        Log.d("TAG21", "Size - " + messages.size());

        adapter = new ChatRecyclerAdapter(results);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.scrollToPosition(results.size()-1);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, final int bottom, int oldLeft, int oldTop, int oldRight, final int oldBottom) {
                if ( bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(messages.size()-1);
                        }
                    }, 100);
                }
            }
        });

      //  getChats();
    }

    @OnClick(R.id.chatBackButtonContainer)
    public void back(View v){
        this.finish();
    }

    void updateList(){
        mRealm.beginTransaction();
        results = mRealm.where(Message.class).equalTo("user", userId)
                .findAll();
        Log.d("TAG21", "Message List Size - " + results.size());
        messages.addAll(results);
        mRealm.commitTransaction();

        if(adapter!=null){
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(results.size()-1);
        }

    }

    @Override
    public void onDestroy() {
        mRealm.close();
        mSocket.off("history");
        mSocket.disconnect();
        mSocket.close();
        listener = null;
        Log.d("TAG21", "Realm close");
        super.onDestroy();
    }

    public long getNextKey() {
        try {
            Number number = mRealm.where(Message.class).max("id");
            if (number != null) {
                return number.longValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    @OnClick(R.id.sendMessageButton)
    public void sendMessage(View v){

        final String messageText = editText.getText().toString();
        editText.setText("");

        ApiFactory.getApi().sendMessage(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId, 0, messageText).enqueue(new retrofit2.Callback<ResponseContainer<SendMessageResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<SendMessageResponse>> call, retrofit2.Response<ResponseContainer<SendMessageResponse>> response) {
                final SendMessageResponse sendMessageResponse = response.body().getResponse();
                Log.d("TAG21", "RESULT " + sendMessageResponse.getMessageId() + " " + sendMessageResponse.getSuccess() );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mRealm.beginTransaction();
                        Message message = mRealm.createObject(Message.class, sendMessageResponse.getMessageId());
                        //message.setId(messageId);
                        message.setUser(userId);
                        message.setTime(Calendar.getInstance().getTimeInMillis());
                        message.setText(messageText);
                        message.setOut(1);
                        message.setWasRead(false);
                        mRealm.commitTransaction();
                        updateList();
                    }
                });
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<SendMessageResponse>> call, Throwable t) {

            }
        });



    /*    Log.d("TAG21", "sending...");
        RequestBody body = new FormBody.Builder()
                .add("access_token", SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN))
                .add("text", "message test")
                .add("peer_id", String.valueOf(userId))
                .add("chat_id", "")
                .build();

        request = new Request.Builder().url("http://192.168.0.104/api/messages.send")
                .post(body)
                .build();


        OkHttpClientFactory.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG21", "FAILURE NEW MESSAGE");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("TAG21", responseString);
            }
        });

*/

    }

    @OnClick(R.id.updateChat)
    public void updateChat(View v){
        Log.d("TAG21", "update chat...");
        getChats();
    }




    void getChats(){
        Log.i("TAG21", "GETCHats");

        final long startTime = Calendar.getInstance().getTimeInMillis();
    /*    RequestBody body = new FormBody.Builder()
                .build();
        Request request = new Request.Builder().url("messages.getLongpollHistory")
                .build();*/

        if(SharedManager.getProperty("ts")==null){
            long l = 15342461596308L;
            SharedManager.addProperty("ts", "15313140976873");
        }

        //String token = "390868736b9c9e65e6aeecac78466427a3726b8c";
        RequestBody body = new FormBody.Builder()
                .add("access_token", SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN))
                .add("ts", SharedManager.getProperty("ts"))
                .build();

         request = new Request.Builder().url("http://192.168.0.104/api/messages.getLongpollHistory")
                .post(body)
                .build();

         callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                long endTime = Calendar.getInstance().getTimeInMillis();
                Log.i("TAG21","FAILURE!!! " + ((endTime - startTime)));
                Log.i("TAG21","FAILURE!!! " + e.getCause() + " " + e.getMessage());
             //   newRequest();
            }

             @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                 Log.i("TAG21", responseString);
                 long endTime = Calendar.getInstance().getTimeInMillis();
                 Log.i("TAG21","RESPONSE!!! " + ((endTime - startTime)/1000));
               // String responseClear = responseString.substring(1,responseString.length()-1);
               // Log.i("TAG21", responseClear);
                chatResponse(responseString);
            }
        };

        newRequest();
    }



// {"response":{"history":[[1,1534238337,4,0,74,{"text":"коуо","from_id":4,"date":1534238337},0],[5,1534238337,4,0,74,"коуо",0]],"ts":15342383373867}}

//             {"history":[{"0":1,"1":1534238429,"2":4,"3":0,"4":"77","5":{"text":"ггг","from_id":4,"date":1534238429},"6":0},{"0":5,"1":1534238429,"2":4,"3":0,"4":"77","5":"ггг","6":0}],"ts":15342384292720}

  //  {"history":[[1,1534241198,4,0,"94",{"text":"овоао","from_id":4,"date":1534241198},0],[5,1534241198,4,0,"94","овоао",0]],"ts":15342411988851}
    void chatResponse(String responseString){
        JSONObject jsonObject = null;
        JSONObject innerResponseObject = null;
        JSONArray jsonArray = null;

        try {
            jsonObject = new JSONObject(responseString);
            Log.i("TAG21", "jsonObj = " + String.valueOf(jsonObject!=null));
            innerResponseObject = jsonObject.getJSONObject("response");
            Log.i("TAG21", "jsonInnerRespObj = " + String.valueOf(innerResponseObject!=null));

            jsonArray = innerResponseObject.getJSONArray("history");
            Log.i("TAG21", "jsonArray = " + String.valueOf(jsonArray!=null));
            Log.i("TAG21", "Size - " + String.valueOf(jsonArray.length()));

            String ts = innerResponseObject.getString("ts");
            Log.i("TAG21", "ts - " + ts);
            SharedManager.addProperty("ts", ts);


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);

                String info = null;
                switch (array.getInt(0)){
                    case 1: info = "new message   ";break;
                    case 2: info = "was read      ";break;
                    case 5: info = "dialog update "; break;
                    case 6: info = "read dialog   "; break;
                }
                Log.i("TAG21", info + array.length() + ": " + array.toString());
                int type = array.getInt(0);
                final int messageId = array.getInt(4);
                final long userId = array.getLong(2);
                final long time = array.getLong(1);

                switch (type){
                    case 1:
                        Log.i("TAG21", "new mes");
                        //How to get text

                        break;
                    case 2:
                        Log.i("TAG21", "was read");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = mRealm
                                        .where(Message.class).equalTo("id", messageId).findFirst();
                                mRealm.beginTransaction();
                                if(message != null){
                                    message.setWasRead(true);
                                }
                                mRealm.commitTransaction();
                                updateList();
                            }
                        });

                        break;
                    case 5:
                        Log.i("TAG21", "Add message to Realm");
                        //new message
                        final String text = array.getString(5);
                        final int out = array.getInt(6);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = mRealm
                                        .where(Message.class).equalTo("id", messageId).findFirst();
                                mRealm.beginTransaction();
                                if(message == null){
                                    message = mRealm.createObject(Message.class, messageId);
                                    //message.setId(messageId);
                                    message.setUser(userId);
                                    message.setTime(time);
                                    message.setText(text);
                                    message.setOut(out);
                                    message.setWasRead(false);
                                } else {
                                    message.setTime(time);
                                }
                                mRealm.commitTransaction();
                                updateList();
                            }
                        });

                        break;
                }
            }
            //newRequest();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG21", "JSON GET RESPONSE ERROR");
            //     newRequest();
        }

               /* JSONObject innerErrorObject;
                try {
                    innerErrorObject = jsonObject.getJSONObject("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

    }

    void chatResponse2(String responseString){
        JSONObject jsonObject = null;
        JSONObject innerResponseObject = null;
        JSONArray jsonArray = null;

        try {
            jsonObject = new JSONObject(responseString);
            Log.i("TAG21", "jsonObj = " + String.valueOf(jsonObject!=null));

            jsonArray = jsonObject.getJSONArray("history");
            Log.i("TAG21", "jsonArray = " + String.valueOf(jsonArray!=null));
            Log.i("TAG21", "Size - " + String.valueOf(jsonArray.length()));

            String ts = jsonObject.getString("ts");
            Log.i("TAG21", "ts - " + ts);
            SharedManager.addProperty("ts", ts);


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);

                String info = null;
                switch (array.getInt(0)){
                    case 1: info = "new message   ";break;
                    case 2: info = "was read      ";break;
                    case 5: info = "dialog update "; break;
                    case 6: info = "read dialog   "; break;
                }
                Log.i("TAG21", info + array.length() + ": " + array.toString());
                int type = array.getInt(0);
                final int messageId = array.getInt(4);
                final long userId = array.getLong(2);
                final long time = array.getLong(1);

                switch (type){
                    case 1:
                        Log.i("TAG21", "new mes");
                        //How to get text

                        break;
                    case 2:
                        Log.i("TAG21", "was read");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = mRealm
                                        .where(Message.class).equalTo("id", messageId).findFirst();
                                mRealm.beginTransaction();
                                if(message != null){
                                    message.setWasRead(true);
                                }
                                mRealm.commitTransaction();
                                updateList();
                            }
                        });

                        break;
                    case 5:
                        Log.i("TAG21", "Add message to Realm");
                        //new message
                        final String text = array.getString(5);
                        final int out = array.getInt(6);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mRealm.isClosed()){
                                    Log.i("TAG21", "Realm closed. Call another");
                                    mRealm = Realm.getDefaultInstance();
                                }
                                Message message = mRealm
                                        .where(Message.class).equalTo("id", messageId).findFirst();
                                mRealm.beginTransaction();
                                if(message == null){
                                    message = mRealm.createObject(Message.class, messageId);
                                    //message.setId(messageId);
                                    message.setUser(userId);
                                    message.setTime(time);
                                    message.setText(text);
                                    message.setOut(out);
                                    message.setWasRead(false);
                                } else {
                                    message.setTime(time);
                                }
                                mRealm.commitTransaction();
                                updateList();
                            }
                        });

                        break;
                }
            }
            //newRequest();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG21", "JSON GET RESPONSE ERROR");
            //     newRequest();
        }

               /* JSONObject innerErrorObject;
                try {
                    innerErrorObject = jsonObject.getJSONObject("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

    }

    private void newRequest() {
        Log.i("TAG21", "new request...");
        OkHttpClientFactory.getClient().newCall(request).enqueue(callback);
    }

}
