package de.leonheuer.skycave.minievents.oregen.listener

import de.leonheuer.skycave.minievents.MiniEvents
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val main: MiniEvents): Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (main.boosterManager.isRunning) {
            main.boosterManager.bossBar.addPlayer(event.player)
        }
    }

}