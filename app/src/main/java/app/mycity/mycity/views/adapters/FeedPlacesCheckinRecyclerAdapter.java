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
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedPlacesCheckinRecyclerAdapter extends RecyclerView.Adapter<FeedPlacesCheckinRecyclerAdapter.ViewHolder> {

    List<Post> postList;

    int layout;
    Context context;

    public FeedPlacesCheckinRecyclerAdapter(List<Post> postList) {
        this.postList = postList;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_photo_row_grid, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get()
                .load(postList.get(position).getAttachments().get(0).getPhoto780())
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return postList.size();
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG21", "open780 - " + postList.get(getAdapterPosition()).getAttachments().get(0).getPhoto780());
                    EventBus.getDefault().post(
                            new EventBusMessages.ShowImage(getAdapterPosition()));
                }
            });

        }

    }

    public void update(List<Post> posts){
        postList = posts;
        notifyDataSetChanged();
        Log.d("TAG21", "update Photo recycler " + postList);
    }
}
