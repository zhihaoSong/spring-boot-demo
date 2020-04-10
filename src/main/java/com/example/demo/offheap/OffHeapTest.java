package com.example.demo.offheap;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class OffHeapTest {

    public static void main(String[] args) throws InterruptedException {

        DirectBufferMemoryImpl directBufferMemory = new DirectBufferMemoryImpl(300000);

        User u1 = new User();
        u1.name = "tony1";
        u1.password = "123456";
        directBufferMemory.put("test1", u1);

        User u2 = new User();
        u2.name = "tony2";
        u2.password = "123456";
        directBufferMemory.put("test2", u2);
        directBufferMemory.put("test1", u2);
//        for (int i = 0; i < 300000000; i++) {
//            TimeUnit.MILLISECONDS.sleep(100);
//            u2 = new User();
//            u2.name = "tony2"+i;
//            if(i%10==0){
//                String bbb = "ssbsb";
//                for (int j = 0; j <1000000 ; j++) {
//                    bbb+="bbbbbbbbbb";
//                }
//                u2.password = "123456"+i+bbb;
//            }else {
//                u2.password = "123456"+i+ Arrays.toString(new byte[10]);
//            }
//            directBufferMemory.put("test2"+i, u2);
//        }

        System.out.println(directBufferMemory.getIfPresent("test1",new User()));
        System.out.println(directBufferMemory.getIfPresent("test2",new User()));
        System.out.println(directBufferMemory.getIfPresent("test3",new User()));

        System.out.println("=======");
    }

}
