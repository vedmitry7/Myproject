package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Dialog {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("out")
    @Expose
    private Integer out;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("online")
    @Expose
    private Integer online;
    @SerializedName("date")
    @Expose
    private Integer date;
    @SerializedName("read")
    @Expose
    private Integer read;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getOut() {
        return out;
    }

    public void setOut(Integer out) {
        this.out = out;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
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
