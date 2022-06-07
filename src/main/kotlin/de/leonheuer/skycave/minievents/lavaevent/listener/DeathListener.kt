package de.leonheuer.skycave.minievents.lavaevent.listener

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.lavaevent.enums.LavaEventState
import de.leonheuer.skycave.minievents.lavaevent.enums.PlayerState
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class DeathListener(private val main: MiniEvents): Listener {

    @EventHandler
    fun onDeath(event: EntityDamageEvent) {
        val player = event.entity
        if (player !is Player)  return
        val uuid = player.uniqueId

        val lavaEvent = main.lavaEvent ?: return
        if (lavaEvent.state != LavaEventState.RUNNING) return
        if (lavaEvent.participants[uuid] != PlayerState.PARTICIPATING) return

        if (event.cause == EntityDamageEvent.DamageCause.FIRE ||
            event.cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
            event.cause == EntityDamageEvent.DamageCause.LAVA
        ) {
            lavaEvent.participants[uuid] = PlayerState.SPECTATING
            lavaEvent.out(player)
            player.fireTicks = 0
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            player.sendMessage(Message.LAVA_EVENT_OUT_SELF.getMessage())
            player.teleport(main.dataManager.lavaEventArea.spectate!!)
        }
    }

}