package de.leonheuer.skycave.minievents.oregen.enums

import org.bukkit.Material

enum class OreResult(val title: String, val chance: Int, val material: Material, val canBeBoosted: Boolean) {

    STONE("Stein", 200, Material.STONE, false),
    COAL("Kohle", 100, Material.COAL_ORE, true),
    IRON("Eisen", 15, Material.IRON_ORE, true),
    GOLD("Gold", 10, Material.GOLD_ORE, true),
    LAPIS("Lapis", 7, Material.LAPIS_ORE, true),
    DIAMOND("Diamant", 2, Material.DIAMOND_ORE, true),
    EMERALD("Smaragd", 1, Material.EMERALD_ORE, true),
    ;

}