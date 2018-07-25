package com.ggp.games.RockPaperScissors;

import com.ggp.IGameDescription;
import com.ggp.IStateVisualizer;
import com.ggp.cli.IGameCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "rock-paper-scissors",
        description = "Variable sized Rock-Paper-Scissors game"
)
public class RPSCommand implements IGameCommand {
    @CommandLine.Parameters(index = "0", paramLabel = "SIZE", description = "size of the game, only odd numbers are allowed (default: ${DEFAULT-VALUE}).", defaultValue="3")
    private int size;

    @Override
    public IGameDescription getGameDescription() {
        return new GameDescription(size);
    }

    @Override
    public IStateVisualizer getStateVisualizer() {
        return new TextStateVisualizer();
    }
}
