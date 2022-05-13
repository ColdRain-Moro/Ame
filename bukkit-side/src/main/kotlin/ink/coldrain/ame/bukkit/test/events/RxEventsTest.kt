package ink.coldrain.ame.bukkit.test.events

import ink.coldrain.ame.bukkit.events.RxEvents
import ink.coldrain.ame.bukkit.events.asyncEvent
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent

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

    RxEvents.create(PlayerBedEnterEvent::class) {
        player.sendMessage("做个好梦")
        return@create player.location
    }.then({ e: PlayerBedEnterEvent, ev: PlayerBedLeaveEvent -> e.player.uniqueId == ev.player.uniqueId }) { loc ->
        println(loc)
    }.subscribe()
}

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