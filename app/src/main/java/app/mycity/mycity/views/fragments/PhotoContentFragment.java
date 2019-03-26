package app.mycity.mycity.views.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.views.adapters.MyRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoContentFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.photoView)
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_content_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.get().load(getArguments().getString("link")).into(imageView);
    }

    public static PhotoContentFragment createInstance(String link) {
        PhotoContentFragment fragment = new PhotoContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("link", link);
        fragment.setArguments(bundle);
        return fragment;
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

}
