package de.ozzc.iot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ocan on 21.04.2016.
 */
public class ResponseState extends RequestState {

    @SerializedName("metadata")
    private MetaData metaData;

    @SerializedName("timestamp")
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public MetaData getMetadata() {
        return metaData;
    }

    public void setMetadata(MetaData metadata) {
        this.metaData = metadata;
    }
}
