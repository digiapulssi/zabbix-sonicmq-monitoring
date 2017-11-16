package com.digia.monitoring.sonicmq.monitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class SonicMQMonitorConfiguration {
    private String location;
    private String domain;
    private String username;
    private String password;
    private long timeout = 60000;
    private List<String> collectors;
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public List<String> getCollectors() {
        return collectors;
    }

    public void setCollectors(List<String> collectors) {
        this.collectors = collectors;
    }

    @Override
    public String toString() {
        String password = (this.password != null && !this.password.isEmpty()) ? "<REDACTED>" : "";
        StringBuilder builder = new StringBuilder();
        builder.append("SonicMQMonitorConfiguration[location=")
            .append(location)
            .append(", domain=")
            .append(domain)
            .append(", username=")
            .append(username)
            .append(", password=")
            .append(password)
            .append(", timeout=")
            .append(timeout)
            .append(", collectors=")
            .append(collectors)
            .append("]");
        return builder.toString();
    }
    
    public static SonicMQMonitorConfiguration load(InputStream in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(SonicMQMonitorConfiguration.class);
        return reader.readValue(in);
    }
    
    public static SonicMQMonitorConfiguration load(String configFile) throws IOException {
        FileInputStream in = new FileInputStream(configFile);
        try {
            return load(in);
        } finally {
            in.close();
        }
    }
}
