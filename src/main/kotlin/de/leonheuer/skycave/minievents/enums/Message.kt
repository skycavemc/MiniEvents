package de.leonheuer.skycave.minievents.enums

import org.bukkit.ChatColor

enum class Message(private val message: String) {

    // global
    PREFIX("&d&l| &5Event &8» "),
    NO_PERMS("&cDu hast keine Rechte für diesen Befehl."),
    INVALID_NUMBER("&c%number ist keine gültige Zahl."),
    NO_PLAYER("&cDieser Befehl ist nur für Spieler."),


    // mining cube general
    MINING_CUBE_UNKNOWN("&cDieser MiningCube ist unbekannt. Erstelle einen mit /miningcube create"),
    MINING_CUBE_UNKNOWN_COMMAND("&cUngültiger Befehl. Siehe /miningcube help"),
    MINING_CUBE_AREA_NOT_GIVEN("&cBitte gib einen MiningCube an. Siehe /miningcube list"),

    // mining cube help
    MINING_CUBE_HELP_GENERATE("&6/miningcube generate <name>\n&8» &7Generiert den MiningCube neu"),
    MINING_CUBE_HELP_CREATE("&6/miningcube create <name>\n&8» &7Erstellt einen MiningCube bei deiner Position"),
    MINING_CUBE_HELP_DELETE("&6/miningcube delete <name>\n&8» &7Entfernt den MiningCube"),
    MINING_CUBE_HELP_POS1("&6/miningcube pos1 <name>\n&8» &7Setzt die erste Position des MiningCube"),
    MINING_CUBE_HELP_POS2("&6/miningcube pos2 <name>\n&8» &7Setzt die zweite Position des MiningCube"),
    MINING_CUBE_HELP_SPAWN("&6/miningcube spawn <name>\n&8» &7Teleportiert dich zum MiningCube Spawn"),
    MINING_CUBE_HELP_SET_SPAWN("&6/miningcube setspawn <name>\n&8» &7Setzt den Spawn für den MiningCube"),
    MINING_CUBE_HELP_SET_CHANCE("&6/miningcube setchance <name> <material> <0-1000>\n&8» &7Setzt die Chance für ein Material"),
    MINING_CUBE_HELP_CHANCES("&6/miningcube chances <name>\n&8» &7Zeigt die Chancen eines MiningCubes an"),
    MINING_CUBE_HELP_TP("&6/miningcube tp <name> <pos1/pos2>\n&8» &7Teleportiert dich zur angegebenen Position"),
    MINING_CUBE_HELP_LIST("&6/miningcube list\n&8» &7Zeigt die Liste der MiningCubes"),
    MINING_CUBE_HELP_HELP("&6/miningcube help\n&8» &7Zeigt Hilfe an"),

    // mining cube generate command
    MINING_CUBE_GENERATE_BEGIN("&7Generation des Bereiches %name gestartet."),
    MINING_CUBE_GENERATE_FINISH("&aGeneration des Bereiches %name abgeschlossen, benötigte Zeit: %time"),

    // mining cube create command
    MINING_CUBE_CREATE_SYNTAX("&c/miningcube create <Name>"),
    MINING_CUBE_CREATE_EXISTS("&cEin MiningCube mit dem Name %name existiert bereits."),
    MINING_CUBE_CREATE_SUCCESS("&7MiningCube &a%name &7erfolgreich bei &adeiner Position &7erstellt."),
    MINING_CUBE_CREATE_SPAWN("&eVergiss nicht das Setzen von Position 1, Position 2 und einem Spawn!"),

