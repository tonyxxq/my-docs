package com.ggp.utils;

import com.ggp.IAction;
import com.ggp.IRandomNode;

import java.util.Iterator;
import java.util.List;

public class UniformRandomNode implements IRandomNode {
    private static final long serialVersionUID = 1L;
    private List<IAction> actions;

    public UniformRandomNode(List<IAction> actions) {
        this.actions = actions;
    }

    @Override
    public double getActionProb(IAction a) {
        return 1d/actions.size();
    }

    @Override
    public Iterator<IRandomNodeAction> iterator() {
        return new Iterator<IRandomNodeAction>() {
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < actions.size();
            }

            @Override
            public IRandomNodeAction next() {
                IRandomNodeAction ret = new IRandomNodeAction() {
                    private int actionIdx = idx;
                    @Override
                    public IAction getAction() {
                        return actions.get(actionIdx);
                    }

                    @Override
                    public double getProb() {
                        return 1d/actions.size();
                    }
                };
                idx++;
                return ret;
            }
        };
    }
}
