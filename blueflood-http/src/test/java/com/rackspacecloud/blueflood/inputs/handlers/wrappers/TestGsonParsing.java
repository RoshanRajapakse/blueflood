package com.rackspacecloud.blueflood.inputs.handlers.wrappers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rackspacecloud.blueflood.inputs.handlers.HttpStatsDIngestionHandler;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestGsonParsing {
    
    private String json;
    
    @Before
    public void readJsonFile() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/resources/sample_payload.json")));
        String curLine = reader.readLine();
        while (curLine != null) {
            sb = sb.append(curLine);
            curLine = reader.readLine();
        }
        json = sb.toString();
    }
    
    @Test
    public void testLameButValidJSON() {
        String badJson = "{}";
        AggregatedPayload payload = HttpStatsDIngestionHandler.createPayload(badJson);
    }
    
    @Test(expected = JsonSyntaxException.class)
    public void testInvalidJSON() {
        String badJson = "{tenantId:}";
        AggregatedPayload payload = HttpStatsDIngestionHandler.createPayload(badJson);
    }
    
    @Test
    public void testBasicAggregatedPayload() {
        Gson gson = new Gson();
        AggregatedPayload payload = gson.fromJson(json, AggregatedPayload.class);

        Assert.assertNotNull(payload);
        Assert.assertEquals("333333", payload.getTenantId());
        Assert.assertEquals(1389211230L, payload.getTimestamp());
        Assert.assertEquals(15000L, payload.getFlushIntervalMillis());
        
        Assert.assertEquals(4, payload.getGauges().size());
        Assert.assertEquals(6, payload.getCounters().size());
        Assert.assertEquals(4, payload.getTimers().size());
        Assert.assertEquals(2, payload.getSets().size());
    }

    @Test
    public void testHistograms() {
        AggregatedPayload payload = new Gson().fromJson(json, AggregatedPayload.class);
        
        Assert.assertNotNull(payload);
        Map<String, AggregatedPayload.Timer> timers = asMap(payload.getTimers());
        
        Assert.assertEquals(4, timers.get("4444444.T1s").getHistogram().size());
        Assert.assertEquals(11, timers.get("3333333.T29s").getHistogram().size());
        Assert.assertEquals(11, timers.get("3333333.T200ms").getHistogram().size());
        
        // this one is non-existant in the json, but we do not want a null map.
        Assert.assertNotNull(timers.get("3333333.T10s").getHistogram());
        Assert.assertEquals(0, timers.get("3333333.T10s").getHistogram().size());
    }
    
    @Test
    public void testPercentiles() {
        AggregatedPayload payload = new Gson().fromJson(json, AggregatedPayload.class);
        
        Assert.assertNotNull(payload);
        Map<String, AggregatedPayload.Timer> timers = asMap(payload.getTimers());
        
        Assert.assertEquals(5, timers.get("4444444.T1s").getPercentiles().size());
        Assert.assertEquals(5, timers.get("3333333.T29s").getPercentiles().size());
        Assert.assertEquals(5, timers.get("3333333.T10s").getPercentiles().size());
        Assert.assertEquals(5, timers.get("3333333.T200ms").getPercentiles().size());
    }
    
    private static Map<String, AggregatedPayload.Timer> asMap(Collection<AggregatedPayload.Timer> timers) {
        Map<String, AggregatedPayload.Timer> map = new HashMap<String, AggregatedPayload.Timer>(timers.size());
        for (AggregatedPayload.Timer timer : timers)
            map.put(timer.getName(), timer);
        return map;
    }
}
