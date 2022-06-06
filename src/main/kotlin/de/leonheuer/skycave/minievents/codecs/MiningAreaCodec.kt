package de.leonheuer.skycave.minievents.codecs

import de.leonheuer.skycave.minievents.miningcube.model.MiningArea
import de.leonheuer.skycave.minievents.model.Codec
import org.bukkit.Bukkit
import org.bukkit.Material
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.util.*

class MiningAreaCodec(
    private val locationCodec: LocationCodec,
    private val vectorCodec: VectorCodec
) : Codec<MiningArea?> {

    override fun encode(obj: MiningArea?): JSONObject? {
        if (obj == null) {
            return null
        }

        val miningArea = JSONObject()
        miningArea["uuid"] = obj.uuid.toString()
        miningArea["key"] = obj.key
        miningArea["world"] = obj.world.uid.toString()
        miningArea["from"] = vectorCodec.encode(obj.from)
        miningArea["to"] = vectorCodec.encode(obj.to)
        miningArea["spawn"] = locationCodec.encode(obj.spawn)

        val chances = JSONArray()
        if (obj.chances.isNotEmpty()) {
            obj.chances.keys.forEach {
                val chanceObj = JSONObject()
                chanceObj["material"] = it.toString()
                chanceObj["chance"] = obj.chances[it]
                chances.add(chanceObj)
            }
        }

        return miningArea
    }

    override fun decode(json: JSONObject?): MiningArea? {
        if (json == null) {
            return null
        }

        val uuid = UUID.fromString(json["uuid"] as String)
        val key = json["key"] as String
        val world = Bukkit.getWorld(
            UUID.fromString(json["world"] as String)
        )!!
        val from = vectorCodec.decode(json["from"] as JSONObject)
        val to = vectorCodec.decode(json["to"] as JSONObject)
        val spawn = locationCodec.decode(json["spawn"] as JSONObject)

        val chancesArray = json["chances"] as JSONArray
        val chances = EnumMap<Material, Int>(Material::class.java)
        chancesArray.forEach {
            val chanceObj = it as JSONObject
            val mat = Material.valueOf(chanceObj["material"] as String)
            val chance = (chanceObj["chance"] as Long).toInt()
            chances[mat] = chance
        }

        if (from == null || to == null) {
            return null
        }

        return MiningArea(
            uuid, key, world, from, to, chances, spawn
        )
    }

    override fun getEncoderClass(): Class<MiningArea> {
        return MiningArea::class.java
    }

}