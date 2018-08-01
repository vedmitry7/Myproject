package app.mycity.mycity.views.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import app.mycity.mycity.Constants;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseAuth;
import app.mycity.mycity.api.model.ResponseContainer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.loginActRegistrationButtonTv)
    TextView textView;

    @BindView(R.id.loginLabel)
    TextView label;

    @BindView(R.id.loginActLoginEt)
    EditText login;

    @BindView(R.id.loginActPasswordEt)
    EditText password;

    int selection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        Typeface type = Typeface.createFromAsset(getAssets(),"abril_fatface_regular.otf");
        label.setTypeface(type);
    }

    @OnClick(R.id.loginActRegistrationButtonTv)
    public void onClick(View v){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

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
    }

    @OnClick(R.id.loginActForgetPasswordButtonTv)
    public void forgetPassword(View view) {
    }


    @OnClick(R.id.loginActEnterButton)
    public void enter(View v) {

        ApiFactory.getApi().authorize(login.getText().toString(), password.getText().toString()).enqueue(new retrofit2.Callback<ResponseContainer<ResponseAuth>>() {
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

                    SharedManager.addProperty(Constants.KEY_MY_ID, responseAuth.getUserId());
                    SharedManager.addProperty(Constants.KEY_ACCESS_TOKEN, responseAuth.getAccessToken());
                    SharedManager.addProperty(Constants.KEY_REFRESH_TOKEN, responseAuth.getRefreshToken());
                    SharedManager.addProperty(Constants.KEY_EXPIRED_AT, responseAuth.getExpiredAt());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();

                } else {
                    Log.i("TAG", "error");

                    // some error
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponseAuth>> call, Throwable t) {

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
}
