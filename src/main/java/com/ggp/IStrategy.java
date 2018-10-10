package com.ggp;

public interface IStrategy {
    double getProbability(IInformationSet s, IAction a);
    Iterable<IInformationSet> getDefinedInformationSets();
}
