package de.leonheuer.skycave.minievents.lavaevent.model

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.lavaevent.enums.LavaEventState
import de.leonheuer.skycave.minievents.lavaevent.enums.PlayerState
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.*

class LavaEvent(private val area: LavaEventArea) {

    val participants = HashMap<UUID, PlayerState>()
    var state = LavaEventState.OPEN
        private set
    private var scheduler: BukkitTask? = null
    private val untouchedBlocks = ArrayList<Block>()
    private val touchedBlocks = ArrayList<Block>()
    private var limit = 0

    fun start(): Boolean {
        if (area.spawn == null || area.spectate == null || area.radius <= 0) {
            return false
        }

        val blocks = area.getAllBlocks() ?: return false
        untouchedBlocks.addAll(blocks)
        limit = untouchedBlocks.count() / 12

        state = LavaEventState.RUNNING
        Bukkit.broadcast(Component.text(Message.LAVA_EVENT_BEGIN.getMessage()))
        val main = JavaPlugin.getPlugin(MiniEvents::class.java)
        scheduler = Bukkit.getScheduler().runTaskTimer(main, this::nextBlock, 0, area.period * 20L)
        return true
    }

    private fun nextBlock() {
        val block = untouchedBlocks.random()
        block.type = Material.LAVA
        block.world.spawnParticle(Particle.LAVA, block.location.clone().add(0.0, 1.0, 0.0), 5)

        untouchedBlocks.remove(block)
        touchedBlocks.add(block)

        if (untouchedBlocks.count() <= limit) {
            stop()
        }
    }

    fun stop(): Boolean {
        if (state == LavaEventState.OPEN) return false
        if (scheduler != null) scheduler!!.cancel()
        for (block in touchedBlocks) {
            block.type = area.material
        }

        val winners = StringJoiner(", ")
        for (entry in participants) {
            val player = Bukkit.getPlayer(entry.key) ?: continue
            if (entry.value == PlayerState.PARTICIPATING) {
                winners.add(player.name)
                if (area.spectate != null) player.teleport(area.spectate!!)
            }
        }
        participants.clear()

        Bukkit.broadcast(Component.text(Message.LAVA_EVENT_WINNERS.getMessage()
            .replace("%winners", winners.toString())))
        return true
    }

    fun out(player: Player) {
        for (uuid in participants.keys) {
            if (uuid == player.uniqueId) continue
            val other = Bukkit.getPlayer(uuid) ?: continue
            other.sendMessage(Message.LAVA_EVENT_OUT.getMessage().replace("%player", player.name))
        }
        if (participants.filter { it.value == PlayerState.PARTICIPATING }.count() <= 1) {
            stop()
        }
    }

}