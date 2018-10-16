package app.mycity.mycity.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Comment;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseAddComment;
import app.mycity.mycity.api.model.ResponseComments;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseDeleteComment;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.adapters.CommentsRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsFragment extends android.support.v4.app.Fragment implements CommentsRecyclerAdapter.CommnentClickListener, TabStacker.TabStackInterface {


    @BindView(R.id.commentsFragmentRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.commentPhoto)
    CircleImageView photo;

    @BindView(R.id.addCommentEditText)
    EditText editText;

    CommentsRecyclerAdapter adapter;

    List<Comment> commentList;
    Map profiles = new HashMap<Long, Profile>();

    @BindView(R.id.commentsPlaceHolder)
    ConstraintLayout placeHolder;


    @BindView(R.id.addCommentProgress)
    ProgressBar progressBar;

    boolean isLoading;

    int totalCount;

    String postId = "46";
    String ownerId = "45";
    private String commentText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_comment_activity, container, false);
        postId = getArguments().getString("postId");
        ownerId = getArguments().getString("ownerId");
        Log.d("TAG21", "Comment postId - " + postId);
        Log.d("TAG21", "Comment postId - " + ownerId);
        ButterKnife.bind(this, view);
        return view;
    }


    public static CommentsFragment createInstance(String fragmentId, int tabPos, String postId, String ownerId, String type) {
        CommentsFragment fragment = new  CommentsFragment();
        Log.i("TAG21", "Create Comment Fragment " + fragmentId);
        Bundle bundle = new Bundle();
        bundle.putString("name", fragmentId);
        bundle.putInt("tabPos", tabPos);
        bundle.putString("postId", postId);
        bundle.putString("ownerId", ownerId);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.profileFragBackButtonContainer)
    public void backButton(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commentList = new ArrayList<>();

        adapter = new CommentsRecyclerAdapter(commentList, profiles);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

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
        adapter.setCommentClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        loadComments(commentList.size());
    }

    private void loadComments(final int offset) {

        Callback callback = new Callback<ResponseContainer<ResponseComments>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseComments>> call, Response<ResponseContainer<ResponseComments>> response) {
                Log.d("TAG21", "COMMENTS RESPONSE ");

                if(response!=null&&response.body().getResponse()!=null){
                    Log.d("TAG21", response.body().getResponse().getItems().size() + " Comment size");
                    isLoading = false;
                    totalCount = response.body().getResponse().getCount();
                    commentList.addAll(response.body().getResponse().getItems());
                    Log.d("TAG21", commentList.size() + " Comments size, count = " + totalCount);

                    if(response.body().getResponse().getProfiles()!=null){
                        for (Profile p: response.body().getResponse().getProfiles()
                                ) {
                            profiles.put(p.getId(), p);
                            Log.d("TAG21", "ADD ONE " + p.getFirstName() + " " + p.getLastName());
                            Log.d("TAG21", "ADD ONE " + p.toString());

                        }
                    }
                    adapter.update(commentList, profiles);
                    if(commentList.size()==0){
                        placeHolder.setVisibility(View.VISIBLE);
                    } else {
                        placeHolder.setVisibility(View.GONE);
                    }

                    if(offset==0){
                        recyclerView.scrollToPosition(0);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseContainer<ResponseComments>> call, Throwable t) {

            }
        };

        switch (getArguments().getString("type")){

            case "post":

                ApiFactory.getApi().getCommentsPost(
                        SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                        "1",
                        postId,
                        ownerId,
                        offset,
                        "1",
                        20,
                        "photo_130").enqueue(callback);
                break;

            case "photo":

                ApiFactory.getApi().getCommentsPost(
                        SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                        "1",
                        postId,
                        ownerId,
                        offset,
                        "1",
                        20,
                        "photo_130").enqueue(callback);
                break;
        }


    }

    @OnClick(R.id.addComment)
    public void addComment(View v){
        progressBar.setVisibility(View.VISIBLE);
        commentText = editText.getText().toString();
        editText.setText("");

        Callback callback = new Callback<ResponseContainer<ResponseAddComment>>() {
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
                    progressBar.setVisibility(View.GONE);
                    commentList.add(0, comment);
                    adapter.notifyDataSetChanged();

                    placeHolder.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseAddComment>> call, Throwable t) {
                Log.d("TAG21", "add comrent fail " );
            }
        };

        switch (getArguments().getString("type")){

            case "post":
                Log.d("TAG21", "post photoId - " + postId + " owner id - " + ownerId );
                ApiFactory.getApi().addComment(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), postId, ownerId, commentText).enqueue(callback);
                break;

            case "photo":
                Log.d("TAG21", "photo photoId - " + postId + " owner id - " + ownerId );
                ApiFactory.getApi().addCommentPhoto(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), postId, ownerId, commentText).enqueue(callback);
                break;
        }
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
    public void deleteComment(final int position) {
        Log.d("TAG21", "delete comments in act");
        Callback callback = new Callback<ResponseContainer<ResponseDeleteComment>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseDeleteComment>> call, Response<ResponseContainer<ResponseDeleteComment>> response) {
                if(response!=null&&response.body().getResponse() != null && response.body().getResponse().getSuccess())
                    commentList.remove(position);
                adapter.notifyItemRemoved(position);
            }
            @Override
            public void onFailure(Call<ResponseContainer<ResponseDeleteComment>> call, Throwable t) {
            }
        };

        switch (getArguments().getString("type")){
            case "post":
                Log.d("TAG21", "del post photoId - " + postId + " owner id - " + ownerId );
                ApiFactory.getApi().deleteComment(
                        SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                        commentList.get(position).getId(),
                        ownerId).enqueue(callback);
                break;
            case "photo":
                Log.d("TAG21", "del photo photoId - " + postId + " owner id - " + ownerId );
                ApiFactory.getApi().deleteCommentPhoto(
                        SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                        commentList.get(position).getId(),
                        ownerId).enqueue(callback);
                break;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {

    }

    @Override
    public View onSaveTabFragmentInstance(Bundle bundle) {
        return null;
    }

    @Override
    public void onRestoreTabFragmentInstance(Bundle bundle) {

    }
}
