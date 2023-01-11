package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.inventorys.FriendRequestInventory;

public class Requests implements CommandType {

    @Override
    public String getName() {
        return "requests";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        // Open friend requests inventory.
        try {
            FriendRequestInventory friendRequestInventory = new FriendRequestInventory(section.getSection(this.getName()), user);
            friendRequestInventory.open();
        } catch (Exception exception) {
            MessageManager.warn("Exception occurred when opening a friend request inventory!");
            exception.printStackTrace();
        }

        return new CommandStatus();
    }
}
