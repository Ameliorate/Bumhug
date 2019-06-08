package dev.civmc.bumhug.hacks

import dev.civmc.bumhug.Hack
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerInteractEvent

public class DaytimeBed: Hack(), Listener {
	override val configName = "daytimeBed"
	override val prettyName = "Daytime Bed"
	
	private val message = config.getString("message")
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	fun bedRClickToSetSpawn(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK || event.clickedBlock.type != Material.BED_BLOCK) {
			return;
		}

		event.player.setBedSpawnLocation(event.clickedBlock.location, false);
		event.player.sendTitle("", message, 10, 70, 20);
	}
}