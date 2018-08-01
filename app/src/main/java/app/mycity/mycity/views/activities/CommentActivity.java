package app.mycity.mycity.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Comment;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseAddComment;
import app.mycity.mycity.api.model.ResponseComments;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.adapters.CommentsRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {


    @BindView(R.id.commentsFragmentRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.commentPhoto)
    CircleImageView photo;

    @BindView(R.id.addCommentEditText)
    EditText editText;

    CommentsRecyclerAdapter adapter;

    List<Comment> commentList;
    Map profiles = new HashMap<Long, Profile>();

    boolean isLoading;

    int totalCount;

    String postId = "46";
    String ownerId = "45";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postId = getIntent().getStringExtra("postId");
        ownerId = getIntent().getStringExtra("ownerId");
        Log.d("TAG21", "Comment postId - " + postId);
        Log.d("TAG21", "Comment postId - " + ownerId);
        ButterKnife.bind(this);

        commentList = new ArrayList<>();

        adapter = new CommentsRecyclerAdapter(commentList, profiles);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);


        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + commentList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= commentList.size()){
                            Log.d("TAG21", "load comments ");
                            loadComments(commentList.size());
                        }
                    }
                }
            }
        };

        if(SharedManager.getProperty(Constants.KEY_PHOTO_130)!=null){
            Picasso.get().load(SharedManager.getProperty(Constants.KEY_PHOTO_130)).into(photo);
            Log.d("TAG21", "SharedManager.getProperty(Constants.KEY_PHOTO_130) NUUUUUUUUUUUUL ");
        } else {
            Log.d("TAG21", SharedManager.getProperty(Constants.KEY_PHOTO_130) );

        }

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        loadComments(commentList.size());
    }


    private void loadComments(int offset) {

        ApiFactory.getApi().getComment(
                SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                "1",
                postId,
                ownerId,
                offset,
                "1",
                20,
                "photo_130").enqueue(new Callback<ResponseContainer<ResponseComments>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseComments>> call, Response<ResponseContainer<ResponseComments>> response) {
                Log.d("TAG21", "COMMENTS RESPONSE ");

                if(response!=null&&response.body().getResponse()!=null){
                    Log.d("TAG21", response.body().getResponse().getItems().size() + " Comment size");

                    commentList = response.body().getResponse().getItems();

                    if(response.body().getResponse().getProfiles()!=null){
                        for (Profile p: response.body().getResponse().getProfiles()
                                ) {
                            profiles.put(p.getId(), p);
                            Log.d("TAG21", "ADD ONE " + p.getFirstName() + " " + p.getLastName());
                        }
                    }
                    adapter.update(commentList, profiles);
                    recyclerView.scrollToPosition(0);
                }

            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseComments>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.addComment)
    public void addComment(View v){
        ApiFactory.getApi().addComment(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), postId, ownerId, editText.getText().toString()).enqueue(new Callback<ResponseContainer<ResponseAddComment>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseAddComment>> call, Response<ResponseContainer<ResponseAddComment>> response) {
                Log.d("TAG21", "add comrent response " );

            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseAddComment>> call, Throwable t) {
                Log.d("TAG21", "add comrent fail " );
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.LikeComment event) {
        Log.d("TAG21", "Like " + event.getItemId());

        if (commentList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "comment",
                    commentList.get(event.getAdapterPosition()).getId().toString(),
                    commentList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        commentList.get(event.getAdapterPosition()).getLikes().setCount(response.body().getResponse().getLikes());
                        commentList.get(event.getAdapterPosition()).getLikes().setUserLikes(0);
                        adapter.notifyItemChanged(event.getAdapterPosition());

                    }

                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (commentList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 0) {
            Log.d("TAG21", "Like");
            ApiFactory.getApi().like(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "comment",
                    commentList.get(event.getAdapterPosition()).getId().toString(),
                    commentList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        commentList.get(event.getAdapterPosition()).getLikes().setCount(response.body().getResponse().getLikes());
                        commentList.get(event.getAdapterPosition()).getLikes().setUserLikes(1);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }
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

}
