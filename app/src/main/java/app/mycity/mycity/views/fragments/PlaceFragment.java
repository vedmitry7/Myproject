package app.mycity.mycity.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.mycity.mycity.R;
import app.mycity.mycity.views.adapters.PlacePagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.toolbarTitle)
    TextView title;

    @BindView(R.id.backButton)
    ImageView backButton;



    @BindView(R.id.placeViewPager)
    ViewPager viewPager;
    @BindView(R.id.placeTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.delivery)
    View delivery;
    @BindView(R.id.toolbarContent)
    LinearLayout toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_fragment, container, false);

        ButterKnife.bind(this, view);


        ImageView imageView = view.findViewById(R.id.place_image);
        //imageView.setImageResource(R.drawable.teapizdec);
        String imagePath = getArguments().getString("photo780");
        String name = getArguments().getString("name");
        title.setText(name);

        String placeId = getArguments().getString("placeId");

        Picasso.get().load(imagePath).into(imageView);

        final LinearLayout layout = view.findViewById(R.id.toolbarContent);
        layout.setVisibility(View.VISIBLE);

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    Log.d("TAG21", String.valueOf(verticalOffset));
                    Log.d("TAG21", String.valueOf(verticalOffset) + " collapsed");
                    title.setTextColor(Color.BLACK);
                    layout.setVisibility(View.VISIBLE);
                    delivery.setVisibility(View.GONE);
                    toolbar.setBackgroundColor(Color.WHITE);

                    backButton.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                } else if (verticalOffset == 0) {
                    Log.d("TAG21", String.valueOf(verticalOffset) + " = 0");
                    layout.setVisibility(View.VISIBLE);
                    title.setTextColor(Color.WHITE);
                    backButton.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
                    // Expanded
                } else {
                    // Somewhere in between
                    Log.d("TAG21", String.valueOf(verticalOffset) + " between");
                    delivery.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });


        PlacePagerAdapter adapter = new PlacePagerAdapter(getChildFragmentManager(), placeId);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
