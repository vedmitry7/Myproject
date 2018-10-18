package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceCategoryResponce {

    @SerializedName("response")
    @Expose
    private List<PlaceCategory> response = null;

    public List<PlaceCategory> getResponse() {
        return response;
    }

    public void setResponse(List<PlaceCategory> response) {
        this.response = response;
    }
}
