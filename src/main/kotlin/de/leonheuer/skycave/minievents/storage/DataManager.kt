package de.leonheuer.skycave.minievents.storage

import de.leonheuer.skycave.minievents.MiniEvents
import de.leonheuer.skycave.minievents.codecs.LavaEventAreaCodec
import de.leonheuer.skycave.minievents.codecs.LocationCodec
import de.leonheuer.skycave.minievents.codecs.MiningAreaCodec
import de.leonheuer.skycave.minievents.codecs.VectorCodec
import de.leonheuer.skycave.minievents.lavaevent.model.LavaEventArea
import de.leonheuer.skycave.minievents.miningcube.model.MiningArea
import org.bukkit.Location
import org.bukkit.Material
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class DataManager(private val main: MiniEvents) {

    private val miningAreaList = ArrayList<MiningArea>()
    private val path = main.dataFolder.path
    private val miningAreaCodec = MiningAreaCodec(LocationCodec(), VectorCodec())
    private val lavaEventAreaCodec = LavaEventAreaCodec(LocationCodec())
    lateinit var lavaEventArea: LavaEventArea
        private set

    init {
        loadLavaEventArea()
        loadMiningAreas()
    }

    private fun loadLavaEventArea() {
        val dir = File(path)
        if (!dir.isDirectory) dir.mkdirs()

        val file = File("$path/lavaEventArea.json")
        try {
            if (!file.isFile) file.createNewFile()
            lavaEventArea = LavaEventArea(null, null, 0, 0, Material.OBSIDIAN)
            saveLavaEventArea()
            return
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val parser = JSONParser()
        try {
            val obj = parser.parse(FileReader(file)) as JSONObject
            val area = lavaEventAreaCodec.decode(obj) ?: return
            lavaEventArea = area
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    fun saveLavaEventArea(): Boolean {
        val dir = File(path)
        if (!dir.isDirectory) dir.mkdirs()

        val file = File("$path/lavaEventArea.json")
        try {
            if (!file.isFile) file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val writer = FileWriter(file)
            val json = lavaEventAreaCodec.encode(lavaEventArea) ?: return false
            writer.write(json.toString())
            writer.flush()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    private fun loadMiningAreas() {
        val dir = File("$path/miningAreas")
        if (!dir.isDirectory) {
            return
        }
        val files = dir.listFiles() ?: return

        val parser = JSONParser()
        for (file in files) {
            try {
                val obj = parser.parse(FileReader(file)) as JSONObject
                val area = miningAreaCodec.decode(obj) ?: continue
                miningAreaList.add(area)
            } catch (e: ParseException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }

    fun saveMiningAreas() {
        if (miningAreaList.isEmpty()) {
            return
        }

        for (entry in miningAreaList) {
            saveMiningArea(entry)
        }
    }

    private fun saveMiningArea(miningArea: MiningArea): Boolean {
        val dir = File("$path/miningAreas")
        if (!dir.isDirectory) dir.mkdirs()

        val file = File("$path/miningAreas", "${miningArea.uuid}.json")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        try {
            val writer = FileWriter(file)
            val json = miningAreaCodec.encode(miningArea) ?: return false
            writer.write(json.toString())
            writer.flush()
            main.logger.info("MiningCube area saved to file.")
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    fun getMiningArea(key: String): MiningArea? {
        return miningAreaList.firstOrNull { it.key == key }
    }

    fun getMiningAreas(): List<MiningArea> {
        return miningAreaList
    }

    fun createMiningArea(key: String, firstLocation: Location) {
        val miningArea = MiningArea(UUID.randomUUID(), key, firstLocation.world,
            firstLocation.toVector(), firstLocation.toVector(),
            EnumMap(Material::class.java), null
        )
        miningAreaList.add(miningArea)
        saveMiningArea(miningArea)
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

}