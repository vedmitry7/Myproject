package app.mycity.mycity.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.OkHttpClientFactory;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.adapters.CheckinRecyclerAdapter;
import app.mycity.mycity.views.adapters.FeedRecyclerAdapter;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.arnaudguyon.tabstacker.TabStacker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedFragment extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface {


    @BindView(R.id.feedFragmentRecyclerView)
    RecyclerView recyclerView;

    FeedRecyclerAdapter adapter;

    List<Post> postList;
    Map profiles = new HashMap<Long, Profile>();

    boolean isLoading;

    int totalCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        postList = new ArrayList<>();
        adapter = new FeedRecyclerAdapter(postList, profiles);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + postList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount > postList.size()){
                            Log.d("TAG21", "load feed ");
                            loadFeed(postList.size());
                        }
                    }
                }
            }
        };

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        loadFeed(postList.size());
    }

    private void loadFeed(int offset) {

        ApiFactory.getApi().getFeed(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "1", offset, "photo_130").enqueue(new retrofit2.Callback<ResponseContainer<ResponseWall>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponseWall>> call, retrofit2.Response<ResponseContainer<ResponseWall>> response) {

                if(response!=null && response.body().getResponse()!=null){
                    Log.d("TAG21", "RESPONSE FEED OK");

                    totalCount = response.body().getResponse().getCount();

                    postList.addAll(response.body().getResponse().getItems());

                    for (Profile p: response.body().getResponse().getProfiles()
                            ) {
                        profiles.put(p.getId(), p);
                    }

                    Log.d("TAG21", "post size - " + postList.size() + " total - " + totalCount);
                    isLoading = false;

                } else {
                    Log.d("TAG21", "RESPONSE ERROR ");
                }

               adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponseWall>> call, Throwable t) {

            }
        });

        /*RequestBody requestBody = new FormBody.Builder()
                .add("access_token", SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN))
                .add("extended", "1")
                .build();

        final Request request = new Request.Builder().url("http://192.168.0.104/api/feed.get")
                .post(requestBody)
                .build();

        OkHttpClientFactory.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG21", "fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TAG21", response.toString());
            }
        });*/

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.LikePost event) {
        Log.d("TAG21", "Like " + event.getItemId());

        if (postList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        postList.get(event.getAdapterPosition()).getLikes().setCount(response.body().getResponse().getLikes());
                        postList.get(event.getAdapterPosition()).getLikes().setUserLikes(0);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }

                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (postList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 0) {
            Log.d("TAG21", "Like");
            ApiFactory.getApi().like(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        postList.get(event.getAdapterPosition()).getLikes().setCount(response.body().getResponse().getLikes());
                        postList.get(event.getAdapterPosition()).getLikes().setUserLikes(1);
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
        EventBus.getDefault().unregister(this);
        super.onStop();
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
