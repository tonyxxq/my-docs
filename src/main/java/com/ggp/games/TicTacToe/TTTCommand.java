package com.ggp.games.TicTacToe;

import com.ggp.IGameDescription;
import com.ggp.IStateVisualizer;
import com.ggp.cli.IGameCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "krieg-ttt",
        description = "5x5 krieg tic-tac-toe"
)
public class TTTCommand implements IGameCommand {
    @Override
    public IGameDescription getGameDescription() {
        return new GameDescription();
    }

    @Override
    public IStateVisualizer getStateVisualizer() {
        return new TextStateVisualizer();
    }
}
