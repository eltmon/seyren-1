package com.seyren.core.domain;


public class CheckState {

    private long count;
    private double mean;
    private double posSum;
    private double negSum;
    private final long minCount;


    public CheckState(long minCount) {
        this.minCount = minCount;
    }

    public CheckState(long minCount,double mean, double posSum, double negSum, long count) {
        this.minCount = minCount;
        this.negSum = negSum;
        this.posSum = posSum;
        this.mean = mean;
        this.count = count;
    }

    public long getMinCount() {
        return minCount;
    }

    public long getCount() {
        return count;
    }

    public double getMean() {
        return mean;
    }


    public double getPosSum() {
        return posSum;
    }

    public double getNegSum() {
        return negSum;
    }




}
