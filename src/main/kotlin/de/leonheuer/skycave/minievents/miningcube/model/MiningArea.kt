package de.leonheuer.skycave.minievents.miningcube.model

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.HashMap

data class MiningArea(
    val uuid: UUID,
    val key: String,
    var world: World,
    var from: Vector,
    var to: Vector,
    var chances: EnumMap<Material, Int>,
    var spawn: Location?
    )