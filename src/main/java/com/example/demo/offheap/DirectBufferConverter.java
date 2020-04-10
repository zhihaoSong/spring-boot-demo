package com.example.demo.offheap;



import java.nio.ByteBuffer;

/**
 * 数据序列化转换
 *
 * @param <V>
 */
public abstract class DirectBufferConverter<V> {

    /**
     * clean direct数据
     *
     * @param direct
     */
    public void dispose(ByteBuffer direct) {

        Cleaner.clean(direct);
    }

    public ByteBuffer to(V from) {
        if (from == null) {
            return null;
        }
        //对象转换数组
        byte[] bytes = toBytes(from);
        // ByteBuffer bf1 =  ByteBuffer.wrap(bytes); //堆内内存
        ByteBuffer bf = ByteBuffer.allocateDirect(bytes.length);
       // System.out.println( ((DirectBuffer) bf).address());
        bf.put(bytes);
        bf.flip();
        return bf;
    }

    abstract public byte[] toBytes(V value);

    abstract public V toObject(byte[] value);

    public V from(ByteBuffer to) {
        if (to == null) {
            return null;
        }
        byte[] bs = new byte[to.capacity()];
        to.get(bs);
        to.flip();
        return toObject(bs);
    }

}