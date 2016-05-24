/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 *
 * @author user
 */
public class ByteArrayUtilsDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            byte[] org = "This is a simple.".getBytes();
            byte[] search = "simple".getBytes();
            //int last = lastIndexOf(org, search, 19);
            byte[] mixed = ByteArrayUtils.append(org, search);
            long t1 = 0;
            //long t2 = 0;
            int f1 = 0;
            //int f2 = 0;

            for (int i = 0; i < 10000; i++) {
                long s1 = System.nanoTime();
                f1 = ByteArrayUtils.indexOf(org, search, 0);
                long s2 = System.nanoTime();
                t1 = t1 + (s2 - s1);
            }
            System.out.println("=====int互轉======");
            System.out.println("int 20 轉Byte：" + Arrays.toString(ByteArrayUtils.toByteArray(255, 1)));//1=1 byte
            System.out.println("byte 20 轉int：" + ByteArrayUtils.toInt(ByteArrayUtils.toByteArray(255, 1)));
            System.out.println("=====String轉換======");
            System.out.println("org byte轉String：" + ByteArrayUtils.toString(mixed));
            System.out.println("======Short轉換======");
            System.out.println("short轉byte："+ Arrays.toString(ByteArrayUtils.toByteArray(Short.MAX_VALUE)));
            System.out.println("byte轉short："+ ByteArrayUtils.toShort(ByteArrayUtils.toByteArray(Short.MAX_VALUE), 0));
            System.out.println("======Float轉換======");
            System.out.println("float轉byte："+ Arrays.toString(ByteArrayUtils.toByteArray(Float.MAX_VALUE,1)));
            System.out.println("byte轉float："+ ByteArrayUtils.tofloat(ByteArrayUtils.toByteArray(Float.MAX_VALUE,1)));
            System.out.println("======取代======");
            System.out.println("org內找出search並已YYY字串取代：" + ByteArrayUtils.toString(ByteArrayUtils.arrayReplace(org, search, new String("Replace Simple").getBytes(), 0)));
            System.out.println("======合併=======");
            System.out.println("org apend search：" + ByteArrayUtils.toString(ByteArrayUtils.append(org, search)));
            System.out.println("======搜尋=======");
            System.out.println("搜尋花費時間：" + t1 / 10000);
            System.out.println("搜尋到的字串起始位置：" + f1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
