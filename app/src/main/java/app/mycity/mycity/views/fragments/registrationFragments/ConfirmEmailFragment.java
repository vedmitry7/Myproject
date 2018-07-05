package app.mycity.mycity.views.fragments.registrationFragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import app.mycity.mycity.views.activities.RegisterActivityDataStore;
import app.mycity.mycity.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmEmailFragment extends Fragment {

    RegisterActivityDataStore dataStore;

    @BindView(R.id.codeEt)
    EditText code;
    @BindView(R.id.confirmEmailFragmentTextViewInfo)
    TextView info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_email, container, false);

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
    public void confirmEmail(View view){
        Log.i("TAG", "confirmCode");
        dataStore.setCode(code.getText().toString());
        dataStore.checkEmailCodeAndRegistration();
    }

    public void codeIsWrong(){
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                info.setTextColor(Color.parseColor("#ff0000"));
                info.setText("Код введен не верно");
            }
        });

    }

}
