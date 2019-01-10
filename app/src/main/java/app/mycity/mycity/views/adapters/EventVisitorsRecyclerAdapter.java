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
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventVisitorsRecyclerAdapter extends RecyclerView.Adapter<EventVisitorsRecyclerAdapter.ViewHolder> {

    List<Profile> profileList;

    int layout;
    Context context;

    public EventVisitorsRecyclerAdapter(List<Profile> postList) {
        this.profileList = postList;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_visitor_photo, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("TAG21", "bind visitors" + position + " " + profileList.get(position).getPhoto130());
        Picasso.get()
                .load(profileList.get(position).getPhoto360())
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return profileList.size();
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
                    EventBus.getDefault().post(new EventBusMessages.OpenUser(profileList.get(getAdapterPosition()).getId()));
                }
            });

        }

    }

    public void update(List<Profile> posts){
        profileList = posts;
        notifyDataSetChanged();
        Log.d("TAG21", "update Photo recycler " + profileList);
    }
}
