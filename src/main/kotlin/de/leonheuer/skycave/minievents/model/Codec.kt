package de.leonheuer.skycave.minievents.model

import org.json.simple.JSONObject

interface Codec<T> {

    fun encode(obj: T): JSONObject?
    fun decode(obj: Any?): T
    fun getEncoderClass(): Class<out T>

}