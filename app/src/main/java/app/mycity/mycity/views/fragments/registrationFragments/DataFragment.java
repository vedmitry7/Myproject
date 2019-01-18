package app.mycity.mycity.views.fragments.registrationFragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    @BindView(R.id.spinnerCountry)
    Spinner spinnerCountry;


    @BindView(R.id.spinnerCity)
    Spinner spinnerCity;

    private String sex = "2";

    Context context;

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

    }

    private void loadCountries() {

        ApiFactory.getApi().getCountries().enqueue(new Callback<ResponseContainer<ResponseCountries>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseCountries>> call, Response<ResponseContainer<ResponseCountries>> response) {
                ArrayList<String> data = new ArrayList<>();
                data.add("Выберите страну");


                final List<Country> list = response.body().getResponse().getItems();
                for (int i = 0; i < response.body().getResponse().getItems().size(); i++) {
                    data.add(list.get(i).getTitle());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCountry.setAdapter(adapter);

                spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if(position!=0)
                        loadCities(list.get(position-1).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
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
            @Override
            public void onResponse(Call<ResponseContainer<ResponseCities>> call, Response<ResponseContainer<ResponseCities>> response) {
                ArrayList<String> data = new ArrayList<>();
                data.add("Выберите город");


                final List<City> list = response.body().getResponse().getItems();
                for (int i = 0; i < response.body().getResponse().getItems().size(); i++) {
                    if(i!=0){
                        data.add(list.get(i-1).getTitle());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCity.setAdapter(adapter);

                spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
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

        } else {
            dataStore.setInfo(firstName.getText().toString(), secondName.getText().toString(), birthday.getText().toString(), sex);
            dataStore.nextEmailStep();
        }

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

    @OnClick(R.id.dataFragBirthdayIb)
    public void setDate(View view){
        setDate();
    }

    @OnClick(R.id.dataFragBirthdayTv)
    public void setDate2(View view){
        setDate();
    }

    private void setDate() {
        Log.i("TAG", "setDate");
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Log.i("TAG", "Date " + i + " " + i1 + " " + i2);
                String date = i2+"."+i1+"."+i;
                birthday.setTextColor(Color.parseColor("#000000"));
                birthday.setText(date);
            }
        },
                mYear,
                mMonth,
                mDay)
                .show();
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
