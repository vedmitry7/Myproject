package app.mycity.mycity.views.fragments.settings;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.LocationService;
import app.mycity.mycity.R;
import app.mycity.mycity.SocketService;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.Success;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.LoginActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettingsFragment extends Fragment implements TabStacker.TabStackInterface {


    @BindView(R.id.firstName)
    TextView firstName;

    @BindView(R.id.lastName)
    TextView lastName;

    @BindView(R.id.birthDay)
    TextView birthDay;
    @BindView(R.id.about)
    TextView about;
    @BindView(R.id.save)
    Button save;

    @BindView(R.id.progressPlaceHolder)
    ConstraintLayout placeHolder;

    DateFormat dateFormatDB = new SimpleDateFormat("dd.MM.yyyy");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        save.setVisibility(View.GONE);
        loadInfo();
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.backButton)
    public void sadsa(View v){
        getActivity().onBackPressed();
    }

    private void loadInfo() {
        ApiFactory.getApi().getUser(App.accessToken(), "bdate,about").enqueue(new Callback<ResponseContainer<Profile>>() {
            @Override
            public void onResponse(Call<ResponseContainer<Profile>> call, Response<ResponseContainer<Profile>> response) {


                if(response.body().getResponse()!=null){

                    Profile profile = response.body().getResponse();
                    firstName.setText(profile.getFirstName());
                    lastName.setText(profile.getLastName());
                    birthDay.setText(profile.getBirthday());
                    about.setText(profile.getAbout());
                    placeHolder.setVisibility(View.GONE);


                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<Profile>> call, Throwable t) {

            }
        });
    }


    private void showSaveButton(){

        save.setText("Сохранить");
        save.setBackgroundResource(R.drawable.places_top_bar_bg);
        save.setTextColor(Color.parseColor("#009688"));
        save.setVisibility(View.VISIBLE);

        Log.d("TAG21", "NOT MEMBER " );

 /*       save.setText("Подписаться");
        save.setBackgroundResource(R.drawable.places_top_bar_bg_choosen);
        save.setTextColor(Color.WHITE);
        save.setVisibility(View.VISIBLE);*/

    }

    @OnClick(R.id.changeBirthday)
    public void changeBirthday(View v){
        final Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = dateFormatDB.parse(birthDay.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.setTime(date);

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Log.i("TAG", "Date " + i + " " + i1 + " " + i2);
                //String date = i2+"."+i1+"."+i;

                Calendar calendar = Calendar.getInstance();
                calendar.set(i,  i1, i2);
                birthDay.setText(dateFormatDB.format(calendar.getTime()));
                Log.i("TAG", "Date " + dateFormatDB.format(c.getTime()));
                showSaveButton();

            }
        },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    @OnClick(R.id.changeName)
    public void changeName(View v){
        App.showKeyboard(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view =     getLayoutInflater().inflate(R.layout.change_name_dialog, null);
        builder.setView(view);

        builder.setTitle("Имя");
        final EditText editText = view.findViewById(R.id.editText);

        editText.setText(firstName.getText());
        editText.setSelection(firstName.getText().length());

        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if(!firstName.getText().toString().equals(editText.getText().toString())){
                    firstName.setText(editText.getText());
                    showSaveButton();
                    App.closeKeyboard(getContext());
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                App.closeKeyboard(getContext());
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }
    @OnClick(R.id.changeLastName)
    public void changeLastName(View v){
        App.showKeyboard(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view =    getLayoutInflater().inflate(R.layout.change_name_dialog, null);
        builder.setView(view);

        builder.setTitle("Фамилия");
        final EditText editText = view.findViewById(R.id.editText);

        editText.setText(lastName.getText());
        editText.setSelection(lastName.getText().length());

        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if(!lastName.getText().toString().equals(editText.getText().toString())){
                    lastName.setText(editText.getText());
                    showSaveButton();
                    App.closeKeyboard(getContext());
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                App.closeKeyboard(getContext());
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    @OnClick(R.id.changeAbout)
    public void changeAbout(View v){
        App.showKeyboard(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view =    getLayoutInflater().inflate(R.layout.change_name_dialog, null);
        builder.setView(view);

        builder.setTitle("Обо мне");
        final EditText editText = view.findViewById(R.id.editText);

        editText.setText(about.getText());
        editText.setSelection(about.getText().length());

        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if(!about.getText().toString().equals(editText.getText().toString())){
                    about.setText(editText.getText());
                    showSaveButton();
                    App.closeKeyboard(getContext());
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                App.closeKeyboard(getContext());
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.save)
    public void save(View v){
        placeHolder.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        ApiFactory.getApi().saveProfileInfo(App.accessToken(),
                firstName.getText().toString(),
                lastName.getText().toString(),
                birthDay.getText().toString(),
                about.getText().toString())
                .enqueue(new Callback<ResponseContainer<Success>>() {
                    @Override
                    public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {

                        if(response.body().getResponse()!=null){
                            placeHolder.setVisibility(View.GONE);
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                            builder.setMessage("Данные сохранены");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            android.app.AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {

                    }
                });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
