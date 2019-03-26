package app.mycity.mycity.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuccessCreatePlace {
    @SerializedName("success")
    @Expose
    private Integer success;

    @SerializedName("place_id")
    @Expose
    private String id;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
