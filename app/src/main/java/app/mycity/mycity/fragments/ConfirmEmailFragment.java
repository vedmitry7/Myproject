package app.mycity.mycity.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import app.mycity.mycity.DataStore;
import app.mycity.mycity.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmEmailFragment extends Fragment {

    DataStore dataStore;

    @BindView(R.id.codeEt)
    EditText code;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_email_fragment, container, false);

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
    public void confirmEmail(View view){
        Log.i("TAG", "confirmCode");
        dataStore.setCode(code.getText().toString());
        dataStore.checkEmailCode();
    }

}
