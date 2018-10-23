package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Comment;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    List<Comment> commentList;
    Map profiles;

    int layout;
    Context context;
    CommnentClickListener commnentClickListener;

    public interface CommnentClickListener{
        void deleteComment(int position);
    }

    public void setCommentClickListener(CommnentClickListener commnentClickListener){
        this.commnentClickListener = commnentClickListener;
    }

    public CommentsRecyclerAdapter(List<Comment> commentList, Map profiles) {
        this.commentList = commentList;
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      //  Picasso.get().load(commentList.get(position).getText()).into(holder.ownerImage);

        if(profiles.containsKey(commentList.get(position).getFromId())){
            Profile profile = (Profile) profiles.get(commentList.get(position).getFromId());
            Log.d("TAG21", "profile - " + profile.getFirstName() + " " + profile.getLastName());
            Picasso.get()
                    .load(profile.getPhoto130())
                    .into(holder.ownerImage);
            holder.name.setText(profile.getFirstName()+ " " + profile.getLastName());
        } else {
            Log.d("TAG21", "not in profiles");
            if(commentList.get(position).getFromId()== SharedManager.getProperty(Constants.KEY_MY_ID)){
                Log.d("TAG21", "add own picture");
                Picasso.get()
                        .load(SharedManager.getProperty(Constants.KEY_PHOTO_130))
                        .into(holder.ownerImage);
                holder.name.setText(SharedManager.getProperty(Constants.KEY_MY_FULL_NAME));
            }
        }

        holder.time.setText(Util.getDatePretty(commentList.get(position).getDate()));

        holder.text.setText(commentList.get(position).getText());

        if(holder.likesCount!=null)
            holder.likesCount.setText(String.valueOf(commentList.get(position).getLikes().getCount()));

        if(holder.likeIcon!=null){
            if(commentList.get(position).getLikes().getUserLikes()==1){
                holder.likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_black_18dp));
                holder.likeIcon.setColorFilter(context.getResources().getColor(R.color.colorAccentRed));
                holder.likesCount.setTextColor(context.getResources().getColor(R.color.colorAccentRed));
            } else {
                holder.likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_outline_grey600_18dp));
                holder.likeIcon.setColorFilter(context.getResources().getColor(R.color.black_30percent));
                holder.likesCount.setTextColor(context.getResources().getColor(R.color.black_67percent));
            }
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.likeIcon)
        ImageView likeIcon;

        @Nullable
        @BindView(R.id.commentPhoto)
        ImageView ownerImage;

        @BindView(R.id.commentName)
        TextView name;

        @BindView(R.id.commentPostTime)
        TextView time;

        @BindView(R.id.commentText)
        TextView text;

        @Nullable
        @BindView(R.id.likesCount)
        TextView likesCount;

        @BindView(R.id.commentsSettings)
        ImageView settings;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.LikeComment(
                            commentList.get(getAdapterPosition()).getId().toString(),
                            commentList.get(getAdapterPosition()).getOwnerId().toString(),
                            getAdapterPosition()));
                }
            });
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(profiles.containsKey(commentList.get(getAdapterPosition()).getFromId())){
                        EventBus.getDefault().post(new EventBusMessages.OpenUser(((Profile) profiles.get(commentList.get(getAdapterPosition()).getFromId())).getId()));
                    }
                }
            };

            name.setOnClickListener(clickListener);
            ownerImage.setOnClickListener(clickListener);

            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.popupmenu);
                    MenuItem delete = popupMenu.getMenu().findItem(R.id.deleteComment);
                    MenuItem complain = popupMenu.getMenu().findItem(R.id.complainComment);
                    Log.d("TAG21", "click delete " + commentList.get(getAdapterPosition()).getFromId());
                    Log.d("TAG21", "click delete " + SharedManager.getProperty(Constants.KEY_MY_ID));

                    if(commentList.get(getAdapterPosition()).getFromId().equals(SharedManager.getProperty(Constants.KEY_MY_ID))){
                        delete.setVisible(true);
                    } else {
                        complain.setVisible(true);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.deleteComment:
                                    commnentClickListener.deleteComment(getAdapterPosition());
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

    }

    public void update(List<Comment> comments, Map profiles){
        commentList = comments;
        this.profiles.putAll(profiles);
        notifyDataSetChanged();
        Log.d("TAG21", "update Photo recycler p - " + profiles.size());
    }
}
