package com.ggp.players.deepstack;

import com.ggp.IGameListener;
import com.ggp.IPlayerFactory;
import com.ggp.cli.IPlayerFactoryCommand;
import com.ggp.players.deepstack.debug.ResolvingListener;
import picocli.CommandLine;

@CommandLine.Command(
        name = "deepstack",
        description = "Deepstack player"
)
public class DeepstackPlayerCommand implements IPlayerFactoryCommand {
    @CommandLine.Option(names = {"-i", "--iters"}, defaultValue = "50",
            description = "no. of resolving iterations to run at each step")
    private int iterations;

    private ResolvingListener listener = new ResolvingListener();

    @Override
    public IPlayerFactory getPlayerFactory() {
        return new DeepstackPlayer.Factory(iterations, listener);
    }

    @Override
    public IGameListener getGameListener() {
        return listener;
    }
}
