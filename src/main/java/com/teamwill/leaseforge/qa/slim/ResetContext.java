package com.teamwill.leaseforge.qa.slim;

public class ResetContext extends AbstractScript {
    public ResetContext() {
        ContextHolder.INSTANCE.reset();
    }
}
