package org.unitedlands.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.UnitedWar;
import org.unitedlands.commands.handlers.ICommandHandler;
import org.unitedlands.commands.handlers.command.town.warcamps.TownWarWarCampCreateSubcommandHandler;
import org.unitedlands.commands.handlers.command.town.warcamps.TownWarWarCampTpSubcommandHandler;
import org.unitedlands.util.Formatter;
import org.unitedlands.util.Messenger;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.object.AddonCommand;


public class TownWarcampCommands implements CommandExecutor, TabCompleter {

    private final UnitedWar plugin;
    private final Map<String, ICommandHandler> handlers = new HashMap<>();

    public TownWarcampCommands(UnitedWar plugin) {
        this.plugin = plugin;
        TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.TOWN, "warcamp", this));

        registerHandlers();
    }

    private void registerHandlers() {
        handlers.put("create", new TownWarWarCampCreateSubcommandHandler(plugin));
        handlers.put("tp", new TownWarWarCampTpSubcommandHandler(plugin));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias,
            String[] args) {

        if (args.length == 0)
            return null;

        List<String> options = null;
        String input = args[args.length - 1];

        if (args.length == 1) {
            options = new ArrayList<>(handlers.keySet());
        } else {
            String subcommand = args[0].toLowerCase();
            ICommandHandler handler = handlers.get(subcommand);

            if (handler != null) {
                options = handler.handleTab(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return Formatter.getSortedCompletions(input, options);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String[] args) {

        // TODO: Send help message
        if (args.length == 0)
            return false;

        String subcommand = args[0].toLowerCase();
        ICommandHandler handler = handlers.get(subcommand);

        if (handler == null) {
            Messenger.sendMessage(sender, "invalid-command", true);
            return false;
        }

        handler.handleCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

}
