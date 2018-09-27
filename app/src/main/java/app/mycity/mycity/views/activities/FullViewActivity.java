package app.mycity.mycity.views.activities;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Comment;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.ResponseAddComment;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.util.SharedManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullViewActivity extends AppCompatActivity {

    @BindView(R.id.addCommentEditTextFullView)
    EditText editText;

    @BindView(R.id.progressBar)
    ConstraintLayout progressBar;

    String postId;
    String ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        ButterKnife.bind(this);

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);


        String path = getIntent().getStringExtra("path");
        postId = getIntent().getStringExtra("postId");
        ownerId = getIntent().getStringExtra("ownerId");
        Log.d("TAG21", "Info - " + postId + " " + ownerId + " " + path);


        Picasso.get().load(path)
                .into(photoView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG21", "ssss");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                });
      //  photoView.setImageResource(R.drawable.image);

    }


    @OnClick(R.id.addCommentFullView)
    public void addComment(View v){
        Log.d("TAG21", "add comment " + postId + " " + ownerId);

        final String commentText = editText.getText().toString();
        editText.setText("");
        ApiFactory.getApi().addComment(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), postId, ownerId, commentText).enqueue(new Callback<ResponseContainer<ResponseAddComment>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseAddComment>> call, Response<ResponseContainer<ResponseAddComment>> response) {
                Log.d("TAG21", "add comrent response " );

                if(response!=null&&response.body().getResponse()!=null){
                    Log.d("TAG21", "comment ID - " + response.body().getResponse().getCommentId() );

                    Comment comment = new Comment();
                    comment.setText(commentText);
                    comment.setOwnerId(SharedManager.getProperty(Constants.KEY_MY_ID));
                    comment.setDate((int) (Calendar.getInstance().getTimeInMillis()/1000));
                    comment.setId(response.body().getResponse().getCommentId());
                    comment.setFromId(SharedManager.getProperty(Constants.KEY_MY_ID));
                    comment.setPostId(postId);
                    Likes likes = new Likes();
                    likes.setUserLikes(0);
                    likes.setCount(0);
                    comment.setLikes(likes);
                }

            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseAddComment>> call, Throwable t) {
                Log.d("TAG21", "add comrent fail " );
            }
        });
    }
}
