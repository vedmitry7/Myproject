package app.mycity.mycity.views.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.api.model.CheckTokenResponse;
import app.mycity.mycity.api.model.RefreshTokenResponse;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseAuth;
import app.mycity.mycity.api.model.ResponseContainer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final int CODE_FORGOT_PASSWORD = 27;
    public static final int CODE_REGISTRATION = 26;

    @BindView(R.id.loginActRegistrationButtonTv)
    TextView textView;

    @BindView(R.id.loginLabel)
    TextView label;

    @BindView(R.id.loginActLoginEt)
    EditText login;

    @BindView(R.id.loginActPasswordEt)
    EditText password;


    @BindView(R.id.loginString)
    View loginString;

    @BindView(R.id.passwordString)
    View passwordString;
    int selection = 0;


    @BindView(R.id.loginActEnterButtonContainer)
    CardView loginActEnterButtonContainer;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout progressBarPlaceHolder;

    @BindView(R.id.noInternetContainer)
    ConstraintLayout noInternetContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_new);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        ButterKnife.bind(this);

        checkToken();

        if(!App.isOnline(this)){
            noInternetContainer.setVisibility(View.VISIBLE);
        }

        Typeface type = Typeface.createFromAsset(getAssets(),"abril_fatface_regular.otf");
        label.setTypeface(type);



        login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    loginString.setBackgroundColor(getResources().getColor(R.color.black_30percent));
                }
                else {
                    loginString.setBackgroundColor(getResources().getColor(R.color.black_10percent));
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    passwordString.setBackgroundColor(getResources().getColor(R.color.black_30percent));
                }
                else {
                    passwordString.setBackgroundColor(getResources().getColor(R.color.black_10percent));
                }
            }
        });
        loginActEnterButtonContainer.setVisibility(View.GONE);


        if(SharedManager.getProperty(Constants.KEY_LOGIN)!=null){
            login.setText(SharedManager.getProperty(Constants.KEY_LOGIN));
            password.setText(SharedManager.getProperty(Constants.KEY_PASSWORD));
        } else {

        }

    }

    private void checkToken() {
        ApiFactory.getApi().checkToken(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<CheckTokenResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<CheckTokenResponse>> call, Response<ResponseContainer<CheckTokenResponse>> response) {
                if(response.body().getResponse()!=null){
                    if(response.body().getResponse().getSuccess()==1){
                        SharedManager.addBooleanProperty("login", true);
                        launchApp();
                    }
                } else {
                    progressBarPlaceHolder.setVisibility(View.GONE);
                    loginActEnterButtonContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<CheckTokenResponse>> call, Throwable t) {

                if(!App.isOnline(LoginActivity.this)){
                    noInternetContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateToken(){
        ApiFactory.getApi().updateToken(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), SharedManager.getProperty(Constants.KEY_REFRESH_TOKEN)).enqueue(new Callback<ResponseContainer<RefreshTokenResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<RefreshTokenResponse>> call, Response<ResponseContainer<RefreshTokenResponse>> response) {

                RefreshTokenResponse tokenResponse = response.body().getResponse();
                if(tokenResponse!=null){
                    SharedManager.addProperty(Constants.KEY_ACCESS_TOKEN, tokenResponse.getAccessToken());
                    SharedManager.addProperty(Constants.KEY_REFRESH_TOKEN, tokenResponse.getRefreshToken());
                    SharedManager.addProperty(Constants.KEY_EXPIRED_AT, String.valueOf(tokenResponse.getExpiredAt()));
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<RefreshTokenResponse>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.loginActRegistrationButtonTv)
    public void onClick(View v){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

/*
    @OnClick(R.id.showPasswordBtn)
    public void showPassword(View v){
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selection = password.getSelectionStart();
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP:
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        password.setSelection(selection);
                        return true;
                }
                return false;
            }
        });
    }*/

    @OnClick(R.id.loginActForgetPasswordButtonTv)
    public void forgetPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivityForResult(intent, CODE_FORGOT_PASSWORD);
    }


    @OnClick(R.id.loginActEnterButton)
    public void enter(View v) {

        String loginText = login.getText().toString();
        String passwordText = password.getText().toString();

  /*      if(v.getId()==R.id.kolia){
            loginText = "nicker08@inbox.ru";
            passwordText = "12345678";
        }

        if(v.getId()==R.id.misha){
            loginText = "Winchester_1995@mail.ru";
            passwordText = "12345678";
        }
*/

        final String finalLoginText = loginText;


        final String finalPasswordText = passwordText;
        ApiFactory.getApi().authorize(loginText, passwordText).enqueue(new retrofit2.Callback<ResponseContainer<ResponseAuth>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponseAuth>> call, retrofit2.Response<ResponseContainer<ResponseAuth>> response) {
/**
 Impossible to catch error
 Log.i("TAG", String.valueOf(response.body() != null)); true
 Log.i("TAG", String.valueOf(response.isSuccessful())); true
 Log.i("TAG", String.valueOf(response.errorBody() == null)); true
 **/
                ResponseAuth responseAuth = response.body().getResponse();
                if(responseAuth != null){
                    Log.i("TAG", "USER ID - " + String.valueOf(responseAuth.getUserId()));
                    Log.i("TAG", "TOKEN - " + responseAuth.getAccessToken());
                    Log.i("TAG", "REFRESH_TOKEN - " + responseAuth.getRefreshToken());
                    Log.i("TAG", "REFRESH_TOKEN - " + responseAuth.getRefreshToken());

                    SharedManager.addProperty(Constants.KEY_MY_ID, responseAuth.getUserId());
                    SharedManager.addProperty(Constants.KEY_ACCESS_TOKEN, responseAuth.getAccessToken());
                    SharedManager.addProperty(Constants.KEY_REFRESH_TOKEN, responseAuth.getRefreshToken());
                    SharedManager.addProperty(Constants.KEY_EXPIRED_AT, responseAuth.getExpiredAt());
                    SharedManager.addProperty(Constants.KEY_LOGIN, finalLoginText);
                    SharedManager.addProperty(Constants.KEY_PASSWORD, finalPasswordText);


                    SharedManager.addBooleanProperty("login", true);
                    launchApp();

                } else {
                    Toast.makeText(LoginActivity.this, "response wrong", Toast.LENGTH_SHORT).show();
                    Log.i("TAG", "error");

                    // some error
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponseAuth>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "failure " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
/*
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("email", "vedmitry7@gmail.com")
                .add("password", "123456789")
                .build();

        Request request = new Request.Builder().url("http://192.168.0.104/api/auth.authorize")
                .post(body)
                .build();

        okhttp3.Response response = null;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", "FAILURE - " + e.getLocalizedMessage());
                Log.i("TAG", "FAILURE - " + e.getPostId());
                Log.i("TAG", "FAILURE - " + e.getCause());

                if(e instanceof ConnectException){
                    Log.i("TAG", "FAILURE - conn exc" );
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


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


                String userId = null;
                String acccesToken = null;
                String refreshToken = null;
                String expiriedAt = null;

                try {
                     userId = innerObject.getString("user_id");
                     acccesToken = innerObject.getString("access_token");
                     refreshToken = innerObject.getString("refresh_token");
                     expiriedAt = innerObject.getString("expired_at");

                    PersistantStorage.addProperty(Constants.KEY_MY_ID, userId);
                    PersistantStorage.addProperty(Constants.KEY_ACCESS_TOKEN, acccesToken);
                    PersistantStorage.addProperty(Constants.KEY_REFRESH_TOKEN, refreshToken);

                    Log.i("TAG", userId + " " + acccesToken +  " " + refreshToken + " " + expiriedAt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();

            }
        });*/
    }

    void launchApp(){
        Intent intent = new Intent(LoginActivity.this, MainActivity3.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CODE_FORGOT_PASSWORD){
            if(resultCode == RESULT_OK){
                final AlertDialog.Builder alert = new AlertDialog.Builder(
                        this);
                alert.setMessage("Пароль успешно изменен");
                alert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                alert.show();
            }
        }
    }
}
