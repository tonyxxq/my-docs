package com.ggp.games.LeducPoker;

import com.ggp.IGameDescription;
import com.ggp.IStateVisualizer;
import com.ggp.cli.IGameCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "leduc-poker",
        description = "Simple Leduc poker"
)
public class LeducPokerCommand implements IGameCommand {
    @CommandLine.Parameters(index = "0", paramLabel = "MONEY1", description = "Starting money for player 1 (default: ${DEFAULT-VALUE})", defaultValue = "7")
    private int money1;

    @CommandLine.Parameters(index = "1", paramLabel = "MONEY2", description = "Starting money for player 2 (default: ${DEFAULT-VALUE})", defaultValue = "7")
    private int money2;

    @Override
    public IGameDescription getGameDescription() {
        return new GameDescription(money1, money2);
    }

    @Override
    public IStateVisualizer getStateVisualizer() {
        return new TextStateVisualizer();
    }
}