    // mining cube delete command
    MINING_CUBE_DELETE_SUCCESS("&cMiningCube %name erfolgreich entfernt."),

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
    MINING_CUBE_SET_CHANCE_SYNTAX("&c/miningcube setchance <Name> <Material> <0-1000>"),
    MINING_CUBE_SET_CHANCE_INVALID_MATERIAL("&c%mat ist kein gültiges Material. Tipp: Nutze die Tab-Vervollständigung!"),
    MINING_CUBE_SET_CHANCE_NUMBER_OUT_OF_RANGE("&cBitte gib eine Zahl zwischen 0 und 1000 an."),
    MINING_CUBE_SET_CHANCE_SUCCESS("&7Chance für &a%mat &7auf &a%chance &7gesetzt."),
    MINING_CUBE_SET_CHANCE_WARNING("&eAchtung: Die Chancen ergeben zusammen über 1000. (%sum)"),

    // mining cube chances command
    MINING_CUBE_CHANCES("&7Chancen für MiningCube: &7%entries"),
    MINING_CUBE_CHANCES_ENTRY("&e%mat&8(&f%chance&8)"),
    MINING_CUBE_CHANCES_UNSET("&cBisher wurden noch keine Wahrscheinlichkeiten festgelegt."),

    // mining cube tp command
    MINING_CUBE_TP_SYNTAX("&c/miningcube tp <Name> <pos1/pos2>"),
    MINING_CUBE_TP_SUCCESS("&aTeleportiere zur Position %pos..."),
    MINING_CUBE_TP_POSITIONS("&cBitte gib pos1 oder pos2 ein."),

    // mining cube list command
    MINING_CUBE_LIST_NONE("&cEs existieren keine MiningCubes."),
    MINING_CUBE_LIST("&aListe der MiningCubes: &7%list"),


    // erze command
    ORES_HEADER("&aWahrscheinlichkeiten für den Erze Generator:"),
    ORES_CHILD("&8» &7%chance\u0025&8: &e%name"),
    ORES_HEADER_BOOSTED("&aWahrscheinlichkeiten für den Erze Generator:\n&d&lBoost aktiv! &eMultiplikator: %multiplierx, Dauer: %durance"),
    ORES_CHILD_BOOSTED("&8» &d%chance\u0025&8: &e%name"),

    // booster command
    BOOSTER_UNKNOWN("&cDieser Befehl existiert nicht. /booster help"),
    BOOSTER_START("&e&lDer &3&lErzeBoost &e&lwurde gestartet! &c&lDauer: %durance, &d&lMultiplikator: %multiplierx"),
    BOOSTER_START_LONGER("&e&lDer &3&lErzeBoost &e&lwurde auf &c&l%durance &e&lverlängert mit dem Multiplikator &d&l%multiplierx&e&l!"),
    BOOSTER_COUNTDOWN("&8» &eDer &3ErzeBoost &ebeginnt in &c%sec Sekunden!"),

    // booster start subcommand
    BOOSTER_START_SYNTAX("&e/booster start <sekunden> <multiplikator>"),
    BOOSTER_START_INVALID("&cBitte gib gültige Zahlen an."),
    BOOSTER_START_SUCCESS("&aDer Booster wurde erfolgreich gestartet."),

    // booster stop subcommand
    BOOSTER_STOP_SUCCESS("&cDer Booster wurde angehalten."),
    BOOSTER_STOP_ALREADY("&cDer Booster ist momentan nicht aktiv."),

    // booster status subcommand
    BOOSTER_STATUS("&aStatus: &7%state, &eDauer: &7%durance, &dMultiplikator: &7%multiplier"),

    // booster help
    BOOSTER_HELP_START("&b/booster start <sekunden> <multiplikator>\n&8» &7Startet den Booster"),
    BOOSTER_HELP_STOP("&b/booster stop\n&8» &7Stoppt den Booster"),
    BOOSTER_HELP_STATUS("&b/booster status\n&8» &7Zeigt den Status an"),
    BOOSTER_HELP_HELP("&b/booster help\n&8» &7Zeigt Hilfe an"),

    // booster boss bar
    BOOSTER_BOSS_BAR("&3&lErzeBoost &e&laktiv! &c&lDauer: &c%durance"),


