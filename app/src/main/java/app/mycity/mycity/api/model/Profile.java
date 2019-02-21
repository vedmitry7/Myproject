package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("bdate")
    @Expose
    private String birthday;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("online")
    @Expose
    private Integer online;
    @SerializedName("screen_name")
    @Expose
    private String screenName;
    @SerializedName("photo_130")
    @Expose
    private String photo130;
    @SerializedName("photo_780")
    @Expose
    private String photo780;
    @SerializedName("photo_360")
    @Expose
    private String photo360;
    @SerializedName("photo_550")
    @Expose
    private String photo550;

    @SerializedName("comment")
    @Expose
    private Comment comment;

    @SerializedName("is_subscription")
    @Expose
    private Integer isSubscription;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Integer getIsSubscription() {
        return isSubscription;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setIsSubscription(Integer isSubscription) {
        this.isSubscription = isSubscription;
    }

    public String getPhoto360() {
        return photo360;
    }

    public void setPhoto360(String photo360) {
        this.photo360 = photo360;
    }

    public String getPhoto550() {
        return photo550;
    }

    public void setPhoto550(String photo550) {
        this.photo550 = photo550;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
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

    @Override
    public String toString() {
        return "Profile - " + id + " " + firstName + " " + lastName;
    }
}