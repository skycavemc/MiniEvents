package de.leonheuer.skycave.minievents.lavaevent.model

import de.leonheuer.skycave.minievents.lavaevent.enums.LavaEventState
import de.leonheuer.skycave.minievents.lavaevent.enums.PlayerState
import java.util.*

class LavaEvent {

    val participants = HashMap<UUID, PlayerState>()
    var state = LavaEventState.OPEN

}