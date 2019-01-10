package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckinRecyclerAdapter extends RecyclerView.Adapter<CheckinRecyclerAdapter.ViewHolder> {


    public static final int GRID_LAYOUT = 0;
    public static final int LINEAR_LAYOUT = 1;

    List<Post> postList;
    Map<String, Group> groups;

    Context context;
    ImageClickListener imageClickListener;

    public interface ImageClickListener{
        void onClick(int position);
    }

    public void setImageClickListener(ImageClickListener imageClickListener){
        this.imageClickListener = imageClickListener;
    }

    public CheckinRecyclerAdapter(List<Post> postList, Map groups) {
        this.postList = postList;
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;


        if(context==null){
            context = parent.getContext();
        }

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_row_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int displayWidth = metrics.widthPixels;
        int paddings = App.dpToPx(context, 8);
        int pictureWidth = (displayWidth - paddings)/3;

        Log.d("TAG", "picture width - " + pictureWidth);


        if(postList.get(position).getAttachments()!=null && postList.get(position).getAttachments().get(0).getPhoto360()!=null) {
            Picasso.get()
                    .load(postList.get(position).getAttachments().get(0).getPhoto360())
                    .resize(pictureWidth, pictureWidth)
                    .centerCrop()
                    .into(holder.photo);
        } else {
            Picasso.get()
                    .load("/")
                    .placeholder(R.drawable.ic_baseline_error_outline_24px)
                    .into(holder.photo);
        }


        if(holder.likesCount!=null)
            holder.likesCount.setText(String.valueOf(postList.get(position).getLikes().getCount()));

        if(holder.commentText!=null)
            holder.commentText.setText(String.valueOf(postList.get(position).getComments().getCount()));

        if(holder.likeIcon!=null){
            if(postList.get(position).getLikes().getUserLikes()==1){
                holder.likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_black_18dp));
                holder.likeIcon.setColorFilter(context.getResources().getColor(R.color.colorAccentRed));
                holder.likesCount.setTextColor(context.getResources().getColor(R.color.colorAccentRed));
            } else {
                holder.likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_outline_grey600_18dp));
                holder.likesCount.setTextColor(context.getResources().getColor(R.color.black_67percent));
            }
        }


        if(holder.place!=null){
            if(groups.containsKey(postList.get(position).getPlaceId())){
                holder.place.setText((groups.get(postList.get(position).getPlaceId())).getName());
            } else {
                holder.place.setText("");
            }
        }
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
        @BindView(R.id.commentsCount)
        TextView commentText;

        @Nullable
        @BindView(R.id.commentButton)
        ImageView commentButton;

        @Nullable
        @BindView(R.id.likeIcon)
        ImageView likeIcon;

        @Nullable
        @BindView(R.id.likesCount)
        TextView likesCount;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            final int adapterPosition = getAdapterPosition();

            if(photo!=null){
                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //imageClickListener.onClick(getAdapterPosition());
                        EventBus.getDefault().post(
                                new EventBusMessages.ShowImage(getAdapterPosition()));
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

            if(commentButton!=null){
                commentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new EventBusMessages.OpenComments(postList.get(getAdapterPosition()).getId(), postList.get(getAdapterPosition()).getOwnerId(), "post"));
                    }
                });
            }
            if(place!=null)
                place.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(groups.containsKey(postList.get(adapterPosition).getPlaceId())){
                            EventBus.getDefault().post(new EventBusMessages.OpenPlace(groups.get(postList.get(adapterPosition)).getId()));
                        }
                    }
                });


        }
    }

    public void update(List<Post> posts, Map groups){
        postList = posts;
        this.groups = groups;
        notifyDataSetChanged();
        Log.d("TAG", "update Photo recycler");
    }
}
