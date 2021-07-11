package de.leonheuer.skycave.minievents

import de.leonheuer.skycave.minievents.lavaevent.command.LavaEventCommand
import de.leonheuer.skycave.minievents.lavaevent.listener.DeathListener
import de.leonheuer.skycave.minievents.lavaevent.manager.LavaEventManager
import de.leonheuer.skycave.minievents.manager.DataManager
import de.leonheuer.skycave.minievents.miningcube.command.MiningCubeCommand
import de.leonheuer.skycave.minievents.oregen.command.BoosterCommand
import de.leonheuer.skycave.minievents.oregen.command.ErzeCommand
import de.leonheuer.skycave.minievents.oregen.listener.CobbleGenerationListener
import de.leonheuer.skycave.minievents.oregen.listener.PlayerJoinListener
import de.leonheuer.skycave.minievents.oregen.manager.BoosterManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MiniEvents: JavaPlugin() {

    lateinit var dataManager: DataManager
        private set
    lateinit var lavaEventManager: LavaEventManager
        private set
    lateinit var boosterManager: BoosterManager
        private set

    @Suppress("Deprecation")
    override fun onEnable() {
        dataManager = DataManager(this)
        //lavaEventManager = LavaEventManager()
        boosterManager = BoosterManager(this)

        getCommand("miningcube")!!.setExecutor(MiningCubeCommand(this))
        //getCommand("lavaevent")!!.setExecutor(LavaEventCommand(this))
        getCommand("erze")!!.setExecutor(ErzeCommand(this))
        getCommand("booster")!!.setExecutor(BoosterCommand(this))

        val pm = Bukkit.getPluginManager()
        //pm.registerEvents(DeathListener(this), this)
        pm.registerEvents(CobbleGenerationListener(this), this)
        pm.registerEvents(PlayerJoinListener(this), this)

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, {
            if (boosterManager.isRunning) {
                val timeBetween = boosterManager.finished - System.currentTimeMillis()
                val progress = timeBetween / (boosterManager.seconds * 1000.0)
                boosterManager.bossBar.progress = progress
            }
        }, 0, 20L)
    }

    override fun onDisable() {
        dataManager.writeToFile()
    }

}