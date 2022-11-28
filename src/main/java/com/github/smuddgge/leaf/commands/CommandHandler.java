package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.velocitypowered.api.command.CommandManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the command handler.
 */
public class CommandHandler {

    private final List<Command> commands = new ArrayList<>();

    private List<String> registeredCommands = new ArrayList<>();

    /**
     * Used to append a command to the command handler.
     *
     * @param command The command to append.
     */
    public void append(Command command) {
        this.commands.add(command);
    }

    /**
     * Used to register the commands with the proxy server.
     */
    public void register() {
        CommandManager manager = Leaf.getServer().getCommandManager();

        for (Command command : this.commands) {
            // Check if command is enabled
            if (!ConfigCommands.isCommandEnabled(command.getIdentifier())) continue;

            // Check if the command is valid
            if (command.getName() == null) {
                MessageManager.warn("&eUnregistering command &f[command]. &eCommand name not specified in the configuration file."
                        .replace("[command]", command.getIdentifier()));
                continue;
            }

            // Register main command name
            manager.register(manager.metaBuilder(command.getName()).build(), command);
            this.registeredCommands.add(command.getName());

            // Register aliases if they exist
            if (command.getAliases() == null) continue;

            for (String alias : command.getAliases().get()) {
                manager.register(manager.metaBuilder(alias).build(), command);
                this.registeredCommands.add(alias);
            }
        }
    }

    /**
     * Used to unregister all the commands by this plugin
     * in the proxy server.
     */
    public void unregister() {
        CommandManager manager = Leaf.getServer().getCommandManager();

        for (String commandName : this.registeredCommands) {
            // Unregister the command
            manager.unregister(commandName);
        }
    }
}
