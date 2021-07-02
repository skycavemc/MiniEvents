package de.leonheuer.skycave.minievents.lavaevent.manager

import de.leonheuer.skycave.minievents.lavaevent.enums.LavaEventState
import de.leonheuer.skycave.minievents.lavaevent.enums.PlayerState
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class LavaEventManager {

    private val participantHashMap = HashMap<UUID, PlayerState>()
    var eventState = LavaEventState.NOT_RUNNING

    fun getState(uuid: UUID): PlayerState? {
        return participantHashMap[uuid]
    }

    fun getState(player: Player): PlayerState? {
        return participantHashMap[player.uniqueId]
    }

    fun setState(uuid: UUID, state: PlayerState) {
        participantHashMap[uuid] = state
    }

    fun setState(player: Player, state: PlayerState) {
        participantHashMap[player.uniqueId] = state
    }

    fun getPlayerList(state: PlayerState): List<OfflinePlayer> {
        return participantHashMap.keys.filter { participantHashMap[it] == state }.map { Bukkit.getOfflinePlayer(it) }.toList()
    }

    fun getPlayerList(): List<OfflinePlayer> {
        return participantHashMap.keys.map { Bukkit.getOfflinePlayer(it) }.toList()
    }

}