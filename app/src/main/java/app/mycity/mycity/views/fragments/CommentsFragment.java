package app.mycity.mycity.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Comment;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseComments;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.adapters.CommentsRecyclerAdapter;
import app.mycity.mycity.views.adapters.FeedRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsFragment extends android.support.v4.app.Fragment {


    @BindView(R.id.commentsFragmentRecyclerView)
    RecyclerView recyclerView;

    CommentsRecyclerAdapter adapter;

    List<Comment> commentList;
    Map profiles = new HashMap<Long, Profile>();

    boolean isLoading;

    int totalCount;

    String postId = "46";
    String ownerId = "45";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments_fragment, container, false);
        postId = getArguments().getString("postId");
        ownerId = getArguments().getString("ownerId");
        Log.d("TAG21", "Comment postId - " + postId);
        Log.d("TAG21", "Comment postId - " + ownerId);
        ButterKnife.bind(this, view);
        return view;
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
                }

            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseComments>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
