package com.crashhascrashed.whisper;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    Server server;

    List<Whisperer> people;

    public void onEnable() {
        System.out.println("Whisper plugin enabled!");
        this.server = getServer();
        this.people = new ArrayList<>();
    }

    public void onDisable() {
        System.out.println("Whisper plugin disabled!");
    }

    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if (!(s instanceof Player))
            return specialWhisperConsole(s, command, label, args);
        Player player = (Player)s;
        if (command.getName().equalsIgnoreCase("whisper")) {
            if (!s.hasPermission("com.cyber.whisper")) {
                s.sendMessage(ChatColor.RED + "You are not allowed to use this command yet");
                return false;
            }
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Use: /" + label + " <player> <message>");
                return false;
            }
            String target = guessName(args[0]);
            if (!isPlayer(target)) {
                player.sendMessage(ChatColor.RED + "Can't find player " + args[0]);
                return false;
            }
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Use: /" + label + " <player> <message>");
                return false;
            }
            Player receiver = getPlayer(target);
            if (receiver.equals(player)) {
                player.sendMessage(ChatColor.RED + "You can't whisper to yourself silly!");
                return false;
            }
            String message = getMessage(args, true);
            if (!isWhisperer(player)) {
                Whisperer w = new Whisperer(player);
                this.people.add(w);
            }
            Whisperer WSender = getWhisperer(player);
            if (!isWhisperer(receiver)) {
                Whisperer w = new Whisperer(receiver);
                this.people.add(w);
            }
            Whisperer WReceiver = getWhisperer(receiver);
            WSender.sendWhisper(receiver, message);
            WReceiver.setLastSpokenTo(player);
            return true;
        }
        if (command.getName().equalsIgnoreCase("reply") || command.getName().equalsIgnoreCase("r")) {
            if (!s.hasPermission("com.cyber.whisper")) {
                s.sendMessage(ChatColor.RED + "You are not allowed to use this command yet");
                return false;
            }
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Use: /" + label + " <message>");
                return false;
            }
            if (!isWhisperer(player)) {
                Whisperer w = new Whisperer(player);
                this.people.add(w);
            }
            Whisperer WSender = getWhisperer(player);
            if (WSender.getLastSpokenTo() == null) {
                player.sendMessage(ChatColor.RED + "No previous whispers found!");
                return false;
            }
            if (!isPlayer(WSender.getLastSpokenTo().getDisplayName())) {
                player.sendMessage(ChatColor.RED + WSender.getLastSpokenTo().getDisplayName() + " is not online anymore!");
                return false;
            }
            if (!isWhisperer(WSender.getLastSpokenTo())) {
                Whisperer w = new Whisperer(WSender.getLastSpokenTo());
                this.people.add(w);
            }
            Whisperer WReceiver = getWhisperer(WSender.getLastSpokenTo());
            String message = getMessage(args, false);
            WSender.sendWhisper(WReceiver.getPlayer(), message);
            WReceiver.setLastSpokenTo(player);
            return true;
        }
        return false;
    }

    private boolean isPlayer(String name) {
        for (Player p : this.server.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    private Player getPlayer(String name) {
        for (Player p : this.server.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name))
                return p;
        }
        return null;
    }

    private String getMessage(String[] args, boolean removeFirstWord) {
        String msg = "";
        for (String word : args) {
            if (!removeFirstWord) {
                msg = msg + word + " ";
            } else {
                removeFirstWord = false;
            }
        }
        msg = msg.substring(0, msg.length() - 1);
        return msg;
    }

    private boolean isWhisperer(Player p) {
        for (Whisperer w : this.people) {
            if (w.getPlayer().equals(p))
                return true;
        }
        return false;
    }

    private Whisperer getWhisperer(Player p) {
        for (Whisperer w : this.people) {
            if (w.getPlayer().equals(p))
                return w;
        }
        return null;
    }

    private String guessName(String partName) {
        for (Player p : this.server.getOnlinePlayers()) {
            String name = p.getDisplayName();
            String testname = name.toLowerCase();
            int index = 0;
            char[] arrayOfChar;
            int i;
            byte b;
            for (arrayOfChar = partName.toCharArray(), i = arrayOfChar.length, b = 0; b < i; ) {
                Character c = Character.valueOf(arrayOfChar[b]);
                if (!c.equals(Character.valueOf(testname.charAt(index))))
                    break;
                index++;
                if (partName.length() == index)
                    return name;
                b++;
            }
        }
        return partName;
    }

    private boolean specialWhisperConsole(CommandSender s, Command command, String label, String[] args) {
        if (!isPlayer(args[0])) {
            s.sendMessage(ChatColor.RED + "Player not found!");
            return false;
        }
        Player target = getPlayer(args[0]);
        String message = getMessage(args, true);
        target.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Server whispers to you: " + message);
        return true;
    }
}
