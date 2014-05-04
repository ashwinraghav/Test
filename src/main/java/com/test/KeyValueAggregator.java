package com.test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amohanganesh on 5/3/14.
 */
public class KeyValueAggregator implements Runnable {
    String logline;
    public static final String pattern = "(?i)(\".*?)(.+?)(\")";

    public static ConcurrentHashMap<String, Val> store = new ConcurrentHashMap<String, Val>();

    public KeyValueAggregator(String logLine) {
        this.logline = logLine;
    }



    /**
     * Maintains an in memory HashMap of Keys and Values
     * TODO: the hashmap is not persistent. Susceptible to JVM crashes
     * @param
     */
    @Override
    public void run() {
        try {
            String [] parts = logline.split("\"")[1].split("=");

            String key = parts[0];
            int value = Integer.valueOf(parts[1]);

            Val val;
            if (store.containsKey(key)) {
                val = store.get(key);
            } else {
                val = new Val();
            }

            val.bumpFreq();
            val.bumpSum(value);
            System.out.println(Thread.currentThread().getId() + " : Updated Key Value Store");

            //hashmap is thread safe
            store.put(key, val);
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getId() + " : Unable to update store. Incorrect Log format");
        }

    }


    public static class Val {
        Long freq;
        Long sum;

        public Val() {
            this.sum = 0l;
            this.freq = 0l;
        }

        public void bumpFreq() {
            this.freq++;
        }

        public void bumpSum(long val) {
            this.sum += val;
        }

    }
}
