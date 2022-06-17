package com.crashhascrashed.whisper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class Whisperer {
    Player player;

    Player lastSpokenTo;

    ChatColor playerRank;

    ChatColor receiverRank;

    public Whisperer(Player p) {
        this.player = p;
        this.playerRank = getRankColor(p, false);
    }

    public void sendWhisper(Player receiver, String message) {
        ChatColor gray = ChatColor.GRAY;
        ChatColor italic = ChatColor.ITALIC;
        this.receiverRank = getRankColor(receiver, false);
        receiver.sendMessage(gray + "[" + this.playerRank + this.player.getDisplayName() + gray + " -> me] " + italic + "" + message);
        this.player.sendMessage(gray + "[Me -> " + this.receiverRank + receiver.getDisplayName() + gray + "] " + italic + message);
        this.lastSpokenTo = receiver;
    }

    public Player getLastSpokenTo() {
        if (Bukkit.getServer().getOnlinePlayers().contains(this.lastSpokenTo))
            return this.lastSpokenTo;
        return null;
    }

    public void setLastSpokenTo(Player p) {
        this.lastSpokenTo = p;
    }

    public Player getPlayer() {
        return this.player;
    }

    private ChatColor getRankColor(Player p, boolean retry) {
        try {
            Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
            return scoreboard.getEntryTeam(p.getName()).getColor();
        } catch (Exception ex) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "/colors update");
            if (retry)
                return ChatColor.WHITE;
            return getRankColor(p, true);
        }
    }
}
