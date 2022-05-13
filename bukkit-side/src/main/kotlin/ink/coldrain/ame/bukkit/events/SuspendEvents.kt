package ink.coldrain.ame.bukkit.events

import org.bukkit.event.Event
import java.util.function.Function
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * ink.coldrain.ame.bukkit.utils.SuspendEvents
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/11 21:47
 **/
suspend inline fun <reified E: Event, R> asyncEvent(noinline applier: EventChainContext<E, R>.() -> Unit): R = suspendCoroutine { c ->
    Events.subscribe(E::class.java, Function {
        val context = EventChainContext<E, R>()
        context.applier()
        if (context.identifier == null || context.executor == null) {
            error("missing identifier or executor")
        }
        if (!context.identifier!!.invoke(it)) {
            return@Function true
        }
        c.resume(context.executor!!.invoke(it))
        return@Function false
    })
}

class EventChainContext<E: Event, R> {
    var executor: (E.() -> R)? = null

    var identifier: ((E) -> Boolean)? = null

    fun executor(executor: E.() -> R) {
        this.executor = executor
    }

    fun identifier(identifier: (E) -> Boolean) {
        this.identifier = identifier
    }
}