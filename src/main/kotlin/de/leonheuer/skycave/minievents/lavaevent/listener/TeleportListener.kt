package de.leonheuer.skycave.minievents.lavaevent.listener

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class TeleportListener(private val main: MiniEvents) : Listener {

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        if (player.hasPermission("skybee.minievent.lavaevent.admin")) {
            return
        }

        val lavaEvent = main.lavaEvent ?: return
        if (lavaEvent.participants.containsKey(player.uniqueId)) {
            event.isCancelled = true
            player.sendMessage(Message.LAVA_EVENT_TELEPORT.getMessage())
        }
    }

}