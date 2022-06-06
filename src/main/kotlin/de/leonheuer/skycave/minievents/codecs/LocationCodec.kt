package de.leonheuer.skycave.minievents.codecs

import de.leonheuer.skycave.minievents.model.Codec
import org.bukkit.Bukkit
import org.bukkit.Location
import org.json.simple.JSONObject
import java.util.*

class LocationCodec: Codec<Location?> {

    override fun encode(obj: Location?): JSONObject? {
        if (obj == null) {
            return null
        }

        val location = JSONObject()
        location["world"] = obj.world.uid.toString()
        location["x"] = obj.x
        location["y"] = obj.y
        location["z"] = obj.z
        location["yaw"] = obj.yaw
        location["pitch"] = obj.pitch
        return location
    }

    override fun decode(json: JSONObject?): Location? {
        if (json == null) {
            return null
        }
        val world = Bukkit.getWorld(
            UUID.fromString(json["world"] as String)
        )!!
        val x = json["x"] as Double
        val y = json["y"] as Double
        val z = json["z"] as Double
        val yaw = json["yaw"] as Double
        val pitch = json["pitch"] as Double

        return Location(world, x, y, z, yaw.toFloat(), pitch.toFloat())
    }

    override fun getEncoderClass(): Class<Location> {
        return Location::class.java
    }

}