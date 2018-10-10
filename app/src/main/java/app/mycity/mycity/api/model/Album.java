package app.mycity.mycity.api.model;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Album {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("date_created")
    @Expose
    private Long dateCreated;
    @SerializedName("date_updated")
    @Expose
    private Integer dateUpdated;
    @SerializedName("thumb_id")
    @Expose
    private Integer thumbId;
    @SerializedName("privacy_comment")
    @Expose
    private Integer privacyComment;
    @SerializedName("photo_50")
    @Expose
    private String photo50;
    @SerializedName("photo_70")
    @Expose
    private String photo70;
    @SerializedName("photo_780")
    @Expose
    private String photo780;
    @SerializedName("photo_orig")
    @Expose
    private String photoOrig;
    @SerializedName("count_photos")
    @Expose
    private Integer countPhotos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Integer dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Integer getThumbId() {
        return thumbId;
    }

    public void setThumbId(Integer thumbId) {
        this.thumbId = thumbId;
    }

    public Integer getPrivacyComment() {
        return privacyComment;
    }

    public void setPrivacyComment(Integer privacyComment) {
        this.privacyComment = privacyComment;
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

    public Integer getCountPhotos() {
        return countPhotos;
    }

    public void setCountPhotos(Integer countPhotos) {
        this.countPhotos = countPhotos;
    }
}
