package de.leonheuer.skycave.minievents.util

import de.leonheuer.skycave.minievents.enums.Message
import de.leonheuer.skycave.minievents.miningcube.model.MiningArea
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitRunnable

class BlockGenerator(private val miningArea: MiningArea): BukkitRunnable() {

    private var initiator: CommandSender? = null

    fun setInitiator(initiator: CommandSender) {
        this.initiator = initiator
    }

    override fun run() {
        val start = System.currentTimeMillis()
        val from = miningArea.from
        val to = miningArea.to
        for (x in Util.getIntRange(from.blockX, to.blockX)) {
            for (y in Util.getIntRange(from.blockY, to.blockY)) {
                for (z in Util.getIntRange(from.blockZ, to.blockZ)) {
                    val pos = Location(miningArea.world, x.toDouble(), y.toDouble(), z.toDouble())
                    pos.block.type = Util.getRandomMaterialFromHashMap(miningArea.chances)
                }
            }
        }
        val end = System.currentTimeMillis()
        if (initiator != null) {
            initiator!!.sendMessage(Message.MINING_CUBE_GENERATE_FINISH.getMessage()
                .replace("%name", miningArea.key)
                .replace("%time", "${end - start}ms"))
        }
    }

}