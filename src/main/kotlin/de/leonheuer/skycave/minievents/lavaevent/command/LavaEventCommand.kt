package de.leonheuer.skycave.minievents.lavaevent.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.lavaevent.enums.LavaEventState
import de.leonheuer.skycave.minievents.lavaevent.enums.PlayerState
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class LavaEventCommand(private val main: MiniEvents): CommandExecutor, TabCompleter {

    private class SetSpawnCommand(sender: CommandSender, args: Array<out String>) : LavaEventSubCommand(
        sender, args, true, "skybee.minievent.lavaevent.setspawn"
    ) {
        override fun execute() {
            main.dataManager.lavaEventArea.spawn = player.location
            player.sendMessage(Message.LAVA_EVENT_SET_SPAWN_SUCCESS.getMessage())
        }
    }

    private class SetSpectateCommand(sender: CommandSender, args: Array<out String>) : LavaEventSubCommand(
        sender, args, true, "skybee.minievent.lavaevent.setspawn"
    ) {
        override fun execute() {
            main.dataManager.lavaEventArea.spectate = player.location
            player.sendMessage(Message.LAVA_EVENT_SET_SPECTATE_SUCCESS.getMessage())
        }
    }

    private class SetRadiusCommand(sender: CommandSender, args: Array<out String>) : LavaEventSubCommand(
        sender, args, true, "skybee.minievent.lavaevent.setspawn"
    ) {
        override fun execute() {
            try {
                main.dataManager.lavaEventArea.radius = args[1].toInt()
                player.sendMessage(Message.LAVA_EVENT_SET_RADIUS_SUCCESS.getMessage())
            } catch (e: NumberFormatException) {
                sender.sendMessage(Message.INVALID_NUMBER.getMessage()
                    .replace("%number", args[1]))
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            help(sender)
            return true
        }

        when (args[0].lowercase()) {
            "join" -> {
                if (sender !is Player) {
                    sender.sendMessage("§cThis command is for players only.")
                    return true
                }
                when (main.lavaEventManager.eventState) {
                    LavaEventState.NOT_RUNNING -> sender.sendMessage(Message.LAVA_EVENT_NOT_RUNNING.getMessage())
                    LavaEventState.RUNNING -> sender.sendMessage(Message.LAVA_EVENT_RUNNING.getMessage())
                    LavaEventState.FINISHED -> sender.sendMessage(Message.LAVA_EVENT_FINISHED.getMessage())
                    LavaEventState.OPEN -> {
                        val state = main.lavaEventManager.getState(sender)
                        if (state == null || state == PlayerState.OUT) {
                            main.lavaEventManager.setState(sender, PlayerState.WAITING)
                            Bukkit.getOnlinePlayers().filter { it != sender}.forEach {
                                it.sendMessage(Message.LAVA_EVENT_JOIN.getMessage()
                                    .replace("%player", sender.name))
                            }
                            sender.sendMessage(Message.LAVA_EVENT_JOIN_SELF.getMessage())
                            sender.teleport(main.dataManager.lavaEventArea.spawn!!)
                        }
                    }
                }
            }
            "leave" -> {
                if (sender !is Player) {
                    sender.sendMessage("§cThis command is for players only.")
                    return true
                }

                val state = main.lavaEventManager.getState(sender)
                if (state == null || state == PlayerState.OUT) {
                    sender.sendMessage(Message.LAVA_EVENT_NOT_IN.getMessage())
                    return true
                }

                when (main.lavaEventManager.eventState) {
                    LavaEventState.NOT_RUNNING -> sender.sendMessage(Message.LAVA_EVENT_NOT_RUNNING.getMessage())
                    LavaEventState.RUNNING -> {
                        when (state) {
                            PlayerState.PARTICIPATING -> {

                            }
                            PlayerState.SPECTATING -> {

                            }
                            else -> {

                            }
                        }
                    }
                    LavaEventState.FINISHED -> {
                        when (state) {
                            PlayerState.SPECTATING -> {

                            }
                            else -> {

                            }
                        }
                    }
                    LavaEventState.OPEN -> {
                        if (state == PlayerState.OUT) {
                            main.lavaEventManager.setState(sender, PlayerState.WAITING)
                            Bukkit.getOnlinePlayers().filter { it != sender}.forEach {
                                it.sendMessage(Message.LAVA_EVENT_JOIN.getMessage()
                                    .replace("%player", sender.name))
                            }
                            sender.sendMessage(Message.LAVA_EVENT_JOIN_SELF.getMessage())
                            sender.teleport(main.dataManager.lavaEventArea.spawn!!)
                        }
                    }
                }
            }
            "start" -> {

            }
            "stop" -> {

            }
            "setspawn" -> SetSpawnCommand(sender, args).runTask(main)
            "setspectate" -> SetSpectateCommand(sender, args).runTask(main)
            "setradius" -> SetRadiusCommand(sender, args).runTask(main)
            "setmaterial" -> {

            }
            "info" -> {

            }
        }
        return true
    }

    private fun help(sender: CommandSender) {
        sender.sendMessage(Message.LAVA_EVENT_HELP_JOIN.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_LEAVE.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_START.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_STOP.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_SET_SPAWN.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_SET_SPECTATE.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_SET_RADIUS.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_SET_MATERIAL.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_INFO.getFormatted())
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        TODO("Not yet implemented")
    }

}