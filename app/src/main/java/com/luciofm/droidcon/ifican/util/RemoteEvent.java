package com.luciofm.droidcon.ifican.util;

/**
 * Created by luciofm on 10/16/14.
 */
public class RemoteEvent {
    public static final int EVENT_PREV = 0;
    public static final int EVENT_NEXT = 1;
    public static final int EVENT_BACK = 2;
    public static final int EVENT_ADVANCE = 3;

    public int type;

    public RemoteEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
