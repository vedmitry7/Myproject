package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import app.mycity.mycity.api.model.Album;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedPhotoReportAdapter extends RecyclerView.Adapter<FeedPhotoReportAdapter.ViewHolder> {

    List<Album> albumsList;
    Map<String, Group> groups;
    Context context;

    public FeedPhotoReportAdapter(List<Album> albumsList, Map groups){
        this.albumsList = albumsList;
        this.groups = groups;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(context==null)
            context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chronichs_item, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Picasso.get()
                .load(albumsList.get(position).getPhoto780())
                //.centerCrop()
                .into(holder.photo);



        if(groups.containsKey(albumsList.get(position).getGroupId())){
            Group group = (Group) groups.get(albumsList.get(position).getGroupId());
            Picasso.get()
                    .load(group.getPhoto130())
                    .resize(App.dpToPx(context, 36), App.dpToPx(context, 36))
                    .centerCrop()
                    .into(holder.groupPhoto);

            holder.placeName.setText(group.getName());

            holder.albumName.setText(albumsList.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.feedImage)
        ImageView photo;

        @BindView(R.id.groupPhoto)
        ImageView groupPhoto;

        @BindView(R.id.placeName)
        TextView placeName;

        @BindView(R.id.placeLabel)
        TextView albumName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.OpenPhotoReport(albumsList.get(getAdapterPosition())));
                }
            });
        }
    }

    public void update(List<Album> albumsList, Map groups){
        this.albumsList = albumsList;
        this.groups = groups;
        notifyDataSetChanged();
    }
}
