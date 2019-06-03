package dev.civmc.bumhug.hacks

import dev.civmc.bumhug.Hack
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryType

class GameFeaturesHack: Hack(), Listener {
	override val configName = "gameFeatures"
	override val prettyName = "Game Features"
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	fun onPistonActivate(event: BlockPistonExtendEvent) {
		if (!config.getBoolean("pistons")) {
			event.setCancelled(true)
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	fun onHopperMoveItem(event: InventoryMoveItemEvent) {
		if (config.getBoolean("hoppers")) {
			return
		}
		if (event.initiator.type == InventoryType.HOPPER || event.source.type == InventoryType.HOPPER) {
			event.setCancelled(true)
		}
	}
}