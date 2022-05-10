package ink.coldrain.ame.bukkit.test

import ink.coldrain.ame.bukkit.utils.RxEvents
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * ink.coldrain.ame.bukkit.test.RxEventsTest
 * module-ame
 *
 * @author 寒雨
 * @since 2022/5/10 20:12
 **/
fun test() {
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

    RxEvents.create(AsyncPlayerChatEvent::class) {
        player.health
    }.subscribe {
        println(it)
    }
}