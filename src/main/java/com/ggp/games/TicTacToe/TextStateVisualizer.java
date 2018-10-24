package com.ggp.games.TicTacToe;

import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IStateVisualizer;

public class TextStateVisualizer implements IStateVisualizer {
    public static String infoSetToString(IInformationSet s, int role) {
        InformationSet info = (InformationSet) s;
        StringBuilder sb = new StringBuilder();
        char mine = 'X';
        char opponent = 'O';
        if (role == CompleteInformationState.PLAYER_O) {
            mine = 'O';
            opponent = 'X';
        }
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                char cell = ' ';
                switch (info.getFieldValue(x, y)) {
                    case InformationSet.FIELD_MINE:
                        cell = mine;
                        break;
                    case InformationSet.FIELD_ENEMY:
                        cell = opponent;
                        break;
                }
                sb.append('|');
                sb.append(cell);
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    @Override
    public void visualize(IInformationSet s, int role) {
        System.out.print(infoSetToString(s, role));
    }

    @Override
    public void visualize(ICompleteInformationState s) {
        visualize(s.getInfoSetForActingPlayer(), s.getActingPlayerId());
        if (s.isTerminal()) {
            System.out.println("Finished: X: " + s.getPayoff(1) + ", O: " + s.getPayoff(2));
        }
    }
}
