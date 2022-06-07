package de.leonheuer.skycave.minievents.codecs

import de.leonheuer.skycave.minievents.lavaevent.model.LavaEventArea
import de.leonheuer.skycave.minievents.model.Codec
import org.bukkit.Material
import org.json.simple.JSONObject

class LavaEventAreaCodec(
    private val locationCodec: LocationCodec
): Codec<LavaEventArea?> {

    override fun encode(obj: LavaEventArea?): JSONObject? {
        if (obj == null) {
            return null
        }

        val lavaEventArea = JSONObject()
        lavaEventArea["spawn"] = locationCodec.encode(obj.spawn)
        lavaEventArea["spectate"] = locationCodec.encode(obj.spectate)
        lavaEventArea["radius"] = obj.radius
        lavaEventArea["period"] = obj.period
        lavaEventArea["material"] = obj.material.name
        return lavaEventArea
    }

    override fun decode(obj: Any?): LavaEventArea? {
        if (obj == null) {
            return null
        }
        val json = obj as JSONObject

        val spawn = locationCodec.decode(json["spawn"])
        val spectate = locationCodec.decode(json["spectate"])
        val radius = json["radius"] as Long
        val period = json["period"] as Long
        val material = Material.valueOf(json["material"] as String)

        return LavaEventArea(
            spawn, spectate, radius.toInt(), period.toInt(), material
        )
    }

    override fun getEncoderClass(): Class<out LavaEventArea?> {
        return LavaEventArea::class.java
    }

}