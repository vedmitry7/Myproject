package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("from_id")
    @Expose
    private Integer fromId;
    @SerializedName("owner_id")
    @Expose
    private Integer ownerId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("from_group")
    @Expose
    private Integer fromGroup;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("date")
    @Expose
    private Integer date;
    @SerializedName("friends_only")
    @Expose
    private Integer friendsOnly;
    @SerializedName("latitude")
    @Expose
    private Integer latitude;
    @SerializedName("longitude")
    @Expose
    private Integer longitude;
    @SerializedName("likes")
    @Expose
    private Likes likes;
    @SerializedName("attachments")
    @Expose
    private List<Photo> attachments = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(Integer fromGroup) {
        this.fromGroup = fromGroup;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getFriendsOnly() {
        return friendsOnly;
    }

    public void setFriendsOnly(Integer friendsOnly) {
        this.friendsOnly = friendsOnly;
    }

    public Integer getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public List<Photo> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Photo> attachments) {
        this.attachments = attachments;
    }

    public Likes getLikes() {
        return likes;
    }

    public void setLikes(Likes likes) {
        this.likes = likes;
    }
}