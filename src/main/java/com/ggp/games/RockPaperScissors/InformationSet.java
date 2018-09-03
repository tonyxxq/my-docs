package com.ggp.games.RockPaperScissors;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IPercept;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InformationSet implements IInformationSet {
    private int owner;
    private ChooseAction chosenAction;
    private int size;

    public InformationSet(int owner, ChooseAction chosenAction, int size) {
        this.owner = owner;
        this.chosenAction = chosenAction;
        this.size = size;
    }

    @Override
    public IInformationSet next(IAction a) {
        if (!isLegal(a)) return null;
        return new InformationSet(owner, (ChooseAction) a, size);
    }

    @Override
    public IInformationSet applyPercept(IPercept p) {
        if (!isValid(p)) return null;
        return null;
    }

    @Override
    public List<IAction> getLegalActions() {
        if (chosenAction != null) return null;
        ArrayList<IAction> ret = new ArrayList<>(size);
        for (int i = 1; i <= size; ++i) {
            ret.add(new ChooseAction(i));
        }
        return ret;
    }

    @Override
    public boolean isLegal(IAction a) {
        if (chosenAction != null || a == null || a.getClass() != ChooseAction.class) return false;
        ChooseAction ca = (ChooseAction) a;
        if (ca.getChosen() <= 0 || ca.getChosen() > size) return false;
        return true;
    }

    @Override
    public boolean isValid(IPercept p) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformationSet that = (InformationSet) o;
        return owner == that.owner &&
                size == that.size &&
                Objects.equals(chosenAction, that.chosenAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, chosenAction, size);
    }

    @Override
    public int getOwnerId() {
        return owner;
    }

    public ChooseAction getChosenAction() {
        return chosenAction;
    }

    public int getSize() {
        return size;
    }
}
