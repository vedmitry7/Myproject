package app.mycity.mycity.views.fragments.registrationFragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import app.mycity.mycity.R;
import app.mycity.mycity.views.activities.RegisterActivityDataStore;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordFragment extends Fragment {

    RegisterActivityDataStore dataStore;

    @BindView(R.id.passwordFragConfirmPasswordEt)
    EditText confirmPassword;

    @BindView(R.id.passwordFragPasswordEt)
    EditText password;

    @BindView(R.id.emailFragmentTextViewInfo)
    TextView info;

    @BindView(R.id.passwordFragProgressBarContainer)
    ConstraintLayout progressBarContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataStore = (RegisterActivityDataStore) context;
    }

    @OnClick(R.id.passwordFragNext)
    public void setPassword(View view){
        dataStore.setPassword(password.getText().toString(), confirmPassword.getText().toString());
        dataStore.commitPassword();
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    public void wrongCodeLength(){
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                info.setTextColor(Color.parseColor("#ff0000"));
                info.setText("Не верная длина пароля");
                progressBarContainer.setVisibility(View.GONE);
            }
        });
    }


    public void codeNotMatch(){
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                info.setTextColor(Color.parseColor("#ff0000"));
                info.setText("Пароли не соответствуют");
                progressBarContainer.setVisibility(View.GONE);
            }
        });
    }
}
