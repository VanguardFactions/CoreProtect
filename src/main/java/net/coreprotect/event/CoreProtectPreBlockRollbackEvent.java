package net.coreprotect.event;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CoreProtectPreBlockRollbackEvent extends Event implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled;

    private final Block block;
    private final int rollbackType;
    private final String rowUser;

    public CoreProtectPreBlockRollbackEvent(Block block, int rollbackType, String rowUser) {
        this.block = block;
        this.rollbackType = rollbackType;
        this.rowUser = rowUser;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public int getRollbackType() {
        return rollbackType;
    }

    public String getRowUser() {
        return rowUser;
    }

    public Block getBlock() {
        return block;
    }
}
