package app.mycity.mycity.activities;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import app.mycity.mycity.DataStore;
import app.mycity.mycity.R;
import app.mycity.mycity.fragments.registrationFragments.ConfirmEmailFragment;
import app.mycity.mycity.fragments.registrationFragments.DataFragment;
import app.mycity.mycity.fragments.registrationFragments.EmailFragment;
import app.mycity.mycity.fragments.registrationFragments.PasswordFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RegistrationActivity extends AppCompatActivity implements DataStore {


    @BindView(R.id.registrationLabel)
    TextView label;

    String firstName;
    String secondName;
    String birthday;
    String email;
    String sex, code;
    String password, confirm;
    EmailFragment emailFragment;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_authorization);
        ButterKnife.bind(this);

        DataFragment fragment = new DataFragment();

        fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
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


       /* ApiFactory.getApi().registration(firstName, secondName, birthday, email, sex).enqueue(new Callback<FullResponse>() {
            @Override
            public void onResponse(Call<FullResponse> call, Response<FullResponse> response) {
                app.mycity.mycity.api.model.Response resp = response.body().getResponse();
                Log.i("TAG", resp.toString());
                Log.i("TAG", response.body().toString());

            }

            @Override
            public void onFailure(Call<FullResponse> call, Throwable t) {
                Log.i("TAG", "fail");

            }
        });*/
    }

    @Override
    public void setInfo(String firstName, String secondName, String birthday, String sex) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthday = birthday;
        this.sex = sex;
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

        ConfirmEmailFragment emailFragment = new ConfirmEmailFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.fragmentContainer, emailFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        registration();
    }

    private void registration() {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("first_name", firstName)
                .add("last_name", secondName)
                .add("bdate", birthday)
                .add("email", email)
                .add("sex", sex)
                .build();

        Request request = new Request.Builder().url("http://192.168.0.104/api/auth.signUp")
                .post(body)
                .build();

        okhttp3.Response response = null;
        client.newCall(request).enqueue(new okhttp3.Callback() {
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


                String code;

                try {
                    code = innerObject.getString("user_id");
                    Log.i("TAG", String.valueOf(code));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void checkEmail() {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .build();

        Request request = new Request.Builder().url("http://192.168.0.104/api/auth.emailExists")
                .post(body)
                .build();

        okhttp3.Response response = null;
        client.newCall(request).enqueue(new okhttp3.Callback() {
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
                        emailFragment.emailExist();
                    } else {


                        Log.i("TAG", "empty");
                        RegistrationActivity.this.nextConfirmEmailCodeStep();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });

    }

    @Override
    public void checkEmailCode() {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("code", code)
                .build();

        Request request = new Request.Builder().url("http://192.168.0.104/api/auth.checkCode")
                .post(body)
                .build();

        okhttp3.Response response = null;
        client.newCall(request).enqueue(new okhttp3.Callback() {
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
                        Log.i("TAG", "Right");
                        PasswordFragment emailFragment = new PasswordFragment();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
                        transaction.replace(R.id.fragmentContainer, emailFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();


                    } else {
                        Log.i("TAG", "Wrong");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void commitPassword() {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("code", code)
                .add("password", password)
                .add("confirm_password", confirm)
                .add("intro", "0")
                .build();

        Request request = new Request.Builder().url("http://192.168.0.104/api/auth.confirm")
                .post(body)
                .build();

        okhttp3.Response response = null;
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("TAG", responseString);
                String responseClear = responseString.substring(1,responseString.length()-1);
                Log.i("TAG", responseClear);

               /* JSONObject jsonObject = null;
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
                        Log.i("TAG", "Right");



                    } else {
                        Log.i("TAG", "Wrong");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/




            }
        });
    }
}