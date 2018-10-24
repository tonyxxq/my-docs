package com.ggp.utils;

public class AveragedDouble {
    private double val;
    private int count;

    public AveragedDouble(double val) {
        this.val = val;
        this.count = 1;
    }

    public AveragedDouble add(double v) {
        val = (val*count + v)/(count+1);
        count++;
        return this;
    }

    public AveragedDouble add(AveragedDouble v) {
        val = (val*count + v.val*v.count)/(count + v.count);
        count = count + v.count;
        return this;
    }

    public AveragedDouble sum(AveragedDouble v) {
        val += v.val;
        count = 1;
        return this;
    }

    public double getValue() {
        return this.val;
    }
}
