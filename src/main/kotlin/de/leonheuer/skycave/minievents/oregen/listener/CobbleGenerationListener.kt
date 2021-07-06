package de.leonheuer.skycave.minievents.oregen.listener

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.util.Util
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFromToEvent

class CobbleGenerationListener(private val main: MiniEvents): Listener {

    @EventHandler
    fun onBlockFromTo(event: BlockFromToEvent) {
        val source = event.block
        val liquid = source.type
        val block = event.toBlock
        if (source !== block) {
            if (block.type != liquid) {
                if (Util.canGenerate(block, source, event.face)) {
                    event.isCancelled = true
                    Util.playCobbleGenerationEffect(block)
                    if (main.boosterManager.isRunning) {
                        block.type = Util.generateOreBlock(main.boosterManager.multiplier)
                    } else {
                        block.type = Util.generateOreBlock()
                    }
                }
            }
        }
    }

}