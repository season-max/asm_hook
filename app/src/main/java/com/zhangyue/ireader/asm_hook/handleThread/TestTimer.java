package com.zhangyue.ireader.asm_hook.handleThread;

import java.util.Timer;

public class TestTimer extends Timer {

    public TestTimer() {
        super();
    }

    public TestTimer(boolean isDaemon) {
        super(isDaemon);
    }

    public TestTimer(String name) {
        super(name);
    }

    public TestTimer(String name, boolean isDaemon) {
        super(name, isDaemon);
    }
}
