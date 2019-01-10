package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseEventVisitors {

    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("items")
    @Expose
    private List<Profile> items = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Profile> getItems() {
        return items;
    }

    public void setItems(List<Profile> items) {
        this.items = items;
    }
}
