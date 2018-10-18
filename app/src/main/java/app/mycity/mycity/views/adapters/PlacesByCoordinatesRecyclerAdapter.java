package app.mycity.mycity.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlacesByCoordinatesRecyclerAdapter extends RecyclerView.Adapter<PlacesByCoordinatesRecyclerAdapter.ViewHolder> {

    List<Place> placeList;

    int selectedItem = -1;


    public PlacesByCoordinatesRecyclerAdapter(List<Place> placeList) {
        this.placeList = placeList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item_by_coordinates, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get().load(placeList.get(position).getPhoto780()).into(holder.image);

        holder.name.setText(placeList.get(position).getName());

        if(position==selectedItem){
            holder.radioButton.setChecked(true);
        } else {
            holder.radioButton.setChecked(false);
        }
    }

    public String getSelectedPlaceId(){
        if(selectedItem>=0) {
            return placeList.get(selectedItem).getId();
        } else {
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.placeImage)
        CircleImageView image;

        @BindView(R.id.placeName)
        TextView name;

        @BindView(R.id.radioButton)
        RadioButton radioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void update(List<Place> places) {
        placeList = places;
        selectedItem = 0;
        notifyDataSetChanged();
        Log.d("TAG", "update Photo recycler");
    }
}
