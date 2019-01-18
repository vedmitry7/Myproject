package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Parent {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("from_id")
    @Expose
    private String fromId;
    @SerializedName("owner_id")
    @Expose
    private String ownerId;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("from_group")
    @Expose
    private String fromGroup;
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
    @SerializedName("attachments")
    @Expose
    private List<Photo> attachments = null;

    @SerializedName("post")
    @Expose
    private Post post;

    @SerializedName("event")
    @Expose
    private Post event;

    public Post getEvent() {
        return event;
    }

    public void setEvent(Post event) {
        this.event = event;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(String fromGroup) {
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
}
