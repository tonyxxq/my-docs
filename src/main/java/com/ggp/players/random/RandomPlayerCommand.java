package com.ggp.players.random;

import com.ggp.IPlayerFactory;
import com.ggp.cli.IPlayerFactoryCommand;
import picocli.CommandLine;

@CommandLine.Command(name="random", description="Uniform random player")
public class RandomPlayerCommand implements IPlayerFactoryCommand {

    @Override
    public IPlayerFactory getPlayerFactory() {
        return new RandomPlayer.Factory();
    }
}
