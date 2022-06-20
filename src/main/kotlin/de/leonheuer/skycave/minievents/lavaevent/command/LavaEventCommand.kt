package de.leonheuer.skycave.minievents.lavaevent.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.lavaevent.enums.LavaEventState
import de.leonheuer.skycave.minievents.lavaevent.enums.PlayerState
import de.leonheuer.skycave.minievents.lavaevent.model.LavaEvent
import de.leonheuer.skycave.minievents.util.Util
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.StringUtil
import java.util.StringJoiner

class LavaEventCommand(private val main: MiniEvents): CommandExecutor, TabCompleter {

    private val tasks = ArrayList<BukkitTask>()

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
                        player.sendMessage(Message.LAVA_EVENT_JOIN_SELF.getMessage())
                        player.teleport(main.dataManager.lavaEventArea.spawn!!)
                        lavaEvent.participants[uuid] = PlayerState.PARTICIPATING
                        Bukkit.getOnlinePlayers().filter { it != player}.forEach {
                            it.sendMessage(Message.LAVA_EVENT_JOIN.getMessage()
                                .replace("%player", player.name))
                        }
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
                player.sendMessage(Message.LAVA_EVENT_LEAVE.getMessage())
                player.teleport(main.multiverse.mvWorldManager.getMVWorld("Builder").spawnLocation)
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
                val area = main.dataManager.lavaEventArea
                if (area.spawn == null || area.spectate == null || area.radius <= 1 || area.period <= 0) {
                    sender.sendMessage(Message.LAVA_EVENT_START_CONFIG.getMessage())
                    return true
                }

                Bukkit.broadcast(Component.text(Message.LAVA_EVENT_START.getMessage()))
                countdown(60, 0, 20, 40, 50, 57, 58, 59)
                val event = LavaEvent(main.dataManager.lavaEventArea)
                main.lavaEvent = event

