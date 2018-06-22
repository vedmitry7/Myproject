package app.mycity.mycity.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import app.mycity.mycity.DataStore;
import app.mycity.mycity.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DateFragment extends Fragment {

    DataStore dataStore;

/*    @BindView(R.id.firstName)
    EditText firstName;

    @BindView(R.id.secondName)
    EditText secondName;

    @BindView(R.id.spinnerNumberDay)
    Spinner day;

    @BindView(R.id.spinnerNumberMonth)
    Spinner month;

    @BindView(R.id.spinnerNumberYear)
    Spinner year;*/

    @BindView(R.id.bday)
    TextView bday;

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

      /*  String[] days = new String[31];
        for (int i = 0; i < days.length; i++) {
            days[i] = String.valueOf(i+1);
        }

        MyCustomAdapter daysAdapter = new MyCustomAdapter(getActivity(), R.layout.spinner_row, days);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(daysAdapter);
        day.setSelection(0);

        String[] months = new String[12];
        for (int i = 0; i < months.length; i++) {
            months[i] = String.valueOf(i+1);
        }

        MyCustomAdapter monthAdapter = new MyCustomAdapter(getActivity(), R.layout.spinner_row, months);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(monthAdapter);
        month.setSelection(0);

        String[] years = new String[118];
        for (int i = 0; i < years.length; i++) {
            years[i] = String.valueOf(i+1900);
        }

        MyCustomAdapter yearsAdapter = new MyCustomAdapter(getActivity(), R.layout.spinner_row, years);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearsAdapter);
        year.setSelection(100);*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataStore = (DataStore) context;
    }

    @OnClick(R.id.dateFragmentNext)
    public void next(View view){
        dataStore.nextStep();
        Log.i("TAG", "next");
    }


    public class MyCustomAdapter extends ArrayAdapter<String> {

        private String[] date;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            date = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            LayoutInflater inflater = DateFragment.this.getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = row.findViewById(R.id.spinnertext);
            label.setText(date[position]);

            return row;
        }
    }
}
