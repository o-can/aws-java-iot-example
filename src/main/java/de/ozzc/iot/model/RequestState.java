package de.ozzc.iot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ocan on 21.04.2016.
 */
public class RequestState {

    @SerializedName("state")
    private State state;

    @SerializedName("clientToken")
    private String clientToken;

    @SerializedName("version")
    private Integer version;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
