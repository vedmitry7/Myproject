package app.mycity.mycity.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsLongListRecyclerAdapter extends RecyclerView.Adapter<FriendsLongListRecyclerAdapter.ViewHolder> {

    List<User> userList;

    public FriendsLongListRecyclerAdapter(List<User> userList) {
        this.userList = userList;
        Log.d("TAG", "rec created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = userList.get(position).getFirstName() + " " + userList.get(position).getLastName();

        holder.name.setText(position + "     " + name);

        Picasso.get().load("http://192.168.0.104/img/avatar.png").into(holder.image);
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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
          /*  itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", userList.get(getAdapterPosition()).getFirstName() + " open profile");
                    EventBus.getDefault().post(new EventBusMessages.OpenUser(userList.get(getAdapterPosition()).getId()));
                }
            });*/
        }
    }

    public void update(List<User> users){
        userList = users;
        notifyDataSetChanged();
        Log.d("TAG", "update rec");
    }
}
