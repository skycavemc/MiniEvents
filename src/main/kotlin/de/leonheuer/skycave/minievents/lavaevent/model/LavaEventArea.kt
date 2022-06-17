package de.leonheuer.skycave.minievents.lavaevent.model

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import kotlin.math.pow
import kotlin.math.sqrt

data class LavaEventArea(
    var spawn: Location?,
    var spectate: Location?,
    var radius: Int,
    var period: Int,
    var material: Material
    ) {

    fun getAllBlocks(): ArrayList<Block>? {
        val spawn = this.spawn ?: return null
        val result = ArrayList<Block>()

        for (x in (-1 * radius) .. radius) {
            for (z in (-1 * radius) .. radius) {
                val distanceToCenter = sqrt(
                    x.toDouble().pow(2) + z.toDouble().pow(2)
                )
                if (distanceToCenter < radius + 1) {
                    val loc = spawn.clone().add(x.toDouble(), -1.0, z.toDouble())
                    val block = spawn.world.getBlockAt(loc)
                    result.add(block)
                }
            }
        }
        return result
    }

}