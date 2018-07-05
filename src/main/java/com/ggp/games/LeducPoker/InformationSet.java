package com.ggp.games.LeducPoker;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IPercept;
import com.ggp.games.LeducPoker.actions.CallAction;
import com.ggp.games.LeducPoker.actions.FoldAction;
import com.ggp.games.LeducPoker.actions.RaiseAction;
import com.ggp.games.LeducPoker.percepts.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InformationSet implements IInformationSet {
    private final int owner;
    private final Cards privateCard;
    private final Cards publicCard;
    private final int potSize;
    private final int remainingMoney;
    private final int startingMoney;
    private final Rounds round;
    private final boolean wasRaised;
    private final int foldedByPlayer;

    public InformationSet(int owner, Cards privateCard, Cards publicCard, int potSize, int remainingMoney,
                          int startingMoney, Rounds round, boolean wasRaised, int foldedByPlayer
    ) {
        this.owner = owner;
        this.privateCard = privateCard;
        this.publicCard = publicCard;
        this.potSize = potSize;
        this.remainingMoney = remainingMoney;
        this.startingMoney = startingMoney;
        this.round = round;
        this.wasRaised = wasRaised;
        this.foldedByPlayer = foldedByPlayer;
    }

    @Override
    public IInformationSet next(IAction a) {
        if (!isLegal(a)) return null;
        if (a.getClass() == FoldAction.class) {
            return new InformationSet(owner, privateCard, publicCard, potSize, remainingMoney, startingMoney, Rounds.End, false, owner);
        } else if (a.getClass() == CallAction.class) {
            int potUpdate = wasRaised ? getRaiseAmount() : 0;
            potUpdate = Math.min(remainingMoney, potUpdate);
            return new InformationSet(owner, privateCard, publicCard, potSize + potUpdate, remainingMoney - potUpdate, startingMoney, round, false, foldedByPlayer);
        } else if (a.getClass() == RaiseAction.class) {
            int potUpdate = getRaiseAmount();
            return new InformationSet(owner, privateCard, publicCard, potSize + potUpdate, remainingMoney - potUpdate, startingMoney, round, true, foldedByPlayer);
        }
        return null;
    }

    @Override
    public IInformationSet applyPercept(IPercept p) {
        if (!isValid(p)) return null;
        if (p.getTargetPlayer() != owner) return this;
        if (p.getClass() == CardRevealedPercept.class) {
            CardRevealedPercept crp = (CardRevealedPercept) p;
            if (crp.isPublic() && publicCard == null) {
                return new InformationSet(owner, privateCard, crp.getCard(), potSize, remainingMoney, startingMoney, round.next(), false, foldedByPlayer);
            } else if (!crp.isPublic() && privateCard == null) {
                return new InformationSet(owner, crp.getCard(), publicCard, potSize, remainingMoney, startingMoney, round.next(), false, foldedByPlayer);
            } else {
                return null;
            }
        } else if (p.getClass() == PotUpdatePercept.class) {
            PotUpdatePercept pup = (PotUpdatePercept) p;
            return new InformationSet(owner, privateCard, publicCard, pup.getNewPotSize(), remainingMoney, startingMoney, round, true, foldedByPlayer);
        } else if (p.getClass() == ReturnedMoneyPercept.class) {
            ReturnedMoneyPercept rmp = (ReturnedMoneyPercept) p;
            return new InformationSet(owner, privateCard, publicCard, potSize - rmp.getAmount(), remainingMoney + rmp.getAmount(), startingMoney, round, wasRaised, foldedByPlayer);
        } else if (p.getClass() == BettingRoundEndedPercept.class) {
            return new InformationSet(owner, privateCard, publicCard, potSize, remainingMoney, startingMoney, round.next(), false, foldedByPlayer);
        } else if (p.getClass() == OpponentFoldedPercept.class) {
            return new InformationSet(owner, privateCard, publicCard, potSize, remainingMoney, startingMoney, Rounds.End, false, owner == 1 ? 2 : 1);
        }
        return null;
    }

    @Override
    public List<IAction> getLegalActions() {
        if (isTerminal()) return null;
        ArrayList<IAction> ret = new ArrayList<>(3);
        if (isLegal(RaiseAction.instance)) {
            ret.add(RaiseAction.instance);
        }
        ret.add(FoldAction.instance);
        ret.add(CallAction.instance);
        return ret;
    }

    @Override
    public boolean isLegal(IAction a) {
        if (isTerminal() || a == null) return false; // terminal
        if (a.getClass() == FoldAction.class || a.getClass() == CallAction.class) return true;
        if (a.getClass() == RaiseAction.class) return !wasRaised && remainingMoney > getRaiseAmount();
        return false;
    }

    protected int getRaiseAmount() {
        if (round == Rounds.Bet1) return 2;
        if (round == Rounds.Bet2) return 4;
        return 0;
    }

    protected boolean isTerminal() {
        return round == Rounds.End;
    }

    public Cards getPrivateCard() {
        return privateCard;
    }

    public Cards getPublicCard() {
        return publicCard;
    }

    public int getPotSize() {
        return potSize;
    }

    public int getRemainingMoney() {
        return remainingMoney;
    }

    public int getStartingMoney() {
        return startingMoney;
    }

    public Rounds getRound() {
        return round;
    }

    public boolean wasRaised() {
        return wasRaised;
    }

    public int getMyBets() {
        return startingMoney - remainingMoney;
    }

    public int getOwner() {
        return owner;
    }

    public int getFoldedByPlayer() {
        return foldedByPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformationSet that = (InformationSet) o;
        return owner == that.owner &&
                potSize == that.potSize &&
                remainingMoney == that.remainingMoney &&
                startingMoney == that.startingMoney &&
                wasRaised == that.wasRaised &&
                privateCard == that.privateCard &&
                publicCard == that.publicCard &&
                round == that.round &&
                foldedByPlayer == that.foldedByPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, privateCard, publicCard, potSize, remainingMoney, startingMoney, round, wasRaised, foldedByPlayer);
    }

    @Override
    public boolean isValid(IPercept p) {
        if (p.getTargetPlayer() != owner || foldedByPlayer != 0) return false;
        if (p.getClass() == CardRevealedPercept.class) {
            CardRevealedPercept crp = (CardRevealedPercept) p;
            return (crp.isPublic() && publicCard == null) || (!crp.isPublic() && privateCard == null);
        } else if (p.getClass() == PotUpdatePercept.class) {
            PotUpdatePercept pup = (PotUpdatePercept) p;
            // the other player may not have enough money to add full raise amount
            return (round == Rounds.Bet1 || round == Rounds.Bet2) && pup.getNewPotSize() <= potSize + getRaiseAmount() && pup.getNewPotSize() >= potSize;
        } else if (p.getClass() == ReturnedMoneyPercept.class) {
            if (!wasRaised) return false;
            if (round != Rounds.Bet1 && round != Rounds.Bet2) return false;
            ReturnedMoneyPercept rmp = (ReturnedMoneyPercept) p;
            return rmp.getAmount() > 0 && rmp.getAmount() < getRaiseAmount();
        } else if (p.getClass() == BettingRoundEndedPercept.class) {
            return round == Rounds.Bet1 || round != Rounds.Bet2;
        } else if (p.getClass() == OpponentFoldedPercept.class) {
            return (round == Rounds.Bet1 || round != Rounds.Bet2);
        }
        return false;
    }
}
