package com.box.common.core.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.StatFs
import android.text.format.Formatter
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

fun sendEmail(context: Context, receiverMail: Array<String>, subject: String, content: String, filePath: String) {
    val intent = Intent(Intent.ACTION_SEND)
    // 收件人
    intent.putExtra(Intent.EXTRA_EMAIL, receiverMail)
    // 主题
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    // 正文
    intent.putExtra(Intent.EXTRA_TEXT, content)
    // 抄送
    //intent.putExtra(Intent.EXTRA_CC, content)
    //密送
    //intent.putExtra(Intent.EXTRA_BCC, content)
    // 附件
    //val file = File(filePath)
    //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
    //当无法确认发送类型的时候使用如下语句
    //intent.type = "*/*"
    //当没有附件,纯文本发送时使用如下语句
    intent.type = "plain/text"
    //有附件
    //intent.type = "application/octet-stream"
    context.startActivity(Intent.createChooser(intent, "Mail Chooser"))
}

fun getMemoryInfo(context: Context): Array<String>? {
    try {
        val fileReader = FileReader(MEM_INFO_PATH)
        val bufferedReader = BufferedReader(fileReader, 4 * 1024)
        var totalStr = ""
        var availableStr = ""
        var str: String? = bufferedReader.readLine()
        while (str != null) {
            if (str.contains(MEM_TOTAL)) {
                totalStr = str
            } else if (str.contains(MEM_FREE)) {
                availableStr = str
            }
            if (totalStr.isNotEmpty() && availableStr.isNotEmpty()) {
                break
            }
            str = bufferedReader.readLine()
        }
        bufferedReader.close()
        /* \\s表示   空格,回车,换行等空白符,+号表示一个或多个的意思 */
        val pattern = Pattern.compile("[^0-9]")
        val arrayTotal = pattern.matcher(totalStr).replaceAll("")
        val arrayFree = pattern.matcher(availableStr).replaceAll("")
        val array = arrayOf("", "")
        // 获得系统总内存，单位是KB，乘以1024转换为Byte
        if (arrayTotal.isNotEmpty()) {
            val length = arrayTotal.toLong() * 1024
            val lengthStr = Formatter.formatFileSize(context, length)
            array[0] = lengthStr
        }
        if (arrayFree.isNotEmpty()) {
            val length = arrayFree.toLong() * 1024
            val lengthStr = Formatter.formatFileSize(context, length)
            array[1] = lengthStr
        }
        return array
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun getDiskInfo(context: Context): Array<String> {
    val path = Environment.getDataDirectory().path
    val statFs = StatFs(path)
    val blockSize = statFs.blockSize
    val totalBlocks = statFs.blockCount
    val availableBlocks = statFs.availableBlocks

    val romLength = totalBlocks * blockSize.toLong()
    val availableLength = availableBlocks * blockSize.toLong()

    return arrayOf(Formatter.formatFileSize(context, romLength), Formatter.formatFileSize(context, availableLength))
}

const val FEEDBACK_EMAIL = "feedback@newsbox-inc.com"
const val MEM_INFO_PATH = "/proc/meminfo"
const val MEM_TOTAL = "MemTotal"
const val MEM_FREE = "MemFree"