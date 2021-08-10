package ml.exploresurvival.launcher

import ml.exploresurvival.launcher.util.Config
import java.lang.management.ManagementFactory

fun main() {
    println("Hello world!")
    println("${System.getProperty("os.name")} ${System.getProperty("os.arch")} Java ${System.getProperty("java.version")}")
    Config.init()
    while (true) {
        print("ESL> ")
        when (readLine()) {
            "exit" -> break
            else -> println("Unknown command")
        }
    }
}