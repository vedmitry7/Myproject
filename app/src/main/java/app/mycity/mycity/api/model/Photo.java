package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("owner_id")
    @Expose
    private String ownerId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("album_id")
    @Expose
    private Integer albumId;
    @SerializedName("place_id")
    @Expose
    private Integer placeId;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("can_edit")
    @Expose
    private Integer canEdit;
    @SerializedName("photo_130")
    @Expose
    private String photo130;
    @SerializedName("photo_550")
    @Expose
    private String photo550;
    @SerializedName("photo_780")
    @Expose
    private String photo780;
    @SerializedName("photo_360")
    @Expose
    private String photo360;
    @SerializedName("photo_orig")
    @Expose
    private String photoOrig;
    @SerializedName("likes")
    @Expose
    private Likes likes;
    @SerializedName("comments")
    @Expose
    private Comments comments;

    @SerializedName("video_orig")
    @Expose
    private String videoOrig;

    public String getVideoOrig() {
        return videoOrig;
    }

    public void setVideoOrig(String videoOrig) {
        this.videoOrig = videoOrig;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoto550() {
        return photo550;
    }

    public void setPhoto550(String photo550) {
        this.photo550 = photo550;
    }

    public String getPhoto360() {
        return photo360;
    }

    public void setPhoto360(String photo360) {
        this.photo360 = photo360;
    }

    public Likes getLikes() {
        return likes;
    }

    public void setLikes(Likes likes) {
        this.likes = likes;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Integer canEdit) {
        this.canEdit = canEdit;
    }

    public String getPhoto130() {
        return photo130;
    }

    public void setPhoto130(String photo130) {
        this.photo130 = photo130;
    }

    public String getPhoto780() {
        return photo780;
    }

    public void setPhoto780(String photo780) {
        this.photo780 = photo780;
    }

    public String getPhotoOrig() {
        return photoOrig;
    }

    public void setPhotoOrig(String photoOrig) {
        this.photoOrig = photoOrig;
    }

}