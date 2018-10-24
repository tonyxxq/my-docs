package com.ggp.players.deepstack.utils;

import com.ggp.IInformationSet;

import java.util.HashMap;

public class SubgameGadget {
    private static class SubgameRegrets {
        public double followRegret;
        public double terminateRegret;

        public SubgameRegrets(double followRegret, double terminateRegret) {
            this.followRegret = followRegret;
            this.terminateRegret = terminateRegret;
        }
    }
    private HashMap<IInformationSet, Double> opponentFollowProb;
    private HashMap<IInformationSet, SubgameRegrets> regretsGadget;
    private HashMap<IInformationSet, Double> opponentCFV;

    public SubgameGadget(HashMap<IInformationSet, Double> opponentCFV) {
        this.opponentCFV = opponentCFV;
        opponentFollowProb = new HashMap<>(opponentCFV.size());
        regretsGadget = new HashMap<>(opponentCFV.size());
    }

    public double getFollowProb(IInformationSet is) {
        return opponentFollowProb.computeIfAbsent(is, k -> 0.5);
    }

    public void addFollowCFV(IInformationSet os, double followCFV) {
        double trunkCFV = opponentCFV.get(os);
        SubgameRegrets regrets = regretsGadget.computeIfAbsent(os, k -> new SubgameRegrets(0, 0));

        double followProb = Math.max(regrets.followRegret, 0);
        followProb = followProb/(followProb + Math.max(regrets.terminateRegret, 0));
        if (Double.isNaN(followProb)) followProb = 0.5; // during first iteration regretsGadget is 0
        opponentFollowProb.put(os, followProb);
        double gadgetValue = followProb * followCFV + (1 - followProb) * trunkCFV;
        regrets.terminateRegret += trunkCFV  - gadgetValue;
        regrets.followRegret += followCFV - gadgetValue;
    }
}
