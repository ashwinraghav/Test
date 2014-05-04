package com.test;

/**
 * Created by amohanganesh on 5/3/14.
 */
public class PeriodicStoreDumper implements Runnable {

    int timeInterval;

    public PeriodicStoreDumper(int timeInterval){
        this.timeInterval = timeInterval;
    }

    /**
     * Dumps the hashmap to stdout and goes to sleep.
     */

    @Override
    public void run() {
        while (true) {
            for (String key : KeyValueAggregator.store.keySet()) {
                KeyValueAggregator.Val val = KeyValueAggregator.store.get(key);
                System.out.println(String.format(Thread.currentThread().getId() + " : %s = (%s, %s)", key, val.freq, val.sum));
            }
            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getId() + " : Dumper thread was interrupted");
            }
        }

    }
}
