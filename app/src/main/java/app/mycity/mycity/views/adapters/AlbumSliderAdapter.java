package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.util.EventBusMessages;

public class AlbumSliderAdapter extends PagerAdapter {

    Context mContext;
    List<Photo> postList;
    String postId;

    public AlbumSliderAdapter(Context context, List<Photo> postList) {
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


    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        Log.d("TAG23", "init " + i);
        PhotoView mImageView = new PhotoView(mContext);
        //ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Picasso.get().load(postList.get(i).getPhoto780()).into(mImageView);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMessages.ClickOnSliderImage());
            }
        });
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }

    public void update(List<Photo> postList){
        this.postList = postList;
    }

}
