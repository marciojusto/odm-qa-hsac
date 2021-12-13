package com.teamwill.leaseforge.qa.slim;

public class Validations {

    public boolean thatTheIsLessThanMs(long responseTime, long maxResponseTime) {
        return responseTime < maxResponseTime;
    }

}
