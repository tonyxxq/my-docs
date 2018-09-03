package com.ggp.utils.perfect_recall;

import com.ggp.*;

/**
 * Makes a perfect-recall version of given game.
 */
public class PerfectRecallGameDescriptionWrapper implements IGameDescription {
    private IGameDescription gameDesc;
    private PerfectRecallCIS initialState;

    public PerfectRecallGameDescriptionWrapper(IGameDescription gameDesc) {
        this.gameDesc = gameDesc;
        ICompleteInformationState s = gameDesc.getInitialState();
        initialState = new PerfectRecallCIS(s, new PerfectRecallIS(s.getInfoSetForPlayer(1), null), new PerfectRecallIS(s.getInfoSetForPlayer(2), null));
    }

    @Override
    public ICompleteInformationState getInitialState() {
        return initialState;
    }

    @Override
    public ICompleteInformationStateFactory getCISFactory() {
        return new ICompleteInformationStateFactory() {
            private ICompleteInformationStateFactory factory = gameDesc.getCISFactory();
            @Override
            public ICompleteInformationState make(IInformationSet player1, IInformationSet player2, int actingPlayer) {
                PerfectRecallIS p1 = (PerfectRecallIS) player1;
                PerfectRecallIS p2 = (PerfectRecallIS) player2;
                return new PerfectRecallCIS(factory.make(p1.getOrigInfoSet(), p2.getOrigInfoSet(), actingPlayer), p1, p2);
            }
        };
    }
}
