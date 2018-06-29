package app.mycity.mycity.adapters;

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

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsRecyclerAdapter.ViewHolder> {

    List<User> userList;

    public FriendsRecyclerAdapter(List<User> userList) {
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
        Log.d("TAG", "on bind");
        String name = userList.get(position).getFirstName() + " " + userList.get(position).getLastName();

        holder.name.setText(name);

        Picasso.get().load(userList.get(position).getPhoto780()).into(holder.image);
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
        }
    }

    public void update(List<User> users){
        userList = users;
        notifyDataSetChanged();
        Log.d("TAG", "update rec");
    }
}
