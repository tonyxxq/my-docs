package com.ggp.players.deepstack.resolvers;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.players.deepstack.IRegretMatching;
import com.ggp.players.deepstack.IResolvingInfo;
import com.ggp.players.deepstack.IResolvingListener;
import com.ggp.players.deepstack.ISubgameResolver;
import com.ggp.players.deepstack.cfrd.CFRDSubgameRoot;
import com.ggp.players.deepstack.trackers.CFRDTracker;
import com.ggp.players.deepstack.trackers.GameTreeTraversalTracker;
import com.ggp.players.deepstack.utils.InformationSetRange;
import com.ggp.players.deepstack.utils.IterationTimer;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.players.deepstack.utils.SubgameGadget;
import com.ggp.utils.PlayerHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCFRDResolver implements ISubgameResolver {
    protected final int myId;
    protected IInformationSet hiddenInfo;
    protected InformationSetRange range;
    protected HashMap<IInformationSet, Double> opponentCFV;
    protected List<IResolvingListener> resolvingListeners;
    protected IResolvingInfo resInfo = new ResolvingInfo();
    protected Strategy cumulativeStrat = new Strategy();
    protected IRegretMatching regretMatching;
    protected SubgameGadget subgameGadget;
    protected final int opponentId;

    public BaseCFRDResolver(int myId, IInformationSet hiddenInfo, InformationSetRange range, HashMap<IInformationSet, Double> opponentCFV,
                            List<IResolvingListener> resolvingListeners, IRegretMatching regretMatching) {
        this.myId = myId;
        this.hiddenInfo = hiddenInfo;
        this.range = range;
        this.opponentCFV = opponentCFV;
        this.resolvingListeners = resolvingListeners;
        if (this.resolvingListeners == null) this.resolvingListeners = new ArrayList<>();
        this.subgameGadget = new SubgameGadget(opponentCFV);
        this.opponentId = PlayerHelpers.getOpponentId(myId);
        this.regretMatching = regretMatching;
    }

    private class ResolvingInfo implements IResolvingInfo {
        @Override
        public Strategy getUnnormalizedCumulativeStrategy() {
            return cumulativeStrat;
        }

        @Override
        public IInformationSet getHiddenInfo() {
            return hiddenInfo;
        }
    }

    protected void findMyNextTurn(CFRDTracker tracker) {
        ICompleteInformationState s = tracker.getCurrentState();
        if (s.isTerminal()) return;
        if (tracker.isMyNextTurnReached()) {
            tracker.getNtit().addLeaf(s.getInfoSetForPlayer(opponentId), 0);
            tracker.getPsMap().add(tracker.getMyPerceptSequence(), tracker.getOpponentPerceptSequence());
            tracker.getNrt().add(tracker.getOpponentPerceptSequence(), s, tracker.getMyTopAction(), tracker.getRndProb());
            return;
        }
        for (IAction a: s.getLegalActions()) {
            findMyNextTurn(tracker.next(a));
        }
    }

    protected CFRDTracker prepareDataStructures() {
        ICompleteInformationState subgame = new CFRDSubgameRoot(range, opponentCFV, opponentId);
        CFRDTracker tracker = CFRDTracker.createForAct(myId, range.getNorm(), subgame);
        findMyNextTurn(tracker);
        return tracker;
    }

    protected abstract ActResult doAct(CFRDTracker tracker, IterationTimer timeout);

    @Override
    public ActResult act(IterationTimer timeout) {
        resolvingListeners.forEach(listener -> listener.resolvingStart(resInfo));
        CFRDTracker tracker = prepareDataStructures();
        ActResult res = doAct(tracker, timeout);
        resolvingListeners.forEach(listener -> listener.resolvingEnd(resInfo));
        return res;
    }

    protected abstract InitResult doInit(CFRDTracker tracker, IterationTimer timeout);

    @Override
    public InitResult init(ICompleteInformationState initialState, IterationTimer timeout) {
        resolvingListeners.forEach(listener -> listener.resolvingStart(resInfo));
        CFRDTracker tracker = CFRDTracker.createForInit(myId, initialState);
        findMyNextTurn(tracker);
        InitResult res = doInit(tracker, timeout);
        resolvingListeners.forEach(listener -> listener.resolvingEnd(resInfo));
        resolvingListeners.forEach(listener -> listener.initEnd(resInfo));
        return res;
    }
}
