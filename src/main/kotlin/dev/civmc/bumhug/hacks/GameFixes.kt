package dev.civmc.bumhug.hacks

import dev.civmc.bumhug.Hack
import org.bukkit.Material
import org.bukkit.World.Environment
import org.bukkit.block.Biome
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPortalEvent
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.inventory.InventoryHolder

public class GameFixes: Hack(), Listener {
	override val configName = "gameFixes"
	override val prettyName = "Game Fixes"
	
	private val preventStorageTeleport = config.getBoolean("preventStorageTeleport")
	private val preventBedBombing = config.getBoolean("preventBedBombing")
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	fun onStorageTeleport(event: EntityTeleportEvent) {
		if (event.entity is InventoryHolder && preventStorageTeleport) {
			event.setCancelled(true)
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	fun onStoragePortal(event: EntityPortalEvent) {
		if (event.entity is InventoryHolder && preventStorageTeleport) {
			event.setCancelled(true)
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onBedPlace(event: BlockPlaceEvent) {
		if (!preventBedBombing || (event.block.type != Material.BED && event.block.type != Material.BED_BLOCK)) {
			return
		}
		val env = event.block.location.world.environment
		val biome = event.block.biome
		if (env == Environment.NETHER || env == Environment.THE_END || biome == Biome.HELL || biome == Biome.SKY) {
			event.setCancelled(true);
		}
	}
}