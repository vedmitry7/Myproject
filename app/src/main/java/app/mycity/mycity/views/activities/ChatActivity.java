package app.mycity.mycity.views.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Message;
import app.mycity.mycity.api.model.MessageFromApi;
import app.mycity.mycity.api.model.MessageResponse;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseMarkAsRead;
import app.mycity.mycity.api.model.SendMessageResponse;
import app.mycity.mycity.api.model.SuccessResponceNumber;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.adapters.ChatRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chatRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.chatEditText)
    EmojiEditText editText;

    @BindView(R.id.chatProfileImage)
    CircleImageView imageView;

    @BindView(R.id.chatName)
    TextView nameText;

    @BindView(R.id.newMessageIndicator)
    CardView newMessageIndicator;

    @BindView(R.id.dateIndicator)
    CardView dateIndicator;

    @BindView(R.id.dateIndicatorLabel)
    TextView dateIndicatorLabel;

    @BindView(R.id.placesProgressBar)
    ConstraintLayout progressBar;

    @BindView(R.id.chatRootView)
    ConstraintLayout chatRootView;

    @BindView(R.id.change)
    ImageView changeView;

    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    ChatRecyclerAdapter adapter;

    int totalCount;

    private Realm mRealm;

    long lastMyMessageId;

    EmojiPopup emojiPopup;


    List<Message> results = new ArrayList<>();
    String userId;
    public static String imageUrl = "";
    private boolean isLoading;

    @OnClick(R.id.change)
    public void change(View v){
        if(emojiPopup.isShowing()){
            changeView.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_outline));
            emojiPopup.dismiss();
        } else {
            changeView.setImageDrawable(getResources().getDrawable(R.drawable.ic_emoticon_outline));
            emojiPopup.toggle();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        emojiPopup = EmojiPopup.Builder.fromRootView(chatRootView).build(editText);
       // emojiPopup.toggle(); // Toggles visibility of the Popup.
     /*   emojiPopup.dismiss(); // Dismisses the Popup.
        emojiPopup.isShowing();*/

        Log.i("TAG25", "CHAT onCREATE" );

        mRealm = Realm.getDefaultInstance();

        userId = getIntent().getStringExtra("user_id");
        imageUrl = getIntent().getStringExtra("image");

        http://192.168.0.104/src/u4/41342266018b1fe8aa054b4e5f2ec462/o_7e46ed0986.jpg

        Log.i("TAG25", "CHAT Photo130() - " + imageUrl);

        SharedManager.addProperty("unread_" + userId, "0");

        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(Integer.parseInt(userId));

        adapter = new ChatRecyclerAdapter(results);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);


        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("TAG25", "Focus - " + hasFocus);
            }
        });

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG25", "ЗАГРУЗКА ДАННЫХ " + results.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount > results.size()){
                            Log.d("TAG25", "load more ");
                            loadMessages(results.size());
                        }
                    }
                }
            }
        };

        RecyclerView.OnScrollListener scrollListener2 = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                Log.d("chat22", "total " + (totalItemCount));
                Log.d("chat22", "last visible " + (lastVisibleItems));
                Log.d("chat22", "first visible " + (firstVisibleItem));
                Log.d("chat22", "get " + (lastVisibleItems));


