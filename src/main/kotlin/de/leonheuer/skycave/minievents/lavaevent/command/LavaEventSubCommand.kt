package de.leonheuer.skycave.minievents.lavaevent.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

abstract class LavaEventSubCommand(
    protected val sender: CommandSender,
    protected val args: Array<out String>,
    private val playerOnly: Boolean,
    private val permission: String?,
) : BukkitRunnable() {

    protected val main = JavaPlugin.getPlugin(MiniEvents::class.java)
    protected lateinit var player: Player

    override fun run() {
        if (playerOnly) {
            if (sender is Player) {
                player = sender
            } else {
                sender.sendMessage("§cThis command is for players only.")
                return
            }
        }
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(Message.NO_PERMS.getMessage())
            return
        }

        execute()
    }

    abstract fun execute()

}