package indi.hjhk.global;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class GlobalLock {
    private final String lockFilename;
    private FileLock lock = null;

    public GlobalLock(String lockName) {
        this.lockFilename = lockName+".lock";
    }

    public boolean lock(){
        if (lock != null) return true;
        try {
            FileOutputStream fostream = new FileOutputStream(lockFilename);
            FileChannel channel = fostream.getChannel();
            lock = channel.tryLock();
        } catch (IOException e) {
            return false;
        }
        return lock != null;
    }

    public void unlock(){
        if (lock != null){
            try {
                lock.release();
            } catch (IOException ignored) {
            }
            lock = null;
        }
    }

}
