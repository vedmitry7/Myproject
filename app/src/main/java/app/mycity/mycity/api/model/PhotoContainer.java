package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoContainer {

    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("items")
    @Expose
    private List<Photo> photos = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
