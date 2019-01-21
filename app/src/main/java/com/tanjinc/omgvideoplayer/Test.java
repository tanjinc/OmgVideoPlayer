package com.tanjinc.omgvideoplayer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author by tanjincheng, Date on 18-11-16.
 */
public class Test {
    static ArrayList<Integer> list = new ArrayList<Integer>();


    public static void main(String[] args)  {
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        Thread thread1 = new Thread(){
            public void run() {
                synchronized (list) {

                    Iterator<Integer> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Integer integer = iterator.next();
                        System.out.println(integer);
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            };
        };
        Thread thread2 = new Thread(){
            public void run() {
                synchronized (list) {

                    Iterator<Integer> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Integer integer = iterator.next();
                        if (integer == 2)
                            iterator.remove();
                    }
                }
            };
        };
        thread1.start();
        thread2.start();
    }
}