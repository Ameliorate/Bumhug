package dev.civmc.bumhug.hacks

import dev.civmc.bumhug.Bumhug
import dev.civmc.bumhug.Hack
import org.bukkit.event.Listener
import org.bukkit.entity.Player
import org.bukkit.event.entity.ExpBottleEvent
import org.bukkit.event.EventPriority
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.logging.Level

class BottledExp: Hack(), Listener {
    override val configName = "bottledExp"
    override val prettyName = "Bottled Experince"

    val expPerBottle = config.getInt("expPerBottle")

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.LEFT_CLICK_BLOCK
                || event.clickedBlock == null
                || event.clickedBlock.type !== Material.ENCHANTMENT_TABLE)
            return

        val totalExperience = computeCurrentEXP(event.player)

        if (event.player.inventory.itemInMainHand == null ||
                event.player.inventory.itemInMainHand.type != Material.GLASS_BOTTLE ||
                totalExperience < expPerBottle)
            return

        createEXPBottles(event.player, totalExperience)
    }

    private fun createEXPBottles(player: Player, totalExperience: Int) {
        val numberOfBottles = ItemMap(player.inventory).getAmount(ItemStack(Material.GLASS_BOTTLE))
        val expAvailable = totalExperience / expPerBottle
        val bottlesToRemove = Math.min(numberOfBottles, expAvailable)

        Bumhug.instance!!.logger.log(Level.INFO,
                "${player.name} interacted with enchanting table, " +
                        "inventory has $numberOfBottles bottles, $totalExperience total experience, " +
                        "enough to fill $bottlesToRemove bottles")

        if (bottlesToRemove == 0) {
            return
        }

        var noSpace = false
        var expBottleCount = 0
        val removeMap = ItemMap()

        removeMap.addItemAmount(ItemStack(Material.GLASS_BOTTLE), bottlesToRemove)

        for (item in removeMap.itemStackRepresentation) {
            val initialAmount = item.amount

            player.inventory.removeItem(item)
            item.type = Material.EXP_BOTTLE

            val result = player.inventory.addItem(item)

            if (result != null && result.size > 0) {
                item.type = Material.GLASS_BOTTLE
                player.inventory.addItem(item)

                noSpace = true

                Bumhug.instance!!.logger.log(Level.INFO, "Cannot store ${item.amount} " +
                        "exp bottles in inventory for ${player.name}")

                break
            } else {
                expBottleCount += initialAmount

                Bumhug.instance!!.logger.log(Level.INFO, "Turned $initialAmount " +
                        "bottles into exp bottles for ${player.name}")
            }
        }

        if (expBottleCount > 0) {
            val endEXP = totalExperience - expBottleCount * expPerBottle

            Bumhug.instance!!.logger.log(Level.INFO, "Set exp for " + player.name + " to " + endEXP)

            player.level = 0
            player.exp = 0f
            player.giveExp(endEXP)

            player.sendMessage("${ChatColor.GREEN}Created $expBottleCount EXP bottles.")
        }

        if (noSpace) {
            player.sendMessage("${ChatColor.RED}Not enough space in inventory for all EXP bottles.")
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun expBottleEvent(event: ExpBottleEvent) {
        event.experience = expPerBottle
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun expBottleEventMonitor(event: ExpBottleEvent) {
        if (event.experience != expPerBottle) {
            Bumhug.instance!!.logger.log(Level.INFO, "Xp control lost: ${event.experience}")
        }
    }

    fun computeCurrentEXP(player: Player): Int {
        // good luck
        val cLevel = player.level.toFloat()
        val progress = player.exp
        var a = 1f
        var b = 6f
        var c = 0f
        var x = 2f
        var y = 7f
        if (cLevel > 16 && cLevel <= 31) {
            a = 2.5f
            b = -40.5f
            c = 360f
            x = 5f
            y = -38f
        } else if (cLevel >= 32) {
            a = 4.5f
            b = -162.5f
            c = 2220f
            x = 9f
            y = -158f
        }
        return Math.floor((a * cLevel * cLevel + b * cLevel + c + progress * (x * cLevel + y)).toDouble()).toInt()
    }
}