package ink.coldrain.ame.bukkit.utils

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import java.util.function.Consumer

/**
 * ink.coldrain.ame.bukkit.utils.Events
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/10 14:06
 **/
inline fun <reified T : Event> subscribe(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline subscriber: T.() -> Unit
): Events<T> {
    return Events.subscribe(T::class.java, Consumer { it.subscriber() })
}