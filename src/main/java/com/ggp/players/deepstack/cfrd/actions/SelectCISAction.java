package com.ggp.players.deepstack.cfrd.actions;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IRandomNode;

import java.util.Objects;

public class SelectCISAction implements IAction, IRandomNode.IRandomNodeAction {
    private static final long serialVersionUID = 1L;
    private ICompleteInformationState selectedState;
    private double prob;

    public SelectCISAction(ICompleteInformationState selectedState, double prob) {
        this.selectedState = selectedState;
        this.prob = prob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectCISAction that = (SelectCISAction) o;
        return Double.compare(that.prob, prob) == 0 &&
                Objects.equals(selectedState, that.selectedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedState, prob);
    }

    public ICompleteInformationState getSelectedState() {
        return selectedState;
    }

    @Override
    public double getProb() {
        return prob;
    }

    @Override
    public IAction getAction() {
        return this;
    }
}
