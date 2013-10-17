package com.sstr.load.scenario;

public class AveragingTPS {

    private final double[] hold;
    private int index;

    public AveragingTPS(int size) {
        hold = new double[size];
    }

    public AveragingTPS add(double duration) {

        if (index == hold.length) {
            index = 0;
        }

        hold[index++] = 1000 / duration;

        return this;
    }

    public double getTPS() {

        int count = 0;
        double total = 0;
        for (int i = 0; i < hold.length; i++) {
            if (hold[i] != 0) {
                total = total + hold[i];
                count++;
            }
        }

        return total / count;
    }
}
