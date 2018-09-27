package com.ggp;

/**
 * Describes random node action probabilities. Must iterate actions in the same order as original CIS.
 */
public interface IRandomNode extends Iterable<IRandomNode.IRandomNodeAction> {
    interface IRandomNodeAction {
        IAction getAction();
        double getProb();
    }

    double getActionProb(IAction a);
}
