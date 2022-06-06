package de.leonheuer.skycave.minievents.miningcube.command

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.util.Util
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil
import java.util.*

class MiningCubeCommand(private val main: MiniEvents): CommandExecutor, TabCompleter {

    private class GenerateSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, false, 2, true, Message.MINING_CUBE_AREA_NOT_GIVEN
    ) {
        override fun execute() {
            sender.sendMessage(Message.MINING_CUBE_GENERATE_BEGIN.getMessage().replace("%name", miningArea.key))
            val start = System.currentTimeMillis()
            Util.generateCuboidBlockArea(miningArea)
            val end = System.currentTimeMillis()
            sender.sendMessage(Message.MINING_CUBE_GENERATE_FINISH.getMessage()
                .replace("%name", miningArea.key)
                .replace("%time", "${end - start}ms"))
        }
    }

    private class CreateSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, true, 2, false, Message.MINING_CUBE_CREATE_SYNTAX
    ) {
        override fun execute() {
            if (main.dataManager.getMiningArea(args[1]) != null) {
                player.sendMessage(Message.MINING_CUBE_CREATE_EXISTS.getMessage()
                    .replace("%name", args[1]))
                return
            }

            main.dataManager.createMiningArea(args[1], player.location)
            sender.sendMessage(Message.MINING_CUBE_CREATE_SUCCESS.getMessage()
                .replace("%name", args[1]))
            sender.sendMessage(Message.MINING_CUBE_CREATE_SPAWN.getMessage())
        }
    }

    private class DeleteSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, true, 2, true, Message.MINING_CUBE_AREA_NOT_GIVEN
    ) {
        override fun execute() {
            main.dataManager.deleteMiningArea(miningArea)
            sender.sendMessage(Message.MINING_CUBE_DELETE_SUCCESS.getMessage()
                .replace("%name", args[1]))
        }
    }

    private class Pos1Subcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, true, 2, true, Message.MINING_CUBE_AREA_NOT_GIVEN
    ) {
        override fun execute() {
            val world = miningArea.world
            miningArea.world  = player.location.world
            miningArea.from = player.location.toVector()
            if (player.location.world != world) {
                sender.sendMessage(Message.MINING_CUBE_POS1_WORLD_MISMATCH.getMessage())
            } else {
                sender.sendMessage(Message.MINING_CUBE_POS1.getMessage())
            }
        }
    }

    private class Pos2Subcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, true, 2, true, Message.MINING_CUBE_AREA_NOT_GIVEN
    ) {
        override fun execute() {
            val world = miningArea.world
            miningArea.world  = player.location.world
            miningArea.to = player.location.toVector()
            if (player.location.world != world) {
                sender.sendMessage(Message.MINING_CUBE_POS2_WORLD_MISMATCH.getMessage())
            } else {
                sender.sendMessage(Message.MINING_CUBE_POS2.getMessage())
            }
        }
    }

    private class SetSpawnSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, true, 2, true, Message.MINING_CUBE_AREA_NOT_GIVEN
    ) {
        override fun execute() {
            miningArea.spawn = player.location
            sender.sendMessage(Message.MINING_CUBE_SET_SPAWN_SUCCESS.getMessage())
        }
    }

    private class SpawnSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, true, 2, true, Message.MINING_CUBE_AREA_NOT_GIVEN
    ) {
        override fun execute() {
            val spawn = miningArea.spawn
            if (spawn == null) {
                player.sendMessage(Message.MINING_CUBE_SPAWN_UNSET.getMessage())
                return
            }

            player.sendMessage(Message.MINING_CUBE_SPAWN_SUCCESS.getMessage())
            player.teleport(spawn)
        }
    }

    private class SetChanceSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, false, 4, true, Message.MINING_CUBE_SET_CHANCE_SYNTAX
    ) {
        override fun execute() {
            try {
                val mat = Material.valueOf(args[2].uppercase())
                val chance = args[3].toInt()
                if (chance < 0 || chance > 1000) {
                    sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_NUMBER_OUT_OF_RANGE.getMessage())
                    return
                }
                if (chance == 0) {
                    miningArea.chances.remove(mat)
                    sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_SUCCESS.getMessage()
                        .replace("%mat", mat.toString().lowercase())
                        .replace("%chance", chance.toString())
                    )
                    return
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
            } catch (e: IllegalArgumentException) {
                sender.sendMessage(Message.MINING_CUBE_SET_CHANCE_INVALID_MATERIAL.getMessage()
                    .replace("%mat", args[1]))
            } catch (e: NumberFormatException) {
                sender.sendMessage(Message.INVALID_NUMBER.getMessage()
                    .replace("%number", args[2]))
            }
        }
    }

    private class ChancesSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, false, 2, true, Message.MINING_CUBE_AREA_NOT_GIVEN
    ) {
        override fun execute() {
            if (miningArea.chances.isEmpty()) {
                sender.sendMessage(Message.MINING_CUBE_CHANCES_UNSET.getMessage())
                return
            }

            val sj = StringJoiner("§8, §r")
            miningArea.chances.forEach {
                sj.add(Message.MINING_CUBE_CHANCES_ENTRY.getFormatted()
                    .replace("%mat", it.key.toString().lowercase())
                    .replace("%chance", it.value.toString()))
            }
            sender.sendMessage(Message.MINING_CUBE_CHANCES.getMessage().replace("%entries", sj.toString()))
        }
    }

    private class TpSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, true, 3, true, Message.MINING_CUBE_TP_SYNTAX
    ) {
        override fun execute() {
            if (args[2] == "pos1") {
                player.teleport(miningArea.from.toLocation(miningArea.world))
                player.sendMessage(Message.MINING_CUBE_TP_SUCCESS.getMessage().replace("%pos", "1"))
                return
            }

            if (args[2] == "pos2") {
                player.teleport(miningArea.to.toLocation(miningArea.world))
                player.sendMessage(Message.MINING_CUBE_TP_SUCCESS.getMessage().replace("%pos", "2"))
                return
            }

            player.sendMessage(Message.MINING_CUBE_TP_POSITIONS.getMessage())
        }
    }

    private class ListSubcommand(sender: CommandSender, args: Array<out String>): MiningCubeSubcommand(
        sender, args, false, 1, false, Message.MINING_CUBE_TP_SYNTAX
    ) {
        override fun execute() {
            if (main.dataManager.getMiningAreas().isEmpty()) {
                sender.sendMessage(Message.MINING_CUBE_LIST_NONE.getMessage())
                return
            }

            val sj = StringJoiner("§8, §7")
            main.dataManager.getMiningAreas().forEach { sj.add(it.key) }
            player.sendMessage(Message.MINING_CUBE_LIST.getMessage()
                .replace("%list", sj.toString()))
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            help(sender)
            return true
        }

        when(args[0].lowercase()) {
            "generate" -> GenerateSubcommand(sender, args).runTask(main)
            "create" -> CreateSubcommand(sender, args).runTask(main)
            "delete" -> DeleteSubcommand(sender, args).runTask(main)
            "pos1" -> Pos1Subcommand(sender, args).runTask(main)
            "pos2" -> Pos2Subcommand(sender, args).runTask(main)
            "setspawn" -> SetSpawnSubcommand(sender, args).runTask(main)
            "spawn" -> SpawnSubcommand(sender, args).runTask(main)
            "setchance" -> SetChanceSubcommand(sender, args).runTask(main)
            "chances" -> ChancesSubcommand(sender, args).runTask(main)
            "tp" -> TpSubcommand(sender, args).runTask(main)
            "list" -> ListSubcommand(sender, args).runTask(main)
            "help" -> help(sender)
            else -> sender.sendMessage(Message.MINING_CUBE_UNKNOWN_COMMAND.getMessage())
        }
        return true
    }

    private fun help(sender: CommandSender) {
        sender.sendMessage(Message.MINING_CUBE_HELP_GENERATE.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_CREATE.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_DELETE.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_POS1.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_POS2.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_SET_SPAWN.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_SPAWN.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_SET_CHANCE.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_CHANCES.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_TP.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_LIST.getFormatted())
        sender.sendMessage(Message.MINING_CUBE_HELP_HELP.getFormatted())
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val arguments = ArrayList<String>()
        val completions = ArrayList<String>()

        if (args.size == 1) {
            arguments.add("generate")
            arguments.add("create")
            arguments.add("delete")
            arguments.add("generate")
            arguments.add("pos1")
            arguments.add("pos2")
            arguments.add("setspawn")
            arguments.add("spawn")
            arguments.add("setchance")
            arguments.add("chances")
            arguments.add("tp")
            arguments.add("list")
            arguments.add("help")

            StringUtil.copyPartialMatches(args[0], arguments, completions)
        }

        if (args.size == 2) {
            if (args[0] != "list" && args[0] != "help") {
                main.dataManager.getMiningAreas().forEach { arguments.add(it.key) }
                StringUtil.copyPartialMatches(args[1], arguments, completions)
            }
        }

        if (args.size == 3) {
            when (args[0]) {
                "setchance" -> {
                    Material.values().forEach { arguments.add(it.toString().lowercase()) }
                    StringUtil.copyPartialMatches(args[2], arguments, completions)
                }
                "tp" -> {
                    arguments.add("pos1")
                    arguments.add("pos2")
                    StringUtil.copyPartialMatches(args[2], arguments, completions)
                }
            }
        }

        completions.sort()
        return completions
    }

}