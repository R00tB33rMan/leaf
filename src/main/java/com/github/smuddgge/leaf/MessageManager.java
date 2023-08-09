package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents the message manager.
 */
public class MessageManager {

    /**
     * List of players and who they last messaged.
     */
    private static final HashMap<UUID, UUID> lastMessaged = new HashMap<UUID, UUID>();

    /**
     * Used to set who a player last messaged.
     *
     * @param player       Player that sent the message.
     * @param lastMessaged Player the message was sent to.
     */
    public static void setLastMessaged(UUID player, UUID lastMessaged) {
        MessageManager.lastMessaged.put(player, lastMessaged);
        MessageManager.lastMessaged.put(lastMessaged, player);
    }

    /**
     * Used to remove a player from the last messaged list.
     *
     * @param player The player to remove.
     */
    public static void removeLastMessaged(UUID player) {
        MessageManager.lastMessaged.remove(player);
    }

    /**
     * Used to get who is messaging a player.
     *
     * @param player Player to get.
     */
    public static UUID getLastMessaged(UUID player) {
        return MessageManager.lastMessaged.get(player);
    }

    /**
     * Convert to to mini message component.
     *
     * @param message The instance of the message.
     * @return The component.
     */
    public static Component convertMiniMessage(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    /**
     * Used to convert a message to a component with colour.
     *
     * @param message The message to convert.
     * @return The requested component.
     */
    public static Component convert(String message) {
        return MessageManager.convertMiniMessage(message
                .replace("&0", "<reset><black>")
                .replace("&1", "<reset><dark_blue>")
                .replace("&2", "<reset><dark_green>")
                .replace("&3", "<reset><dark_aqua>")
                .replace("&4", "<reset><dark_red>")
                .replace("&5", "<reset><dark_purple>")
                .replace("&6", "<reset><gold>")
                .replace("&7", "<reset><gray>")
                .replace("&8", "<reset><dark_gray>")
                .replace("&9", "<reset><blue>")
                .replace("&a", "<reset><green>")
                .replace("&b", "<reset><aqua>")
                .replace("&c", "<reset><red>")
                .replace("&d", "<reset><light_purple>")
                .replace("&e", "<reset><yellow>")
                .replace("&f", "<reset><white>")
                .replace("&k", "<obf>")
                .replace("&l", "<b>")
                .replace("&m", "<st>")
                .replace("&n", "<u>")
                .replace("&o", "<i>")
                .replace("&r", "<reset>")
        );
    }

    /**
     * Used to convert standard messages into legacy messages.
     *
     * @param message The message to convert.
     * @return The requested string.
     */
    public static String convertToLegacy(String message) {
        return "§r" + message.replace("&", "§");
    }

    /**
     * Used to log information into the console with converted colours.
     *
     * @param message The message to send.
     */
    public static void log(String message) {
        message = "&a[Leaf] &7" + message;

        for (String string : message.split("\n")) {
            Leaf.getServer().getConsoleCommandSource().sendMessage(MessageManager.convert(string));
        }
    }

    /**
     * Used to log information into the console.
     *
     * @param component The component.
     */
    public static void log(Component component) {
        Leaf.getServer().getConsoleCommandSource().sendMessage(component);
    }

    /**
     * Used to log a warning in the console with converted colours.
     *
     * @param message The message to send.
     */
    public static void warn(String message) {
        message = "&a[Leaf] &e[WARNING] &6" + message;
        for (String string : message.split("\n")) {
            Leaf.getServer().getConsoleCommandSource().sendMessage(MessageManager.convert(string));
        }
    }

    public static void logHeader() {
        String message = "\n" +
                "&a __         ______     ______     ______\n" +
                "&a/\\ \\       /\\  ___\\   /\\  __ \\   /\\  ___\\\n" +
                "&a\\ \\ \\____  \\ \\  __\\   \\ \\  __ \\  \\ \\  __\\\n" +
                "&a \\ \\_____\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\\n" +
                "&a  \\/_____/   \\/_____/   \\/_/\\/_/   \\/_/\n" +
                "\n" +
                "    &7By Smudge    Version &e" + Leaf.class.getAnnotation(Plugin.class).version();

        MessageManager.log(message);
    }

    /**
     * Used to send a message to the players that can spy.
     *
     * @param message The message to send.
     */
    public static void sendSpy(String message) {
        for (Player player : Leaf.getServer().getAllPlayers()) {
            User user = new User(player);
            if (Objects.equals(user.getRecord().toggleSeeSpy, "true")) {
                user.sendMessage(message);
            }
        }
    }
}
