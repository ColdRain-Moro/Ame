package ink.coldrain.ame.bukkit.test.orm

import ink.coldrain.ame.bukkit.orm.IntIdEntity
import ink.coldrain.ame.bukkit.orm.annotations.*
import ink.coldrain.ame.bukkit.orm.createTable
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.database.getHost
import java.io.File
import java.util.*

/**
 * ink.coldrain.ame.bukkit.test.ORMTest
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/12 15:44
 **/
@TableName("data")
data class DataEntity(
    @VarChar(32)
    @Unique
    private val uuid: String,
    // 不使用@VarChar注解则会推断为TEXT
    val name: String,
    @Nullable
    @ColumnName("optional_props")
    // 不使用@Nullable注解则默认非空
    val optionalProps: String?,
    // 序列化为json格式存储
    @SerializeAs(SerializeType.JSON)
    val profile: Profile
) : IntIdEntity() {
    // transform
    val uniqueId: UUID
        get() = UUID.fromString(uuid)
}

data class Profile(
    val health: Double,
    val level: Int,
    val prefix: String
)

@Config
lateinit var conf: Configuration
    private set

fun test() {
    // SQL
    val type = conf.getString("database-type")
    val host = if (type?.uppercase() == "SQL") conf.getHost("sql-options") else File(getDataFolder(), "sqlite.db").getHost()
    val source = host.createDataSource()
    val table = host.createTable(DataEntity::class, source)
    // 增删查改
    val entity = DataEntity(
        UUID.randomUUID().toString(),
        "Ame",
        null,
        Profile(20.0, 10, "雨")
    )
    table.insert(entity)
    table.delete { where { "uuid" eq entity.uniqueId } }
    val entities: List<DataEntity> = table.select { where { "name" like "Ame" } }
    table.update {
        where { "uuid" eq entity.uniqueId }
        set("name", "Rain")
    }
}