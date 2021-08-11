package ml.exploresurvival.launcher

import com.google.gson.Gson
import ml.exploresurvival.launcher.util.Config
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.text.SimpleDateFormat

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
                    userinfo
                    set <key> <value>
                    logout
                    exit
                """.trimIndent())
                cmd[0] == "userinfo" -> {
                    if (Config.configData.userName != "") {
                        val sessionString = if (Config.configData.offlineLogin) "类型          离线登录" else "session有效期 ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Config.configData.expire)}(${if (System.currentTimeMillis() < Config.configData.expire) "未过期" else "已过期"})\n类型          在线登录"
                        println("用户名        ${Config.configData.userName}")
                        println(sessionString)
                    } else {
                        println("未登录")
                    }
                }
                cmd[0] == "login" -> {
                    if (Config.configData.session == "") {
                        if (cmd.size == 2 && cmd[1] == "offline") {
                            print("用户名: ")
                            val username = readLine()
                            if (username != null && username != "") {
                                Config.configData.userName = username
                                Config.configData.offlineLogin = true
                                Config.save()
                                println("Ok")
                            } else {
                                println("无效的用户名")
                            }
                        } else {
                            print("用户名: ")
                            val username = readLine()
                            print("密码: ")
                            val password = readLine()
                            if (username != null && password != null) {
                                print("登录中...")
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
                                    println("OK")
                                } else {
                                    println("无法登录: ${resp.reason}")
                                }
                            }
                        }
                    } else {
                        println("你已经登录了")
                    }
                }
                cmd[0] == "logout" -> {
                    if (Config.configData.session != "") {
                        Config.configData.userName = ""
                        Config.configData.session = ""
                        Config.configData.uuid = ""
                        Config.configData.expire = 0
                        Config.configData.offlineLogin = true
                        Config.save()
                        println("OK")
                    } else {
                        println("未登录")
                    }
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
                                else -> println("不存在名为\"${cmd[1]}\"的配置项")
                            }
                        } else {
                            println("请输入数字")
                        }
                    } else if (cmd.size == 1) {
                        println("JvmMemory:Int = ${Config.configData.JvmMemory}")
                    } else {
                        println("未知参数")
                    }
                }
                cmd[0] == "exit" -> break
                else -> println("未知命令")
            }
        }
    }
}