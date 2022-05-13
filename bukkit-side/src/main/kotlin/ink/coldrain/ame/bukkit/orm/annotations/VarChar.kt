package ink.coldrain.ame.bukkit.orm.annotations

/**
 * ink.coldrain.ame.bukkit.orm.annotations.VarChar
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/12 16:04
 **/
@Target(AnnotationTarget.FIELD)
annotation class VarChar(
    val length: Int
)
