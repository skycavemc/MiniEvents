package de.leonheuer.skycave.minievents.oregen.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.util.Util
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil

class BoosterCommand(private val main: MiniEvents): CommandExecutor, TabCompleter {

    @Suppress("Deprecation")
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            help(sender)
            return true
        }
        
        when (args[0]) {
            "start" -> {
                if (args.size < 3) {
                    sender.sendMessage(Message.BOOSTER_START_SYNTAX.getMessage())
                    return true
                }

                try {
                    val seconds = args[1].toInt()
                    val multi = args[2].toInt()

                    if (main.boosterManager.isRunning) {
                        val remaining: Int = main.boosterManager.getRemaining()

                        main.boosterManager.reset()
                        main.boosterManager.start(seconds + remaining, multi)

                        Bukkit.broadcastMessage("")
                        Bukkit.broadcastMessage(
                            Message.BOOSTER_START_LONGER.getFormatted()
                                .replace("%durance", main.boosterManager.getDurance())
                                .replace("%multiplier", main.boosterManager.multiplier.toString()))
                        Bukkit.broadcastMessage("")
                        return true
                    }

                    sender.sendMessage(Message.BOOSTER_START_SUCCESS.getMessage())
                    Util.initiateBoosterCountdown(seconds, multi)
                } catch (e: NumberFormatException) {
                    sender.sendMessage(Message.BOOSTER_START_INVALID.getMessage())
                }
            }
            "stop" -> {
                if (main.boosterManager.isRunning) {
                    main.boosterManager.reset()
                    sender.sendMessage(Message.BOOSTER_STOP_SUCCESS.getMessage())
                } else {
                    sender.sendMessage(Message.BOOSTER_STOP_ALREADY.getMessage())
                }
            }
            "status" -> {
                val state = if (main.boosterManager.isRunning) {
                    "aktiv"
                } else {
                    "nicht aktiv"
                }
                sender.sendMessage(
                    Message.BOOSTER_STATUS.getMessage()
                        .replace("%state", state)
                        .replace("%durance", main.boosterManager.getDurance())
                        .replace("%multiplier", main.boosterManager.multiplier.toString())
                )
            }
            "help" -> help(sender)
            else -> sender.sendMessage(Message.BOOSTER_UNKNOWN.getMessage())
        }
        return true
    }

    private fun help(sender: CommandSender) {
        sender.sendMessage(Message.BOOSTER_HELP_START.getFormatted())
        sender.sendMessage(Message.BOOSTER_HELP_STOP.getFormatted())
        sender.sendMessage(Message.BOOSTER_HELP_STATUS.getFormatted())
        sender.sendMessage(Message.BOOSTER_HELP_HELP.getFormatted())
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val arguments = ArrayList<String>()
        val completions = ArrayList<String>()

        if (args.size == 1) {
            arguments.add("start")
            arguments.add("stop")
            arguments.add("status")
            arguments.add("help")
            StringUtil.copyPartialMatches(args[0], arguments, completions)
        }

        completions.sort()
        return completions
    }

}