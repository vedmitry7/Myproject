package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("owner_id")
    @Expose
    private Integer ownerId;
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
    @SerializedName("latitude")
    @Expose
    private Integer latitude;
    @SerializedName("longitude")
    @Expose
    private Integer longitude;
    @SerializedName("can_edit")
    @Expose
    private Integer canEdit;
    @SerializedName("photo_50")
    @Expose
    private String photo50;
    @SerializedName("photo_70")
    @Expose
    private String photo70;
    @SerializedName("photo_130")
    @Expose
    private String photo130;
    @SerializedName("photo_780")
    @Expose
    private String photo780;
    @SerializedName("photo_orig")
    @Expose
    private String photoOrig;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
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

    public Integer getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Integer canEdit) {
        this.canEdit = canEdit;
    }

    public String getPhoto50() {
        return photo50;
    }

    public void setPhoto50(String photo50) {
        this.photo50 = photo50;
    }

    public String getPhoto70() {
        return photo70;
    }

    public void setPhoto70(String photo70) {
        this.photo70 = photo70;
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