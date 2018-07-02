package com.ggp;

public interface IStrategy {
    double getProbability(IInformationSet s, IAction a);
    IAction sampleAction(IInformationSet s);
}