                val task = Bukkit.getScheduler().runTaskLater(main, Runnable {
                    if (event.participants.count() < 2) {
                        Bukkit.broadcast(Component.text(Message.LAVA_EVENT_ABORT.getMessage()))
                        main.lavaEvent = null
                        return@Runnable
                    }
                    Util.playSoundToAll(Sound.BLOCK_NOTE_BLOCK_PLING)
                    event.start()
                }, 20L * 60)
                tasks.add(task)
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
                if (!lavaEvent.stop()) {
                    main.lavaEvent = null
                }
                tasks.forEach { it.cancel() }
            }
            "setspawn" -> {
                if (!checkConditions(true, "skybee.minievent.lavaevent.admin", sender)) {
                    return true
                }
                val player = sender as Player
                main.dataManager.lavaEventArea.spawn = player.location
                main.dataManager.saveLavaEventArea()
                player.sendMessage(Message.LAVA_EVENT_SET_SPAWN_SUCCESS.getMessage())
            }
            "setspectate" -> {
                if (!checkConditions(true, "skybee.minievent.lavaevent.admin", sender)) {
                    return true
                }
                val player = sender as Player
                main.dataManager.lavaEventArea.spectate = player.location
                main.dataManager.saveLavaEventArea()
                player.sendMessage(Message.LAVA_EVENT_SET_SPECTATE_SUCCESS.getMessage())
            }
            "setradius" -> {
                if (!checkConditions(false, "skybee.minievent.lavaevent.admin", sender)) {
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

                main.dataManager.lavaEventArea.radius = radius
                main.dataManager.saveLavaEventArea()
                sender.sendMessage(Message.LAVA_EVENT_SET_RADIUS_SUCCESS.getMessage()
                    .replace("%radius", radius.toString()))
            }
            "setmaterial" -> {
                if (!checkConditions(false, "skybee.minievent.lavaevent.admin", sender)) {
                    return true
                }

                val material: Material
                try {
                    material = Material.valueOf(args[1])
                } catch (e: IllegalArgumentException) {
                    sender.sendMessage(Message.INVALID_MATERIAL.getMessage())
                    return true
                }

                val dm = main.dataManager
                dm.lavaEventArea.material = material
                dm.lavaEventArea.getAllBlocks()?.forEach { it.type = material }
                dm.saveLavaEventArea()
                sender.sendMessage(Message.LAVA_EVENT_SET_MATERIAL_SUCCESS.getMessage()
                    .replace("%material", material.name))
            }
            "setperiod" -> {
                if (!checkConditions(false, "skybee.minievent.lavaevent.admin", sender)) {
                    return true
                }

                val period: Int
                try {
                    period = Integer.parseInt(args[1])
                } catch (e: NumberFormatException) {
                    sender.sendMessage(Message.INVALID_NUMBER.getMessage()
                        .replace("%number", args[1]))
                    return true
                }

                main.dataManager.lavaEventArea.period = period
                main.dataManager.saveLavaEventArea()
                sender.sendMessage(Message.LAVA_EVENT_SET_PERIOD_SUCCESS.getMessage()
                    .replace("%period", period.toString()))
            }
            "info" -> {
                if (!checkConditions(false, "skybee.minievent.lavaevent.admin", sender)) {
                    return true
                }

                sender.sendMessage(Message.LAVA_EVENT_INFO_HEADER.getFormatted())

                val area = main.dataManager.lavaEventArea
                val spawn = area.spawn
                val spawnLocation = if (spawn == null) "§cNoch nicht gesetzt" else Util.locationAsString(spawn)
                sender.sendMessage(Message.LAVA_EVENT_INFO.getFormatted()
                    .replace("%property", "Spawn").replace("%value", spawnLocation))
                val spectate = area.spectate
                val spectateLocation = if (spectate == null) "§cNoch nicht gesetzt" else Util.locationAsString(spectate)
                sender.sendMessage(Message.LAVA_EVENT_INFO.getFormatted()
                    .replace("%property", "Spectate").replace("%value", spectateLocation))
                sender.sendMessage(Message.LAVA_EVENT_INFO.getFormatted()
                    .replace("%property", "Radius").replace("%value", area.radius.toString()))
                sender.sendMessage(Message.LAVA_EVENT_INFO.getFormatted()
                    .replace("%property", "Period").replace("%value", area.period.toString()))
                sender.sendMessage(Message.LAVA_EVENT_INFO.getFormatted()
                    .replace("%property", "Material").replace("%value", area.material.toString()))

                val event = main.lavaEvent
                if (event != null) {
                    val state = if (event.state == LavaEventState.OPEN) "§aoffen" else "§claufend"
                    sender.sendMessage(Message.LAVA_EVENT_INFO.getFormatted()
                        .replace("%property", "Status").replace("%value", state))
                    val participants = StringJoiner(", ")
                    var count = 0
                    event.participants.keys.forEach {
                        val player = Bukkit.getPlayer(it) ?: return@forEach
                        participants.add(player.name)
                        count++
                    }
                    val participantsMessage = if (count == 0) "§ckeiner" else participants.toString()
                    sender.sendMessage(Message.LAVA_EVENT_INFO.getFormatted()
                        .replace("%property", "Teilnehmer").replace("%value", participantsMessage))
                }

                sender.sendMessage(Message.LAVA_EVENT_INFO_HEADER.getFormatted())
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

    @Suppress("SameParameterValue")
    private fun countdown(timeToStart: Int, vararg seconds: Int) {
        for (delay in seconds) {
            val task = Bukkit.getScheduler().runTaskLater(main, Runnable {
                Bukkit.broadcast(Component.text(Message.LAVA_EVENT_COUNTDOWN.getMessage()
                    .replace("%seconds", (timeToStart - delay).toString())))
                Util.playSoundToAll(Sound.BLOCK_NOTE_BLOCK_BASS)
            }, 20L * delay)
            tasks.add(task)
        }
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