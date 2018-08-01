package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseLike {
    @SerializedName("likes")
    @Expose
    private Integer likes;

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

}
