# Ame

> 雨(あめ) 

自用工具库，封装了一些方便的工具。

## 模块

- common 通用工具
- bukkit-side bukkit平台工具

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
