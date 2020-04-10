package com.example.demo.offheap;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Cleaner implements Closeable {

    private static final String FIELD_NAME_THE_UNSAFE = "theUnsafe";
    private static final Unmapper UNMAP;
    private final AtomicInteger referenceCount;
    private ByteBuffer buffer;

    static {
        String javaVersion = System.getProperty("java.version");
        log.info("current jdk version:{}", javaVersion);
        Unmapper unmap = null;
        try {
            // >=JDK9 class sun.misc.Unsafe { void invokeCleaner(ByteBuffer buf) }
            final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field f = unsafeClass.getDeclaredField(FIELD_NAME_THE_UNSAFE);
            f.setAccessible(true);
            final Object theUnsafe = f.get(null);
            final Method method = unsafeClass.getDeclaredMethod("invokeCleaner", ByteBuffer.class);

            unmap = buffer -> method.invoke(theUnsafe, buffer);
        } catch (Exception e) {
            log.info("Could not access 'Unsafe.invokeCleaner' method, falling back to <=JDK8 impl java version:{}", javaVersion, e);
        }
        if (unmap == null) {
            try {
                // <=JDK8 class DirectByteBuffer { sun.misc.Cleaner cleaner(Buffer buf) }
                //        then call sun.misc.Cleaner.clean
                final Class<?> directByteBufferClass = Class.forName("java.nio.DirectByteBuffer");
                Method getCleaner = directByteBufferClass.getMethod("cleaner");
                getCleaner.setAccessible(true);
                final Class<?> cleanerClass = Class.forName("sun.misc.Cleaner");
                Method clean = cleanerClass.getMethod("clean");
                clean.setAccessible(true);
                unmap = buffer -> {
                    Object cleaner = getCleaner.invoke(buffer);

                    if (cleaner != null) {
                        clean.invoke(cleaner);
                    }
                };
            } catch (Exception e) {
                log.warn("Could not access 'DirectByteBuffer.cleaner' method");
            }
        }

        UNMAP = unmap;
    }

    protected static void clean(ByteBuffer buffer) {

        if (UNMAP != null && buffer != null) {
            try {
                UNMAP.unmap(buffer);
            } catch (Exception e) {
                log.info("Could not unmap buffer", e);
            }
        }
    }

    Cleaner(ByteBuffer buffer) {
        this.referenceCount = new AtomicInteger();
        this.buffer = buffer;
    }

    public Cleaner reference() {
        referenceCount.incrementAndGet();
        return this;
    }

    @Override
    public void close() {
        if (buffer != null) {
            if (referenceCount.decrementAndGet() == 0) {
                clean(buffer);
            }
            buffer = null;
        }
    }

    private interface Unmapper {
        void unmap(ByteBuffer buffer) throws Exception;
    }


}