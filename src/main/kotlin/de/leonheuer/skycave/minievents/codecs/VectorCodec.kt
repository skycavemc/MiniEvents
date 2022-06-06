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
        vector["x"] = obj.x
        vector["y"] = obj.y
        vector["z"] = obj.z
        return vector
    }

    override fun decode(json: JSONObject?): Vector? {
        if (json == null) {
            return null
        }

        val x = json["x"] as Double
        val y = json["y"] as Double
        val z = json["z"] as Double

        return Vector(x, y, z)
    }

    override fun getEncoderClass(): Class<Vector> {
        return Vector::class.java
    }

}