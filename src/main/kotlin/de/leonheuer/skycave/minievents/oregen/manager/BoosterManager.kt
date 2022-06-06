package de.leonheuer.skycave.minievents.oregen.manager

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.enums.Message
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.scheduler.BukkitTask

class BoosterManager(private val main: MiniEvents) {

    val bossBar = Bukkit.getServer().createBossBar("", BarColor.PURPLE, BarStyle.SOLID)
    private var resetTask: BukkitTask? = null
    var isRunning: Boolean = false
        private set
    var multiplier: Int = 0
        private set
    var seconds: Int = 0
        private set
    var finished: Long = 0L
        private set

    fun start(seconds: Int, multiplier: Int) {
        this.multiplier = multiplier
        this.seconds = seconds
        finished = System.currentTimeMillis() + seconds * 1000L
        isRunning = true

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer)
        bossBar.isVisible = true
        bossBar.setTitle(Message.BOOSTER_BOSS_BAR.getFormatted().replace("%durance", getDurance()))
        bossBar.progress = 1.0
        resetTask = Bukkit.getScheduler().runTaskLater(main, this::reset, seconds * 20L)
    }

    fun reset() {
        resetTask!!.cancel()

        multiplier = 1
        isRunning = false
        seconds = 0
        finished = 0

        bossBar.removeAll()
        bossBar.isVisible = false
    }

    fun getRemaining(): Int {
        return (finished - System.currentTimeMillis()).toInt() / 1000
    }

    fun getDurance(): String {
        val durance: String
        val min = seconds / 60
        val sec = seconds % 60
        durance = if (sec == 0) {
            "$min Minuten"
        } else if (min == 0) {
            "$sec Sekunden"
        } else {
            "$min Minuten $sec Sekunden"
        }
        return durance
    }
}