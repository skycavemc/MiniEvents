package de.leonheuer.skycave.minievents.lavaevent.model

import de.leonheuer.skycave.minievents.lavaevent.enums.EventMaterial
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

data class LavaEventArea(
    var spawn: Location?,
    var spectate: Location?,
    var radius: Int,
    val materials: EnumMap<EventMaterial, Material>
    )