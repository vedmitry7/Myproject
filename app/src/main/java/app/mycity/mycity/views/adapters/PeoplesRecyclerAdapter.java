package app.mycity.mycity.views.adapters;

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
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PeoplesRecyclerAdapter extends RecyclerView.Adapter<PeoplesRecyclerAdapter.ViewHolder> {

    List<User> userList;

    public PeoplesRecyclerAdapter(List<User> userList) {
        this.userList = userList;
        Log.d("TAG", "rec created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people, parent, false);
        Log.d("TAG", "rec created");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = userList.get(position).getFirstName() + " " + userList.get(position).getLastName();

        holder.name.setText(name);

        Picasso.get().load(userList.get(position).getPhoto780()).into(holder.image);

        //holder.position.setText(String.valueOf(position+1));

        holder.likesCount.setText(String.valueOf(userList.get(position).getCountLikes()));

        if(userList.get(position).getPlace()!=null){
            holder.placeName.setText(userList.get(position).getPlace().getName());
        } else {
            holder.placeName.setVisibility(View.GONE);
            holder.imageMarker.setVisibility(View.GONE);
        }

        Log.d("TAG21", "Bind FRIEND " + position);
        Log.i("TAG3","bind");
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.friendsRowName)
        TextView name;

        @BindView(R.id.friendsRowImage)
        CircleImageView image;

        @Nullable
        @BindView(R.id.positionTopCount)
        TextView position;

        @BindView(R.id.likesCount)
        TextView likesCount;

        @BindView(R.id.placeName)
        TextView placeName;

        @BindView(R.id.imageMarker)
        ImageView imageMarker;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG21", userList.get(getAdapterPosition()).getFirstName() + " open profile");
                    EventBus.getDefault().post(new EventBusMessages.OpenUser(userList.get(getAdapterPosition()).getId()));
                }
            });
        }
    }

    public void update(List<User> users){
        userList = users;
        Log.d("TAG21", "update rec - rec list = " + userList.size());
        Log.i("TAG3","All list recycler update");
        notifyDataSetChanged();
    }
}