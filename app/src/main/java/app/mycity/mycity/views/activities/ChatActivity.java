package app.mycity.mycity.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.ResponseMarkAsRead;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.OkHttpClientFactory;
import app.mycity.mycity.api.model.Message;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.SendMessageResponse;
import app.mycity.mycity.views.adapters.ChatRecyclerAdapter;
import app.mycity.mycity.views.fragments.profile.SomeoneProfileFragment;
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

    @BindView(R.id.newMessageIndicator)
    CardView newMessageIndicator;

    final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

    ChatRecyclerAdapter adapter;

    private Realm mRealm;

    Callback callback;
    Request request;

    RealmResults<Message> results;

    long userId = 1;
    public static String imageUrl;

    long lastMyMessageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
        // initSocket();
        userId = getIntent().getLongExtra("user_id", 0);
        imageUrl = getIntent().getStringExtra("image");

        if(getIntent().getStringExtra("image")==null){
            imageUrl = "https://wmpics.pics/di-CTC8.jpg";
        }

        SharedManager.addProperty("unread_" + userId, "0");

        String name =  getIntent().getStringExtra("name");

        if(getIntent().getStringExtra("name")==null){
            name = "hui";
        }

        Log.d("TAG21", "Chat open - " + name + userId + imageUrl);

        if(name!=null)
            nameText.setText(name);

        Picasso.get().load(imageUrl).into(imageView);

        mRealm.beginTransaction();
        results = mRealm.where(Message.class).equalTo("user", userId)
                .findAll();
        Log.d("TAG25", "Update chat List. Size - " + results.size());
        mRealm.commitTransaction();


        Log.d("TAG21", "Size - " + results.size());

        adapter = new ChatRecyclerAdapter(results);

     //   layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int lastVisibleItems = mLinearLayoutManager.findLastVisibleItemPosition();
                Log.d("chat22", "total " + (totalItemCount));
                Log.d("chat22", "last visible " + (lastVisibleItems));
                Log.d("chat22", "get " + (lastVisibleItems));
                Log.d("chat22", "message " + results.get(lastVisibleItems).getText() + " my - " + results.get(lastVisibleItems).getOut() + " was read " + results.get(lastVisibleItems).isWasRead());

                if(results.get(lastVisibleItems).getOut()==1){
                    lastMyMessageId = results.get(lastVisibleItems).getId();
                }

                if(lastVisibleItems==totalItemCount-1){
                    Log.d("TAG25", "scroll is down");
                    if(results.get(lastVisibleItems).getOut()==0 && !results.get(lastVisibleItems).isWasRead()){
                        if(newMessageIndicator.getVisibility() == View.VISIBLE){
                            newMessageIndicator.setVisibility(View.GONE);
                        }
                       markAsRead();
                    }
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        newMessageIndicator.setVisibility(View.GONE);
        newMessageIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recyclerView.scrollToPosition(messages.size());
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                newMessageIndicator.setVisibility(View.GONE);
            }
        });
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(results.size()-1);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UpdateChat event){
        Log.d("TAG25", "Chat update Activity");
        updateList();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.chatBackButtonContainer)
    public void back(View v){
        this.finish();
    }

    void updateList(){
        Log.d("TAG25", "||||||||| notifyDataSetChanged" );
        Log.d("TAG25", "list - " + results.size());

        //not necessary action! it update on fly
        mRealm.beginTransaction();
        results = mRealm.where(Message.class).equalTo("user", userId)
                .findAll();
        Log.d("TAG25", "Update chat List. Size - " + results.size());
        mRealm.commitTransaction();

      //  adapter.notifyDataSetChanged();
      //  recyclerView.scrollToPosition(results.size()-1);

        int totalItemCount = mLinearLayoutManager.getItemCount();
        int lastVisibleItems = mLinearLayoutManager.findLastVisibleItemPosition();

        Log.d("TAG25", "total - " + totalItemCount);
        Log.d("TAG25", "last pos+1- " + lastVisibleItems);
        Log.d("TAG25", "list - " + results.size());

        if(adapter!=null){
            adapter.notifyDataSetChanged();

            if(totalItemCount-lastVisibleItems<=2){
                    recyclerView.scrollToPosition(results.size() - 1);
                    Toast.makeText(this, "Scroll down", Toast.LENGTH_SHORT).show();
            } else {
                //scroll if message is not our
                if(results.get(results.size()-1).getOut()!=1){
                  //  recyclerView.scrollToPosition(lastVisibleItems);
                    newMessageIndicator.setVisibility(View.VISIBLE);
                }

            }

        }

        // markAsRead();
    }

    private void markAsRead() {
        Log.d("TAG25", "Mark as READ");
        ApiFactory.getApi().markAsRead(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId).enqueue(new retrofit2.Callback<ResponseContainer<ResponseMarkAsRead>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponseMarkAsRead>> call, retrofit2.Response<ResponseContainer<ResponseMarkAsRead>> response) {
                Log.d("TAG21", "Mark SOME response ");
                if(response!=null && response.isSuccessful()){
                    Log.d("TAG25", "Mark response - success");

                    RealmResults<Message> results = mRealm.where(Message.class)
                            .equalTo("user", userId)
                            .greaterThan("id", lastMyMessageId)
                            .findAll();

                    mRealm.beginTransaction();
                    for (Message m : results
                            ) {
                        Log.d("TAG25", m.getText() + " - was read");
                        m.setWasRead(true);
                    }
                    mRealm.commitTransaction();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponseMarkAsRead>> call, Throwable t) {
                Log.d("TAG21", "Mark failure - " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        mRealm.close();
        Log.d("TAG21", "Realm close");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("TAG21", "NEW INTENT!@!!!!!!!!!!!!!!");
        super.onNewIntent(intent);
    }

    @OnClick(R.id.sendMessageButton)
    public void sendMessage(View v){

        final String messageText = editText.getText().toString();
        editText.setText("");


        String id = SharedManager.getProperty("virtualId");
        if(id==null||id.equals("")){
            id = "1000000";
            SharedManager.addProperty("virtualId", id);
        }
        long longId = Long.parseLong(id);

        final long curId = longId;

        mRealm.beginTransaction();
        final Message message = mRealm.createObject(Message.class, longId);
            //message.setId(messageId);
            message.setUser(userId);
            message.setTime(23423423);
            message.setText("create before r - " + messageText);
            message.setOut(1);
            message.setWasRead(false);
        mRealm.commitTransaction();
        updateList();

        longId++;
        SharedManager.addProperty("virtualId", String.valueOf(longId));

        if(!messageText.equals(""))
        ApiFactory.getApi().sendMessage(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId, 0, messageText).enqueue(new retrofit2.Callback<ResponseContainer<SendMessageResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<SendMessageResponse>> call, retrofit2.Response<ResponseContainer<SendMessageResponse>> response) {
                final SendMessageResponse sendMessageResponse = response.body().getResponse();
                Log.d("TAG25", "RESULT " + sendMessageResponse.getMessageId() + " " + sendMessageResponse.getSuccess() );


                mRealm.beginTransaction();
                Message result = mRealm.where(Message.class).equalTo("id", curId).findFirst();
                Log.d("TAG25", "delete old - " + result.getText() + ", create new" );
                result.deleteFromRealm();

                Message message = mRealm.createObject(Message.class, response.body().getResponse().getMessageId());
                //message.setId(messageId);
                message.setUser(userId);
                message.setTime(23423423);
                message.setText("create from r - " + messageText);
                message.setOut(1);
                message.setWasRead(false);
                mRealm.commitTransaction();
                updateList();
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<SendMessageResponse>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.updateChat)
    public void updateChat(View v){
        Log.d("TAG21", "update chat...");

        recyclerView.scrollToPosition(results.size()-1);

      //  adapter.notifyDataSetChanged();

        //   EventBus.getDefault().post(new EventBusMessages.UpdateSocketConnection());


       // markAsRead();
      //  getChats();
    }

    private void newRequest() {
        Log.i("TAG21", "new request...");
        OkHttpClientFactory.getClient().newCall(request).enqueue(callback);
    }

}
