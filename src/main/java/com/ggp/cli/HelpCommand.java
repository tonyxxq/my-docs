package com.ggp.cli;

import picocli.CommandLine;

import java.io.PrintStream;
import java.util.*;

// TODO: resolve licensing/attribution issues, as this is modified class taken from picocli

@CommandLine.Command(name = "help", header = "Displays help information about the specified command",
        synopsisHeading = "%nUsage: ", helpCommand = true,
        description = {"%nWhen no COMMAND is given, the usage help for the main command is displayed.",
                "If a COMMAND is specified, the help for that command is shown.%n"})
public final class HelpCommand implements CommandLine.IHelpCommandInitializable, Runnable {
    @CommandLine.ParentCommand
    private MainCommand mainCommand;

    @CommandLine.Parameters(paramLabel = "COMMAND", description = "The COMMAND to display the usage help message for.")
    private String[] commands = new String[0];

    private CommandLine self;
    private PrintStream out;
    private PrintStream err;
    private CommandLine.Help.Ansi ansi;

    private static int maxLength(Collection<String> any) {
        List<String> strings = new ArrayList<String>(any);
        Collections.sort(strings, Collections.reverseOrder(CommandLine.Help.shortestFirst()));
        return strings.get(0).length();
    }

    private String commandList(Map<String, ?extends Object> commands) {
        if (commands.isEmpty()) { return ""; }
        int width = self.getUsageHelpWidth();
        int commandLength = maxLength(commands.keySet());
        CommandLine.Help.TextTable textTable = CommandLine.Help.TextTable.forColumns(ansi,
                new CommandLine.Help.Column(commandLength + 2, 2, CommandLine.Help.Column.Overflow.SPAN),
                new CommandLine.Help.Column(width - (commandLength + 2), 2, CommandLine.Help.Column.Overflow.WRAP));

        for (Map.Entry<String, ?extends Object> entry : commands.entrySet()) {
            CommandLine.Command anotation = entry.getValue().getClass().getAnnotation(CommandLine.Command.class);
            String description = anotation.description().length > 0 ? anotation.description()[0] : "";
            textTable.addRowValues(ansi.new Text(entry.getKey()), ansi.new Text(description));
        }
        return textTable.toString();
    }

    private <T> void showCommandSection(String heading, CommandRegistry<T> registry) {
        StringBuilder bld = new StringBuilder();
        bld.append(heading);
        bld.append(commandList(registry.getCommands()));
        out.print(bld.toString());
    }

    private void showGameList() {
        showCommandSection(String.format("%nGames:%n"), mainCommand.getGameRegistry());
    }

    private void showPlayerList() {
        showCommandSection(String.format("%nPlayers:%n"), mainCommand.getPlayerFactoryRegistry());
    }

    private void showGamesAndPlayerList() {
        showGameList();
        showPlayerList();
    }

    public void run() {
        CommandLine parent = self == null ? null : self.getParent();
        if (parent == null) { return; }
        if (commands.length > 0) {
            CommandLine subcommand = parent.getSubcommands().get(commands[0]);
            IGameCommand gameCommand = null;
            IPlayerFactoryCommand pfCommand = null;
            if (subcommand != null) {
                subcommand.usage(out, ansi);
                showGamesAndPlayerList();
            } else if ((gameCommand = mainCommand.getGameRegistry().getCommand(commands[0])) != null) {
                CommandLine.usage(gameCommand, out, ansi);
            } else if((pfCommand = mainCommand.getPlayerFactoryRegistry().getCommand(commands[0])) != null) {
                CommandLine.usage(pfCommand, out, ansi);
            }else {
                throw new CommandLine.ParameterException(parent, "Unknown subcommand '" + commands[0] + "'.", null, commands[0]);
            }
        } else {
            parent.usage(out, ansi);
            showGamesAndPlayerList();
        }
    }

    public void init(CommandLine helpCommandLine, CommandLine.Help.Ansi ansi, PrintStream out, PrintStream err) {
        this.self = helpCommandLine;
        this.ansi = ansi;
        this.out  = out;
        this.err  = err;
    }
}