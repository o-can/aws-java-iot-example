package de.ozzc.iot.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by ocan on 21.04.2016.
 */
public class MetaData {

    @SerializedName("desired")
    private Map<String, TimeStamp> desired;

    @SerializedName("reported")
    private Map<String, TimeStamp> reported;

    public Map<String, TimeStamp> getDesired() {
        return desired;
    }

    public void setDesired(Map<String, TimeStamp> desired) {
        this.desired = desired;
    }

    public Map<String, TimeStamp> getReported() {
        return reported;
    }

    public void setReported(Map<String, TimeStamp> reported) {
        this.reported = reported;
    }
}