    // lava event general
    LAVA_EVENT_OUT("&c%player ist ausgeschieden."),
    LAVA_EVENT_OUT_SELF("&cDu bist ausgeschieden und betrittst nun den Zuschauermodus. /lavaevent leave zum Verlassen."),
    LAVA_EVENT_JOIN("&a%player &7ist dem LavaEvent beigetreten. &8(&3/lavaevent join&8)"),
    LAVA_EVENT_JOIN_SELF("&7Du bist dem LavaEvent beigetreten."),
    LAVA_EVENT_JOIN_ALREADY("&cDu bist dem LavaEvent bereits beigetreten."),
    LAVA_EVENT_NOT_RUNNING("&cDerzeit läuft kein Lava Event."),
    LAVA_EVENT_RUNNING("&cDas Lava Event hat schon begonnen."),
    LAVA_EVENT_NOT_IN("&cDu nimmst nicht am LavaEvent teil."),
    LAVA_EVENT_LEAVE("&cDu hast das LavaEvent verlassen."),
    LAVA_EVENT_END("&7Das LavaEvent ist vorbei!"),
    LAVA_EVENT_WINNERS("&7Es haben gewonnen: &a%winners"),

    // lava event help
    LAVA_EVENT_HELP_JOIN("&e/lavaevent join\n&8» &7Tritt dem laufenden LavaEvent bei"),
    LAVA_EVENT_HELP_LEAVE("&e/lavaevent leave\n&8» &7Verlässt die Wartephase bzw. den Zuschauermodus"),
    LAVA_EVENT_HELP_START("&e/lavaevent start\n&8» &7Startet das LavaEvent"),
    LAVA_EVENT_HELP_STOP("&e/lavaevent stop\n&8» &7Stoppt ein laufendes LavaEvent"),
    LAVA_EVENT_HELP_SET_SPAWN("&e/lavaevent setspawn\n&8» &7Legt den Beitrittsort fest"),
    LAVA_EVENT_HELP_SET_SPECTATE("&e/lavaevent setspectate\n&8» &7Legt den Zuschauerort fest"),
    LAVA_EVENT_HELP_SET_RADIUS("&e/lavaevent join\n&8» &7Setzt den Radius der Spielfläche"),
    LAVA_EVENT_HELP_SET_MATERIAL("&e/lavaevent setmaterial <Material>\n" +
            "&8» &7Setzt das Material für den angegebenen Platzhalter"),
    LAVA_EVENT_HELP_SET_PERIOD("&e/lavaevent setperiod <Dauer>\n" +
            "&8» &7Setzt den Zeitabstand, in dem Blöcke verschwinden"),
    LAVA_EVENT_HELP_INFO("&e/lavaevent info\n&8» &7Liefert Informationen über das LavaEvent"),

    // lava event set spawn command
    LAVA_EVENT_SET_SPAWN_SUCCESS("&aDu hast erfolgreich den Beitrittsort gesetzt."),

    // lava event set spectate command
    LAVA_EVENT_SET_SPECTATE_SUCCESS("&aDu hast erfolgreich den Zuschauerort gesetzt."),

    // lava event set radius command
    LAVA_EVENT_SET_RADIUS_SUCCESS("&aDer Radius wurde auf %radius gesetzt."),

    // lava event set material command
    LAVA_EVENT_SET_MATERIAL_SUCCESS("&aDu hast erfolgreich das Material zu %material gesetzt."),

    // lava event set period command
    LAVA_EVENT_SET_PERIOD_SUCCESS("&aDu hast erfolgreich den Zeitabstand auf %period gesetzt."),

    // lava event start command
    LAVA_EVENT_START("&aDas LavaEvent wurde gestartet! &7Trete bei mit &3/lavaevent join"),
    LAVA_EVENT_START_ALREADY("&cDas LavaEvent läuft bereits."),

    // lava event stop command
    LAVA_EVENT_STOP("&cDas LavaEvent wurde abgebrochen!"),
    ;

    fun getFormatted(): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    fun getMessage(): String {
        return ChatColor.translateAlternateColorCodes('&', PREFIX.message + message)
    }

}