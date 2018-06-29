package app.mycity.mycity.fragments.registrationFragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import app.mycity.mycity.DataStore;
import app.mycity.mycity.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordFragment extends Fragment {

    DataStore dataStore;

    @BindView(R.id.passwordFragConfirmPasswordEt)
    EditText confirmPassword;

    @BindView(R.id.passwordFragPasswordEt)
    EditText password;

    @BindView(R.id.emailFragmentTextViewInfo)
    TextView info;

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
        dataStore = (DataStore) context;
    }

    @OnClick(R.id.passwordFragNext)
    public void setPassword(View view){
        dataStore.setPassword(password.getText().toString(), confirmPassword.getText().toString());
        dataStore.commitPassword();
    }
}
