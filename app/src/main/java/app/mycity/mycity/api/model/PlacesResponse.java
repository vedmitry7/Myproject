package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesResponse {
    @SerializedName("response")
    @Expose
    private List<Place> places = null;

    public List<Place> getResponse() {
        return places;
    }

    public void setResponse(List<Place> response) {
        this.places = response;
    }
}
