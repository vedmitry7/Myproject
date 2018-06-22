package app.mycity.mycity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import app.mycity.mycity.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.textRegistration)
    TextView textView;

    @BindView(R.id.login)
    EditText login;

    @BindView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.textRegistration)
    public void onClick(View v){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void forgetPassword(View view) {

    }


    @OnClick(R.id.enterButton)
    public void enter(View v) {
       /* ApiFactory.getApi().auth(login.getText().toString(), password.getText().toString()).enqueue(new Callback<FullResponse>() {
            @Override
            public void onResponse(Call<FullResponse> call, Response<FullResponse> response) {
                app.mycity.mycity.api.model.Response resp = response.body().getResponse();
                Log.i("TAG", resp.toString());
                Log.i("TAG", resp.toString());

            }

            @Override
            public void onFailure(Call<FullResponse> call, Throwable t) {

            }
        });*/

   /*    ApiFactory.getApi().auth(login.getText().toString(), password.getText().toString()).enqueue(new Callback<JsonObject>() {
           @Override
           public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
               Log.i("TAG", response.toString());
              JsonObject object = response.body();
               Log.i("TAG", object.getAsString());

           }

           @Override
           public void onFailure(Call<JsonObject> call, Throwable t) {

           }
       });*/

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("email", "vedmitry7@gmail.com")
                .add("password", "mycitypass")
                .build();

        Request request = new Request.Builder().url("http://192.168.0.104/api/auth.authorize")
                .post(body)
                .build();

        okhttp3.Response response = null;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

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


                String code = null;

                try {
                     code = innerObject.getString("user_id");
                    Log.i("TAG", code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
