package app.mycity.mycity.views.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.OkHttpClientFactory;
import app.mycity.mycity.views.fragments.registrationFragments.ConfirmEmailFragment;
import app.mycity.mycity.views.fragments.registrationFragments.DataFragment;
import app.mycity.mycity.views.fragments.registrationFragments.EmailFragment;
import app.mycity.mycity.views.fragments.registrationFragments.PasswordFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RegistrationActivity extends AppCompatActivity implements RegisterActivityDataStore {

    @BindView(R.id.registrationLabel)
    TextView label;

    private String firstName;
    private String secondName;
    private String birthday;
    private String email;
    private String sex, code;
    private String password, confirm;

    private String cityId;

    private FragmentManager fragmentManager;
    private EmailFragment emailFragment;
    private ConfirmEmailFragment confirmEmailFragment;
    private PasswordFragment passwordFragment;
    private String countryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        DataFragment fragment = new DataFragment();

        fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        Typeface type = Typeface.createFromAsset(getAssets(),"abril_fatface_regular.otf");
        label.setTypeface(type);
    }

    @Override
    public void setPassword(String password, String confirm) {
        this.password = password;
        this.confirm = confirm;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setInfo(String firstName, String secondName, String birthday, String sex, String cityId, String countryId) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthday = birthday;
        this.sex = sex;
        this.cityId = cityId;
        this.countryId = countryId;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @SuppressLint("ResourceType")
    @Override
    public void nextEmailStep() {
        emailFragment = new EmailFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.fragmentContainer, emailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void nextConfirmEmailCodeStep() {
        Log.d("TAG", "confirmEmailCodeStep");

        confirmEmailFragment = new ConfirmEmailFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.fragmentContainer, confirmEmailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void checkEmail() {
        Log.i("TAG", "checking.........");

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .build();

        Request request = new Request.Builder().url(Constants.URL_BASE + "auth.emailExists")
                .post(body)
                .build();

        okhttp3.Response response = null;
        OkHttpClientFactory.getClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("TAG", responseString);
                String responseClear = responseString.substring(1,responseString.length()-1);
                Log.i("TAG", responseClear);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("TAG", "jsonObj = " + String.valueOf(jsonObject!=null));

                JSONObject innerObject = null;
                try {
                    innerObject = jsonObject.getJSONObject("response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("TAG", "jsonInnerObj = " + String.valueOf(jsonObject!=null));


                boolean code;

                try {
                    code = innerObject.getBoolean("exists");
                    Log.i("TAG", String.valueOf(code));
                    if (code){
                        Log.i("TAG", "сущ");
                        emailFragment.emailExist("Данный email уже занят, выберите другой или восстановите доступ");
                    } else {
                        Log.i("TAG", "empty");
                        sendEmail();
                        nextConfirmEmailCodeStep();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void sendEmail() {
        Log.d("TAG", "Registration");
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .build();
        Request request = new Request.Builder().url(Constants.URL_BASE + "auth.reconfirm")
                .post(body)
                .build();
        okhttp3.Response response = null;
        OkHttpClientFactory.getClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("TAG", responseString);
                String responseClear = responseString.substring(1,responseString.length()-1);
                Log.i("TAG", responseClear);
            }
        });
    }

    @Override
    public void checkEmailCodeAndRegistration() {

        Log.d("TAG", "Registration");

        RequestBody body = new FormBody.Builder()
                .add("first_name", firstName)
                .add("last_name", secondName)
                .add("code", code)
                .add("bdate", birthday)
                .add("email", email)
                .add("sex", sex)
                .add("city_id", cityId)
                .add("country_id", countryId)
                .build();

        Request request = new Request.Builder().url(Constants.URL_BASE + "auth.signUp")
                .post(body)
                .build();

        okhttp3.Response response = null;
        OkHttpClientFactory.getClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("TAG", "resp string - " + responseString + ".!!!");
                String responseClear = responseString.substring(1, responseString.length()-1);
                Log.i("TAG", responseClear);
                JSONObject jsonObject = null;
                JSONObject innerResponseObject = null;
                try {
                    jsonObject = new JSONObject(responseString);
                    Log.i("TAG", "jsonObj = " + String.valueOf(jsonObject!=null));
                    innerResponseObject = jsonObject.getJSONObject("response");
                    Log.i("TAG", "jsonInnerRespObj = " + String.valueOf(innerResponseObject!=null));
                    if(innerResponseObject!=null){
                        Log.i("TAG", "RESPONSE");
                        String success = innerResponseObject.getString("success");
                        if(success.equals("1")){
                            passwordFragment = new PasswordFragment();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
                            transaction.replace(R.id.fragmentContainer, passwordFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();

                        } else {
                            confirmEmailFragment.codeIsWrong();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("TAG", "JSON GET RESPONSE ERROR");
                }

                JSONObject innerErrorObject;
                try {
                    innerErrorObject = jsonObject.getJSONObject("error");
                    if(innerErrorObject!=null){
                        Log.i("TAG", "ERROR");
                        String errorMassage = innerErrorObject.getString("error_msg");
                        if(errorMassage.equals("account_error")){
                            Log.i("TAG", "WRONG_CODE");
                            confirmEmailFragment.codeIsWrong();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("TAG", "jsonInnerObj = " + String.valueOf(jsonObject!=null));

            }
        });
    }

    @Override
    public void commitPassword() {

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("code", code)
                .add("password", password)
                .add("confirm_password", confirm)
                .add("intro", "0")
                .build();

        Request request = new Request.Builder().url(Constants.URL_BASE + "auth.confirm")
                .post(body)
                .build();

        okhttp3.Response response = null;
        OkHttpClientFactory.getClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("TAG", responseString);
                String responseClear = responseString.substring(1,responseString.length()-1);
                Log.i("TAG", responseClear);

                JSONObject jsonObject = null;
                JSONObject innerResponseObject = null;
                try {
                    jsonObject = new JSONObject(responseString);
                    Log.i("TAG", "jsonObj = " + String.valueOf(jsonObject!=null));
                    innerResponseObject = jsonObject.getJSONObject("response");
                    Log.i("TAG", "jsonInnerObj = " + String.valueOf(jsonObject!=null));

                    String success;
                    if(innerResponseObject != null){
                        try {
                            success = innerResponseObject.getString("success");
                            if(success.equals("1")){
                                Log.i("TAG", "All right");
                                RegistrationActivity.this.finish();
                            }
                            Log.i("TAG", String.valueOf(success));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    Log.i("TAG", "GET RESPONSE ERROR");
                    e.printStackTrace();
                }

                JSONObject innerErrorObject;
                try {
                    innerErrorObject = jsonObject.getJSONObject("error");
                    if(innerErrorObject!=null){
                        Log.i("TAG", "ERROR");
                        String errorMassage = innerErrorObject.getString("error_msg");
                        if(errorMassage.equals("account_password_length")){
                            Log.i("TAG", "WRONG_LENGTH");
                            passwordFragment.wrongCodeLength();
                        }
                        if(errorMassage.equals("account_password_not_match")){
                            Log.i("TAG", "NOT MATCH");
                            passwordFragment.codeNotMatch();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.i("TAG", "back key");
        Fragment currentFrag = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if(currentFrag instanceof DataFragment){
            Log.i("TAG", "Data fara");
            this.finish();
        }

        if(currentFrag instanceof EmailFragment){
            if(emailFragment.isWaitAnswer()){
                showDialog("Отменить подтверждение email?");
            } else {
                super.onBackPressed();
            }
        }

        if(currentFrag instanceof ConfirmEmailFragment){
            showDialog("Отменить подтверждение email?");
        }
    }

    private void showDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RegistrationActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}