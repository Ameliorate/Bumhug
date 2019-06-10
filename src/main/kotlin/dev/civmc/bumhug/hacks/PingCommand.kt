package dev.civmc.bumhug.hacks

import dev.civmc.bumhug.Hack
import dev.civmc.bumhug.util.getPlayerByString
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player

class PingCommand: Hack(), CommandExecutor {
    override val configName = "pingCommand"
    override val prettyName = "Ping Command"
    override val commandName = "ping"

    override fun onCommand(sender: CommandSender, command: Command?, commandName: String?, args: Array<out String>): Boolean {
        var player = sender as Player
        if (args.size == 1) {
            if (!sender.hasPermission("bumhug.ping.other")) {
                sender.sendMessage("${ChatColor.RED}You don't have permission to get another player's ping.")
                return true
            }

            val maybePlayer = getPlayerByString(args[0])
            if (maybePlayer == null) {
                sender.sendMessage("${ChatColor.RED}That player was not found")
                return true
            }

            player = maybePlayer
        } else if (args.size > 1)
            return false

        val ping = getPing(player)
        val namePossesive = if (args.size == 0) "Your" else "${player.name}'s"

        sender.sendMessage("$namePossesive ping is ${ChatColor.GRAY}$ping ms")
        return true
    }

    fun getPing(p: Player): Int {
        val craftPlayer = p as CraftPlayer
        return craftPlayer.handle.ping
    }
}