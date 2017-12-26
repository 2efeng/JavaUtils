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

    public static Object getMessage() {
        synchronized (msg) {
            while (msg.isEmpty()) {
                try {
                    msg.wait(100);
                } catch (InterruptedException ie) {
                    //TODO ie to logMsg.log
                    ie.printStackTrace();
                }
                return null;
            }
            return msg.removeFirst();
        }
    }

}
