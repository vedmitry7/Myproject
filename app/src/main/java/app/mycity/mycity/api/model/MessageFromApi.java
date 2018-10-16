package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageFromApi {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("from_id")
    @Expose
    private Integer fromId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("out")
    @Expose
    private Integer out;
    @SerializedName("date")
    @Expose
    private Integer date;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("read")
    @Expose
    private Integer read;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOut() {
        return out;
    }

    public void setOut(Integer out) {
        this.out = out;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

}
