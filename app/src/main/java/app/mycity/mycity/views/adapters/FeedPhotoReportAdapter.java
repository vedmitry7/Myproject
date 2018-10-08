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
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Albume;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedPhotoReportAdapter extends RecyclerView.Adapter<FeedPhotoReportAdapter.ViewHolder> {

    List<Albume> albumsList;
    Context context;

    public FeedPhotoReportAdapter(List<Albume> albumsList){
        this.albumsList = albumsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(context==null)
            context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_feed_checkin_item, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Picasso.get()
                .load(albumsList.get(position).getPhoto780())
                //.centerCrop()
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.feedImage)
        ImageView photo;

        @BindView(R.id.placeLabel)
        TextView place;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.OpenPhotoReport(String.valueOf(albumsList.get(getAdapterPosition()).getId())));
                }
            });
        }
    }

    public void update(List<Albume> albumsList){
        this.albumsList = albumsList;

        notifyDataSetChanged();
    }
}
