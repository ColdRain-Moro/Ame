package ink.coldrain.ame.bukkit.orm

import ink.coldrain.ame.bukkit.orm.annotations.ColumnName
import ink.coldrain.ame.bukkit.orm.annotations.SerializeAs
import ink.coldrain.ame.bukkit.orm.annotations.SerializeType
import ink.coldrain.ame.bukkit.utils.fromBase64
import ink.coldrain.ame.bukkit.utils.fromJson
import ink.coldrain.ame.bukkit.utils.gson
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.common.reflect.Reflex.Companion.unsafeInstance
import taboolib.common.reflect.ReflexClass
import taboolib.module.database.QueryTask
import java.sql.ResultSet
import kotlin.reflect.KClass

/**
 * ink.coldrain.ame.bukkit.orm.QueryResult
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/12 17:01
 **/
@Suppress("UNCHECKED_CAST")
internal fun <T> ResultSet.adaptResultSet(clazz: Class<T>): T {
    val unsafe = clazz.unsafeInstance()
    // 使用Reflex缓存数据类，避免重复获取字段
    val refClazz = ReflexClass.find(clazz)
    refClazz.savingConstructor[0]
        .parameters
        .mapNotNull { refClazz.findField(it.name) }
        .toMutableSet()
        .apply { refClazz.findField("id")?.let { add(it) } }
        .forEach {
            val name = it.columnName()
            val obj = when (it.getAnnotation(SerializeAs::class.java)?.type) {
                SerializeType.JSON -> {
                    gson.fromJson(getString(name), it.genericType)
                }
                SerializeType.BASE64 -> {
                    getString(name).fromBase64<Any>()
                }
                else -> getObject(name)
            }
            unsafe.setProperty(name, obj)
        }
    return unsafe as T
}