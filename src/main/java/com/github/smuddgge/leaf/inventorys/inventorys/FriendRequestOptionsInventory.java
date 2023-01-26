package com.github.smuddgge.leaf.inventorys.inventorys;

import com.github.smuddgge.leaf.FriendManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.inventorys.CustomInventory;
import com.github.smuddgge.leaf.inventorys.InventoryItem;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.item.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class FriendRequestOptionsInventory extends CustomInventory {

    private final FriendRequestRecord requestRecord;
    private final String acceptedPlayerName;

    /**
     * Used to create a custom inventory.
     *
     * @param section The parent configuration section to the inventory.
     * @param user    The user that will open the inventory.
     */
    public FriendRequestOptionsInventory(ConfigurationSection section, User user, FriendRequestRecord requestRecord, String acceptedPlayerName) {
        super(section, user, "options");

        this.requestRecord = requestRecord;
        this.acceptedPlayerName = acceptedPlayerName;
    }

    @Override
    public ItemStack onLoadItemWithFunction(InventoryItem inventoryItem) {
        if (Objects.equals(inventoryItem.getFunctionSection().getString("type"), "accept")) {
            for (int slot : inventoryItem.getSlots(this.getInventoryType())) {
                this.addAction(slot, this::accept);
            }
        }
        if (Objects.equals(inventoryItem.getFunctionSection().getString("type"), "deny")) {
            for (int slot : inventoryItem.getSlots(this.getInventoryType())) {
                this.addAction(slot, this::deny);
            }
        }

        return inventoryItem.getItemStack();
    }

    /**
     * Used to deny a request.
     */
    public void deny() {
        if (Leaf.getDatabase().isDisabled()) return;

        FriendRequestTable friendRequestTable = (FriendRequestTable) Leaf.getDatabase().getTable("FriendRequest");
        friendRequestTable.removeRecord("uuid", requestRecord.uuid);

        FriendRequestInventory friendRequestInventory = new FriendRequestInventory(this.section, this.user);
        friendRequestInventory.open();
    }

    /**
     * Used to accept the friend request.
     */
    public void accept() {
        if (Leaf.getDatabase().isDisabled()) return;

        FriendManager.acceptRequest(requestRecord);

        user.sendMessage(PlaceholderManager.parse(
                this.section.getString("from", "{message} You are now friends with &f<name>"),
                null, new User(null, acceptedPlayerName)
        ));

        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(acceptedPlayerName);
        if (optionalPlayer.isEmpty()) return;

        User userSentTo = new User(optionalPlayer.get());

        userSentTo.sendMessage(PlaceholderManager.parse(
                this.section.getString("sent", "{message} You are now friends with &f<name>"),
                null, user
        ));

        this.close();

        FriendRequestInventory friendRequestInventory = new FriendRequestInventory(this.section, this.user);
        friendRequestInventory.open();
    }
}
