package dev.civmc.bumhug.hacks

import dev.civmc.bumhug.Hack
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import java.lang.IllegalArgumentException
import java.util.*

class SaveDeathInventory: Hack(), Listener, CommandExecutor {
    override val configName = "saveDeathInv"
    override val prettyName = "Save Inventory On Death"
    override val commandName = "reloadInventory"

    companion object {
        var inventories: HashMap<UUID, Array<ItemStack?>> = HashMap();
        var exp: HashMap<UUID, Pair<Int, Float>> = HashMap()
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        inventories.put(event.entity.uniqueId, event.entity.inventory.contents)
        exp.put(event.entity.uniqueId, Pair(event.entity.level, event.entity.exp))
    }

    override fun onCommand(sender: CommandSender, command: Command, commandName: String, args: Array<out String>): Boolean {
        if (args.size != 1)
            return false

        var player: Player?
        try {
            player = Bukkit.getPlayer(UUID.fromString(args[0]))
        } catch (e: IllegalArgumentException) {
            player = Bukkit.getPlayer(args[0]);
        }

        if (player == null) {
            sender.sendMessage("${ChatColor.RED}${args[0]} is not valid or is not online")
            return true;
        }

        if (!SaveDeathInventory.inventories.containsKey(player.uniqueId)) {
            sender.sendMessage("${ChatColor.RED}That player does not have a saved inventory. Has the server restarted?")
        }

        player.inventory.contents = inventories[player.uniqueId]
        player.level = exp[player.uniqueId]!!.first
        player.exp = exp[player.uniqueId]!!.second;

        return true;
    }
}
