package de.ozzc.iot.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ocan on 21.04.2016.
 */
public class State {

    @SerializedName("desired")
    private Map<String, Object> desiredAttributes;

    @SerializedName("reported")
    private Map<String, Object> reportedAttributes;

    @SerializedName("delta")
    private Map<String, Object> deltaAttributes;

    public Map<String, Object> getDesiredAttributes() {
        return desiredAttributes;
    }

    public void setDesiredAttributes(Map<String, Object> desiredAttributes) {
        this.desiredAttributes = desiredAttributes;
    }

    public Map<String, Object> getReportedAttributes() {
        return reportedAttributes;
    }

    public void setReportedAttributes(Map<String, Object> reportedAttributes) {
        this.reportedAttributes = reportedAttributes;
    }

    public Map<String, Object> getDeltaAttributes() {
        return deltaAttributes;
    }

    public void setDeltaAttributes(Map<String, Object> deltaAttributes) {
        this.deltaAttributes = deltaAttributes;
    }

    public Object getDesiredAttribute(String attribute) {
        if (attribute != null) {
            if(desiredAttributes != null) {
                return desiredAttributes.get(attribute);
            }
        }
        return null;
    }

    public void setDesiredAttribute(String attribute, Object value) {
        if (attribute != null) {
            if (desiredAttributes == null) {
                desiredAttributes = new HashMap<>();
            }
            desiredAttributes.put(attribute, value);
        }
    }

    public Object removeDesiredAttribute(String attribute) {
        if (attribute != null) {
            if(desiredAttributes != null) {
                return desiredAttributes.remove(attribute);
            }
        }
        return null;
    }
}
