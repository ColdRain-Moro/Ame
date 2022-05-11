package ink.coldrain.ame.bukkit.utils

import org.bukkit.event.Event
import java.lang.reflect.ParameterizedType
import java.util.function.Consumer
import java.util.function.Function
import kotlin.reflect.KClass

/**
 * ink.coldrain.ame.utils.EventsSuspendable
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/10 13:49
 **/
class RxEvents<P, E: Event, N> internal constructor(
    private val identifier: ((Any, E) -> Boolean)?,
    private val previous: RxEvents<*, *, *>? = null,
    private val subscriber: E.(P) -> N
) {
    private var next: RxEvents<*, *, *>? = null

    /**
     * 向事件链上叠加事件
     *
     * @param EV
     * @param NE
     * @param identifier
     * @param mapper
     * @receiver
     * @receiver
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <EV: Event, NE> then(identifier: (E, EV) -> Boolean, mapper: EV.(N) -> NE): RxEvents<N, EV, NE> {
        val func: (Any, EV) -> Boolean = { a, b ->
            identifier(a as E, b)
        }
        return RxEvents(func, this, mapper).also { next = it }
    }

    /**
     * 订阅之前积累的所有事件链
     */
    @Suppress("UNCHECKED_CAST")
    fun subscribe(consumer: (N) -> Unit = {}) {
        // 递归到最上层
        previous?.let {
            it.subscribe()
            return
        }
        Events.subscribe(getEventClass<E>(), Consumer {
            subscribeNext(it.subscriber(Unit as P), it)
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun subscribeNext(res: Any?, lastEvent: Event) {
        val nxt = next as? RxEvents<Any?, Event, Any?>
        val clazz = nxt?.getEventClass<Event>() ?: return
        Events.subscribe(clazz, Function {
            // 对不上就什么都不做
            if (nxt.identifier?.invoke(lastEvent, it) == false) {
                return@Function true
            }
            val result = nxt.subscriber(it, res)
            subscribeNext(result, it)
            return@Function false
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Event> getEventClass(): Class<T> {
        val genericSuperclass = javaClass.genericSuperclass // 得到继承的父类填入的泛型（必须是具体的类型，不能是 T 这种东西）
        if (genericSuperclass is ParameterizedType) {
            val typeArguments = genericSuperclass.actualTypeArguments
            for (type in typeArguments) {
                if (type is Class<*> && Event::class.java.isAssignableFrom(type)) {
                    return type as Class<T>
                } else if (type is ParameterizedType) { // 泛型中有泛型时并不为 Class<*>
                    val rawType = type.rawType // 这时 rawType 一定是 Class<*>
                    if (rawType is Class<*> && Event::class.java.isAssignableFrom(rawType)) {
                        return rawType as Class<T>
                    }
                }
            }
        }
        error("event class not found")
    }

    companion object {
        fun <E: Event, N> create(eventClazz: KClass<E>, subscriber: E.() -> N): RxEvents<Unit, E, N> {
            val theSubscriber: E.(Unit) -> N = { subscriber() }
            return RxEvents(null, null, theSubscriber)
        }
    }
}