package de.ozzc.iot.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by ocan on 21.04.2016.
 */
public class MetaData {

    @SerializedName("desired")
    private Map<String, Timestamp> desired;

    @SerializedName("reported")
    private Map<String, Timestamp> reported;

    public Map<String, Timestamp> getDesired() {
        return desired;
    }

    public void setDesired(Map<String, Timestamp> desired) {
        this.desired = desired;
    }

    public Map<String, Timestamp> getReported() {
        return reported;
    }

    public void setReported(Map<String, Timestamp> reported) {
        this.reported = reported;
    }
}
