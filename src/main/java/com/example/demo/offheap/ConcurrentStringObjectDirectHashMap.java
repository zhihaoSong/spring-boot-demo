package com.example.demo.offheap;

import java.io.*;

/**
 * 简单流转换
 */
public class ConcurrentStringObjectDirectHashMap extends ConcurrentDirectHashMap<String, Object> {

    @Override
    protected byte[] convertObjectToBytes(Object value) {
        return serialize(value);
    }

    @Override
    protected Object convertBytesToObject(byte[] value) {
        return deserialize(value);
    }

    /**
     * 序列化对象，转换成字节数组
     *
     * @param obj
     * @return
     */
    static byte[] serialize(Object obj) {
        byte[] result = null;
        ByteArrayOutputStream fos = null;
        ObjectOutputStream o = null;

        try {
            fos = new ByteArrayOutputStream();
            o = new ObjectOutputStream(fos);
            o.writeObject(obj);
            result = fos.toByteArray();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            closeQuietly(fos, o);
        }

        return result;
    }

    /**
     * 反序列化字节数字，转换成对象
     *
     * @param bytes
     * @return
     */
    static Object deserialize(byte[] bytes) {
        InputStream fis = null;
        ObjectInputStream o = null;

        try {
            fis = new ByteArrayInputStream(bytes);
            o = new ObjectInputStream(fis);
            return o.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        } finally {
            closeQuietly(fis, o);
        }

        return null;
    }

    /**
     * 安全关闭io流
     *
     * @param closeables
     */
    public static void closeQuietly(Closeable... closeables) {

        if (closeables == null || closeables.length == 0) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
