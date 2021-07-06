package de.leonheuer.skycave.minievents.oregen.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.oregen.enums.OreResult
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.text.DecimalFormat

class ErzeCommand(private val main: MiniEvents): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("")

        if (main.boosterManager.isRunning) {
            sender.sendMessage(
                Message.ORES_HEADER_BOOSTED.getFormatted()
                    .replace("%multiplier", java.lang.String.valueOf(main.boosterManager.multiplier))
                    .replace("%durance", main.boosterManager.getDurance())
            )
            OreResult.values().filter{ it.canBeBoosted }.forEach {
                val fmt = DecimalFormat("0.#")
                sender.sendMessage(Message.ORES_CHILD_BOOSTED.getFormatted()
                    .replace("%name".toRegex(), it.title)
                    .replace("%chance".toRegex(), fmt.format(it.chance / 10 * main.boosterManager.multiplier)))
            }
        } else {
            sender.sendMessage(Message.ORES_HEADER.getFormatted())
            OreResult.values().forEach {
                val fmt = DecimalFormat("0.#")
                sender.sendMessage(Message.ORES_CHILD.getFormatted()
                    .replace("%name", it.title)
                    .replace("%chance", fmt.format(it.chance / 10)))
            }
        }

        sender.sendMessage("")
        return true
    }

}