package app.mycity.mycity.views.fragments.places;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
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
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponsePlaces;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.PlacesRecyclerAdapter;
import app.mycity.mycity.views.adapters.PlacesTopBarAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class PlacesFragment extends Fragment implements TabStacker.TabStackInterface {


    @BindView(R.id.placesFragmentRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.horizontalRecyclerView)
    RecyclerView horizontalRecyclerView;

    @BindView(R.id.placesProgressBar)
    ConstraintLayout placesProgressBar;


    @BindView(R.id.toolBarTitle)
    TextView title;

    PlacesRecyclerAdapter adapter;

    PlacesTopBarAdapter placesTopBarAdapter;

    List<Place> placeList;

    boolean isLoading;

    int totalCount;

    Storage storage;


    @OnClick(R.id.mainActAddBtn)
    public void photo(View v){
        Log.d("TAG21", "PHOTO - ");
        EventBus.getDefault().post(new EventBusMessages.MakeCheckin());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.places_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("TAG23", "create ");


        Util.indicateTabImageView(getContext(), view, 1);
        Util.setOnTabClick(view);

        title.setText("Места");

        placeList = new ArrayList<>();
        adapter = new PlacesRecyclerAdapter(placeList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + placeList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= placeList.size()){
                            Log.d("TAG21", "load feed ");
                            loadPlaces(placeList.size());
                        }
                    }
                }
            }
        };

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        loadPlaces(placeList.size());


        List<String> data = new ArrayList<>();
        data.add("Все");
        data.add("Бары");
        data.add("Рестораны");
        data.add("Кино");
        data.add("Ночные клубы");

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        horizontalRecyclerView.setHasFixedSize(true);
        placesTopBarAdapter = new PlacesTopBarAdapter(data);
        horizontalRecyclerView.setAdapter(placesTopBarAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void gfdsgsd(EventBusMessages.SortPlaces event){

    }

    private void loadPlaces(int offset) {
        ApiFactory.getApi().getPlaces(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, 552).enqueue(new retrofit2.Callback<ResponseContainer<ResponsePlaces>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, retrofit2.Response<ResponseContainer<ResponsePlaces>> response) {
                if(response.body()!=null){

                    placesProgressBar.setVisibility(View.GONE);
                    placeList.addAll(response.body().getResponse().getItems());
                    Log.d("TAG21", "Places size" + response.body().getResponse().getItems().size());
                    adapter.update(placeList);
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        storage = (Storage) context;
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

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
