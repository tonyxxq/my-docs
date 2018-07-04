package com.ggp.games.LeducPoker;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IPercept;
import com.ggp.games.LeducPoker.actions.CallAction;
import com.ggp.games.LeducPoker.actions.DealCardAction;
import com.ggp.games.LeducPoker.actions.FoldAction;
import com.ggp.games.LeducPoker.actions.RaiseAction;
import com.ggp.games.LeducPoker.percepts.*;

import java.util.ArrayList;
import java.util.List;

public class CompleteInformationState implements ICompleteInformationState {
    private final InformationSet player1IS;
    private final InformationSet player2IS;
    private final int actingPlayer;
    private ArrayList<IAction> legalDealActions;

    public CompleteInformationState(InformationSet player1IS, InformationSet player2IS, int actingPlayer) {
        this.player1IS = player1IS;
        this.player2IS = player2IS;
        this.actingPlayer = actingPlayer;
        Rounds round = player2IS.getRound();
        if (round == Rounds.PrivateCard || round == Rounds.PublicCard) {
            legalDealActions = new ArrayList<>(3);
            int[] availableCards = {2,2,2};
            if (player1IS.getPrivateCard() != null) {
                availableCards[player1IS.getPrivateCard().ordinal()]--;
            }
            if (player2IS.getPrivateCard() != null) {
                availableCards[player2IS.getPrivateCard().ordinal()]--;
            }
            // public card doesnt have to be considered, because if its not null, then PublicCard round is already over
            int player;
            if (round == Rounds.PublicCard) {
                player = 0;
            } else {
                player = player1IS.getPrivateCard() == null ? 1 : 2;
            }
            for (Cards c: Cards.values()) {
                if (availableCards[c.ordinal()] > 0) {
                    legalDealActions.add(new DealCardAction(c, player));
                }
            }
        }
    }

    @Override
    public boolean isTerminal() {
        return player1IS.isTerminal();
    }

    @Override
    public int getActingPlayerId() {
        return actingPlayer;
    }

    @Override
    public double getPayoff(int player) {
        if (!isTerminal() || (player != 1 && player != 2)) return 0;
        int winner = 0;
        Cards publicCard = player1IS.getPublicCard();
        Cards c1 = player1IS.getPrivateCard(), c2 = player2IS.getPrivateCard();
        if (c1 == publicCard) {
            winner = 1;
        } else if (c2 == publicCard) {
            winner = 2;
        } else if (c1.ordinal() > c2.ordinal()) {
            winner = 1;
        } else if (c2.ordinal() > c1.ordinal()) {
            winner = 2;
        }
        if (player1IS.getFoldedByPlayer() != 0) {
            winner = player1IS.getFoldedByPlayer() == 1 ? 2 : 1;
        }

        InformationSet playerIS = (player == 1) ? player1IS : player2IS;
        if (winner == 0) {
            return 0; // draw, both players get their bets back
        } else if (winner == player) {
            return playerIS.getPotSize() - playerIS.getMyBets();
        } else {
            return -playerIS.getMyBets();
        }
    }

    @Override
    public List<IAction> getLegalActions() {
        if (isTerminal()) return null;
        if (actingPlayer == 1) {
            return player1IS.getLegalActions();
        } else if (actingPlayer == 2) {
            return player2IS.getLegalActions();
        } else {
            // random node
            return legalDealActions;
        }
    }

    @Override
    public IInformationSet getInfoSetForPlayer(int player) {
        if (player == 1) return player1IS;
        if (player == 2) return player2IS;
        return null;
    }

    @Override
    public ICompleteInformationState next(IAction a) {
        if (!isLegal(a)) return null;
        IInformationSet is1 = player1IS, is2 = player2IS;
        if (actingPlayer == 1) {
            is1 = is1.next(a);
        } else if (actingPlayer == 2) {
            is2 = is2.next(a);
        }
        for (IPercept p: getPercepts(a)) {
            if (p.getTargetPlayer() == 1) {
                is1 = is1.applyPercept(p);
            } else if (p.getTargetPlayer() == 2) {
                is2 = is2.applyPercept(p);
            }
        }
        Rounds nextRound = ((InformationSet)is2).getRound();
        int newActingPlayer = 0;

        switch (nextRound) {
            case PrivateCard:
            case PublicCard:
            case Start:
            case End:
                newActingPlayer = 0;
                break;
            case Bet1:
            case Bet2:
                newActingPlayer = actingPlayer == 1 ? 2 : 1;
                break;
        }

        return new CompleteInformationState((InformationSet) is1, (InformationSet) is2, newActingPlayer);
    }

    @Override
    public Iterable<IPercept> getPercepts(IAction a) {
        if(!isLegal(a)) return null;
        ArrayList<IPercept> ret = new ArrayList<>();
        InformationSet otherPlayer = (actingPlayer == 1) ? player2IS : player1IS;
        if (a.getClass() == FoldAction.class) {
            ret.add(new OpponentFoldedPercept(otherPlayer.getOwner()));
            return ret;
        } else if (a.getClass() == RaiseAction.class) {
            int diff = otherPlayer.getRaiseAmount() - otherPlayer.getRemainingMoney();
            int increase = otherPlayer.getRaiseAmount();
            if (diff > 0) {
                ret.add(new ReturnedMoneyPercept(actingPlayer, diff));
                increase =  otherPlayer.getRemainingMoney();

            }
            ret.add(new PotUpdatePercept(otherPlayer.getOwner(), otherPlayer.getPotSize() + increase));
            return ret;
        } else if (a.getClass() == CallAction.class) {
            boolean wasRaised = player1IS.wasRaised();
            if (actingPlayer == 2 || wasRaised) {
                InformationSet player = (InformationSet) getInfoSetForActingPlayer();
                if (wasRaised) {
                    ret.add(new PotUpdatePercept(actingPlayer == 1 ? 2 : 1, otherPlayer.getPotSize() + Math.min(player.getRaiseAmount(), player.getRemainingMoney())));
                }
                ret.add(new BettingRoundEndedPercept(1));
                ret.add(new BettingRoundEndedPercept(2));
            }
            return ret;
        } else if (a.getClass() == DealCardAction.class) {
            DealCardAction dc = (DealCardAction) a;
            if (dc.isPublic()) {
                ret.add(new CardRevealedPercept(1, dc.getCard(), true));
                ret.add(new CardRevealedPercept(2, dc.getCard(), true));
            } else {
                ret.add(new CardRevealedPercept(dc.getPlayer(), dc.getCard(), false));
            }
            return ret;
        }
        return null;
    }

    @Override
    public boolean isLegal(IAction a) {
        if (a == null || isTerminal()) return false;
        if (actingPlayer == 1) return player1IS.isLegal(a);
        if (actingPlayer == 2) return player2IS.isLegal(a);
        return legalDealActions.contains(a);
    }
}
