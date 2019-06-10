package dev.civmc.bumhug.hacks

import dev.civmc.bumhug.Bumhug
import dev.civmc.bumhug.Hack
import dev.civmc.bumhug.util.tryToTeleportVertically
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.inventory.meta.FireworkMeta
import java.util.logging.Level

class GameFeatures: Hack(), Listener {
	override val configName = "gameFeatures"
	override val prettyName = "Game Features"
	
	private val pistons = config.getBoolean("pistons")
	private val hoppers = config.getBoolean("hoppers")
	private val packedIceInHell = config.getBoolean("packedIceInHell")
	private val villagerTrading = config.getBoolean("villagerTrading")
	private val witherSpawning = config.getBoolean("witherSpawning")
	private val shulkerBoxUse = config.getBoolean("shulkerBoxUse")
	private val enderChestUse = config.getBoolean("enderChestUse")
	private val enderChestPlacement = config.getBoolean("enderChestPlacement")
	private val disableElytraFirework = config.getBoolean("disableElytraFirework")
	private val enableMinecartTeleporter = config.getBoolean("minecartTeleporter")

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPistonActivate(event: BlockPistonExtendEvent) {
		if (!pistons) {
			event.setCancelled(true)
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	fun onHopperMoveItem(event: InventoryMoveItemEvent) {
		if (!hoppers) {
			if (event.initiator.type == InventoryType.HOPPER) {
				event.setCancelled(true)
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPackedIcePlace(event: BlockPlaceEvent) {
		if (!packedIceInHell && event.block.type == Material.PACKED_ICE && event.block.biome == Biome.HELL) {
			if (event.player != null) {
				event.player.sendMessage("" + ChatColor.RED + "Packed ice cannot be placed in hell.")
			}
			event.setCancelled(true)
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onVillagerTrade(event: PlayerInteractEntityEvent) {
		if (!villagerTrading) {
			val npc = event.rightClicked
			if (npc != null) {
				if (npc.type == EntityType.VILLAGER) {
					event.setCancelled(true)
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onWitherSpawn(event: CreatureSpawnEvent) {
		if (!witherSpawning) {
			if (event.entityType == EntityType.WITHER && event.spawnReason == SpawnReason.BUILD_WITHER) {
				event.setCancelled(true)
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onEnderChestPlacement(event: BlockPlaceEvent) {
		if (!enderChestPlacement) {
			if (event.block.type == Material.ENDER_CHEST) {
				event.setCancelled(true)
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true) 
	fun EnderChestUse(event: PlayerInteractEvent) {
		if (!enderChestUse) {
			if (event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock.type == Material.ENDER_CHEST) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onShulkerBoxUse(event: InventoryOpenEvent){
		if (!shulkerBoxUse && event.inventory.type == InventoryType.SHULKER_BOX) {
			event.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onShulkerBoxHoppering(event: InventoryMoveItemEvent) {
		if (!shulkerBoxUse && event.destination != null && event.source != null) {
			if (event.destination.type == InventoryType.SHULKER_BOX || event.source.type == InventoryType.SHULKER_BOX) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	fun onPlayerFirework(event: PlayerInteractEvent) {
		if (disableElytraFirework && event.item.itemMeta is FireworkMeta) {
			val meta: FireworkMeta = event.item.itemMeta as FireworkMeta

			if (event.player.isFlying)
				event.isCancelled = true

			if (!meta.hasEffects())
				event.isCancelled = true

			// double ended test: try to disable all fireworks if flying, but also disable all empty fireworks.
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerQuitInMinecart(event: PlayerQuitEvent) {
		if (enableMinecartTeleporter) {
			if (event.player.vehicle == null)
				return

			val vehicleLocatoin = event.player.vehicle.location
			event.player.leaveVehicle()

			if (!tryToTeleportVertically(event.player, vehicleLocatoin, "logged out")) {
				event.player.setHealth(0.000000)
				Bumhug.instance!!.logger.log(Level.INFO, "Player '${event.player.name}' logged out in vehicle: killed")
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onVehicleExitTeleport(event: VehicleExitEvent) {
		if (enableMinecartTeleporter) {
			if (event.vehicle == null)
				return

			val player: Player
			val passanger = event.exited;
			if (passanger is Player) player = passanger else return

			Bukkit.getScheduler().runTaskLater(Bumhug.instance, {
				if (!tryToTeleportVertically(player, event.vehicle.location, "exiting vehicle")) {
					player.health = 0.000000
					Bumhug.instance!!.logger.log(Level.INFO, "Player '${player.name}' exiting vehicle: killed")
				}
			}, 2)
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	fun onVehicleDestoryTeleport(event: VehicleDestroyEvent) {
		if (enableMinecartTeleporter) {
			if (event.vehicle == null)
				return

			var passangers = event.vehicle.passengers;
			if (passangers == null || passangers.isEmpty())
				return

			passangers.removeIf { !(it is Player) }
			passangers.forEach {
				run {
					var player = it as Player
					Bukkit.getScheduler().runTaskLater(Bumhug.instance, {
						if (!tryToTeleportVertically(player, event.vehicle.location, "in destroyed vehicle")) {
							it.health = 0.000000
							Bumhug.instance!!.logger.log(Level.INFO, "Player '${player.name}' exiting vehicle: killed")
						}
					}, 2)
				}
			}
		}
	}
}