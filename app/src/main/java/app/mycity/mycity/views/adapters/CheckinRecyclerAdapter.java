package app.mycity.mycity.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Photo;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckinRecyclerAdapter extends RecyclerView.Adapter<CheckinRecyclerAdapter.ViewHolder> {


    List<Photo> photoList;

    public CheckinRecyclerAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_row, parent, false);

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
        }
    }

    public void update(List<Photo> photos){
        photoList = photos;
        notifyDataSetChanged();
        Log.d("TAG", "update Photo recycler");
    }
}
