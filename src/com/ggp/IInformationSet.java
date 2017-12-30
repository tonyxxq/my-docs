package com.ggp;

import java.util.List;

public interface IInformationSet {
    IInformationSet next(IAction a);
    IInformationSet applyPercept(IPercept p);
    List<IAction> getLegalActions();
    boolean isLegal(IAction a);
}
