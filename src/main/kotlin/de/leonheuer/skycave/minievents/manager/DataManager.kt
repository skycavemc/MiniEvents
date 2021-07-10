package de.leonheuer.skycave.minievents.manager

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.lavaevent.enums.EventMaterial
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
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.HashMap

class DataManager(private val main: MiniEvents) {

    private val miningAreaList = ArrayList<MiningArea>()
    var lavaEventArea = LavaEventArea(null, null, 0, EnumMap(EventMaterial::class.java))
    private val path = main.dataFolder.path

    init {
        readFromFile()
    }

    private fun readFromFile() {
        val dir = File("$path/miningAreas")
        if (!dir.exists() || dir.listFiles() == null || dir.listFiles()!!.isEmpty()) {
            return
        }

        val parser = JSONParser()

        for (file in dir.listFiles()!!) {
            try {
                val reader = FileReader(file)
                val obj = parser.parse(reader) as JSONObject

                val uuid = UUID.fromString(obj["uuid"] as String)
                val key = obj["key"] as String

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

                miningAreaList.add(MiningArea(uuid, key,
                    Bukkit.getWorld(UUID.fromString(obj["world"] as String))!!,
                    from, to, chances, spawn
                ))
            } catch (e: ParseException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }

    fun writeToFile() {
        if (miningAreaList.isEmpty()) {
            return
        }

        for (entry in miningAreaList) {
            writeToFile(entry)
        }
    }

    private fun writeToFile(miningArea: MiningArea) {
        val dir = File("$path/miningAreas")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File("$path/miningAreas", "${miningArea.uuid}.json")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }

        try {
            val writer = FileWriter(file)
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

            obj["uuid"] = miningArea.uuid.toString()
            obj["key"] = miningArea.key
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

    fun getMiningArea(uuid: UUID): MiningArea? {
        return miningAreaList.firstOrNull { it.uuid == uuid }
    }

    fun getMiningArea(key: String): MiningArea? {
        return miningAreaList.firstOrNull { it.key == key }
    }

    fun isKeyExisting(key: String): Boolean {
        return miningAreaList.firstOrNull { it.key == key } != null
    }

    fun createMiningArea(key: String, firstLocation: Location) {
        val miningArea = MiningArea(UUID.randomUUID(), key, firstLocation.world,
            firstLocation.toVector(), firstLocation.toVector(),
            HashMap(), null
        )
        miningAreaList.add(miningArea)
        writeToFile(miningArea)
    }

    fun deleteMiningArea(miningArea: MiningArea) {
        val dir = File("$path/miningAreas/deleted")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        miningAreaList.remove(miningArea)

        try {
            Files.move(
                Path.of("$path/miningAreas/${miningArea.uuid}.json"),
                Path.of("$path/miningAreas/deleted/${miningArea.uuid}.json"),
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }

    fun getMiningAreas(): List<MiningArea> {
        return miningAreaList
    }

}