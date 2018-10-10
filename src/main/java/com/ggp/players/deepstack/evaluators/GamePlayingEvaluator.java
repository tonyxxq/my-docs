package com.ggp.players.deepstack.evaluators;

import com.ggp.GameManager;
import com.ggp.IGameDescription;
import com.ggp.IPlayerFactory;
import com.ggp.players.deepstack.DeepstackPlayer;
import com.ggp.players.deepstack.ISubgameResolver;
import com.ggp.players.deepstack.debug.StrategyAggregatorListener;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.players.random.RandomPlayer;
import com.ggp.utils.NormalizingStrategyWrapper;
import com.ggp.utils.recall.ImperfectRecallExploitability;

import java.util.List;

/**
 * Evaluates Deepstack configuration by playing number of games against random opponent, while aggregating computed strategies
 * at encountered decision points.
 */
public class GamePlayingEvaluator implements IDeepstackEvaluator {
    private int initMs;
    private int timeoutMs;
    StrategyAggregatorListener stratAggregator;
    private int gameCount;

    /**
     * Constructor
     * @param initMs timeout for deepstack initialization
     * @param logPointsMs ASC ordered list of times when strategies should be aggregated
     * @param gameCount number of games to play
     */
    public GamePlayingEvaluator(int initMs, List<Integer> logPointsMs, int gameCount) {
        this.initMs = initMs;
        this.timeoutMs = logPointsMs.get(logPointsMs.size() - 1);
        this.stratAggregator = new StrategyAggregatorListener(logPointsMs);
        this.gameCount = gameCount;
    }

    @Override
    public List<EvaluatorEntry> evaluate(IGameDescription gameDesc, ISubgameResolver.Factory subgameResolverFactory) {
        IPlayerFactory deepstack = new DeepstackPlayer.Factory(subgameResolverFactory, stratAggregator);
        IPlayerFactory random = new RandomPlayer.Factory();

        for (int i = 0; i < gameCount; ++i) {
            IPlayerFactory pl1 = deepstack, pl2 = random;
            if (i % 2 == 1) {
                pl1 = random;
                pl2 = deepstack;
            }
            stratAggregator.reinit();
            GameManager manager = new GameManager(pl1, pl2, gameDesc);
            manager.run(initMs, timeoutMs);
            List<EvaluatorEntry> entries = stratAggregator.getEntries();
            Strategy strat = entries.get(entries.size() - 1).getAggregatedStrat();
            double exp = ImperfectRecallExploitability.computeExploitability(new NormalizingStrategyWrapper(strat), gameDesc);
            System.out.println(String.format("Game %d: defined IS %d, last strategy exploitability %f", i, strat.countDefinedInformationSets(), exp));
        }

        for (EvaluatorEntry entry: stratAggregator.getEntries()) {
            entry.getAggregatedStrat().normalize();
        }
        return stratAggregator.getEntries();
    }

    @Override
    public String getConfigString() {
        return "Gameplay{" +
                "init=" + initMs +
                ", count=" + gameCount +
                '}';
    }
}
