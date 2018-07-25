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

import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.Photo;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckinRecyclerAdapter extends RecyclerView.Adapter<CheckinRecyclerAdapter.ViewHolder> {


    public static final int GRID_LAYOUT = 0;
    public static final int LINEAR_LAYOUT = 1;

    List<Photo> photoList;
    List<Likes> likeList;

    int layout;
    Context context;

    public CheckinRecyclerAdapter(List<Photo> photoList, List<Likes> likeList) {
        this.photoList = photoList;
        this.likeList = likeList;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        Log.i("TAG", "LAYOUT = " + layout);


        if(context==null){
            context = parent.getContext();
        }
        if(layout == LINEAR_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_row_list, parent, false);
            return new ViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_row_grid, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("TAG", "BIND = " + position);
        Picasso.get()
                .load(photoList.get(position).getPhoto780())
                .into(holder.photo);

        if(holder.likesCount!=null)
        holder.likesCount.setText(String.valueOf(likeList.get(position).getCount()));

        if(holder.likeIcon!=null){
            if(likeList.get(position).getUserLikes()==1){
                holder.likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_black_18dp));
            } else {
                holder.likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_outline_grey600_18dp));
            }
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photoRowImageView)
        ImageView photo;

        @Nullable
        @BindView(R.id.placeLabel)
        TextView place;

        @Nullable
        @BindView(R.id.likeIcon)
        ImageView likeIcon;

        @Nullable
        @BindView(R.id.likesCount)
        TextView likesCount;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void update(List<Photo> photos){
        photoList = photos;
        notifyDataSetChanged();
        Log.d("TAG", "update Photo recycler");
    }
}
