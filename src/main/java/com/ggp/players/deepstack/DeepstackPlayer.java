package com.ggp.players.deepstack;

import com.ggp.*;
import com.ggp.players.deepstack.debug.RPSListener;
import com.ggp.players.deepstack.utils.*;
import com.ggp.utils.PlayerHelpers;

import java.util.*;

public class DeepstackPlayer implements IPlayer {
    public static class Factory implements IPlayerFactory {
        private ISubgameResolver.Factory resolverFactory;
        private IResolvingListener listener;

        public Factory(ISubgameResolver.Factory resolverFactory, IResolvingListener listener) {
            this.resolverFactory = resolverFactory;
            this.listener = listener;
        }

        @Override
        public IPlayer create(IGameDescription game, int role) {
            DeepstackPlayer ret = new DeepstackPlayer(role, game, resolverFactory);
            if (listener != null) ret.registerResolvingListener(listener);
            return ret;
        }
    }

    private int id;
    private int opponentId;
    private InformationSetRange range;
    private IInformationSet hiddenInfo;
    private HashMap<IInformationSet, Double> opponentCFV;
    private ICompleteInformationStateFactory cisFactory;
    private NextTurnInfoTree ntit;
    private Map<IInformationSet, NextRangeTree> myISToNRT = new HashMap<>();
    private IGameDescription gameDesc;
    private PerceptSequence.Builder myPSBuilder = new PerceptSequence.Builder();
    private IAction myLastAction;
    private PerceptSequenceMap psMap;
    private IStrategy lastCumulativeStrategy;
    private ArrayList<IResolvingListener> resolvingListeners = new ArrayList<>();
    private ISubgameResolver.Factory resolverFactory;
    private double opponentCFVNorm = 1;

    public DeepstackPlayer(int id, IGameDescription gameDesc, ISubgameResolver.Factory resolverFactory) {
        this.id = id;
        this.opponentId = PlayerHelpers.getOpponentId(id);
        range = new InformationSetRange(id);
        IInformationSet initialSet = gameDesc.getInitialInformationSet(id);
        hiddenInfo = initialSet;
        cisFactory = gameDesc.getCISFactory();
        IInformationSet initialOpponentSet = gameDesc.getInitialInformationSet(2 - id + 1);
        range.init(gameDesc.getInitialState());
        myISToNRT.put(initialSet, new NextRangeTree());
        opponentCFV = new HashMap<>(1);
        opponentCFV.put(initialOpponentSet, 0d);
        this.gameDesc = gameDesc;
        this.resolverFactory = resolverFactory;
    }

    public void registerResolvingListener(IResolvingListener listener) {
        if (listener != null && !resolvingListeners.contains(listener))
            resolvingListeners.add(listener);
    }

    private ISubgameResolver createResolver() {
        return resolverFactory.create(id, range, opponentCFV, cisFactory, resolvingListeners);
    }

    @Override
    public void init(long timeoutMillis) {
        IterationTimer timer = new IterationTimer(timeoutMillis);
        timer.start();
        ISubgameResolver r = createResolver();
        ISubgameResolver.InitResult res = r.init(gameDesc.getInitialState(), timer);
        ntit = res.ntit;
        myISToNRT.put(gameDesc.getInitialInformationSet(id), res.nrt);
        psMap = res.psMap;
        opponentCFVNorm = res.opponentCFVNorm;
    }

    @Override
    public int getRole() {
        return id;
    }

    private IAction act(IAction forcedAction, long timeoutMillis) {
        IterationTimer timer = new IterationTimer(timeoutMillis);
        timer.start();
        opponentCFV = new HashMap<>(ntit.getOpponentValues().size());
        ntit.getOpponentValues().forEach((is, cfv) -> opponentCFV.put(is, cfv.getValue() / opponentCFVNorm));
        range.advance(psMap.getPossibleSequences(myPSBuilder.close()), myISToNRT, lastCumulativeStrategy);
        ISubgameResolver r = createResolver();
        ISubgameResolver.ActResult res = r.act(timer);
        lastCumulativeStrategy = res.cumulativeStrategy;

        IAction selectedAction;
        if (forcedAction == null) {
            selectedAction = res.cumulativeStrategy.sampleAction(hiddenInfo);
        } else {
            selectedAction = forcedAction;
        }

        myLastAction = selectedAction;
        ntit = res.actionToNTIT.get(selectedAction);
        psMap = res.actionToPsMap.get(selectedAction);
        myISToNRT = res.myISToNRT;
        hiddenInfo = hiddenInfo.next(selectedAction);
        opponentCFVNorm = res.opponentCFVNorm;
        return selectedAction;
    }

    @Override
    public void forceAction(IAction forcedAction, long timeoutMillis) {
        act(forcedAction, timeoutMillis);
    }

    @Override
    public IAction act(long timeoutMillis) {
        return act(null, timeoutMillis);
    }

    @Override
    public void receivePercepts(IPercept percept) {
        myPSBuilder.add(percept);
        hiddenInfo = hiddenInfo.applyPercept(percept);
        ntit = ntit.getNext(percept);
    }
}
