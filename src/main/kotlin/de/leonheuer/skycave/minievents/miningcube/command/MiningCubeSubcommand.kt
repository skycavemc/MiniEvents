package de.leonheuer.skycave.minievents.miningcube.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.miningcube.model.MiningArea
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

abstract class MiningCubeSubcommand(
    protected val sender: CommandSender,
    protected val args: Array<out String>,
    private val playerOnly: Boolean,
    private val requiredArgs: Int,
    private val checkForArea: Boolean,
    private val message: Message
): BukkitRunnable() {

    protected val main = JavaPlugin.getPlugin(MiniEvents::class.java)
    private var givenArea: MiningArea? = null
    protected lateinit var miningArea: MiningArea
    protected lateinit var player: Player

    override fun run() {
        if (playerOnly) {
            if (sender !is Player) {
                sender.sendMessage("&cThis command is for players only.")
                return
            }
            player = sender
        }
        if (requiredArgs > 1 && args.size < requiredArgs) {
            sender.sendMessage(message.getMessage())
            return
        }
        if (checkForArea) {
            givenArea = main.dataManager.getMiningArea(args[1])
            if (givenArea == null) {
                sender.sendMessage(Message.MINING_CUBE_UNKNOWN.getMessage())
                return
            }
            miningArea = givenArea as MiningArea
        }

        try {
            execute()
        } catch (e: UninitializedPropertyAccessException) {
            sender.sendMessage("§cThe command failed to execute due to an internal error.")
        }
    }

    abstract fun execute()

}