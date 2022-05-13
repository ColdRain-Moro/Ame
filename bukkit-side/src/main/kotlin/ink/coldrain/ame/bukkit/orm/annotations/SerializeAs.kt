package ink.coldrain.ame.bukkit.orm.annotations

/**
 * ink.coldrain.ame.bukkit.orm.annotations.SerializeAs
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/12 17:09
 **/
@Target(AnnotationTarget.FIELD)
annotation class SerializeAs(val type: SerializeType)

enum class SerializeType {
    JSON, BASE64
}
