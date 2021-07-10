package de.leonheuer.skycave.minievents.lavaevent.listener

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.lavaevent.enums.LavaEventState
import de.leonheuer.skycave.minievents.lavaevent.enums.PlayerState
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class DeathListener(private val main: MiniEvents): Listener {

    @EventHandler
    fun onDeath(event: EntityDamageEvent) {
        val player = event.entity
        if (player !is Player) {
            return
        }

        val eventState = main.lavaEventManager.eventState
        if (eventState == LavaEventState.NOT_RUNNING ||
            eventState == LavaEventState.FINISHED ||
            eventState == LavaEventState.OPEN) {
            return
        }

        val state = main.lavaEventManager.getState(player)
        if (state == null || state == PlayerState.SPECTATING || state == PlayerState.WAITING) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.FIRE ||
            event.cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
            event.cause == EntityDamageEvent.DamageCause.LAVA
        ) {
            main.lavaEventManager.setState(player, PlayerState.SPECTATING)
            main.lavaEventManager.getPlayerList()
                .filter { it.isOnline }
                .filter { it.uniqueId != player.uniqueId }
                .forEach { Bukkit.getPlayer(it.name!!)!!.sendMessage(Message.LAVA_EVENT_OUT.getMessage()
                    .replace("%player", player.name)) }
            player.sendMessage(Message.LAVA_EVENT_OUT_SELF.getMessage())
            val area = main.dataManager.lavaEventArea
            if (area == null) {
                player.gameMode = GameMode.SPECTATOR
            } else {
                player.teleport(area.spectate!!)
            }
        }
    }

}