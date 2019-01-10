package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rate {

    @SerializedName("all")
    @Expose
    private RatePart all;
    @SerializedName("quality")
    @Expose
    private RatePart quality;
    @SerializedName("interior")
    @Expose
    private RatePart interior;
    @SerializedName("service")
    @Expose
    private RatePart service;
    @SerializedName("price")
    @Expose
    private RatePart price;

    public RatePart getAll() {
        return all;
    }

    public void setAll(RatePart all) {
        this.all = all;
    }

    public RatePart getQuality() {
        return quality;
    }

    public void setQuality(RatePart quality) {
        this.quality = quality;
    }

    public RatePart getInterior() {
        return interior;
    }

    public void setInterior(RatePart interior) {
        this.interior = interior;
    }

    public RatePart getService() {
        return service;
    }

    public void setService(RatePart service) {
        this.service = service;
    }

    public RatePart getPrice() {
        return price;
    }

    public void setPrice(RatePart price) {
        this.price = price;
    }
}
