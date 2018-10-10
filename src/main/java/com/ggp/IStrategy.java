package com.ggp;

import java.io.Serializable;

public interface IStrategy extends Serializable {
    double getProbability(IInformationSet s, IAction a);
    Iterable<IInformationSet> getDefinedInformationSets();
}
