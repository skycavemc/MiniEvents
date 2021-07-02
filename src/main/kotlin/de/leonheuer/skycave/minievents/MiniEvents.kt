package de.leonheuer.skycave.minievents

import de.leonheuer.skycave.minievents.lavaevent.command.LavaEventCommand
import de.leonheuer.skycave.minievents.lavaevent.manager.LavaEventManager
import de.leonheuer.skycave.minievents.manager.DataManager
import de.leonheuer.skycave.minievents.miningcube.command.MiningCubeCommand
import org.bukkit.plugin.java.JavaPlugin

class MiniEvents: JavaPlugin() {

    lateinit var dataManager: DataManager
        private set
    lateinit var lavaEventManager: LavaEventManager
        private set

    override fun onEnable() {
        dataManager = DataManager(this)
        lavaEventManager = LavaEventManager()

        getCommand("miningcube")!!.setExecutor(MiningCubeCommand(this))
        getCommand("lavaevent")!!.setExecutor(LavaEventCommand())
    }

    override fun onDisable() {
        dataManager.writeToFile()
    }

}