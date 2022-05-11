val taboolibVersion: String by rootProject

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1-native-mt")

    compileOnly("io.izzel:taboolib:${taboolibVersion}:common")
    compileOnly("io.izzel:taboolib:${taboolibVersion}:common-5")
    compileOnly("io.izzel:taboolib:${taboolibVersion}:module-kether")
    compileOnly("io.izzel:taboolib:${taboolibVersion}:module-configuration")
    compileOnly("io.izzel:taboolib:${taboolibVersion}:module-database")
    compileOnly("io.izzel:taboolib:${taboolibVersion}:module-ui")
    compileOnly("io.izzel:taboolib:${taboolibVersion}:module-chat")
    compileOnly("io.izzel:taboolib:${taboolibVersion}:module-lang")
    compileOnly("io.izzel:taboolib:${taboolibVersion}:platform-bukkit")
}