package de.leonheuer.skycave.minievents

import de.leonheuer.skycave.minievents.lavaevent.command.LavaEventCommand
import de.leonheuer.skycave.minievents.lavaevent.listener.DeathListener
import de.leonheuer.skycave.minievents.lavaevent.model.LavaEvent
import de.leonheuer.skycave.minievents.storage.DataManager
import de.leonheuer.skycave.minievents.miningcube.command.MiningCubeCommand
import de.leonheuer.skycave.minievents.oregen.command.BoosterCommand
import de.leonheuer.skycave.minievents.oregen.command.ErzeCommand
import de.leonheuer.skycave.minievents.oregen.listener.CobbleGenerationListener
import de.leonheuer.skycave.minievents.oregen.listener.PlayerJoinListener
import de.leonheuer.skycave.minievents.oregen.manager.BoosterManager
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class MiniEvents: JavaPlugin() {

    lateinit var dataManager: DataManager
        private set
    lateinit var boosterManager: BoosterManager
        private set
    var lavaEvent: LavaEvent? = null

    override fun onEnable() {
        dataManager = DataManager(this)
        boosterManager = BoosterManager(this)

        registerCommand("miningcube", MiningCubeCommand(this))
        registerCommand("lavaevent", LavaEventCommand(this))
        registerCommand("erze", ErzeCommand(this))
        registerCommand("booster", BoosterCommand(this))

        registerEvents(
            DeathListener(this),
            CobbleGenerationListener(this),
            PlayerJoinListener(this)
        )

        server.scheduler.runTaskTimerAsynchronously(this, Runnable {
            if (boosterManager.isRunning) {
                val timeBetween = boosterManager.finished - System.currentTimeMillis()
                val progress = timeBetween / (boosterManager.seconds * 1000.0)
                boosterManager.bossBar.progress = progress
            }
        }, 0, 20L)
    }

    private fun registerCommand(cmd: String, executor: CommandExecutor) {
        val command = getCommand(cmd)
        if (command == null) {
            logger.severe("No entry for command $cmd found in the plugin.yml.")
            return
        }
        command.setExecutor(executor)
    }

    private fun registerEvents(vararg events: Listener) {
        for (event in events) {
            server.pluginManager.registerEvents(event, this)
        }
    }

    override fun onDisable() {
        dataManager.saveMiningAreas()
    }

}