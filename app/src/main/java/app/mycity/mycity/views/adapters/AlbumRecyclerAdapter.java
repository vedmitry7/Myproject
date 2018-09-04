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
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.Post;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.ViewHolder> {

    List<Photo> photoList;

    public AlbumRecyclerAdapter(List<Photo> postList) {
        this.photoList = postList;
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
                .load(photoList.get(position).getPhotoOrig())
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
        }

    }

    public void update(List<Photo> photos){
        photoList = photos;
        notifyDataSetChanged();
        Log.d("TAG21", "update Photo recycler " + photoList);
    }
}
