package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseComments {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("items")
    @Expose
    private List<Comment> items = null;
    @SerializedName("profiles")
    @Expose
    private List<Profile> profiles = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Comment> getItems() {
        return items;
    }

    public void setItems(List<Comment> items) {
        this.items = items;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
}
