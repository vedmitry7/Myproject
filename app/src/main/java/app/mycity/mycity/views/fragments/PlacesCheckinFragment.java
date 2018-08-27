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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.adapters.CheckinRecyclerAdapter;
import app.mycity.mycity.views.adapters.PlacesCheckinRecyclerAdapter;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesCheckinFragment extends android.support.v4.app.Fragment {

    LinearLayoutManager mLayoutManager;

    @BindView(R.id.placeRecyclerAdapter)
    RecyclerView recyclerView;

    List<Post> postList;
    PlacesCheckinRecyclerAdapter adapter;
    Map profiles = new HashMap<Long, Profile>();

    boolean isLoading;
    int totalCount;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_checkin, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.addItemDecoration(new ImagesSpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        postList = new ArrayList<>();

        adapter = new PlacesCheckinRecyclerAdapter(postList);
       // adapter.setImageClickListener(this);
        recyclerView.setAdapter(adapter);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + postList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= postList.size()){
                            Log.d("TAG21", "load feed ");
                            loadMedia(postList.size());
                        }
                    }
                }
            }
        };
        loadMedia(postList.size());

        //super.onViewCreated(view, savedInstanceState);
    }

    private void loadMedia(int offset) {
        ApiFactory.getApi().getGroupWallById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "1", "checkin", "1").enqueue(new Callback<ResponseContainer<ResponseWall>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseWall>> call, Response<ResponseContainer<ResponseWall>> response) {


                if(response!=null && response.body().getResponse()!=null){
                    Log.d("TAG21", "RESPONSE FEED OK");

                    totalCount = response.body().getResponse().getCount();

                    postList.addAll(response.body().getResponse().getItems());

                    for (Profile p: response.body().getResponse().getProfiles()
                            ) {
                        profiles.put(p.getId(), p);
                    }

                    Log.d("TAG21", "post size - " + postList.size());

                } else {
                    Log.d("TAG21", "RESPONSE ERROR ");
                }

                adapter.update(postList);
             //   adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseWall>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
