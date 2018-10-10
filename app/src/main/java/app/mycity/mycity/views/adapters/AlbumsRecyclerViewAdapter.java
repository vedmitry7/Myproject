package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Album;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.filter_desc_post.ExpandableLayout;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.ViewHolder> {

    private List<Album>albumes;
    Map map;
    Context context;
    boolean expend;

    // data is passed into the constructor
    public AlbumsRecyclerViewAdapter(List<Album> data, Map albums) {
        this.albumes = data;
        map = albums;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(context==null)
            context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_albums_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("TAG21", position + " Albums BIND - " +  albumes.get(position).getTitle() + "expend - " + expend);
        holder.name.setText(albumes.get(position).getTitle());
        holder.date.setText(Util.getDate(albumes.get(position).getDateCreated()));

        Picasso.get().load(albumes.get(position).getPhotoOrig()).into(holder.preview);

        holder.count.setText(albumes.get(position).getCountPhotos() + " фото");

 /*       if(holder.expandableLayout.isExpanded()){
            holder.expendButton.setRotation(180);
        } else {
            holder.expendButton.setRotation(0);
        }*/


        if(expend){
            holder.expandableLayout.toggleExpansion();
            expend = false;
        }

        if(map.containsKey(albumes.get(position).getId())){
            Log.d("TAG21",  "MAP CONTAINS KEY - " + albumes.get(position).getId());
            LinearLayoutManager mLayoutManager = new GridLayoutManager(context, 3);
            int itemDecorationCount = holder.recyclerView.getItemDecorationCount();
            for (int i = 0; i < itemDecorationCount; i++) {
                holder.recyclerView.removeItemDecorationAt(0);
            }
            holder.recyclerView.addItemDecoration(new ImagesSpacesItemDecoration(3, App.dpToPx(context, 4), false));
            holder.recyclerView.setLayoutManager(mLayoutManager);
            AlbumRecyclerAdapter adapter = new AlbumRecyclerAdapter((List<Photo>) map.get(albumes.get(position).getId()));
            holder.recyclerView.setAdapter(adapter);
        } else {
            Log.d("TAG21",  "MAP DOESN'T CONTAINS KEY - " + albumes.get(position).getId());
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return albumes.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.albumName)
        TextView name;

        @BindView(R.id.albumDate)
        TextView date;

        @BindView(R.id.albumPreview)
        ImageView preview;

        @BindView(R.id.albumButtonExpend)
        ImageView expendButton;

        @BindView(R.id.albumPhotoCount)
        TextView count;

        @BindView(R.id.album_expandable_layout)
        ExpandableLayout expandableLayout;

        @BindView(R.id.albumRecyclerView)
        RecyclerView recyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG21",  "Click");
                    if(!expandableLayout.isExpanded()){
                        Log.d("TAG21",  "not expended");
                    }

                    expandableLayout.toggleExpansion();
                    EventBus.getDefault().post(new EventBusMessages.LoadAlbum(albumes.get(getAdapterPosition()).getId(), getAdapterPosition()));
                    //    notifyDataSetChanged();
                }
            });


        }

    }

    boolean notFirst;
    public void updatePosition(List<Album> albumsList, Map albums, int adapterPosition) {
        this.albumes = albumsList;
        this.map = albums;
        //notifyDataSetChanged();
        if(!notFirst)
            expend = true;
        notFirst = true;
        notifyItemChanged(adapterPosition);
    }

    public void update(List<Album> albumes, Map map){
        this.albumes = albumes;
        this.map = map;
        notifyDataSetChanged();
    }
}