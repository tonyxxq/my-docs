package com.ggp;

import java.util.List;

public interface IInformationSet extends Comparable, Iterable<ICompleteInformationState> {
    IInformationSet next(IAction a);
    IInformationSet applyPercept(IPercept p);
    List<IAction> getLegalActions();
    boolean isLegal(IAction a);
}
