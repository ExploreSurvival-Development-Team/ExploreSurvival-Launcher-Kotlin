package ml.exploresurvival.launcher

import com.google.gson.Gson
import ml.exploresurvival.launcher.util.Config
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

data class Response(
    val success: Boolean,
    val session: String,
    val uuid: String,
    val expire: Long,
    val reason: String
)

fun main() {
    val client = OkHttpClient()
    val gson = Gson()

    println("Hello world!")
    println("${System.getProperty("os.name")} ${System.getProperty("os.arch")} Java ${System.getProperty("java.version")}")
    Config.init()
    while (true) {
        print("ESL> ")
        val cmd = readLine()?.split(" ")
        if (cmd != null && cmd[0] != "") {
            when {
                cmd[0] == "help" -> println("""
                    login [offline]
                    set <key> <value>
                    logout
                    exit
                """.trimIndent())
                cmd[0] == "login" -> {
                    print("Username: ")
                    val username = readLine()
                    print("Password: ")
                    val password = readLine()
                    if (username != null && password != null) {
                        println("Login...")
                        val request = Request.Builder()
                            .url("https://www.opencomputers.ml:7331/ExploreSurvival/login.jsp?username=${URLEncoder.encode(username, Charsets.UTF_8)}&password=${URLEncoder.encode(password, Charsets.UTF_8)}")
                            .build()
                        val resp = gson.fromJson(client.newCall(request).execute().body?.string() ?: "{}", Response::class.java)
                        if (resp.success) {
                            Config.configData.userName = username
                            Config.configData.session = resp.session
                            Config.configData.uuid = resp.uuid
                            Config.configData.expire = resp.expire
                            Config.configData.offlineLogin = false
                            Config.save()
                            print("OK")
                        } else {
                            println("Failed to login: ${resp.reason}")
                        }
                    }
                }
                cmd[0] == "login offline" -> {
                    print("Username: ")
                    val username = readLine()
                    if (username != null) {
                        Config.configData.userName = username
                        Config.configData.offlineLogin = true
                        Config.save()
                    }
                }
                cmd[0] == "logout" -> {
                    Config.configData.userName = ""
                    Config.configData.session = ""
                    Config.configData.uuid = ""
                    Config.configData.expire = 0
                    Config.configData.offlineLogin = true
                    Config.save()
                    print("OK")
                }
                cmd[0] == "set" -> {
                    if (cmd.size == 3) {
                        val value = cmd[2].toIntOrNull()
                        if (value != null) {
                            when (cmd[1]) {
                                "JvmMemory" -> {
                                    Config.configData.JvmMemory = value
                                    Config.save()
                                }
                                else -> println("Unknown args")
                            }
                        } else {
                            println("Not a number")
                        }
                    } else if (cmd.size == 1) {
                        println("JvmMemory:Int = ${Config.configData.JvmMemory}")
                    } else {
                        println("Unknown args")
                    }
                }
                cmd[0] == "exit" -> break
                else -> println("Unknown command")
            }
        }
    }
}