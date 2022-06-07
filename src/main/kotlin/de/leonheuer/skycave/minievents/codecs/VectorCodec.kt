package de.leonheuer.skycave.minievents.codecs

import de.leonheuer.skycave.minievents.model.Codec
import org.bukkit.util.Vector
import org.json.simple.JSONObject

class VectorCodec: Codec<Vector?> {

    override fun encode(obj: Vector?): JSONObject? {
        if (obj == null) {
            return null
        }

        val vector = JSONObject()
        vector["x"] = obj.x.toLong()
        vector["y"] = obj.y.toLong()
        vector["z"] = obj.z.toLong()
        return vector
    }

    override fun decode(obj: Any?): Vector? {
        if (obj == null) {
            return null
        }
        val json = obj as JSONObject

        val x = json["x"] as Long
        val y = json["y"] as Long
        val z = json["z"] as Long

        return Vector(x.toDouble(), y.toDouble(), z.toDouble())
    }

    override fun getEncoderClass(): Class<Vector> {
        return Vector::class.java
    }

}