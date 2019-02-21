package app.mycity.mycity.views.fragments.registrationFragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.mycity.mycity.App;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.City;
import app.mycity.mycity.api.model.Country;
import app.mycity.mycity.api.model.ResponseCities;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseCountries;
import app.mycity.mycity.views.activities.RegisterActivityDataStore;
import app.mycity.mycity.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFragment extends Fragment {

    RegisterActivityDataStore dataStore;

    @BindView(R.id.dataFragBirthdayTv)
    TextView birthday;

    @BindView(R.id.dataFragFirstNameEt)
    EditText firstName;

    @BindView(R.id.dataFragSecondNameEt)
    EditText secondName;

    @BindView(R.id.dataFragSexRadioGroup)
    RadioGroup radioGroup;
/*
    @BindView(R.id.spinnerCountry)
    Spinner spinnerCountry;

    @BindView(R.id.spinnerCity)
    Spinner spinnerCity;*/


    @BindView(R.id.cityEditText)
    AutoCompleteTextView cityEditText;

    @BindView(R.id.countryEditText)
    AutoCompleteTextView countryEditText;

    private String sex = "2";

    Context context;
    private String cityId = "";
    private String countryId = "";
    private String birthdayData;

    private List<Country> countries;
    List<City> cities;

    private boolean countryFilled;
    private boolean cityFilled;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadCountries();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.male:
                        sex = "2";
                        break;
                    case R.id.female:
                        sex = "1";
                        break;
                }
            }
        });

        context = getContext();

        firstName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    firstName.setText(firstName.getText().toString().trim());
                    secondName.requestFocus();
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    Log.d("TAG21", "et back");
                    getActivity().onBackPressed();
                }
                return true;
            }
        });

        secondName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    secondName.setText(secondName.getText().toString().trim());
                    setDate();
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    Log.d("TAG21", "et back");
                    getActivity().onBackPressed();
                }
                return true;
            }
        });


        countryEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String country = countryEditText.getText().toString().trim();

                    for (int i = countries.size()-1; i >= 0; i--) {
                        if(country.equals(countries.get(i).getTitle())){
                            countryFilled = true;
                            loadCities(countries.get(i).getId());
                            countryId = countries.get(i).getId();
                            Log.i("TAG21", "valid " + countries.get(i).getId());
                            Log.i("TAG21", "valid " + countries.get(i).getTitle());
                        }
                    }


                    cityEditText.requestFocus();
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    Log.d("TAG21", "et back");
                    getActivity().onBackPressed();
                }
                return true;
            }
        });

        countryEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    Log.i("TAG21", "Lose focus" );

                    String country = countryEditText.getText().toString().trim();

                    for (int i = countries.size()-1; i >= 0; i--) {
                        if(country.equals(countries.get(i).getTitle())){
                            countryFilled = true;
                            loadCities(countries.get(i).getId());
                            countryId = countries.get(i).getId();
                            Log.i("TAG21", "lf valid " + countries.get(i).getId());
                            Log.i("TAG21", "lf valid " + countries.get(i).getTitle());
                        }
                    }
                }
            }
        });


        cityEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    App.closeKeyboard(context);
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    Log.d("TAG21", "et back");
                    getActivity().onBackPressed();
                }
                return true;
            }
        });

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!countryFilled){
                    //Toast.makeText(getContext(), "Сперва заполните поле страны", Toast.LENGTH_SHORT).show();
                    countryEditText.requestFocus();
                    if(!cityEditText.getText().toString().equals("")){
                        cityEditText.setText("");
                        showAlert("Сперва заполните поле страны");
                    }
                }
            }
        });
        firstName.requestFocus();
        App.showKeyboard(getContext());
    }

    private void loadCountries() {

        ApiFactory.getApi().getCountries().enqueue(new Callback<ResponseContainer<ResponseCountries>>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<ResponseContainer<ResponseCountries>> call, Response<ResponseContainer<ResponseCountries>> response) {
                final ArrayList<String> data = new ArrayList<>();
                countries = response.body().getResponse().getItems();
                for (int i = 0; i < response.body().getResponse().getItems().size(); i++) {
                    data.add(countries.get(i).getTitle());
                }
                countryEditText.setAdapter(new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, data));

                countryEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        for (int i = 0; i < data.size(); i++) {
                            if(countryEditText.getText().toString().equals(data.get(i))){
                                loadCities(countries.get(i).getId());
                                countryId = countries.get(i).getId();
                                cityEditText.requestFocus();
                                countryFilled = true;
                                Log.i("TAG", "valid " + countries.get(i).getId());
                                Log.i("TAG", "valid " + countries.get(i).getTitle());
                                return;
                            }
                        }
                        Log.i("TAG", "valid 2" + position);

                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseCountries>> call, Throwable t) {

            }
        });
    }

    private void loadCities(String countryId) {
        ApiFactory.getApi().getCities(countryId).enqueue(new Callback<ResponseContainer<ResponseCities>>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<ResponseContainer<ResponseCities>> call, Response<ResponseContainer<ResponseCities>> response) {
                final ArrayList<String> data = new ArrayList<>();


                cities = response.body().getResponse().getItems();
                for (int i = 0; i < response.body().getResponse().getItems().size(); i++) {
                    if(i!=0){
                        data.add(cities.get(i-1).getTitle());
                    }
                }
                cityEditText.setAdapter(new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, data));
                cityEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        cityId = cities.get(position).getId();
                    }
                });

            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseCities>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataStore = (RegisterActivityDataStore) context;
    }

    @OnClick(R.id.dataFragmentNext)
    public void next(View view){

        if(isValidLogin(firstName.getText().toString()) && isValidLogin(secondName.getText().toString())){
            Log.i("TAG", "valid");
        } else {
            Log.i("TAG", "invalid");
            showAlert("Имя и фамилия не должны быть менее двух символов");
            return;
        }

        if(birthday.getText().toString().equals("Дата рождения")){
            Log.i("TAG", "invalid birthday " + birthday.getText().toString());
            showAlert("Заполните поле даты рождения");
            return;
        }

        if (cityId.length()==0 && cityEditText.getText().toString().length()==0) {
            showAlert("Заполните поля страна и город");
            Log.i("TAG21", "id and field null " );
            return;
        }

        if (cityId.length()==0 && cityEditText.getText().toString().length()!=0) {
            Log.i("TAG21", "id is null / field not null" );
            String city = cityEditText.getText().toString().trim();
            for (int i = cities.size()-1; i >= 0; i--) {
                if(city.equalsIgnoreCase(cities.get(i).getTitle())){
                    cityFilled = true;
                    loadCities(cities.get(i).getId());
                    cityId = cities.get(i).getId();
                    Log.i("TAG21", "ch valid " + cities.get(i).getId());
                    Log.i("TAG21", "ch valid " + cities.get(i).getTitle());
                }
            }
            if(!cityFilled){
                return;
            }
        }




        dataStore.setInfo(firstName.getText().toString(), secondName.getText().toString(), birthdayData, sex, cityId, countryId);
        dataStore.nextEmailStep();

        Log.i("TAG", "next");
    }

    private void showAlert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(s);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.dataFragBirthdayIb)
    public void setDate(View view){
        setDate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.dataFragBirthdayTv)
    public void setDate2(View view){
        setDate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setDate() {
        Log.i("TAG", "setDate");


        App.closeKeyboard(getActivity().getApplicationContext());
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Log.i("TAG", "Date " + i + " " + i1 + " " + i2);
                //String date = i2+"."+i1+"."+i;
                birthday.setTextColor(Color.parseColor("#000000"));

                Calendar calendar = Calendar.getInstance();
                calendar.set(i,  i1, i2);
                DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
                DateFormat dateFormatForServer = new SimpleDateFormat("dd.MM.yyyy");
                Date date = calendar.getTime();
                String dateText = dateFormat.format(date);
                birthdayData = dateFormatForServer.format(calendar.getTime());
                birthday.setText(dateText);
                App.showKeyboard(getContext());

            }
        },
                mYear,
                mMonth,
                mDay)
                .show();
        countryEditText.requestFocus();
    }

/*    public boolean isValidLogin(String s) {
        return s.length() >= 2;
    }*/

    public boolean isValidLogin(String email) {
        String ePattern = "^[а-яА-Я]{2,}|[a-zA-Z]{2,}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
