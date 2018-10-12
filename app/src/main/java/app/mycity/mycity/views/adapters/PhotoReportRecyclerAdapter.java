package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoReportRecyclerAdapter extends RecyclerView.Adapter<PhotoReportRecyclerAdapter.ViewHolder> {

    List<Photo> photoList;

    int layout;
    Context context;

    String albumId;

    public PhotoReportRecyclerAdapter(List<Photo> photoList, String albumId) {
        this.photoList = photoList;
        this.albumId = albumId;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_row_grid, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get()
                .load(photoList.get(position).getPhoto780())
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photoRowImageView)
        ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.PhotoReportPhotoClick(getAdapterPosition()));
                }
            });
        }
    }

    public void update(List<Photo> posts){
        photoList = posts;
        notifyDataSetChanged();
        Log.d("TAG21", "update Photo recycler " + photoList);
    }
}
