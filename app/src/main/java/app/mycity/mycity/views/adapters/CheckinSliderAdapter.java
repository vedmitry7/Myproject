package app.mycity.mycity.views.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.views.fragments.SimpleFragment;

public class CheckinSliderAdapter extends PagerAdapter {

    Context mContext;
    List<Post> postList;
    String postId;

    Map<Integer, SimpleExoPlayer> map = new HashMap();

    public CheckinSliderAdapter(Context context,  List<Post> postList) {
        mContext = context;
        this.postList = postList;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o==view;
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {

        Log.d("TAG24", " init item " + i);


        if(postList.get(i).getAttachments().get(0).getType().equals("video")){
            Log.d("TAG24", " video ");
            LayoutInflater inflater = (LayoutInflater)   mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.exo_player, null);

            SimpleExoPlayerView playerView = view.findViewById(R.id.videoView);

            SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(mContext),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(false);
            MediaSource mediaSource = buildMediaSource(Uri.parse(postList.get(i).getAttachments().get(0).getVideoOrig()));
            player.prepare(mediaSource, true, false);
            ((ViewPager) container).addView(view);
            map.put(i, player);
            Log.d("TAG24", " added");
            return playerView;

        } else {
            Log.d("TAG24", " photo ");
            PhotoView mImageView = new PhotoView(mContext);
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Picasso.get().load(postList.get(i).getAttachments().get(0).getPhoto780()).into(mImageView);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.ClickOnSliderImage());
                }
            });
            ((ViewPager) container).addView(mImageView, 0);
            Log.d("TAG24", " added");
            return mImageView;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        Log.d("TAG24", " destroy " + i);
        ((ViewPager) container).removeView((View) obj);
    }

    public void update(List<Post> postList){
        this.postList = postList;
    }

    public void selectPosition(int position) {

        Log.d("TAG24", " selected " + position);

        if(map.containsKey(position-1)){
            SimpleExoPlayer player = map.get(position-1);
            releasePlayer(player);
            Log.d("TAG24", "release " + (position-1));
        }
        if(map.containsKey(position+1)){
            SimpleExoPlayer player = map.get(position+1);
            releasePlayer(player);
            Log.d("TAG24", "release " + (position+1));
        }

     /*   if(map.containsKey(position)) {
            SimpleExoPlayer player = map.get(position);
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }*/
    }

    private void releasePlayer(SimpleExoPlayer player) {

        if (player != null) {

          //  player.setPlayWhenReady(false);
          //  player.getPlaybackState();
            //player.seekTo(0);
            //player.stop();
            //player.release();
        }
    }

    public void releaseAllPlayers(){
        Log.d("TAG24", "release All" );
        for (SimpleExoPlayer p :map.values()
                ) {
            p.getPlaybackState();
            p.stop();
            p.release();

        }
    }
}
