package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Visits {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("user_visit")
    @Expose
    private Integer userVisit;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getUserVisit() {
        return userVisit;
    }

    public void setUserVisit(Integer userVisit) {
        this.userVisit = userVisit;
    }
}
