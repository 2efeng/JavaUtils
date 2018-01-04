package com.hzf.utils.MsgManage;

import java.util.LinkedList;

public class MessageManage {

    /**
     * 缓存数据
     */
    private static final LinkedList<Object> msg = new LinkedList<>();

    public static void putMessage(Object obj) {
        synchronized (msg) {
            msg.add(obj);
            msg.notify();
        }
    }

    public static int getMessageCount() {
        return msg.size();
    }

    public static Object getMessage() throws Exception {
        synchronized (msg) {
            while (msg.isEmpty() || getMessageCount() == 0) {
                msg.wait(100);
                return null;
            }
            return msg.removeFirst();
        }
    }

}
