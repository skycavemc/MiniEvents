package de.leonheuer.skycave.minievents.lavaevent.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.lavaevent.enums.LavaEventState
import de.leonheuer.skycave.minievents.lavaevent.enums.PlayerState
import de.leonheuer.skycave.minievents.lavaevent.model.LavaEvent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class LavaEventCommand(private val main: MiniEvents): CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            help(sender)
            return true
        }

        when (args[0].lowercase()) {
            "join" -> {
                if (!checkConditions(true, null, sender)) return true
                val player = sender as Player

                val lavaEvent = main.lavaEvent
                if (lavaEvent == null) {
                    player.sendMessage(Message.LAVA_EVENT_NOT_RUNNING.getMessage())
                    return true
                }

                val uuid = player.uniqueId
                if (lavaEvent.participants.containsKey(uuid)) {
                    player.sendMessage(Message.LAVA_EVENT_JOIN_ALREADY.getMessage())
                    return true
                }

                when (lavaEvent.state) {
                    LavaEventState.RUNNING -> player.sendMessage(Message.LAVA_EVENT_RUNNING.getMessage())
                    LavaEventState.OPEN -> {
                        lavaEvent.participants[uuid] = PlayerState.PARTICIPATING
                        Bukkit.getOnlinePlayers().filter { it != player}.forEach {
                            it.sendMessage(Message.LAVA_EVENT_JOIN.getMessage()
                                .replace("%player", player.name))
                        }
                        player.sendMessage(Message.LAVA_EVENT_JOIN_SELF.getMessage())
                        player.teleport(main.lavaEventArea.spawn!!)
                    }
                }
            }
            "leave" -> {
                if (!checkConditions(true, null, sender)) return true
                val player = sender as Player

                val lavaEvent = main.lavaEvent
                if (lavaEvent == null) {
                    player.sendMessage(Message.LAVA_EVENT_NOT_RUNNING.getMessage())
                    return true
                }

                val uuid = player.uniqueId
                if (!lavaEvent.participants.containsKey(uuid)) {
                    player.sendMessage(Message.LAVA_EVENT_NOT_IN.getMessage())
                    return true
                }

                lavaEvent.participants.remove(uuid)
                lavaEvent.out(player)
                sender.sendMessage(Message.LAVA_EVENT_LEAVE.getMessage())
                //TODO teleport to spawn
            }
            "start" -> {
                if (!checkConditions(false, "skybee.minievent.lavaevent.start", sender)) {
                    return true
                }
                val lavaEvent = main.lavaEvent
                if (lavaEvent != null) {
                    sender.sendMessage(Message.LAVA_EVENT_START_ALREADY.getMessage())
                    return true
                }
                Bukkit.broadcast(Component.text(Message.LAVA_EVENT_START.getMessage()))
                main.lavaEvent = LavaEvent(main.lavaEventArea)
                main.lavaEvent!!.start() //TODO countdown
            }
            "stop" -> {
                if (!checkConditions(false, "skybee.minievent.lavaevent.start", sender)) {
                    return true
                }
                val lavaEvent = main.lavaEvent
                if (lavaEvent == null) {
                    sender.sendMessage(Message.LAVA_EVENT_NOT_RUNNING.getMessage())
                    return true
                }
                Bukkit.broadcast(Component.text(Message.LAVA_EVENT_STOP.getMessage()))
                lavaEvent.stop()
                main.lavaEvent = null
            }
            "setspawn" -> {
                if (!checkConditions(true, "skybee.minievent.lavaevent.admin", sender)) {
                    return true
                }
                val player = sender as Player
                main.lavaEventArea.spawn = player.location
                player.sendMessage(Message.LAVA_EVENT_SET_SPAWN_SUCCESS.getMessage())
            }
            "setspectate" -> {
                if (!checkConditions(true, "skybee.minievent.lavaevent.admin", sender)) {
                    return true
                }
                val player = sender as Player
                main.lavaEventArea.spectate = player.location
                player.sendMessage(Message.LAVA_EVENT_SET_SPECTATE_SUCCESS.getMessage())
            }
            "setradius" -> {
                if (!checkConditions(true, "skybee.minievent.lavaevent.admin", sender)) {
                    return true
                }

                val radius: Int
                try {
                    radius = Integer.parseInt(args[1])
                } catch (e: NumberFormatException) {
                    sender.sendMessage(Message.INVALID_NUMBER.getMessage()
                        .replace("%number", args[1]))
                    return true
                }

                val player = sender as Player
                main.lavaEventArea.radius = radius
                player.sendMessage(Message.LAVA_EVENT_SET_RADIUS_SUCCESS.getMessage())
            }
            "setmaterial" -> {

            }
            "setperiod" -> {

            }
            "info" -> {

            }
        }
        return true
    }

    @Suppress("SameParameterValue")
    private fun checkConditions(playerOnly: Boolean, permission: String?, sender: CommandSender) : Boolean {
        if (playerOnly) {
            if (sender !is Player) sender.sendMessage(Message.NO_PLAYER.getMessage())
            return sender is Player
        }

        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(Message.NO_PERMS.getMessage())
            return false
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
        sender.sendMessage(Message.LAVA_EVENT_HELP_SET_PERIOD.getFormatted())
        sender.sendMessage(Message.LAVA_EVENT_HELP_INFO.getFormatted())
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val arguments = ArrayList<String>()
        val completions = ArrayList<String>()

        if (args.size == 1) {
            arguments.addAll(arrayOf(
                "join", "leave", "start", "stop", "setspawn",
                "setspectate", "setradius", "setmaterial", "setperiod", "info")
            )
            StringUtil.copyPartialMatches(args[0], arguments, completions)
        }
        if (args.size == 2) {
            if (args[0].equals("setmaterial", true)) {
                arguments.addAll(Material.values().map { it.name })
                StringUtil.copyPartialMatches(args[1], arguments, completions)
            }
        }

        completions.sort()
        return completions
    }

}