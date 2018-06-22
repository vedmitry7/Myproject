package app.mycity.mycity.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;

import app.mycity.mycity.DataStore;
import app.mycity.mycity.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DateFragment extends Fragment {

    DataStore dataStore;

    @BindView(R.id.birthday)
    TextView birthday;

    @BindView(R.id.firstNameEt)
    EditText firstName;

    @BindView(R.id.secondNameEt)
    EditText secondName;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    private String sex = "2";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.male:
                        sex = "2";
                        break;
                    case R.id.female:
                        sex = "1";
                        break;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataStore = (DataStore) context;
    }

    @OnClick(R.id.dateFragmentNext)
    public void next(View view){
        dataStore.setInfo(firstName.getText().toString(), secondName.getText().toString(), birthday.getText().toString(), sex);
        dataStore.nextEmailStep();
        Log.i("TAG", "next");
    }

    @OnClick(R.id.imageViewSetDate)
    public void setDate(View view){
        Log.i("TAG", "setDate");
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Log.i("TAG", "Date " + i + " " + i1 + " " + i2);
                String date = i2+"."+i1+"."+i;
                birthday.setTextColor(Color.parseColor("#000000"));
                birthday.setText(date);
            }
        },
                mYear,
                mMonth,
                mDay)
                .show();
    }
}
