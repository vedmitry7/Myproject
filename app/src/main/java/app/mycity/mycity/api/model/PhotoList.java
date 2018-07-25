package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhotoList {

    @SerializedName("src")
    @Expose
    private String src;
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("type")
    @Expose
    private String type;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("src", src);
            jsonObject.put("width", width);
            jsonObject.put("height", height);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public JSONObject getJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("src", src);
            jsonObject.put("width", width);
            jsonObject.put("height", height);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}