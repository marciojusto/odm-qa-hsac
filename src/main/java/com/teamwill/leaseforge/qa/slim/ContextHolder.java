package com.teamwill.leaseforge.qa.slim;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class ContextHolder {
    public static final ContextHolder INSTANCE = new ContextHolder();

    private final Map<String, Object> contextData = new HashMap<>();

    public <T> T get(String key) {
        return (T) contextData.get(key);
    }

    @SneakyThrows
    public void reset() {
        contextData.clear();

        String jobName = System.getProperty("JOB_NAME");
        if (jobName == null) {
            String testedEnv = RunFitnesse.JOB_NAME.substring(RunFitnesse.JOB_NAME.length() - 3);
            put("TESTED_ENV", testedEnv);
            put("TESTED_ENV_SRC", "DEFAULT");
        } else {
            String testedEnv = jobName.substring(jobName.length() - 3);
            put("TESTED_ENV", testedEnv);
            put("TESTED_ENV_SRC", "app.properties");
        }

    }

    public void put(String key, Object value) {
        contextData.put(key, value);
    }

    public Map<String, Object> allContextData() {
        return new HashMap<>(contextData);
    }
}
