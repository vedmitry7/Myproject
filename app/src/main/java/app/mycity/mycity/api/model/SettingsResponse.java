package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingsResponse {

    @SerializedName("like_comment")
    @Expose
    private Integer likeComment;
    @SerializedName("like_post")
    @Expose
    private Integer likePost;
    @SerializedName("comment_post")
    @Expose
    private Integer commentPost;
    @SerializedName("follow")
    @Expose
    private Integer follow;
    @SerializedName("new_message")
    @Expose
    private Integer newMessage;
    @SerializedName("place_event")
    @Expose
    private Integer placeEvent;
    @SerializedName("place_action")
    @Expose
    private Integer placeAction;

    public Integer getLikeComment() {
        return likeComment;
    }

    public void setLikeComment(Integer likeComment) {
        this.likeComment = likeComment;
    }

    public Integer getLikePost() {
        return likePost;
    }

    public void setLikePost(Integer likePost) {
        this.likePost = likePost;
    }

    public Integer getCommentPost() {
        return commentPost;
    }

    public void setCommentPost(Integer commentPost) {
        this.commentPost = commentPost;
    }

    public Integer getFollow() {
        return follow;
    }

    public void setFollow(Integer follow) {
        this.follow = follow;
    }

    public Integer getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(Integer newMessage) {
        this.newMessage = newMessage;
    }

    public Integer getPlaceEvent() {
        return placeEvent;
    }

    public void setPlaceEvent(Integer placeEvent) {
        this.placeEvent = placeEvent;
    }

    public Integer getPlaceAction() {
        return placeAction;
    }

    public void setPlaceAction(Integer placeAction) {
        this.placeAction = placeAction;
    }
}
