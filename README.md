# Ame

> 雨(あめ) 

自用工具库，封装了一些方便的工具。

## 模块

- common 通用工具
- bukkit-side bukkit平台工具

## 计划

- [x] RxJava & 协程风格链条式事件订阅封装
- [x] taboolib database orm+解耦封装 (MySQL/SQLite两种方式只用写一个类)
- [ ] redis封装

## 有趣的工具

### RxJava风格的事件订阅封装

~~~kotlin
RxEvents.create(AsyncPlayerChatEvent::class) {
        player.sendMessage("吾与徐公孰美？")
    }.then({ e: AsyncPlayerChatEvent, ev: AsyncPlayerChatEvent -> e.player.uniqueId == ev.player.uniqueId }) {
        val answer = message.equals("徐公不若君之美也")
        if (answer) {
            player.sendMessage("(开心)")
        } else {
            player.sendMessage("(难过)")
        }
        return@then answer
    }.then({ e: AsyncPlayerChatEvent, ev: EntityDamageEvent -> e.player.uniqueId == ev.entity.uniqueId }) { answer ->
        // 如果之前没有拍马屁，下一次受到伤害翻倍
        if (!answer) {
            damage += damage
        }
    }.subscribe()
~~~

Ame允许你通过传入回调函数进行一个只执行一次的，快餐式的事件订阅，并且通过类似RxJava的api风格设计使你可以在一次事件结束后接收其返回值并立刻开始下一类型事件的订阅。
当然你也可以选择不继续进行事件的订阅，直接消费掉上个回调函数的返回值。

~~~kotlin
RxEvents.create(AsyncPlayerChatEvent::class) {
    player.health
}.subscribe {
    println(it)
}
~~~

### 可挂起的事件订阅封装

~~~kotlin
suspend fun getSleepTime(player: Player): Long {
    // 上床时间
    val time = asyncEvent<PlayerBedEnterEvent, Long> {
        identifier { player.uniqueId == it.player.uniqueId }
        executor {
            player.sendMessage("晚安")
            return@executor System.currentTimeMillis()
        }
    }
    // 起床时间
    val time2 = asyncEvent<PlayerBedLeaveEvent, Long> {
        identifier { player.uniqueId == it.player.uniqueId }
        executor {
            player.sendMessage("早安")
            return@executor System.currentTimeMillis()
        }
    }
    return time - time2
}
~~~

在协程作用域中，你可以使用`asyncEvent`订阅一个仅执行一次的事件，并获取它的返回值。在事件触发并被处理完成之前，它会一直挂起。
怎么样？是不是看起来更加直观？

### 面向对象的TabooLib数据库操作封装

~~~kotlin
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
~~~

Ame允许你使用一个数据类搭配注解来定义表的结构，并且对SQLite与MySQL做了统一的兼容操作

只需一份代码，便可以在两种数据库中运行你的数据库逻辑！


