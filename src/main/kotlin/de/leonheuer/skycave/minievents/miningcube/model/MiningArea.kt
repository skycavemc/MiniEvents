package de.leonheuer.skycave.minievents.miningcube.model

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector

data class MiningArea(var world: World, var from: Vector, var to: Vector, var chances: HashMap<Material, Int>, var spawn: Location?)