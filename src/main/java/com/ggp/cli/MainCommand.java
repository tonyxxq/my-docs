package com.ggp.cli;

import picocli.CommandLine;

@CommandLine.Command(name="./GeneralDeepstack", subcommands = {RunCommand.class, HelpCommand.class}, mixinStandardHelpOptions=true)
public class MainCommand implements Runnable {
    CommandRegistry<IGameCommand> gameRegistry = new CommandRegistry<>();
    CommandRegistry<IPlayerFactoryCommand> playerFactoryRegistry = new CommandRegistry<>();

    public void registerGameCommand(IGameCommand cmd) {
        gameRegistry.register(cmd);
    }

    public void registerPlayerFactoryCommand(IPlayerFactoryCommand cmd) {
        playerFactoryRegistry.register(cmd);
    }

    public CommandRegistry<IGameCommand> getGameRegistry() {
        return gameRegistry;
    }

    public CommandRegistry<IPlayerFactoryCommand> getPlayerFactoryRegistry() {
        return playerFactoryRegistry;
    }

    @Override
    public void run() {
    }
}
