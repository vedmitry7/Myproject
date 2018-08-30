package app.mycity.mycity.views.fragments.places;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Place;
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
    private Place place;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_fragment, container, false);

        ButterKnife.bind(this, view);
        Log.d("TAG21", "onCreateView");

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

        return view;
    }


    void createPagerAdapter(){
        PlacePagerAdapter adapter = new PlacePagerAdapter(getChildFragmentManager(), place);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // UI updates must run on MainThread
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Place place) {
        this.place = place;
        Log.d("TAG21", "PLACE - " + place.getName());

        if(this.place != null) {
           // EventBus.getDefault().removeStickyEvent(place);
        }
        createPagerAdapter();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("TAG21", "onCreateView");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
