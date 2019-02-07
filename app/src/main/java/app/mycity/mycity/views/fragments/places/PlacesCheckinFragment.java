package app.mycity.mycity.views.fragments.places;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.PlacesCheckinRecyclerAdapter;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import app.mycity.mycity.views.fragments.NotificationsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesCheckinFragment extends android.support.v4.app.Fragment {

    LinearLayoutManager mLayoutManager;

    @BindView(R.id.placesFragmentCheckinRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.placeCheckinPlaceHolder)
    ConstraintLayout placeCheckinPlaceHolder;

    @BindView(R.id.checkinCount)
    TextView checkinCount;

    List<Post> postList;
    PlacesCheckinRecyclerAdapter adapter;
    Map profiles = new HashMap<Long, Profile>();


    String placeId;

    boolean isLoading;
    int totalCount;

    Storage storage;
    private boolean mayRestore;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public static PlacesCheckinFragment createInstance(String name, String placeId) {
        PlacesCheckinFragment fragment = new PlacesCheckinFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("placeId", placeId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_checkin, container, false);

        placeId = getArguments().getString("placeId");

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.addItemDecoration(new ImagesSpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false));
        recyclerView.setLayoutManager(mLayoutManager);
      //  recyclerView.setNestedScrollingEnabled(false);

        adapter = new PlacesCheckinRecyclerAdapter(postList);
       // adapter.setImageClickListener(this);
        recyclerView.setAdapter(adapter);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

                Log.d("TAG23", "last visible item - " + lastVisibleItems);
                Log.d("TAG23", "total - " + totalItemCount);

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG23", "ЗАГРУЗКА ДАННЫХ " + postList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= postList.size()){
                            Log.d("TAG23", "load feed ");
                            loadMedia(postList.size());
                        }
                    }
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        loadMedia(postList.size());


        //super.onViewCreated(view, savedInstanceState);
    }

    private void loadMedia(final int offset) {

        if(mayRestore){
            Log.d("TAG21", "RESTORE PLACE CHECKIN - " + placeId);
            if(postList.size()==0){
                placeCheckinPlaceHolder.setVisibility(View.VISIBLE);
            }
            adapter.update(postList);
        } else {
            Log.d("TAG21", "Can not RESTORE PLACE CHECKIN " + placeId);
            ApiFactory.getApi().getGroupWallById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), placeId, "checkin", "1","photo_360", offset, 100).enqueue(new Callback<ResponseContainer<ResponseWall>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseWall>> call, Response<ResponseContainer<ResponseWall>> response) {


                    if(response.body().getResponse()!=null){
                        Log.d("TAG21", "RESPONSE FEED OK");
                        Log.d("TAG23", "RESPONSE FEED OK");

                        totalCount = response.body().getResponse().getCount();

                        checkinCount.setText("Чекинов (" + totalCount + ")");

                        if(totalCount == 0){
                            placeCheckinPlaceHolder.setVisibility(View.VISIBLE);
                        }

                        isLoading = false;

                        postList.addAll(response.body().getResponse().getItems());

                        if(response.body().getResponse().getProfiles()!=null
                                )
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

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        storage = (Storage) context;

        postList = (List<Post>) storage.getDate(getArguments().get("name")+ "_postList");
        profiles = (Map) storage.getDate(getArguments().get("name")+ "_profiles");

        if(postList==null){
            Log.d("TAG21", "restore null");
            postList = new ArrayList<>();
            profiles = new HashMap();
        } else {
            Log.d("TAG21", "restore ok - " + postList.size());
            mayRestore = true;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void placeContent(EventBusMessages.OpenPlaceContent event) {
        EventBus.getDefault().post(new EventBusMessages.OpenPlacePhoto(
                placeId, event.getPostId())
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.d("TAG21", "Stop CHECKIN FRAGMENT Save " + getArguments().getString("name"));
        Log.d("TAG21", "!!!!!!!!!!  Save " + getArguments().get("name") + "_postList");
        Log.d("TAG21", "!!!!!!!!!!  Save " + getArguments().get("name") + "_profiles");
        storage.setDate(getArguments().get("name") + "_postList", postList);
        storage.setDate(getArguments().get("name") + "_profiles", profiles);
    }
}
