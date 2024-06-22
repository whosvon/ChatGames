package com.whosvon.chatgames.chatgames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public final class ChatgamesWhosvon extends JavaPlugin implements Listener, CommandExecutor {

    private String currentQuestion;
    private String currentAnswer;
    private int interval;
    private String rewardCommand;
    private int mathMaxValue;
    private List<String> mathOperations;
    private List<String> words;
    private String startMessage;
    private String correctAnswerMessage;
    private String reloadMessage;
    private String invalidCommandMessage;
    private List<String> rewardTypes;
    private Random random;

    @Override
    public void onEnable() {
        getLogger().info("ChatgamesWhosvon has been enabled!");
        saveDefaultConfig();
        loadConfiguration();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("chatgame").setExecutor(this); // Register command executor
        scheduleQuestionTask();
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatgamesWhosvon has been disabled!");
    }

    private void loadConfiguration() {
        interval = getConfig().getInt("interval");
        rewardCommand = getConfig().getString("reward_command");
        mathMaxValue = getConfig().getInt("math_max_value");
        mathOperations = getConfig().getStringList("math_operations");
        words = getConfig().getStringList("words");
        startMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("start_message"));
        correctAnswerMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("correct_answer_message"));
        reloadMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("reload_message"));
        invalidCommandMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("invalid_command_message"));
        rewardTypes = getConfig().getStringList("reward_types");
        random = new Random();
    }

    private void scheduleQuestionTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                generateQuestion();
                Bukkit.broadcastMessage(startMessage + " " + ChatColor.AQUA + currentQuestion);
            }
        }.runTaskTimer(this, 0, interval * 20); // interval in seconds, converted to ticks
    }

    private void generateQuestion() {
        if (random.nextBoolean()) {
            // Generate math question
            int a = random.nextInt(mathMaxValue);
            int b = random.nextInt(mathMaxValue);
            String operation = mathOperations.get(random.nextInt(mathOperations.size()));
            currentQuestion = "What is " + a + " " + operation + " " + b + "?";
            currentAnswer = calculateAnswer(a, b, operation);
        } else {
            // Generate spelling challenge
            currentAnswer = words.get(random.nextInt(words.size()));
            currentQuestion = "Spell the word: " + new StringBuilder(currentAnswer).reverse().toString();
        }
    }

    private String calculateAnswer(int a, int b, String operation) {
        switch (operation) {
            case "+": return String.valueOf(a + b);
            case "-": return String.valueOf(a - b);
            case "*": return String.valueOf(a * b);
            case "/": return String.valueOf(a / b);
            default: return "";
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Check if the message equals currentAnswer (case-insensitive)
        if (message.trim().equalsIgnoreCase(currentAnswer)) {
            Bukkit.broadcastMessage(ChatColor.GREEN + correctAnswerMessage.replace("%player%", player.getName()));
            rewardPlayer(player);
            currentAnswer = null; // Reset the answer to avoid multiple winners
        }
    }

    private void rewardPlayer(Player player) {
        String rewardCommand = rewardTypes.get(random.nextInt(rewardTypes.size())).replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("chatgame")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /chatgame <answer>");
                return true;
            }

            String playerAnswer = args[0];

            // Check if the provided answer matches currentAnswer (case-insensitive)
            if (playerAnswer.trim().equalsIgnoreCase(currentAnswer)) {
                Player player = (Player) sender;
                Bukkit.broadcastMessage(ChatColor.GREEN + correctAnswerMessage.replace("%player%", player.getName()));
                rewardPlayer(player);
                currentAnswer = null; // Reset the answer to avoid multiple winners
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Incorrect answer!");
                return true;
            }
        }
        return false;
    }
}