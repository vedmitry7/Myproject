package app.mycity.mycity.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TopUsersRecyclerAdapter extends RecyclerView.Adapter<TopUsersRecyclerAdapter.ViewHolder> {

    List<User> userList;

    public TopUsersRecyclerAdapter(List<User> userList) {
        this.userList = userList;
        Log.d("TAG", "rec created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_user_row, parent, false);
        Log.d("TAG", "rec created");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = userList.get(position).getFirstName() + " " + userList.get(position).getLastName();

        holder.name.setText(name);

        Picasso.get().load(userList.get(position).getPhoto780()).into(holder.image);

        holder.position.setText(String.valueOf(position+1));

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

        @BindView(R.id.positionTopCount)
        TextView position;

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
