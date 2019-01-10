package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    List<Post> postList;
    Map profiles;
    Map<String, Group> groups;

    int layout;
    Context context;
    ImageClickListener imageClickListener;

    public interface ImageClickListener{
        void onClick(int position);
    }

    public void setImageClickListener(ImageClickListener imageClickListener){
        this.imageClickListener = imageClickListener;
    }

    public FeedRecyclerAdapter(List<Post> postList, Map profiles, Map groups) {
        this.postList = postList;
        this.profiles = profiles;
        this.groups = groups;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(context==null)
            context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Picasso.get()
                .load(postList.get(position).getAttachments()
                .get(0).getPhoto780()).resize(App.dpToPx(context, 360), App.dpToPx(context, 360))
                .centerCrop()
                .into(holder.photo);

        if(profiles.containsKey(postList.get(position).getOwnerId())){
            Profile profile = (Profile) profiles.get(postList.get(position).getOwnerId());
            Picasso.get()
                    .load(profile.getPhoto130())
                    .resize(App.dpToPx(context, 36), App.dpToPx(context, 36))
                    .centerCrop()
                    .into(holder.ownerImage);
            holder.name.setText(profile.getFirstName()+ " " + profile.getLastName());
        }

        for (Map.Entry item : groups.entrySet())
        {
            Log.d("TAG", position + " Bind - " + item.getKey() + " " + ((Group)item.getValue()).getName());
        }

        if(groups.containsKey(postList.get(position).getPlaceId())){
            String name = (groups.get(postList.get(position).getPlaceId())).getName();
            Log.d("TAG", "groups contains " + postList.get(position).getPlaceId() + " so write " + name);
            holder.place.setText((groups.get(postList.get(position).getPlaceId())).getName());
        } else {
            Log.d("TAG", "groups doesnt contains " + postList.get(position).getPlaceId());
            holder.place.setText("albumName absent");

        }

        holder.time.setText(Util.getDatePretty(postList.get(position).getDate()));

        if(holder.likesCount!=null)
            holder.likesCount.setText(String.valueOf(postList.get(position).getLikes().getCount()));

        if(holder.likeIcon!=null){
            if(postList.get(position).getLikes().getUserLikes()==1){
                holder.likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_black_18dp));
                holder.likeIcon.setColorFilter(context.getResources().getColor(R.color.colorAccentRed));
                holder.likesCount.setTextColor(context.getResources().getColor(R.color.colorAccentRed));
            } else {
                holder.likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_outline_grey600_18dp));
                holder.likeIcon.setColorFilter(context.getResources().getColor(R.color.grey600));
                holder.likesCount.setTextColor(context.getResources().getColor(R.color.black_67percent));
            }
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.feedImage)
        ImageView photo;

        @BindView(R.id.commentButton)
        ImageView commentButton;

        @BindView(R.id.placeLabel)
        TextView place;

        @Nullable
        @BindView(R.id.likeIcon)
        ImageView likeIcon;

        @Nullable
        @BindView(R.id.feedPhoto)
        ImageView ownerImage;

        @BindView(R.id.feedName)
        TextView name;

        @BindView(R.id.feedPostTime)
        TextView time;

        @Nullable
        @BindView(R.id.likesCount)
        TextView likesCount;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if(photo!=null){
                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                   //     imageClickListener.onClick(getAdapterPosition());
                    }
                });
            }

            if(likeIcon != null){
                likeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMessages.LikePost(
                                postList.get(getAdapterPosition()).getId().toString(),
                                postList.get(getAdapterPosition()).getOwnerId().toString(),
                                getAdapterPosition()));
                    }
                });
            }

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.OpenComments(
                            postList.get(getAdapterPosition()).getId().toString(),
                            postList.get(getAdapterPosition()).getOwnerId().toString(), "post"));
                }
            });

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.OpenUser(
                            postList.get(getAdapterPosition()).getOwnerId()));
                }
            });

            place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG21", "click albumName - " + getAdapterPosition());
                    if(getAdapterPosition()!=-1){
                        if(groups.containsKey(postList.get(getAdapterPosition()).getPlaceId())){
                            Log.d("TAG21", "click albumName contains " );
                            EventBus.getDefault().post(new EventBusMessages.OpenPlace(
                                            groups.get(postList.get(getAdapterPosition()).getPlaceId()).getId()));
                        } else {
                            Log.d("TAG21", "click albumName not contains" );
                        }
                    } else {
                        Log.d("TAG21", "click albumName -1  - " + getAdapterPosition());
                    }
                }
            });


        }
    }

    public void update(List<Post> posts, Map profiles, Map groups){
        postList = posts;
        this.profiles = profiles;
        this.groups = groups;

        notifyDataSetChanged();
        Log.d("TAG", "update Photo recycler - " + groups.size());
    }
}
