package app.mycity.mycity.views.fragments.places;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.PlaceCategoryResponce;
import app.mycity.mycity.api.model.PlaceCategory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponsePlaces;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.PlacesRecyclerAdapter;
import app.mycity.mycity.views.adapters.PlacesTopBarAdapter;
import app.mycity.mycity.views.fragments.feed.ChronicsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesFragment extends Fragment implements TabStacker.TabStackInterface {


    @BindView(R.id.placesFragmentRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.horizontalRecyclerView)
    RecyclerView categoriesRecyclerView;

    @BindView(R.id.placesProgressBar)
    ConstraintLayout placesProgressBar;

    @BindView(R.id.toolBarTitle)
    TextView title;

    @BindView(R.id.placesFragmentMessage)
    TextView placesFragmentMessage;

    @BindView(R.id.editTextSearch)
    TextView search;

    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    PlacesRecyclerAdapter adapter;

    PlacesTopBarAdapter placesCategoriesAdapter;

    List<Place> placeList;
    List<PlaceCategory> placeCategories;

    boolean isLoading;

    int totalCount;

    Storage storage;

    String order = "rate";
    private View fragmentView;


/*
    @OnClick(R.id.mainActAddBtn)
    public void photo(View v){
        Log.d("TAG21", "PHOTO - ");
        EventBus.getDefault().post(new EventBusMessages.MakeCheckin());
    }
*/

    public static PlacesFragment createInstance(String name) {
        PlacesFragment fragment = new PlacesFragment();
        Log.i("TAG21", "Create FeedFragment " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.places_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.backButton)
    public void sadsa(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("TAG23", "create ");

        fragmentView = view;

        if(SharedManager.getProperty("placesOrder")!=null && !SharedManager.getProperty("placesOrder").equals("")){
            order = SharedManager.getProperty("placesOrder");
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    clearSearch.setVisibility(View.GONE);
                } else {
                    clearSearch.setVisibility(View.VISIBLE);
                }
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    placeList = new ArrayList<>();
                    adapter.notifyDataSetChanged();
                    placesProgressBar.setVisibility(View.VISIBLE);
                    App.hideKeyboard(getActivity());
                    loadPlaces(0, 0, search.getText().toString(), order);
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    search.clearFocus();
                    return true;
                }
                return false;
            }
        });

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                clearSearch.setVisibility(View.GONE);
                loadPlaces(0, placesCategoriesAdapter.getCategoryId(), "", order);
            }
        });


        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, -1);
        Util.setUnreadCount(view);

/*        Util.indicateTabImageView(getContext(), view, 3);
        Util.setOnTabClick(view);*/

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
                            loadPlaces(placeList.size(), placesCategoriesAdapter.getCategoryId(), "", order);
                        }
                    }
                }
            }
        };

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //recyclerView.addOnScrollListener(scrollListener);

        loadPlaces(placeList.size(), 0, "", order);

        placeCategories = new ArrayList<>();
        PlaceCategory placeCategory = new PlaceCategory();
        placeCategory.setId(0);
        placeCategory.setTitle("Все");
        placeCategories.add(placeCategory);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView.setLayoutManager(horizontalLayoutManager);
        categoriesRecyclerView.setHasFixedSize(true);
        placesCategoriesAdapter = new PlacesTopBarAdapter(placeCategories);
        categoriesRecyclerView.setAdapter(placesCategoriesAdapter);

/*        String[] sortStrings = {"Рейтинг", "Колличество людей", "Колличество чекинов"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sortStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);*/

        loadCategories();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UnreadCountUpdate event){
        Util.setUnreadCount(fragmentView);
        Log.d("TAG25", "Update TOTAL UNREAD COUNT   -  Chronics");
    }


    @OnClick(R.id.sortButton)
    public void sort(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Сортировка");


        View view = getActivity().getLayoutInflater().inflate(R.layout.places_sort_dialog, null);
        builder.setView(view);




        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        if(order.equals("rate"))
        radioGroup.check(R.id.ratingRadioButton);
        if(order.equals("new"))
            radioGroup.check(R.id.onlineCountRadioButton);
        if(order.equals("pop"))
            radioGroup.check(R.id.checkinCountRadioButton);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.ratingRadioButton:
                        Log.d("TAG23", "r ");
                        SharedManager.addProperty("placesOrder", "rate");
                        order = "rate";
                        break;
                    case R.id.onlineCountRadioButton:
                        Log.d("TAG23", "o ");
                        SharedManager.addProperty("placesOrder", "new");
                        order = "new";
                        break;
                    case R.id.checkinCountRadioButton:
                        Log.d("TAG23", "c");
                        SharedManager.addProperty("placesOrder", "pop");
                        order = "pop";
                        break;
                }
            }
        });

        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                loadPlaces(0, placesCategoriesAdapter.getCategoryId(), "", order);
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void gfdsgsd(EventBusMessages.SortPlaces event){
        placeList = new ArrayList<>();
        placesProgressBar.setVisibility(View.VISIBLE);
        loadPlaces(0, placesCategoriesAdapter.getCategoryId(), search.getText().toString(), order);
        search.clearFocus();
    }

    private void loadPlaces(final int offset, int category, String search, String order) {
        ApiFactory.getApi().getPlaces(App.accessToken(), offset, App.chosenCity(), category, order, search).enqueue(new retrofit2.Callback<ResponseContainer<ResponsePlaces>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, retrofit2.Response<ResponseContainer<ResponsePlaces>> response) {
                if(response.body()!=null){
                    totalCount = response.body().getResponse().getCount();
                    placesProgressBar.setVisibility(View.GONE);
                    if(offset==0){
                        placeList = response.body().getResponse().getItems();
                    } else {
                        placeList.addAll(response.body().getResponse().getItems());
                    }
                    Log.d("TAG21", "Places size" + response.body().getResponse().getItems().size());
                    adapter.update(placeList);

                    if(response.body().getResponse().getItems().size()==0){
                        Log.d("TAG21", "Places size НОООООООООООООООООООООЛЬ!" );
                        placesFragmentMessage.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("TAG21", "Places size не НОООООООООООООООООООООЛЬ!" );
                        placesFragmentMessage.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, Throwable t) {
                Log.d("TAG21", "places fail "  + t.getLocalizedMessage());
            }
        });
    }

    private void loadCategories() {
        ApiFactory.getApi().getPlaceCategories(App.accessToken()).enqueue(new Callback<PlaceCategoryResponce>() {
            @Override
            public void onResponse(Call<PlaceCategoryResponce> call, Response<PlaceCategoryResponce> response) {
                if(response.body()!=null&&response.body().getResponse()!=null){
                    placeCategories.addAll(response.body().getResponse());
                    placesCategoriesAdapter.update(placeCategories);
                }
            }

            @Override
            public void onFailure(Call<PlaceCategoryResponce> call, Throwable t) {

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
