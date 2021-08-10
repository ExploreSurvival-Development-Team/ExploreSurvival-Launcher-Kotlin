package ml.exploresurvival.launcher.util

import com.google.gson.Gson
import java.io.File

data class ConfigData(
    val userName: String,
    val session: String,
    val uuid: String,
    val offlineLogin: Boolean,
    val JvmMemory: Int,
    val expire: Long
)

object Config {
    private val configFile = File("./esl.json")
    private val gson = Gson()
    private var isInit = false

    lateinit var configData: ConfigData
    fun init() {
        if (!configFile.exists()) {
            configData = ConfigData("", "", "", true, 1024, 0)
            configFile.writeText(gson.toJson(configData))
        } else {
            configData = gson.fromJson(configFile.readText(), ConfigData::class.java)
        }
        isInit = true
    }
    fun save() {
        if (isInit) {
            configFile.writeText(gson.toJson(configData))
        }
    }
}