// error
                if(results.size()!=0)  {
                    Log.d("chat22", "message " + results.get(firstVisibleItem).getText() + " my - " + results.get(firstVisibleItem).getOut() + " was read " + results.get(firstVisibleItem).isWasRead());
                    dateIndicatorLabel.setText(Util.getDate_ddMMyyyy(results.get(lastVisibleItems).getTime()));
                }


                if(results.get(firstVisibleItem).getOut()==1){
                    lastMyMessageId = results.get(firstVisibleItem).getId();
                }

                    Log.d("chat22", "scroll is down");
                    if(results.get(firstVisibleItem).getOut()==0 && !results.get(firstVisibleItem).isWasRead()){
                        if(newMessageIndicator.getVisibility() == View.VISIBLE){
                            newMessageIndicator.setVisibility(View.GONE);
                        }
                        //markAsRead();
                        Log.d("chat22", "MARk as read message " + results.get(firstVisibleItem).getText() +  "id  " + results.get(firstVisibleItem).getId());
                        markAsReadMessage(results.get(firstVisibleItem).getId());
                        results.get(firstVisibleItem).setWasRead(true);
                    } else {
                        Log.d("chat22", "not my or already read " + results.get(firstVisibleItem).isWasRead());
                    }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.addOnScrollListener(scrollListener2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        newMessageIndicator.setVisibility(View.GONE);
        newMessageIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recyclerView.scrollToPosition(messages.size());
               // recyclerView.smoothScrollToPosition(0);
                recyclerView.scrollToPosition(0);
                newMessageIndicator.setVisibility(View.GONE);
            }
        });
        //adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);

        loadMessages(0);
    }


    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void loadMessages(int offset) {
        Log.d("TAG25", "Try to load. Offset - " + offset);

        ApiFactory.getApi().getMessages(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId, offset).enqueue(new retrofit2.Callback<ResponseContainer<MessageResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<MessageResponse>> call, Response<ResponseContainer<MessageResponse>> response) {


                if(response.body()!=null && response.body().getResponse()!=null){
                    Log.d("TAG25", "Messages size - " + response.body().getResponse().getCount());
                    if(response.body().getResponse().getCount()==0){
                        dateIndicator.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Log.d("TAG25", "pr g");
                        return;
                    }

                    nameText.setText(response.body().getResponse().getProfiles().get(1).getFirstName() + " " +
                            response.body().getResponse().getProfiles().get(1).getLastName());

                    Picasso.get().load(response.body().getResponse().getProfiles().get(1).getPhoto130()).into(imageView);

                    adapter.setImageUrl(response.body().getResponse().getProfiles().get(1).getPhoto130());

                    totalCount = response.body().getResponse().getCount();
                    dateIndicator.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);


                    for (MessageFromApi m: response.body().getResponse().getItems()){
                        Message message = new Message();
                        message.setOut(m.getOut());
                        message.setText(m.getText());
                        message.setId(m.getId());
                        message.setTime(m.getDate());
                        message.setWasSended(true);
                        message.setWasRead(m.getRead()==1);
                        Log.d("TAG25", "Message text - " + message.getText());
                        results.add(message);
                    }
                    isLoading = false;
                    adapter.update(results);
                    if(results.size() == response.body().getResponse().getItems().size()){
                        recyclerView.scrollToPosition(0);
                    }
                    markAsRead();
                } else {
                    Log.d("TAG25", "SOMETHING NULL ");
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<MessageResponse>> call, Throwable t) {

            }
        });
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UpdateChat event){
        Log.d("TAG25", "Chat update Activity");
        updateList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.NewChatMessage event){
        Log.d("TAG25", "Chat update Activity - " + event.getMessage().getUser());


        if(!String.valueOf(event.getMessage().getUser()).equals(userId))
            return;

        if(dateIndicator.getVisibility()==View.GONE){
            dateIndicator.setVisibility(View.VISIBLE);
        }

        if(event.getOut()==0){
            Log.d("TAG25", "NEW message from someone");
            //add message
            results.add(0, event.getMessage());
            adapter.notifyItemInserted(0);
            //scroll down or show indicator
            if(layoutManager.findFirstVisibleItemPosition()==0){
                recyclerView.scrollToPosition(0);
                markAsReadMessage(event.getMessage().getId());
            }
            else {
                newMessageIndicator.setVisibility(View.VISIBLE);
            }
        }

        if(event.getOut()==1) {
            Log.d("TAG25", "NEW message from ME - id - " + event.getMessage().getId());
            //change Time

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean exist = false;
                    for (int i = 0; i < results.size(); i++) {
                        if (results.get(i).getId() == event.getMessage().getId()) {
                            exist = true;
                            results.get(i).setTime(event.getMessage().getTime());
                            adapter.notifyItemChanged(i);
                            Log.d("TAG25", results.get(i) + " with text");
                            break;
                        }
                    }

                    if(!exist){
                        results.add(0, event.getMessage());
                        adapter.notifyItemInserted(0);
                        recyclerView.scrollToPosition(0);
                    }
                }
            }, 300);



        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.MessageWasRead event){
        Log.d("TAG25", "message was read update Activity");

    /*    for (Message m:results
             ) {
            if(m.getId()==event.getMessageId()){
                Log.d("TAG25", "found - " + m.getText());
                m.setWasRead(true);
            }
        }
        adapter.notifyDataSetChanged();*/

        for (int i = 0; i < results.size(); i++) {
            if(results.get(i).getId()==event.getMessageId()){
                results.get(i).setWasRead(true);
                adapter.notifyItemChanged(i);
                break;
            }
        }
        //adapter.update(results);
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

        synchronized (mRealm){
            mRealm.beginTransaction();
            Message realmMessage = mRealm.createObject(Message.class, longId);
            //message.setId(messageId);
            //realmMessage.setUser(userId);
            realmMessage.setTime(23423423);
            realmMessage.setText("create before r - " + messageText);
            realmMessage.setOut(1);
            realmMessage.setWasRead(false);
            mRealm.commitTransaction();
        }

        updateList();
        longId++;
        SharedManager.addProperty("virtualId", String.valueOf(longId));

        final Message message = new Message();
        message.setId(curId);
        //message.setUser(userId);
        message.setTime(System.currentTimeMillis()/1000);
        message.setText(messageText);
        message.setOut(1);
        message.setWasRead(false);


        results.add(0, message);
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);

        if(!messageText.equals(""))
            ApiFactory.getApi().sendMessage(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId, 0, messageText).enqueue(new retrofit2.Callback<ResponseContainer<SendMessageResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<SendMessageResponse>> call, retrofit2.Response<ResponseContainer<SendMessageResponse>> response) {
                    final SendMessageResponse sendMessageResponse = response.body().getResponse();
                    Log.d("TAG25", "RESULT " + sendMessageResponse.getMessageId() + " " + sendMessageResponse.getSuccess());


                    synchronized (mRealm){
                        mRealm.beginTransaction();
                        Message result = mRealm.where(Message.class).equalTo("id", curId).findFirst();
                        Log.d("TAG25", "delete old - " + result.getText() + ", create new" );
                        result.deleteFromRealm();
                        mRealm.commitTransaction();
                    }

                    for (Message m: results
                         ) {
                        if(m.getId()==curId){
                            m.setId(response.body().getResponse().getMessageId());
                            m.setWasSended(true);
                            Log.d("TAG25", "SET ID for response message");
                            m.setText(messageText);
                        }
                    }
                    //adapter.notifyDataSetChanged();
                    adapter.notifyItemChanged(0);
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<SendMessageResponse>> call, Throwable t) {

                }
            });
    }

    private void markAsRead() {
        Log.d("TAG25", "Mark as READ");
        Log.d("chat22", "Mark as READ");
        ApiFactory.getApi().markAsRead(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId).enqueue(new retrofit2.Callback<ResponseContainer<SuccessResponceNumber>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<SuccessResponceNumber>> call, retrofit2.Response<ResponseContainer<SuccessResponceNumber>> response) {
                Log.d("TAG21", "Mark SOME response ");
                if(response.body().getResponse().getSuccess()==1){



                    Log.d("TAG25", "Mark response - success");
                    Log.d("chat22", "no Mark all message as read");

                    for (Message m:results
                         ) {
                        //m.setWasRead(true);
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<SuccessResponceNumber>> call, Throwable t) {
                Log.d("TAG21", "Mark failure - " + t.getLocalizedMessage());
            }
        });
    }

        private void markAsReadMessage(final long messageId) {
            Log.d("TAG25", "Mark as READ");
            Log.d("chat22", "Mark as READ message - " + messageId);
            ApiFactory.getApi().markAsReadMessages(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), messageId).enqueue(new Callback<ResponseContainer<SuccessResponceNumber>>() {
                @Override
                public void onResponse(Call<ResponseContainer<SuccessResponceNumber>> call, Response<ResponseContainer<SuccessResponceNumber>> response) {
                    Log.d("chat22", "resp - " + messageId);
                    if(response.body().getResponse()!=null && response.body().getResponse().getSuccess()==1){
                        for (Message m:results
                             ) {
                            if(m.getId()==messageId){
                                Log.d("chat22", "Mark Special ! MESSAGE with id " + messageId + " as read");
                                m.setWasRead(true);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<SuccessResponceNumber>> call, Throwable t) {
                    Log.d("chat22", "fail - " + messageId);
                }
            });
        }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.DeleteChatMessage event){
        Log.d("TAG25", "message delete");

        ApiFactory.getApi().deleteMessages(App.accessToken(), event.getId()).enqueue(new Callback<ResponseContainer<SuccessResponceNumber>>() {
            @Override
            public void onResponse(Call<ResponseContainer<SuccessResponceNumber>> call, Response<ResponseContainer<SuccessResponceNumber>> response) {
                if(response.body()!=null && response.body().getResponse()!=null){
                    if(response.body().getResponse().getSuccess()==1){
                        for (int i = 0; i < results.size(); i++) {
                            if(results.get(i).getId() == event.getId()){
                                results.remove(i);
                                adapter.notifyItemRemoved(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<SuccessResponceNumber>> call, Throwable t) {

            }
        });
        //adapter.update(results);
    }

    private void updateList() {

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Log.d("TAG25", "Stop chat");
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        Log.d("TAG25", "Resume chat");
        super.onResume();
    }

    @OnClick(R.id.chatBackButtonContainer)
    public void back(View v){
        this.finish();
    }

    @Override
    public void onDestroy() {
        Log.d("TAG25", "chat destroy");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("TAG21", "NEW INTENT!@!!!!!!!!!!!!!!");
        super.onNewIntent(intent);
    }

    public String getCurrentChatUser() {
        return userId;
    }

}
