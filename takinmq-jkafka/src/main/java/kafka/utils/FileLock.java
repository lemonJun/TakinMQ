package kafka.utils;

import kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;

/**
 * A file lock a la flock/funlock
 * <p/>
 * The given path will be created and opened if it doesn't exist.
 */
public class FileLock {
    public final File file;

    public FileLock(File file) {
        this.file = file;

        try {
            file.createNewFile(); // create the file if it doesn't exist
            channel = new RandomAccessFile(file, "rw").getChannel();
        } catch (IOException e) {
            throw new KafkaException(e);
        }
    }

    private FileChannel channel;
    private java.nio.channels.FileLock flock = null;

    Logger logger = LoggerFactory.getLogger(FileLock.class);

    /**
     * Lock the file or throw an exception if the lock is already held
     */
    public void lock() {
        try {
            synchronized (this) {
                logger.trace("Acquiring lock on {}", file.getAbsolutePath());
                flock = channel.lock();
            }
        } catch (IOException e) {
            throw new KafkaException(e);
        }
    }

    /**
     * Try to lock the file and return true if the locking succeeds
     */
    public boolean tryLock() {
        synchronized (this) {
            logger.trace("Acquiring lock on {}", file.getAbsolutePath());
            try {
                // weirdly this method will return null if the lock is held by another
                // process, but will throw an exception if the lock is held by this process
                // so we have to handle both cases
                flock = channel.tryLock();
                return flock != null;
            } catch (OverlappingFileLockException e) {
                return false;
            } catch (IOException e) {
                throw new KafkaException(e);
            }
        }
    }

    /**
     * Unlock the lock if it is held
     */
    public void unlock() {
        synchronized (this) {
            logger.trace("Releasing lock on {}", file.getAbsolutePath());
            if (flock != null)
                try {
                    flock.release();
                } catch (IOException e) {
                    throw new KafkaException(e);
                }
        }
    }

    /**
     * Destroy this lock, closing the associated FileChannel
     */
    public void destroy() {
        synchronized (this) {
            unlock();
            Utils.closeQuietly(channel);
        }
    }
}
