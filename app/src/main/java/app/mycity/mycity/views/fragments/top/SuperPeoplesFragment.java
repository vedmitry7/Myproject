package app.mycity.mycity.views.fragments.top;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import app.mycity.mycity.api.model.PlaceCategory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.adapters.PeoplesRecyclerAdapter;
import app.mycity.mycity.views.adapters.PeoplesTopBarAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import fr.arnaudguyon.tabstacker.TabStacker.TabStackInterface;

public class SuperPeoplesFragment extends Fragment implements TabStackInterface {


    @BindView(R.id.myAllFriendsRecyclerAdapter)
    RecyclerView recyclerView;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout progressBarPlaceHolder;

    @BindView(R.id.horizontalRecyclerView)
    RecyclerView categoriesRecyclerView;

    @BindView(R.id.editTextSearch)
    TextView search;

    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    PeoplesRecyclerAdapter adapter;
    List<User> userList;

    PeoplesTopBarAdapter placesCategoriesAdapter;

    List<PlaceCategory> placeCategories;

    String filter = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.super_people_fragment, container, false);

        Log.d("TAG", "Create " + this.getClass().getSimpleName());
        Log.i("TAG3","All list created");
        ButterKnife.bind(this, view);
        return view;
    }

    public static SuperPeoplesFragment createInstance(String name, int stackPos, int tabPos) {
        SuperPeoplesFragment fragment = new SuperPeoplesFragment();
        Log.i("TAG21", "Create Profile " + name + tabPos);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("tabPos", tabPos);
        bundle.putInt("stackPos", stackPos);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.backButton)
    public void sadsa(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, -1);

        userList = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
        adapter = new PeoplesRecyclerAdapter(userList);
        recyclerView.setAdapter(adapter);
        Log.d("TAG", "ViewCreated " + this.getClass().getSimpleName());

        progressBarPlaceHolder.setVisibility(View.GONE);


        getUsersList("", "");


        placeCategories = new ArrayList<>();
        PlaceCategory placeCategory = new PlaceCategory();
        placeCategory.setId(0);
        placeCategory.setTitle("Все");
        placeCategories.add(placeCategory);

        PlaceCategory inPalce = new PlaceCategory();
        inPalce.setId(1);
        inPalce.setTitle("В заведениях");
        placeCategories.add(inPalce);

        PlaceCategory inPlaceReady = new PlaceCategory();
        inPlaceReady.setId(2);
        inPlaceReady.setTitle("Готовы к знакомству");
        placeCategories.add(inPlaceReady);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView.setLayoutManager(horizontalLayoutManager);
        categoriesRecyclerView.setHasFixedSize(true);
        placesCategoriesAdapter = new PeoplesTopBarAdapter(placeCategories);
        categoriesRecyclerView.setAdapter(placesCategoriesAdapter);

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
                    userList = new ArrayList<>();
                    adapter.notifyDataSetChanged();
                    App.hideKeyboard(getActivity());
                    getUsersList("", search.getText().toString());
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
                getUsersList("", "");
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void gfdsgsd(EventBusMessages.SortPeople event){
        Log.i("TAG21","sort - " + event.getPosition());


        userList = new ArrayList<>();


        switch (event.getPosition()){
            case 0:
                filter = "";
                break;
            case 1:
                filter = "in_place";
                break;
            case 2:
                filter = "ready_meet";
                break;
        }

        getUsersList(filter, search.getText().toString());
        search.clearFocus();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        Log.d("TAG", "Attach " + this.getClass().getSimpleName());
        Log.i("TAG3","All list attach");
    }

    private void getUsersList(String filter, String search){
        Log.d("TAG", "getUsersList " + this.getClass().getSimpleName());
        Log.i("TAG21","getUsers - f:" + filter + " s:" + search);
        int sex = 0;
        if(SharedManager.getBooleanProperty("sortBySex")){
            sex = SharedManager.getIntProperty("sex");
        }
        int ageFrom = 0;
        int ageTo = 100;
        if(SharedManager.getBooleanProperty("sortByAge")){
            ageFrom = SharedManager.getIntProperty("ageFrom")+18;
            ageTo = SharedManager.getIntProperty("ageTo")+18;
            Log.i("TAG21", "Sort by age f: " + ageFrom + " to:" + ageTo );
        } else {
            Log.i("TAG21", "not Sort by age ");
        }


        ApiFactory.getApi().getTopUsersInPlacesWithSorting(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "photo_360,place,count_likes", "top", filter, sex, ageFrom, ageTo, "1").enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {
                UsersContainer users = response.body().getResponse();

                if(users != null){
                    userList = users.getFriends();
                    Log.d("TAG", "Users all loaded. List size = " + userList.size());
                    adapter.update(userList);
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.sortButton)
    public void sort(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Фильтры");

        View view = getActivity().getLayoutInflater().inflate(R.layout.people_sort_dialog, null);
        builder.setView(view);

        final RadioGroup radioGroup = view.findViewById(R.id.radioGroupSex);
        radioGroup.check(R.id.ratingRadioButton);
        RadioButton male = (RadioButton) view.findViewById(R.id.maleRadioButton);
        RadioButton female = (RadioButton) view.findViewById(R.id.femaleRadioButton);

        int sex = SharedManager.getIntProperty("sex");
        if(sex==0){
            SharedManager.addIntProperty("sex", 2);
        }
        if(sex==1){
            female.setChecked(true);
        } else {
            male.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.maleRadioButton:
                        Log.d("TAG23", "m ");
                        SharedManager.addIntProperty("sex", 2);
                        break;
                    case R.id.femaleRadioButton:
                        Log.d("TAG23", "f ");
                        SharedManager.addIntProperty("sex", 1);
                        break;
                }
            }
        });

        boolean sortBySex = SharedManager.getBooleanProperty("sortBySex");

        final CheckBox checkboxSex = view.findViewById(R.id.checkBoxSexActive);
        checkboxSex.setChecked(sortBySex);

        for(int i = 0; i < radioGroup.getChildCount(); i++){
            ((RadioButton)radioGroup.getChildAt(i)).setEnabled(sortBySex);
        }


        checkboxSex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedManager.addBooleanProperty("sortBySex", isChecked);
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    ((RadioButton)radioGroup.getChildAt(i)).setEnabled(isChecked);
                }
            }
        });

        String[] from_array = new String[30];
        for (int i = 0; i < from_array.length; i++) {
            from_array[i] = "От " + (i+18);
        }

        final Spinner spinnerFrom = view.findViewById(R.id.spinnerFrom);
        ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, from_array);

        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapterFrom);


        String[] to_array = new String[30];
        for (int i = 0; i < to_array.length; i++) {
            to_array[i] = "До " + (i+18);
        }

        final Spinner spinnerTo = view.findViewById(R.id.spinnerTo);
        ArrayAdapter<String> adapterTo = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, to_array);

        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(adapterTo);
        spinnerTo.setAdapter(adapterTo);


        int from = SharedManager.getIntProperty("ageFrom");
        int to = SharedManager.getIntProperty("ageTo");

        if(from==-1){
            SharedManager.addIntProperty("ageFrom", 0);
            SharedManager.addIntProperty("ageTo", 7);
        }

        final int[] positionFrom = {SharedManager.getIntProperty("ageFrom")};
         final int[] positionTo = {SharedManager.getIntProperty("ageTo")};

     //   final int[] positionFrom = {0};
     //   final int[] positionTo = {7};

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedManager.addIntProperty("ageFrom", position);
                positionFrom[0] = position;
                if (positionFrom[0] > positionTo[0]) {
                    positionTo[0] = positionFrom[0];
                    spinnerTo.setSelection(positionTo[0]);
                    SharedManager.addIntProperty("ageTo", position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedManager.addIntProperty("ageTo", position);
                positionTo[0] = position;
                if (positionTo[0] < positionFrom[0]) {
                    positionFrom[0] = positionTo[0];
                    spinnerFrom.setSelection(positionTo[0]);
                    SharedManager.addIntProperty("ageFrom", position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinnerFrom.setSelection(positionFrom[0]);
        spinnerTo.setSelection(positionTo[0]);

        boolean sortByAge = SharedManager.getBooleanProperty("sortByAge");

        CheckBox checkboxAge = view.findViewById(R.id.checkBoxAgeActive);
        checkboxAge.setChecked(sortByAge);

        spinnerFrom.setEnabled(sortByAge);
        spinnerTo.setEnabled(sortByAge);

        checkboxAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedManager.addBooleanProperty("sortByAge", isChecked);
                spinnerFrom.setEnabled(isChecked);
                spinnerTo.setEnabled(isChecked);
            }
        });

        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getUsersList(filter,"");
                // User clicked OK button
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

    private void getFriendsListById(){
        Log.d("TAG", "getFriendsListById " + this.getClass().getSimpleName());

        ApiFactory.getApi().getUsersById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),"sdfsd", "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {



                UsersContainer users = response.body().getResponse();

                if(users != null){
                    userList = users.getFriends();
                    Log.d("TAG", "Users all loaded. List size = " + userList.size());
                    adapter.update(userList);
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });
    }


    public void onStart() {
        super.onStart();
        Log.d("TAG", "Start " + this.getClass().getSimpleName());

    }


    public void onResume() {
        super.onResume();
        Log.d("TAG", "Resume " + this.getClass().getSimpleName());
        Log.i("TAG3","All list resume");

    }

    public void onPause() {
        super.onPause();
        Log.d("TAG", "Pause " + this.getClass().getSimpleName());
        Log.i("TAG21","Friends ALL FRIENDS  resume");
        Log.i("TAG3","All list pause");

    }

    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.d("TAG", "Stop " + this.getClass().getSimpleName());

    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TAG", "Destroy view " + this.getClass().getSimpleName());

    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "Destroy " + this.getClass().getSimpleName());
        Log.i("TAG3","All list destroy");

    }

    public void onDetach() {
        super.onDetach();
        Log.d("TAG", "Detach " + this.getClass().getSimpleName());
        Log.i("TAG3","All list detach");

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
