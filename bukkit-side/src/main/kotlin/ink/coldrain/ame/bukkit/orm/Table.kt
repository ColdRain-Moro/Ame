package ink.coldrain.ame.bukkit.orm

import ink.coldrain.ame.bukkit.orm.annotations.Nullable
import ink.coldrain.ame.bukkit.orm.annotations.TableName
import ink.coldrain.ame.bukkit.orm.annotations.Unique
import taboolib.module.database.*
import taboolib.module.database.Table
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 * ink.coldrain.ame.bukkit.orm.Table
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/12 16:31
 **/
class Table<E : IEntity, H : ColumnBuilder>(
    private val entityClazz: KClass<E>,
    private val name: String,
    private val host: Host<H>,
    private val dataSource: DataSource
) {

    @Suppress("UNCHECKED_CAST")
    private val table by lazy {
        Table(name, host) {
            if (host.columnBuilder is SQL) {
                this as Table<Host<SQL>, SQL>
                if (IntIdEntity::class.java.isAssignableFrom(entityClazz.java)) {
                    add {
                        name("id")
                        type(ColumnTypeSQL.INT) {
                            options(
                                ColumnOptionSQL.PRIMARY_KEY,
                                ColumnOptionSQL.AUTO_INCREMENT,
                                ColumnOptionSQL.NOTNULL,
                                ColumnOptionSQL.UNIQUE_KEY
                            )
                        }
                    }
                }
                entityClazz.java.fieldList().forEach { f ->
                    add {
                        name(f.columnName())
                        val (columnType, param1) = f.inferSQLType()
                        type(columnType, param1) {
                            val options = mutableListOf<ColumnOptionSQL>()
                            if (f.getAnnotation(Nullable::class.java) == null) {
                                options.add(ColumnOptionSQL.NOTNULL)
                            }
                            if (f.getAnnotation(Unique::class.java) != null) {
                                options.add(ColumnOptionSQL.UNIQUE_KEY)
                            }
                            options(*options.toTypedArray())
                        }
                    }
                }
            } else if (host.columnBuilder is SQLite) {
                this as Table<Host<SQLite>, SQLite>
                if (IntIdEntity::class.java.isAssignableFrom(entityClazz.java)) {
                    add {
                        name("id")
                        type(ColumnTypeSQLite.INTEGER) {
                            options(
                                ColumnOptionSQLite.PRIMARY_KEY,
                                ColumnOptionSQLite.AUTOINCREMENT,
                                ColumnOptionSQLite.NOTNULL,
                                ColumnOptionSQLite.UNIQUE
                            )
                        }
                    }
                }
                entityClazz.java.fieldList().forEach { f ->
                    add {
                        name(f.columnName())
                        val (columnType, param1) = f.inferSQLiteType()
                        type(columnType, param1) {
                            val options = mutableListOf<ColumnOptionSQLite>()
                            if (f.getAnnotation(Nullable::class.java) == null) {
                                options.add(ColumnOptionSQLite.NOTNULL)
                            }
                            if (f.getAnnotation(Unique::class.java) != null) {
                                options.add(ColumnOptionSQLite.UNIQUE)
                            }
                            options(*options.toTypedArray())
                        }
                    }
                }
            }
        }.also { it.createTable(dataSource, true) }
    }

    fun select(selectTask: ActionSelect.() -> Unit): List<E> {
        return table.workspace(dataSource) {
            select { selectTask() }
        }.map { adaptResultSet(entityClazz.java) }
    }

    fun update(updateTask: ActionUpdate.() -> Unit): Int {
        return table.update(dataSource) {
            updateTask()
        }
    }

    fun insert(entity: E): Int {
        val columnFields = entity.fieldList().filter { it.name != "id" }
        val kvs = columnFields.associate { it.columnName() to it.getHandledValue(entity) }.filter { it.value != null }
        return table.insert(dataSource, *kvs.keys.toTypedArray()) {
            value(*kvs.values.filterNotNull().toTypedArray())
        }
    }

    fun delete(deleteTask: ActionDelete.() -> Unit) {
        table.delete(dataSource) {
            deleteTask()
        }
    }
}

fun <C: ColumnBuilder, E: IEntity> Host<C>.createTable(entityClazz: KClass<E>, dataSource: DataSource): ink.coldrain.ame.bukkit.orm.Table<E, C> {
    return Table(entityClazz, entityClazz.tableName(), this, dataSource)
}

internal fun KClass<*>.tableName(): String {
    return java.getAnnotation(TableName::class.java)?.name ?: simpleName.toString().lowercase()
}