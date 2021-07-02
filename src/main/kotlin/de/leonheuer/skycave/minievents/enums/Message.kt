package de.leonheuer.skycave.minievents.enums

import org.bukkit.ChatColor

enum class Message(private val message: String) {

    PREFIX("&e&l| &6Event &8» "),

    // mining cube general
    MINING_CUBE_NO_AREA("&cBitte leg zuerst einen Mining-Bereich fest. Siehe /miningcube help"),
    MINING_CUBE_UNKNOWN_COMMAND("&cUngültiger Befehl. Siehe /miningcube help"),

    // mining cube help
    MINING_CUBE_HELP_GENERATE("&6/miningcube generate\n&8» &7Generiert den Mining-Würfel neu"),
    MINING_CUBE_HELP_POS1("&6/miningcube pos1\n&8» &7Setzt die erste Position des Mining-Würfels"),
    MINING_CUBE_HELP_POS2("&6/miningcube pos2\n&8» &7Setzt die zweite Position des Mining-Würfels"),
    MINING_CUBE_HELP_SPAWN("&6/miningcube spawn\n&8» &7Teleportiert dich zum MiningCube Spawn"),
    MINING_CUBE_HELP_SET_SPAWN("&6/miningcube setspawn\n&8» &7Setzt den Spawn für MiningCube"),
    MINING_CUBE_HELP_SET_CHANCE("&6/miningcube setchance <material> <0-1000>\n&8» &7Setzt die Chance für ein Material"),
    MINING_CUBE_HELP_CHANCES("&6/miningcube setchance <material> <0-1000>\n&8» &7Setzt die Chance für ein Material"),
    MINING_CUBE_HELP_TP("&6/miningcube tp <pos1/pos2>\n&8» &7Teleportiert dich zur angegebenen Position"),

    // mining cube generate command
    MINING_CUBE_GENERATE_PROCESS("&aGeneriere Mining-Würfel..."),

    // mining cube pos1 command
    MINING_CUBE_POS1("&aPosition 1 erfolgreich geändert."),
    MINING_CUBE_POS1_WORLD_MISMATCH("&aPosition 1 erfolgreich geändert. &cWarnung: Position 2 liegt in einer anderen Welt!"),

    // mining cube pos2 command
    MINING_CUBE_POS2("&aPosition 2 erfolgreich geändert."),
    MINING_CUBE_POS2_WORLD_MISMATCH("&aPosition 2 erfolgreich geändert. &cWarnung: Position 1 liegt in einer anderen Welt!"),

    // mining cube set spawn command
    MINING_CUBE_SET_SPAWN_SUCCESS("&aDu hast erfolgreich den MiningCube Spawn gesetzt."),

    // mining cube spawn command
    MINING_CUBE_SPAWN_SUCCESS("&aDu hast dich zum MiningCube Spawn teleportiert."),
    MINING_CUBE_SPAWN_UNSET("&cDer Spawn wurde noch nicht festgelegt."),

    // mining cube set chance command
    MINING_CUBE_SET_CHANCE_SYNTAX("&c/miningcube setchance <Material> <0-1000>"),
    MINING_CUBE_SET_CHANCE_INVALID_MATERIAL("&c%mat ist kein gültiges Material. Tipp: Nutze die Tab-Vervollständigung!"),
    MINING_CUBE_SET_CHANCE_INVALID_NUMBER("&c%number ist keine gültige Zahl."),
    MINING_CUBE_SET_CHANCE_NUMBER_OUT_OF_RANGE("&cBitte gib eine Zahl zwischen 0 und 1000 an."),
    MINING_CUBE_SET_CHANCE_SUCCESS("&7Chance für &a%mat &7auf &a%chance &7gesetzt."),
    MINING_CUBE_SET_CHANCE_WARNING("&eAchtung: Die Chancen ergeben zusammen über 1000. (%sum)"),

    // mining cube chances command
    MINING_CUBE_CHANCES("&7Chancen für MiningCube: &7%entries"),
    MINING_CUBE_CHANCES_ENTRY("&e%mat&8(&f%chance&8)"),
    MINING_CUBE_CHANCES_UNSET("&cBisher wurden noch keine Wahrscheinlichkeiten festgelegt."),

    // mining cube tp command
    MINING_CUBE_TP_SYNTAX("&c/miningcube tp <pos1/pos2>"),
    MINING_CUBE_TP_SUCCESS("&aTeleportiere zur Position %pos..."),
    MINING_CUBE_TP_POSITIONS("&cBitte gib pos1 oder pos2 ein."),

    // lava event messages
    LAVA_EVENT_OUT("&c%player ist ausgeschieden."),
    LAVA_EVENT_OUT_SELF("&cDu bist ausgeschieden."),
    ;

    fun getFormatted(): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    fun getMessage(): String {
        return ChatColor.translateAlternateColorCodes('&', PREFIX.message + message)
    }

}