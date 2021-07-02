package de.leonheuer.skycave.minievents.util

import de.leonheuer.skycave.minievents.miningcube.model.MiningArea
import org.bukkit.Location
import org.bukkit.Material
import kotlin.random.Random

object Util {

    fun generateCuboidBlockArea(miningArea: MiningArea) {
        val from = miningArea.from
        val to = miningArea.to
        for (x in getIntRange(from.blockX, to.blockX)) {
            for (y in getIntRange(from.blockY, to.blockY)) {
                for (z in getIntRange(from.blockZ, to.blockZ)) {
                    val pos = Location(miningArea.world, x.toDouble(), y.toDouble(), z.toDouble())
                    pos.block.type = getRandomMaterialFromHashMap(miningArea.chances)
                }
            }
        }
    }

    private fun getRandomMaterialFromHashMap(hashMap: HashMap<Material, Int>): Material {
        val random = Random.nextInt(1000)
        var i = 0
        for (entry in hashMap) {
            i += entry.value
            if (i < random) {
                return entry.key
            }
        }
        return hashMap.keys.last()
    }

    fun getChanceSum(chances: Collection<Int>): Int {
        var result = 0
        chances.forEach { result += it }
        return result
    }

    private fun getIntRange(first: Int, second: Int): IntRange {
        if (first < second) {
            return first..second
        }
        if (second < first) {
            return second..first
        }
        return first..second
    }

}