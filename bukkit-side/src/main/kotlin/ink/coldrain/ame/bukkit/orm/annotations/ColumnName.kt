package ink.coldrain.ame.bukkit.orm.annotations

/**
 * ink.coldrain.ame.bukkit.orm.annotations.ColumnName
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/12 17:24
 **/
@Target(AnnotationTarget.FIELD)
annotation class ColumnName(
    val name: String
)
