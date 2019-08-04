package com.jinshuai;

import com.jinshuai.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author: JS
 * @date: 2019/08/03
 * @description: 定时清理超时socket
 */
@Slf4j
public class IdleCleaner extends Thread {

    private static final int IDLE_TIME = 5;

    IdleCleaner(String name) {
        super(name);
    }

    @Override
    public void run() {
        while (true) {
            Iterator<WeakReference<HttpSession>> iterator = EventLoop.sessions.iterator();
            while (iterator.hasNext()) {
                HttpSession session = iterator.next().get();
                if (session == null) {
                    iterator.remove();
                } else {
                    session.checkIdle(IDLE_TIME);
                }
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                log.error("sleep error", e);
            }
        }

    }
}