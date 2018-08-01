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

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    List<Post> postList;
    Map profiles;

    int layout;
    Context context;
    ImageClickListener imageClickListener;

    public interface ImageClickListener{
        void onClick(int position);
    }

    public void setImageClickListener(ImageClickListener imageClickListener){
        this.imageClickListener = imageClickListener;
    }

    public FeedRecyclerAdapter(List<Post> postList, Map profiles) {
        this.postList = postList;
        this.profiles = profiles;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(postList.get(position).getAttachments().get(0).getPhoto780()).into(holder.photo);

        if(profiles.containsKey(postList.get(position).getOwnerId())){
            Profile profile = (Profile) profiles.get(postList.get(position).getOwnerId());
            Picasso.get()
                    .load(profile.getPhoto130())
                    .into(holder.ownerImage);
            holder.name.setText(profile.getFirstName()+ " " + profile.getLastName());
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

        @Nullable
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
                            postList.get(getAdapterPosition()).getOwnerId().toString()));
                }
            });
        }

    }

    public void update(List<Post> posts){
        postList = posts;
        notifyDataSetChanged();
        Log.d("TAG", "update Photo recycler");
    }
}
