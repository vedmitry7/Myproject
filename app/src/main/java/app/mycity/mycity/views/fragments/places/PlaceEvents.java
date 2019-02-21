package app.mycity.mycity.views.fragments.places;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponseVisit;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.PlacesEventRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceEvents extends android.support.v4.app.Fragment{


    @BindView(R.id.placeEventsFragmentRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.placeEventsPlaceHolder)
    RelativeLayout placeHolderNoEvents;

    @BindView(R.id.placeHolderInfo)
    TextView placeHolderInfo;

    PlacesEventRecyclerAdapter adapter;

    List<Post> postList;
    HashMap<String, Group> groups = new HashMap<String, Group>();

    boolean isLoading;
    int totalCount;

    Storage storage;

    boolean mayRestore;
    private int position;

    LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_events_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static PlaceEvents createInstance(String name, String placeId) {
        PlaceEvents fragment = new PlaceEvents();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("placeId", placeId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new PlacesEventRecyclerAdapter(postList, groups);

        layoutManager = new LinearLayoutManager(getActivity());

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

        if(mayRestore){
            adapter.update(postList, groups);
            recyclerView.scrollToPosition(position);
        } else {
            ApiFactory.getApi().getEvents(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), getArguments().getString("placeId"), "1", offset).enqueue(new retrofit2.Callback<ResponseContainer<ResponseWall>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseWall>> call, retrofit2.Response<ResponseContainer<ResponseWall>> response) {

                    if(response!=null && response.body().getResponse()!=null){
                        Log.d("TAG21", "RESPONSE Events OK");

                        if(response.body().getResponse().getCount()==0){
                            placeHolderInfo.setText("Событий пока не намечается");
                            placeHolderNoEvents.setVisibility(View.VISIBLE);
                        } else {
                            placeHolderNoEvents.setVisibility(View.GONE);
                        }

                        totalCount = response.body().getResponse().getCount();
                        if(totalCount>0){
                            postList.addAll(response.body().getResponse().getItems());
                            for (Group g: response.body().getResponse().getGroups()
                                    ) {
                                groups.put(g.getId(), g);
                            }
                        }

                        Log.d("TAG21", "Events size - " + postList.size() + " total - " + totalCount);
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
        }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.LikePost event) {
        Log.d("TAG21", "Like itemId - " + event.getItemId() + " owner - " +  postList.get(event.getAdapterPosition()).getOwnerId().toString());

        if (postList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "event",
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
                    "event",
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void visit(final EventBusMessages.AddVisitor event) {
        if (postList.get(event.getAdapterPosition()).getVisits().getUserVisit() == 1) {
            Log.d("TAG21", "unvisit");
            ApiFactory.getApi().removeVisit(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseVisit>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseVisit>> call, retrofit2.Response<ResponseContainer<ResponseVisit>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getVisitors());
                    if (response != null && response.body() != null) {
                        postList.get(event.getAdapterPosition()).getVisits().setCount(response.body().getResponse().getVisitors());
                        postList.get(event.getAdapterPosition()).getVisits().setUserVisit(0);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }

                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseVisit>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (postList.get(event.getAdapterPosition()).getVisits().getUserVisit() == 0) {
            Log.d("TAG21", "Visit");
            ApiFactory.getApi().addVisit(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseVisit>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseVisit>> call, retrofit2.Response<ResponseContainer<ResponseVisit>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getVisitors());
                    if (response != null && response.body() != null) {
                        postList.get(event.getAdapterPosition()).getVisits().setCount(response.body().getResponse().getVisitors());
                        postList.get(event.getAdapterPosition()).getVisits().setUserVisit(1);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseVisit>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        storage = (Storage) context;

        postList = (List<Post>) storage.getDate(getArguments().get("name")+ "_eventsPostList");
        groups = (HashMap<String, Group>) storage.getDate(getArguments().get("name")+ "_eventsGroups");


        if(postList==null){
            Log.d("TAG21", "restore null");
            postList = new ArrayList<>();
            groups = new HashMap();
        } else {
            Log.d("TAG21", "restore ok - " + postList.size());
            position = (int) storage.getDate(getArguments().get("name") + "_eventsScrollPosition");
            mayRestore = true;
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
        Log.d("TAG21", "Stop EVENTS FRAGMENT Save " + getArguments().getString("name"));
        storage.setDate(getArguments().get("name") + "_eventsPostList", postList);
        storage.setDate(getArguments().get("name") + "_eventsGroups", groups);
        storage.setDate(getArguments().get("name") + "_eventsScrollPosition", layoutManager.findFirstVisibleItemPosition());

        super.onStop();
    }

}
