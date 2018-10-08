package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public class Place implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
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
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("screen_name")
    @Expose
    private String screenName;
    @SerializedName("is_closed")
    @Expose
    private Integer isClosed;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("cover_id")
    @Expose
    private Integer coverId;
    @SerializedName("cover_50")
    @Expose
    private String cover50;
    @SerializedName("cover_70")
    @Expose
    private String cover70;
    @SerializedName("cover_780")
    @Expose
    private String cover780;
    @SerializedName("cover_orig")
    @Expose
    private String coverOrig;
    @SerializedName("country_id")
    @Expose
    private Integer countryId;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("schedule")
    @Expose
    private String schedule;
    @SerializedName("instagram")
    @Expose
    private String instagram;
    @SerializedName("count_members")
    @Expose
    private Integer countMembers;
    @SerializedName("rate")
    @Expose
    private Integer rate;
    @SerializedName("count_rate")
    @Expose
    private Integer countRate;
    @SerializedName("date_create")
    @Expose
    private Integer dateCreate;
    @SerializedName("creator_id")
    @Expose
    private Integer creatorId;
    @SerializedName("deactivated")
    @Expose
    private Integer deactivated;
    @SerializedName("verified")
    @Expose
    private Integer verified;
    @SerializedName("is_member")
    @Expose
    private Integer isMember;
    @SerializedName("can_edit")
    @Expose
    private Integer canEdit;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("distance")
    @Expose
    private Object distance;

    @SerializedName("count_members_in_place")
    @Expose
    private Integer countMembersInPlace;

    public Integer getCountMembersInPlace() {
        return countMembersInPlace;
    }

    public void setCountMembersInPlace(Integer countMembersInPlace) {
        this.countMembersInPlace = countMembersInPlace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Integer getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Integer isClosed) {
        this.isClosed = isClosed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCoverId() {
        return coverId;
    }

    public void setCoverId(Integer coverId) {
        this.coverId = coverId;
    }

    public String getCover50() {
        return cover50;
    }

    public void setCover50(String cover50) {
        this.cover50 = cover50;
    }

    public String getCover70() {
        return cover70;
    }

    public void setCover70(String cover70) {
        this.cover70 = cover70;
    }

    public String getCover780() {
        return cover780;
    }

    public void setCover780(String cover780) {
        this.cover780 = cover780;
    }

    public String getCoverOrig() {
        return coverOrig;
    }

    public void setCoverOrig(String coverOrig) {
        this.coverOrig = coverOrig;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public Integer getCountMembers() {
        return countMembers;
    }

    public void setCountMembers(Integer countMembers) {
        this.countMembers = countMembers;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getCountRate() {
        return countRate;
    }

    public void setCountRate(Integer countRate) {
        this.countRate = countRate;
    }

    public Integer getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Integer dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(Integer deactivated) {
        this.deactivated = deactivated;
    }

    public Integer getVerified() {
        return verified;
    }

    public void setVerified(Integer verified) {
        this.verified = verified;
    }

    public Integer getIsMember() {
        return isMember;
    }

    public void setIsMember(Integer isMember) {
        this.isMember = isMember;
    }

    public Integer getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Integer canEdit) {
        this.canEdit = canEdit;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Object getDistance() {
        return distance;
    }

    public void setDistance(Object distance) {
        this.distance = distance;
    }

}
