package com.hy.frame.util

import android.annotation.TargetApi
import android.os.Build
import android.os.StatFs
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.DecimalFormat

object FileUtil {
    val SIZETYPE_B = 1// 获取文件大小单位为B的double值
    val SIZETYPE_KB = 2// 获取文件大小单位为KB的double值
    val SIZETYPE_MB = 3// 获取文件大小单位为MB的double值
    val SIZETYPE_GB = 4// 获取文件大小单位为GB的double值

    /**
     * 获取文件指定文件的指定单位的大小

     * @param filePath 文件路径
     *
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     *
     * @return double值的大小
     */
    fun getFileOrFilesSize(filePath: String, sizeType: Int): Double {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            if (file.isDirectory) {
                blockSize = getFileSizes(file)
            } else {
                blockSize = getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("获取文件大小", "获取失败!")
        }

        return FormetFileSize(blockSize, sizeType)
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小

     * @param filePath 文件路径
     *
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    fun getAutoFileOrFilesSize(filePath: String): String {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            if (file.isDirectory) {
                blockSize = getFileSizes(file)
            } else {
                blockSize = getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("获取文件大小", "获取失败!")
        }

        return FormetFileSize(blockSize)
    }

    /**
     * 获取指定文件大小

     * @param file File
     *
     * @return
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            var fis: FileInputStream?
            fis = FileInputStream(file)
            size = fis.available().toLong()
            fis.close()
        } else {
            file.createNewFile()
            Log.e("获取文件大小", "文件不存在!")
        }
        return size
    }

    /**
     * 获取指定文件夹

     * @param f
     *
     * @return
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFileSizes(f: File): Long {
        var size: Long = 0
        val flist = f.listFiles()
        for (i in flist.indices) {
            if (flist[i].isDirectory) {
                size = size + getFileSizes(flist[i])
            } else {
                size = size + getFileSize(flist[i])
            }
        }
        return size
    }

    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    fun FormetFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        var fileSizeString: String?
        val wrongSize = "0B"
        if (fileS == 0L) {
            return wrongSize
        }
        if (fileS < 1024) {
            fileSizeString = df.format(fileS.toDouble()) + "B"
        } else if (fileS < 1048576) {
            fileSizeString = df.format(fileS.toDouble() / 1024) + "KB"
        } else if (fileS < 1073741824) {
            fileSizeString = df.format(fileS.toDouble() / 1048576) + "MB"
        } else {
            fileSizeString = df.format(fileS.toDouble() / 1073741824) + "GB"
        }
        return fileSizeString
    }

    /**
     * 转换文件大小,指定转换的类型
     * @param fileS
     * @param sizeType
     * @return
     */
    fun FormetFileSize(fileS: Long, sizeType: Int): Double {
        val df = DecimalFormat("#.00")
        var fileSizeLong = 0.0
        when (sizeType) {
            SIZETYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble()))!!
            SIZETYPE_KB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1024))!!
            SIZETYPE_MB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1048576))!!
            SIZETYPE_GB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1073741824))!!
            else -> {
            }
        }
        return fileSizeLong
    }

    /**
     * 删除该路径下的所有文件
     * @param path
     * @return
     */
    fun delAllFile(path: String?) {
        if (path == null)
            return
        val file = File(path)
        if (!file.exists())
            return
        if (!file.isDirectory)
            return
        val tempList = file.list()
        var temp: File?
        for (i in tempList.indices) {
            if (path.endsWith(File.separator)) {
                temp = File(path + tempList[i])
            } else {
                temp = File(path + File.separator + tempList[i])
            }
            if (temp.isFile) {
                temp.delete()
            }
            if (temp.isDirectory) {
                delAllFile(path + "/" + tempList[i])// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i])// 再删除空文件夹
            }
        }
    }

    fun delFolder(folderPath: String) {
        try {
            delAllFile(folderPath) // 删除完里面所有内容
            val myFilePath = File(folderPath)
            myFilePath.delete() // 删除空文件夹
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Access to a directory available size.

     * @param path path.
     *
     * @return Long size.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getDirSize(path: String): Long {
        val stat = StatFs(path)
        val blockSize: Long
        val availableBlocks: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.blockSizeLong
            availableBlocks = stat.availableBlocksLong
        } else {
            blockSize = stat.blockSize.toLong()
            availableBlocks = stat.availableBlocks.toLong()
        }
        return availableBlocks * blockSize
    }

    /**
     * If the folder can be written.

     * @param path path.
     *
     * @return True: success, or false: failure.
     */
    fun canWrite(path: String): Boolean {
        return File(path).canWrite()
    }

    /**
     * If the folder can be read.

     * @param path path.
     *
     * @return True: success, or false: failure.
     */
    fun canRead(path: String): Boolean {
        return File(path).canRead()
    }

    /**
     * Create a folder, If the folder exists is not created.

     * @param folderPath folder path.
     *
     * @return True: success, or false: failure.
     */
    fun createFolder(folderPath: String): Boolean {
        if (!TextUtils.isEmpty(folderPath)) {
            val folder = File(folderPath)
            return createFolder(folder)
        }
        return false
    }

    /**
     * Create a folder, If the folder exists is not created.

     * @param targetFolder folder path.
     *
     * @return True: success, or false: failure.
     */
    fun createFolder(targetFolder: File): Boolean {
        if (targetFolder.exists())
            return true
        return targetFolder.mkdirs()
    }

    /**
     * Create a file, If the file exists is not created.

     * @param filePath file path.
     *
     * @return True: success, or false: failure.
     */
    fun createFile(filePath: String): Boolean {
        if (!TextUtils.isEmpty(filePath)) {
            val file = File(filePath)
            return createFile(file)
        }
        return false
    }

    /**
     * Create a file, If the file exists is not created.

     * @param targetFile file.
     *
     * @return True: success, or false: failure.
     */
    fun createFile(targetFile: File): Boolean {
        if (targetFile.exists())
            return true
        try {
            return targetFile.createNewFile()
        } catch (e: IOException) {
            return false
        }

    }

    /**
     * Create a new file, if the file exists, delete and create again.

     * @param filePath file path.
     *
     * @return True: success, or false: failure.
     */
    fun createNewFile(filePath: String): Boolean {
        if (!TextUtils.isEmpty(filePath)) {
            val file = File(filePath)
            return createNewFile(file)
        }
        return false
    }

    /**
     * Create a new file, if the file exists, delete and create again.

     * @param targetFile file.
     *
     * @return True: success, or false: failure.
     */
    fun createNewFile(targetFile: File): Boolean {
        if (targetFile.exists())
            targetFile.delete()
        try {
            return targetFile.createNewFile()
        } catch (e: IOException) {
            return false
        }

    }

    /**
     * Return the MIME type for the given url.

     * @param url the url, path…
     *
     * @return The MIME type for the given extension or null iff there is none.
     */
    fun getMimeTypeByUrl(url: String): String {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))
    }

}