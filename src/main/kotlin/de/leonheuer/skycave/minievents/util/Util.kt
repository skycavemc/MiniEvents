package de.leonheuer.skycave.minievents.util

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.miningcube.model.MiningArea
import de.leonheuer.skycave.minievents.oregen.enums.OreResult
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

object Util {

    private val main = JavaPlugin.getPlugin(MiniEvents::class.java)

    fun getRandomMaterialFromHashMap(hashMap: HashMap<Material, Int>): Material {
        var random = Random.nextInt(1000)
        for (entry in hashMap) {
            random -= entry.value
            if (random < 0) {
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

    fun getIntRange(first: Int, second: Int): IntRange {
        if (first < second) {
            return first..second
        }
        if (second < first) {
            return second..first
        }
        return first..second
    }

    fun canGenerate(block: Block, source: Block, face: BlockFace?): Boolean {
        val liquid = source.type
        return when (block.type) {
            // lava flows into water
            Material.WATER -> liquid == Material.LAVA
            Material.AIR, Material.CAVE_AIR, Material.VOID_AIR -> {
                if (liquid == Material.LAVA) {
                    return when (face) {
                        // lava flows into air and has water in front, left or right
                        BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST -> hasWater(block, face)
                        // lava flows down and has water around it
                        BlockFace.DOWN -> hasWaterAround(block)
                        else -> false
                    }
                } else if (liquid == Material.WATER) {
                    return false
                }
                false
            }
            else -> false
        }
    }

    private fun hasWater(block: Block, face: BlockFace): Boolean {
        return block.getRelative(face).type == Material.WATER ||
                block.getRelative(turnFaceClockwise(face)).type == Material.WATER ||
                block.getRelative(turnFaceCounterClockwise(face)).type == Material.WATER
    }

    private fun hasWaterAround(block: Block): Boolean {
        return block.getRelative(BlockFace.NORTH).type == Material.WATER ||
                block.getRelative(BlockFace.EAST).type == Material.WATER ||
                block.getRelative(BlockFace.SOUTH).type == Material.WATER ||
                block.getRelative(BlockFace.WEST).type == Material.WATER
    }

    private fun turnFaceClockwise(input: BlockFace): BlockFace {
        return when (input) {
            BlockFace.NORTH -> BlockFace.EAST
            BlockFace.EAST -> BlockFace.SOUTH
            BlockFace.SOUTH -> BlockFace.WEST
            BlockFace.WEST -> BlockFace.NORTH
            else -> throw IllegalArgumentException()
        }
    }

    private fun turnFaceCounterClockwise(input: BlockFace): BlockFace {
        return when (input) {
            BlockFace.NORTH -> BlockFace.WEST
            BlockFace.EAST -> BlockFace.NORTH
            BlockFace.SOUTH -> BlockFace.EAST
            BlockFace.WEST -> BlockFace.SOUTH
            else -> throw IllegalArgumentException()
        }
    }

    fun generateOreBlock(): Material {
        var rnd: Int = Random.nextInt(1000)
        for (result in OreResult.values()) {
            rnd -= result.chance
            if (rnd < 0) return result.material
        }
        return Material.COBBLESTONE
    }

    fun generateOreBlock(multiplier: Int): Material {
        var rnd: Int = Random.nextInt(1000)
        for (result in OreResult.values()) {
            if (result.canBeBoosted) {
                rnd -= result.chance * multiplier
                if (rnd < 0) return result.material
            }
        }
        return Material.COBBLESTONE
    }

    fun playCobbleGenerationEffect(block: Block) {
        val x = block.x.toDouble()
        val y = block.y.toDouble()
        val z = block.z.toDouble()
        Bukkit.getServer().scheduler.runTaskAsynchronously(main, Runnable {
            block.world.playSound(
                block.location, Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 2.5f + Math.random()
                    .toFloat()
            )
            for (i in 0..3) {
                val location = Location(
                    block.world,
                    x + Math.random(),
                    y + 0.85 + Math.random(),
                    z + Math.random()
                )
                block.world.spawnParticle(Particle.SMOKE_LARGE, location, 1, 0.0, 0.0, 0.0, 0.0)
            }
        })
    }

    @Suppress("Deprecation")
    fun initiateBoosterCountdown(seconds: Int, multiplier: Int) {
        Bukkit.broadcastMessage(Message.BOOSTER_COUNTDOWN.getFormatted().replace("%sec", "10"))
        playSoundToAll(Sound.BLOCK_NOTE_BLOCK_BASS)

        var delay = 140L
        for (i in 3 downTo 1) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, {
                Bukkit.broadcastMessage(Message.BOOSTER_COUNTDOWN.getFormatted()
                    .replace("%sec", i.toString()))
                playSoundToAll(Sound.BLOCK_NOTE_BLOCK_BASS)
            }, delay)
            delay += 20
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, {
            main.boosterManager.start(seconds, multiplier)

            Bukkit.broadcastMessage("")
            Bukkit.broadcastMessage(Message.BOOSTER_START.getFormatted()
                    .replace("%durance", main.boosterManager.getDurance())
                    .replace("%multiplier", main.boosterManager.multiplier.toString()))
            Bukkit.broadcastMessage("")

            playSoundToAll(Sound.BLOCK_NOTE_BLOCK_PLING)
        }, delay)
    }

    private fun playSoundToAll(sound: Sound) {
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, sound, 1f, 1f)
        }
    }

}