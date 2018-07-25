package com.ggp.cli;

import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry<T> {
    private HashMap<String, T> repository = new HashMap<>();

    public void register(T cmd) {
        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.forAnnotatedObject(cmd);
        String name = spec.name();
        if (repository.containsKey(name)) {
            // TODO: conflict resolution
        }
        repository.put(name, cmd);
    }

    public T getCommand(String name) {
        return repository.getOrDefault(name, null);
    }

    public Map<String, T> getCommands() {
        return repository;
    }
}
