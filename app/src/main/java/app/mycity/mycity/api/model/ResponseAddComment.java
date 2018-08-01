package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseAddComment {
    @SerializedName("comment_id")
    @Expose
    private String commentId;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
