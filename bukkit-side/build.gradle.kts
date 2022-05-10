val taboolibVersion: String by rootProject

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    implementation("io.izzel:taboolib:${taboolibVersion}:common")
    implementation("io.izzel:taboolib:${taboolibVersion}:common-5")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-kether")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-configuration")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-database")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-ui")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-chat")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-lang")
    implementation("io.izzel:taboolib:${taboolibVersion}:platform-bukkit")
}