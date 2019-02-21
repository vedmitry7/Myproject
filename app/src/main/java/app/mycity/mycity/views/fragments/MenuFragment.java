package app.mycity.mycity.views.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.City;
import app.mycity.mycity.api.model.ResponseCities;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends Fragment implements TabStacker.TabStackInterface {

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.cardViewCheckins)
    CardView cardViewCheckins;
    @BindView(R.id.cardViewChronics)
    CardView cardViewChronics;
    @BindView(R.id.cardViewPlaces)
    CardView cardViewPlaces;
    @BindView(R.id.cardViewPeoples)
    CardView cardViewPeoples;
    @BindView(R.id.cardViewEvents)
    CardView cardViewEvents;
    @BindView(R.id.cardViewServices)
    CardView cardViewServices;

    @BindView(R.id.choosenCity)
    TextView city;
    AutoCompleteTextView cityEditText;
    private View fragmentView;

    List<City> cities;
    private boolean cityChosen;

    AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;
        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, 0);
        Util.setUnreadCount(view);

        city.setText(SharedManager.getProperty("chosen_city"));

        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"abril_fatface_regular.otf");
        toolBarTitle.setTypeface(type);
        Log.d("TAG23", "create ");

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    ((CardView)v).setCardElevation(App.dpToPx(getContext(), 2));
                    ((CardView)v).setScaleX(0.99f);
                    ((CardView)v).setScaleY(0.99f);
                }
                if(event.getAction()== MotionEvent.ACTION_UP){
                    ((CardView)v).setCardElevation(App.dpToPx(getContext(), 5));
                    ((CardView)v).setScaleX(1f);
                    ((CardView)v).setScaleY(1f);
                }
                return false;
            }
        };

        cardViewCheckins.setOnTouchListener(onTouchListener);
        cardViewChronics.setOnTouchListener(onTouchListener);
        cardViewPlaces.setOnTouchListener(onTouchListener);
        cardViewPeoples.setOnTouchListener(onTouchListener);
        cardViewEvents.setOnTouchListener(onTouchListener);
        cardViewServices.setOnTouchListener(onTouchListener);
    }

    @OnClick(R.id.cardViewCheckins)
    public void che(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenFeed());
    }

    @OnClick(R.id.cardViewChronics)
    public void chr(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenChronics());
    }

    @OnClick(R.id.cardViewPlaces)
    public void pl(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenPlaces());
    }

    @OnClick(R.id.cardViewPeoples)
    public void pe(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenPeople());
    }

    @OnClick(R.id.cardViewEvents)
    public void ev(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenEvents());
    }

    @OnClick(R.id.cardViewServices)
    public void se(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenServices());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UnreadCountUpdate event){
        Util.setUnreadCount(fragmentView);
        Log.d("TAG25", "Update TOTAL UNREAD COUNT   -  MENU");
    }


    @OnClick(R.id.chooseCityButton)
    public void chooseCityButton(View v){
        cityChosen = false;
        App.showKeyboard(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выбор города");
        builder.setCancelable(false);

        View view = getActivity().getLayoutInflater().inflate(R.layout.choose_city_dialog, null);
        builder.setView(view);

        cityEditText = view.findViewById(R.id.searchEditText);

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TAG23", "after " + s.toString() + ".");
                if(s.toString().length()>0)
                loadCities(s.toString());
            }
        });
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String city =  cityEditText.getText().toString().trim();

                for (int i = cities.size()-1; i >= 0; i--) {
                    if(cities.get(i).getTitle().startsWith(city)){

                        String chosenCity = cities.get(i).getTitle();
                        if(cities.get(i).getTitle().contains(",")){
                            chosenCity = cities.get(i).getTitle().substring(0, cities.get(i).getTitle().indexOf(","));
                        }
                        SharedManager.addProperty("chosen_city", chosenCity);
                        SharedManager.addIntProperty("chosen_city_id", Integer.parseInt(cities.get(i).getId()));
                        MenuFragment.this.city.setText(chosenCity);
                        App.closeKeyboard(getContext());
                        dialog.dismiss();
                        return;
                    }
                }
                App.closeKeyboard(getContext());
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                App.closeKeyboard(getContext());
            }
        });


        dialog = builder.create();
        dialog.show();

    }


    private void loadCities(String search) {
        ApiFactory.getApi().getCitiesWithSearch(search).enqueue(new Callback<ResponseContainer<ResponseCities>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseCities>> call, Response<ResponseContainer<ResponseCities>> response) {
                final ArrayList<String> data = new ArrayList<>();

                cities = response.body().getResponse().getItems();

                for (int i = 0; i < response.body().getResponse().getItems().size(); i++) {
                        data.add(cities.get(i).getTitle());
                        Log.d("TAG23", cities.get(i).getTitle());
                }

                cityEditText.setAdapter(new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, data));
                cityEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("TAG23", "city - " + cities.get(position).getTitle() + " id - " + cities.get(position).getId());
                        cityChosen = true;

                        String chosenCity = cities.get(position).getTitle();
                        if(cities.get(position).getTitle().contains(",")){
                            chosenCity = cities.get(position).getTitle().substring(0, cities.get(position).getTitle().indexOf(","));
                        }
                        SharedManager.addProperty("chosen_city", chosenCity);
                        SharedManager.addIntProperty("chosen_city_id", Integer.parseInt(cities.get(position).getId()));

                        city.setText(chosenCity);
                        App.closeKeyboard(getContext());
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseCities>> call, Throwable t) {
                Log.d("TAG23", "fail " + t.getCause());
            }
        });
    }


    @OnClick(R.id.settingsButton)
    public void settingsButton(View v){
        EventBus.getDefault().post(new EventBusMessages.MainSettings());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
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
