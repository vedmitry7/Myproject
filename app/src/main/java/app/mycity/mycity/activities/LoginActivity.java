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

import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.FullResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    public void enter(View v){
        ApiFactory.getApi().auth(login.getText().toString(), password.getText().toString()).enqueue(new Callback<FullResponse>() {
            @Override
            public void onResponse(Call<FullResponse> call, Response<FullResponse> response) {
                app.mycity.mycity.api.model.Response resp = response.body().getResponse();
                Log.i("TAG", resp.toString());
            }

            @Override
            public void onFailure(Call<FullResponse> call, Throwable t) {

            }
        });
    }
}
