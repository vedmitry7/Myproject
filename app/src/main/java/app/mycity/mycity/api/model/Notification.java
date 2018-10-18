package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Notification {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("date")
    @Expose
    private Integer date;
    @SerializedName("viewed")
    @Expose
    private Integer viewed;
    @SerializedName("feedback")
    @Expose
    private Profile feedback;
    @SerializedName("parents")
    @Expose
    private List<Parent> parents = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getViewed() {
        return viewed;
    }

    public void setViewed(Integer viewed) {
        this.viewed = viewed;
    }

    public Profile getFeedback() {
        return feedback;
    }

    public void setFeedback(Profile feedback) {
        this.feedback = feedback;
    }

    public List<Parent> getParents() {
        return parents;
    }

    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }
}
