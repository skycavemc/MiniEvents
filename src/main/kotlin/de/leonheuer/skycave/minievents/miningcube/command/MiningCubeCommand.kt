package de.leonheuer.skycave.minievents.miningcube.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.miningcube.model.MiningArea
import de.leonheuer.skycave.minievents.util.Util
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MiningCubeCommand(private val main: MiniEvents): CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            help(sender)
            return true
        }

        when(args[0]) {
            "generate" -> {
                val miningArea = main.dataManager.miningArea
                if (miningArea == null) {
                    sender.sendMessage(Message.MINING_CUBE_NO_AREA.getMessage())
                    return true
                }
                sender.sendMessage(Message.MINING_CUBE_GENERATE_PROCESS.getMessage())
                Util.generateCuboidBlockArea(miningArea)
                return true
            }
            "pos1" -> {
                if (sender !is Player) {
                    main.logger.severe("This command is for players only.")
                    return true
                }

                val miningArea = main.dataManager.miningArea
                if (miningArea == null) {
                    main.dataManager.miningArea = MiningArea(sender.location.world, sender.location.toVector(),
                        sender.location.toVector(), HashMap(), null)
                    sender.sendMessage(Message.MINING_CUBE_POS1.getMessage())
                    return true
                }

                val world = miningArea.world
                miningArea.world  = sender.location.world
                miningArea.from = sender.location.toVector()
                if (sender.location.world != world) {
                    sender.sendMessage(Message.MINING_CUBE_POS1_WORLD_MISMATCH.getMessage())
                } else {
                    sender.sendMessage(Message.MINING_CUBE_POS1.getMessage())
                }
                return true
            }
            "pos2" -> {
                if (sender !is Player) {
                    main.logger.severe("This command is for players only.")
                    return true
                }

                val miningArea = main.dataManager.miningArea
                if (miningArea == null) {
                    main.dataManager.miningArea = MiningArea(sender.location.world, sender.location.toVector(),
                        sender.location.toVector(), HashMap(), null)
                    sender.sendMessage(Message.MINING_CUBE_POS2.getMessage())
                    return true
                }

                val world = miningArea.world
                miningArea.world  = sender.location.world
                miningArea.to = sender.location.toVector()
                if (sender.location.world != world) {
                    sender.sendMessage(Message.MINING_CUBE_POS2_WORLD_MISMATCH.getMessage())
                } else {
                    sender.sendMessage(Message.MINING_CUBE_POS2.getMessage())
                }
                return true
            }
            "setspawn" -> {
                if (sender !is Player) {
                    main.logger.severe("This command is for players only.")
                    return true
                }

                val miningArea = main.dataManager.miningArea
                if (miningArea == null) {
                    sender.sendMessage(Message.MINING_CUBE_NO_AREA.getMessage())
                    return true
                }

                miningArea.spawn = sender.location
                sender.sendMessage(Message.MINING_CUBE_SET_SPAWN_SUCCESS.getMessage())
                return true
            }
            "spawn" -> {
                if (sender !is Player) {
                    main.logger.severe("This command is for players only.")
                    return true
                }

                val miningArea = main.dataManager.miningArea
                if (miningArea == null) {
                    sender.sendMessage(Message.MINING_CUBE_NO_AREA.getMessage())
                    return true
                }

                val spawn = miningArea.spawn
                if (spawn == null) {
                    sender.sendMessage(Message.MINING_CUBE_SPAWN_UNSET.getMessage())
                    return true
                }

                sender.sendMessage(Message.MINING_CUBE_SPAWN_SUCCESS.getMessage())
                sender.teleport(spawn)
                return true
            }
            "setchance" -> {
                if (args.size < 3) {
                    sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_SYNTAX.getMessage())
                    return true
                }

                try {
                    val mat = Material.valueOf(args[1].uppercase())
                    val chance = args[2].toInt()
                    if (chance < 0 || chance > 1000) {
                        sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_NUMBER_OUT_OF_RANGE.getMessage())
                        return true
                    }
                    val miningArea = main.dataManager.miningArea
                    if (miningArea == null) {
                        sender.sendMessage(Message.MINING_CUBE_NO_AREA.getMessage())
                        return true
                    }
                    if (chance == 0) {
                        miningArea.chances.remove(mat)
                        sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_SUCCESS.getMessage()
                            .replace("%mat", mat.toString().lowercase())
                            .replace("%chance", chance.toString())
                        )
                        return true
                    }
                    miningArea.chances[mat] = chance
                    sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_SUCCESS.getMessage()
                        .replace("%mat", mat.toString().lowercase())
                        .replace("%chance", chance.toString())
                    )
                    val sum = Util.getChanceSum(miningArea.chances.values)
                    if (sum > 1000) {
                        sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_WARNING.getMessage().replace("%sum", sum.toString()))
                    }
                    return true
                } catch (e: IllegalArgumentException) {
                    sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_INVALID_MATERIAL.getMessage()
                        .replace("%mat", args[1]))
                } catch (e: NumberFormatException) {
                    sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_INVALID_NUMBER.getMessage()
                        .replace("%number", args[2]))
                }
            }
            "chances" -> {
                val miningArea = main.dataManager.miningArea
                if (miningArea == null) {
                    sender.sendMessage(Message.MINING_CUBE_NO_AREA.getMessage())
                    return true
                }

                if (miningArea.chances.isEmpty()) {
                    sender.sendMessage(Message.MINING_CUBE_CHANCES_UNSET.getMessage())
                    return true
                }

                val sj = StringJoiner("§8, §r")
                miningArea.chances.forEach {
                    sj.add(Message.MINING_CUBE_CHANCES_ENTRY.getFormatted()
                        .replace("%mat", it.key.toString().lowercase())
                        .replace("%chance", it.value.toString()))
                }
                sender.sendMessage(Message.MINING_CUBE_CHANCES.getMessage().replace("%entries", sj.toString()))
                return true
            }
            "tp" -> {
                if (sender !is Player) {
                    main.logger.severe("This command is for players only.")
                    return true
                }

                if (args.size < 2) {
                    sender.sendMessage(Message.MINING_CUBE_TP_SYNTAX.getMessage())
                    return true
                }

                val miningArea = main.dataManager.miningArea
                if (miningArea == null) {
                    sender.sendMessage(Message.MINING_CUBE_NO_AREA.getMessage())
                    return true
                }

                if (args[1] == "pos1") {
                    sender.teleport(miningArea.from.toLocation(miningArea.world))
                    sender.sendMessage(Message.MINING_CUBE_TP_SUCCESS.getMessage().replace("%pos", "1"))
                    return true
                }

                if (args[1] == "pos2") {
                    sender.teleport(miningArea.to.toLocation(miningArea.world))
                    sender.sendMessage(Message.MINING_CUBE_TP_SUCCESS.getMessage().replace("%pos", "2"))
                    return true
                }

                sender.sendMessage(Message.MINING_CUBE_TP_POSITIONS.getMessage())
                return true
            }
            "help" -> {
                help(sender)
                return true
            }
            else -> {
                sender.sendMessage(Message.MINING_CUBE_UNKNOWN_COMMAND.getMessage())
            }
        }
        return true
    }

    private fun help(sender: CommandSender) {
        sender.sendMessage(Message.MINING_CUBE_HELP_GENERATE.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_POS1.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_POS2.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_SET_SPAWN.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_SPAWN.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_SET_CHANCE.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_CHANCES.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_TP.getFormatted())
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        val arguments = ArrayList<String>()
        val completions = ArrayList<String>()

        if (args.size == 1) {
            arguments.add("generate")
            arguments.add("pos1")
            arguments.add("pos2")
            arguments.add("setspawn")
            arguments.add("spawn")
            arguments.add("setchance")
            arguments.add("chances")
            arguments.add("tp")
            arguments.add("help")

            StringUtil.copyPartialMatches(args[0], arguments, completions)
        } else if (args.size == 2) {
            when (args[0]) {
                "setchance" -> {
                    Material.values().forEach { arguments.add(it.toString().lowercase()) }
                    StringUtil.copyPartialMatches(args[1], arguments, completions)
                }
                "tp" -> {
                    arguments.add("pos1")
                    arguments.add("pos2")
                    StringUtil.copyPartialMatches(args[1], arguments, completions)
                }
            }
        }

        completions.sort()
        return completions
    }

}