package de.ozzc.iot.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ocan on 21.04.2016.
 */
public class RequestStateTest {


    @Test
    public void testJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        RequestState requestState = new RequestState();
        State state = new State();

        state.setDesiredAttribute("attribute1", 2);
        state.setDesiredAttribute("attribute2", "string2");
        state.setDesiredAttribute("attributeN", true);

        Map<String, Object> reportedAttributes = new HashMap<>();
        reportedAttributes.put("attribute1", 1);
        reportedAttributes.put("attribute2", "string1");
        reportedAttributes.put("attributeN", false);
        state.setReportedAttributes(reportedAttributes);

        requestState.setState(state);
        requestState.setClientToken("FAHJ");
        requestState.setVersion(1);
        System.out.println(gson.toJson(requestState));

    }


}