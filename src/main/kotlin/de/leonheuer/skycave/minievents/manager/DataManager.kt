package de.leonheuer.skycave.minievents.manager

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.lavaevent.model.LavaEventArea
import de.leonheuer.skycave.minievents.miningcube.model.MiningArea
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.util.Vector
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.HashMap

class DataManager(private val main: MiniEvents) {

    var miningArea: MiningArea? = null
    var lavaEventArea: LavaEventArea? = null
    private val path = main.dataFolder.path

    init {
        readFromFile()
    }

    private fun readFromFile() {
        val dir = File(path)
        val miningCube = File(path, "miningCube.json")
        if (!dir.exists() || !miningCube.exists()) {
            return
        }

        try {
            val reader = FileReader(miningCube)
            val parser = JSONParser()
            val obj = parser.parse(reader) as JSONObject

            val fromObject = obj["from"] as JSONObject
            val from = Vector(
                (fromObject["x"] as Long).toDouble(),
                (fromObject["y"] as Long).toDouble(),
                (fromObject["z"] as Long).toDouble()
            )

            val toObject = obj["to"] as JSONObject
            val to = Vector(
                (toObject["x"] as Long).toDouble(),
                (toObject["y"] as Long).toDouble(),
                (toObject["z"] as Long).toDouble()
            )

            val chancesArray = obj["chances"] as JSONArray
            val chances = HashMap<Material, Int>()
            chancesArray.forEach {
                val chanceObj = it as JSONObject
                val mat = Material.valueOf(chanceObj["material"] as String)
                val chance = (chanceObj["chance"] as Long).toInt()
                chances[mat] = chance
            }

            val spawnRawObject = obj["spawn"]
            var spawn: Location? = null
            if (spawnRawObject != null) {
                val spawnObject = spawnRawObject as JSONObject
                spawn = Location(
                    Bukkit.getWorld(UUID.fromString(spawnObject["world"] as String))!!,
                    spawnObject["x"] as Double,
                    spawnObject["y"] as Double,
                    spawnObject["z"] as Double,
                    (spawnObject["yaw"] as Double).toFloat(),
                    (spawnObject["pitch"] as Double).toFloat()
                )
            }

            miningArea = MiningArea(
                Bukkit.getWorld(UUID.fromString(obj["world"] as String))!!,
                from, to, chances, spawn
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    fun writeToFile() {
        val miningArea = this.miningArea ?: return

        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val miningCube = File(path, "miningCube.json")
        if (!miningCube.exists()) {
            try {
                miningCube.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }

        try {
            val writer = FileWriter(miningCube)
            val obj = JSONObject()

            val from = JSONObject()
            from["x"] = miningArea.from.blockX
            from["y"] = miningArea.from.blockY
            from["z"] = miningArea.from.blockZ

            val to = JSONObject()
            to["x"] = miningArea.to.blockX
            to["y"] = miningArea.to.blockY
            to["z"] = miningArea.to.blockZ

            val chances = JSONArray()
            if (miningArea.chances.isNotEmpty()) {
                miningArea.chances.keys.forEach {
                    val chanceObj = JSONObject()
                    chanceObj["material"] = it.toString()
                    chanceObj["chance"] = miningArea.chances[it]
                    chances.add(chanceObj)
                }
            }

            var spawn: JSONObject? = null
            val miningSpawn = miningArea.spawn
            if (miningSpawn != null) {
                spawn = JSONObject()
                spawn["world"] = miningSpawn.world.uid.toString()
                spawn["x"] = miningSpawn.x
                spawn["y"] = miningSpawn.y
                spawn["z"] = miningSpawn.z
                spawn["yaw"] = miningSpawn.yaw
                spawn["pitch"] = miningSpawn.pitch
            }

            obj["world"] = miningArea.world.uid.toString()
            obj["from"] = from
            obj["to"] = to
            obj["chances"] = chances
            obj["spawn"] = spawn

            writer.write(obj.toJSONString())
            writer.flush()
            main.logger.info("MiningCube area saved to file.")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}