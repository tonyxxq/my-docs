package com.ggp.players.deepstack;

import com.ggp.IPlayerFactory;
import com.ggp.cli.IPlayerFactoryCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "deepstack",
        description = "Deepstack player"
)
public class DeepstackPlayerCommand implements IPlayerFactoryCommand {
    @CommandLine.Option(names = {"-i", "--iters"}, defaultValue = "50",
            description = "no. of resolving iterations to run at each step")
    private int iterations;

    @Override
    public IPlayerFactory getPlayerFactory() {
        return new DeepstackPlayer.Factory(iterations);
    }
}
