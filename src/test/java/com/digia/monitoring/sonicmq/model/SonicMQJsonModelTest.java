package com.digia.monitoring.sonicmq.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonicsw.mf.mgmtapi.runtime.IAgentManagerProxy;

/**
 * <p>Test cases for model JSON serialization.</p>
 * 
 * <p>These tests are only to verify that model objects are JSON serializable. They do not test that output structure
 * matches some specific model.</p>
 * 
 * <p>JSON results are output to logger so they can be used as reference of produced elements.</p>
 * 
 * @author Sami Pajunen
 */
public class SonicMQJsonModelTest {
    
    private Logger logger = LoggerFactory.getLogger(SonicMQJsonModelTest.class);

    @Test
    public void testConnectionDataToJson() throws Exception {
        ConnectionMemberData child1 = new ConnectionMemberData();
        child1.addAttribute("name", "child1");
        ConnectionMemberData child2 = new ConnectionMemberData();
        child2.addAttribute("name", "child2");
        ArrayList<ConnectionMemberData> children = new ArrayList<ConnectionMemberData>();
        children.add(child1);
        children.add(child2);
        ConnectionMemberData member = new ConnectionMemberData(children);
        member.addAttribute("name", "parent");
        
        SubscriberData subscriber = new SubscriberData("subscription", "topic", 42, 4242);
        
        ConnectionData root = new ConnectionData("localhost", "test", Collections.singletonList(subscriber), Collections.singletonList(member));
        
        String json = convertToJson(root);
        logger.info(json);
    }

    @Test
    public void testDiscoveryItemsToJson() throws Exception {
        DiscoveryItems<NamedDiscoveryItem> items = new DiscoveryItems<NamedDiscoveryItem>();
        items.add(new NamedDiscoveryItem("broker1"));
        items.add(new NamedDiscoveryItem("broker2"));
        
        String json = convertToJson(items);
        logger.info(json);
    }
    
    @Test
    public void testSonicMQMonitoringDataToJson() throws Exception {
        SonicMQMonitoringData data = new SonicMQMonitoringData("domain");
        
        String json = convertToJson(data);
        logger.info(json);
    }
    
    @Test
    public void testDiscoveryItemDataToJson() throws Exception {
        DiscoveryItemData itemData = new DiscoveryItemData();
        itemData.setValue(IAgentManagerProxy.SYSTEM_POLLTHREADS_CURRENTPOOLSIZE_METRIC_ID.getAbsoluteName(), 31);
        itemData.setValue(IAgentManagerProxy.SYSTEM_POLLTHREADS_MAXPOOLSIZE_METRIC_ID.getAbsoluteName(), 100);
        itemData.setValue(IAgentManagerProxy.SYSTEM_POLLTHREADS_POOLWAITS_METRIC_ID.getAbsoluteName(), 2);
        itemData.setValue("broker.State", "STARTED");
        
        String json = convertToJson(itemData);
        logger.info(json);
    }
    
    private String convertToJson(Object value) throws JsonGenerationException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }
}
