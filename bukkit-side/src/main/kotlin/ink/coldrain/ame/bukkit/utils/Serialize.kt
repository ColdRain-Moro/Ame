package ink.coldrain.ame.bukkit.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * ink.coldrain.ame.bukkit.utils.Serialize
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/12 17:32
 **/
val gson: Gson = GsonBuilder().create()

fun Any.toBase64(): String {
    ByteArrayOutputStream().use { byteArrayOutputStream ->
        BukkitObjectOutputStream(byteArrayOutputStream).use { bukkitObjectOutputStream ->
            bukkitObjectOutputStream.writeObject(this)
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        }
    }
}

// Base64 decode
@Suppress("UNCHECKED_CAST")
fun <T> String.fromBase64(): T {
    ByteArrayInputStream(Base64.getDecoder().decode(this)).use { byteArrayInputStream ->
        BukkitObjectInputStream(byteArrayInputStream).use { bukkitObjectInputStream ->
            return bukkitObjectInputStream.readObject() as T
        }
    }
}



inline fun <reified T> String.fromJson(): T {
    return gson.fromJson(this, object : TypeToken<T>() {}.type)
}

fun Any.toJson(): String {
    return gson.toJson(this)
}