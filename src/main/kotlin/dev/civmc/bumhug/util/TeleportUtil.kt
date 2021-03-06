package dev.civmc.bumhug.util

import dev.civmc.bumhug.Bumhug
import java.util.LinkedList

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.logging.Level

fun checkForTeleportSpace(loc: Location): Boolean {
    val block = loc.block
    val mat = block.type
    if (mat.isSolid) {
        return false
    }
    val above = block.getRelative(BlockFace.UP)
    return if (above.type.isSolid) {
        false
    } else true
}

fun tryToTeleportVertically(player: Player, location: Location, reason: String): Boolean {
    var loc = location.clone()
    loc.x = Math.floor(loc.x) + 0.500000
    loc.y = Math.floor(loc.y) + 0.02
    loc.z = Math.floor(loc.z) + 0.500000
    val baseLoc = loc.clone()
    val world = baseLoc.world
    // Check if teleportation here is viable
    var performTeleport = checkForTeleportSpace(loc)
    if (!performTeleport) {
        loc.y = loc.y + 1.000000
        performTeleport = checkForTeleportSpace(loc)
    }
    if (performTeleport) {
        player.velocity = Vector()
        player.teleport(loc)
        Bumhug.instance.logger.log(Level.INFO, "Player '${player.name}' $reason: Teleported to $loc")
        return true
    }
    loc = baseLoc.clone()
    // Create a sliding window of block types and track how many of those
    //  are solid. Keep fetching the block below the current block to move down.
    var air_count = 0
    val air_window = LinkedList<Material>()
    loc.y = (world.maxHeight.toFloat() - 2).toDouble()
    var block = world.getBlockAt(loc)
    for (i in 0..3) {
        val block_mat = block.type
        if (!block_mat.isSolid) {
            ++air_count
        }
        air_window.addLast(block_mat)
        block = block.getRelative(BlockFace.DOWN)
    }
    // Now that the window is prepared, scan down the Y-axis.
    while (block.y >= 1) {
        val block_mat = block.type
        if (block_mat.isSolid) {
            if (air_count == 4) {
                player.velocity = Vector()
                loc = block.location
                loc.x = Math.floor(loc.x) + 0.500000
                loc.y = loc.y + 1.02
                loc.z = Math.floor(loc.z) + 0.500000
                player.teleport(loc)
                Bumhug.instance.logger.log(Level.INFO, "Player '${player.name}' $reason: Teleported to $loc")
                return true
            }
        } else {
            ++air_count
        }
        air_window.addLast(block_mat)
        if (!air_window.removeFirst().isSolid) {
            --air_count
        }
        block = block.getRelative(BlockFace.DOWN)
    }
    return false
}

