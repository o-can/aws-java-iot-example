package de.ozzc.iot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ocan on 21.04.2016.
 */
public class Timestamp {

    @SerializedName("timestamp")
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
