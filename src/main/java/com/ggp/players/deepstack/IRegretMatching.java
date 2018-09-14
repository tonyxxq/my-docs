package com.ggp.players.deepstack;

import com.ggp.IInformationSet;
import com.ggp.IStrategy;
import com.ggp.players.deepstack.utils.Strategy;

public interface IRegretMatching {
    void addActionRegret(IInformationSet is, int actionIdx, double regretDiff);
    boolean hasInfoSet(IInformationSet is);
    void getRegretMatchedStrategy(IInformationSet is, Strategy strat);
    void getRegretMatchedStrategy(Strategy strat);
    void initInfoSet(IInformationSet is);
}
