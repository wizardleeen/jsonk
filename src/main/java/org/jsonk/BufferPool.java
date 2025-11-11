package org.jsonk;

import java.util.concurrent.ConcurrentLinkedQueue;

class BufferPool {

    public static final BufferPool instance = new BufferPool();
    public static final int BUF_SIZE = 4096;
    private final ConcurrentLinkedQueue<char[]> buffers = new ConcurrentLinkedQueue<>();
    private static final int MAX_IDLE = 1024;
    private static final int INITIAL = 16;

    private BufferPool() {
        for (int i = 0; i < INITIAL; i++) {
            buffers.offer(new char[BUF_SIZE]);
        }
    }

    public char[] take() {
//        return new char[256];
        var buf = buffers.poll();
        if (buf == null)
            buf = new char[BUF_SIZE];
        return buf;
    }

    public void ret(char[] buf) {
        if (buffers.size() < MAX_IDLE)
            buffers.offer(buf);
    }

}
