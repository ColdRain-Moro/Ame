package ink.coldrain.ame.bukkit.orm

import ink.coldrain.ame.bukkit.orm.annotations.ColumnName
import ink.coldrain.ame.bukkit.orm.annotations.SerializeAs
import ink.coldrain.ame.bukkit.orm.annotations.SerializeType
import ink.coldrain.ame.bukkit.orm.annotations.VarChar
import ink.coldrain.ame.bukkit.utils.gson
import ink.coldrain.ame.bukkit.utils.toBase64
import ink.coldrain.ame.bukkit.utils.toJson
import taboolib.common.reflect.ReflexClass
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.ColumnTypeSQLite
import java.lang.reflect.Field

/**
 * ink.coldrain.ame.bukkit.orm.IEntity
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/12 15:59
 **/
interface IEntity

abstract class IntIdEntity : IEntity {
    var id: Int = -1
        internal set
}

internal fun Field.getHandledValue(any: Any): Any? {
    return when (getAnnotation(SerializeAs::class.java)?.type) {
        SerializeType.JSON -> get(any).toJson()
        SerializeType.BASE64 -> get(any).toBase64()
        else -> return get(any)
    }
}

internal fun Field.columnName(): String {
    return getAnnotation(ColumnName::class.java)?.name ?: name
}

internal fun IEntity.fieldSet(): Set<Field> {
    val refClazz = ReflexClass.find(this::class.java)
    return refClazz.savingConstructor[0]
        .parameters
        .mapNotNull { refClazz.findField(it.name) }
        .toMutableSet()
        .apply {
            if (this@fieldSet is IntIdEntity) {
                refClazz.findField("id")?.let { add(it) }
            }
        }
}

internal fun IEntity.fieldList(): List<Field> {
    val refClazz = ReflexClass.find(this::class.java)
    return refClazz.savingConstructor[0]
        .parameters
        .mapNotNull { refClazz.findField(it.name) }
        .run {
            if (this@fieldList is IntIdEntity) {
                mutableListOf(refClazz.findField("id")).apply { addAll(this@run) }
            } else this
        }
        .filterNotNull()
}

internal fun <T : IEntity> Class<T>.fieldList(): List<Field> {
    val refClazz = ReflexClass.find(this)
    return refClazz.savingConstructor[0]
        .parameters
        .mapNotNull { refClazz.findField(it.name) }
}

internal fun Field.inferSQLType(): Pair<ColumnTypeSQL, Int> {
    val serializeAs = getAnnotation(SerializeAs::class.java)
    if (serializeAs != null) {
        if (serializeAs.type == SerializeType.JSON) return ColumnTypeSQL.JSON to 0
        return ColumnTypeSQL.TEXT to 0
    }
    return when (type) {
        Int::class.java -> ColumnTypeSQL.INT to 0
        Long::class.java -> ColumnTypeSQL.BIGINT to 0
        Float::class.java -> ColumnTypeSQL.FLOAT to 0
        Double::class.java -> ColumnTypeSQL.DOUBLE to 0
        Boolean::class.java -> ColumnTypeSQL.BOOLEAN to 0
        String::class.java -> getAnnotation(VarChar::class.java)?.let { ColumnTypeSQL.VARCHAR to it.length }
            ?: (ColumnTypeSQL.TEXT to 0)
        else -> error("failed to infer type ${type.name} to SQL column type")
    }
}

internal fun Field.inferSQLiteType(): Pair<ColumnTypeSQLite, Int> {
    val serializeAs = getAnnotation(SerializeAs::class.java)
    if (serializeAs != null) {
        return ColumnTypeSQLite.TEXT to 0
    }
    return when (type) {
        Int::class.java, Long::class.java, Boolean::class.java -> ColumnTypeSQLite.INTEGER to 0
        Double::class.java, Float::class.java -> ColumnTypeSQLite.REAL to 0
        String::class.java -> ColumnTypeSQLite.TEXT to 0
        else -> error("failed to infer type ${type.name} to SQL column type")
    }
}