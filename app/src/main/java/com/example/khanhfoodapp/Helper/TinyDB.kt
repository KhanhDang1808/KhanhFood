package com.example.khanhfoodapp.Helper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Environment
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import com.example.khanhfoodapp.Domain.Foods
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.collections.ArrayList
import com.google.gson.Gson as Gson1

class TinyDB(private var appContext: Context) {

    private var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    private var DEFAULT_APP_IMAGEDATA_DIRECTORY: String = ""
    private var lastImagePath: String = ""

    fun getImage(path: String): Bitmap? {
        var bitmapFromPath: Bitmap? = null
        try {
            bitmapFromPath = BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmapFromPath
    }

    fun getSavedImagePath(): String {
        return lastImagePath
    }

    fun putImage(theFolder: String, theImageName: String, theBitmap: Bitmap): String {

        DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder
        val mFullPath = setupFullPath(theImageName)

        if (mFullPath != "") {
            lastImagePath = mFullPath
            saveBitmap(mFullPath, theBitmap)
        }

        return mFullPath
    }

    fun putImageWithFullPath(fullPath: String, theBitmap: Bitmap): Boolean {
        return   saveBitmap(fullPath, theBitmap)
    }

    private fun setupFullPath(imageName: String): String {
        val mFolder = File(Environment.getExternalStorageDirectory(), DEFAULT_APP_IMAGEDATA_DIRECTORY)

        if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists()) {
            if (!mFolder.mkdirs()) {
                Log.e("ERROR", "Failed to setup folder")
                return ""
            }
        }

        return mFolder.path + '/' + imageName
    }

    private fun saveBitmap(fullPath: String, bitmap: Bitmap): Boolean {

        var fileCreated = false
        var bitmapCompressed = false
        var streamClosed = false

        val imageFile = File(fullPath)

        if (imageFile.exists())
            if (!imageFile.delete())
                return false

        try {
            fileCreated = imageFile.createNewFile()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(imageFile)
            bitmapCompressed = bitmap.compress(CompressFormat.PNG, 100, out)

        } catch (e: Exception) {
            e.printStackTrace()
            bitmapCompressed = false

        } finally {
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                    streamClosed = true

                } catch (e: IOException) {
                    e.printStackTrace()
                    streamClosed = false
                }
            }
        }

        return (fileCreated && bitmapCompressed && streamClosed)
    }

    fun getInt(key: String): Int {
        return preferences.getInt(key, 0)
    }

    fun getListInt(key: String): ArrayList<Int> {
        val myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚")
        val arrayToList = ArrayList<String>(listOf(*myList))
        val newList = ArrayList<Int>()

        for (item in arrayToList)
            newList.add(Integer.parseInt(item))

        return newList
    }

    fun getLong(key: String): Long {
        return preferences.getLong(key, 0)
    }

    fun getFloat(key: String): Float {
        return preferences.getFloat(key, 0f)
    }

    fun getDouble(key: String): Double {
        val number = getString(key)

        return try {
            java.lang.Double.parseDouble(number)

        } catch (e: NumberFormatException) {
            0.0
        }
    }

    fun getListDouble(key: String): ArrayList<Double> {
        val myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚")
        val arrayToList = ArrayList<String>(listOf(*myList))
        val newList = ArrayList<Double>()

        for (item in arrayToList)
            newList.add(java.lang.Double.parseDouble(item))

        return newList
    }

    fun getListLong(key: String): ArrayList<Long> {
        val myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚")
        val arrayToList = ArrayList<String>(listOf(*myList))
        val newList = ArrayList<Long>()

        for (item in arrayToList)
            newList.add(java.lang.Long.parseLong(item))

        return newList
    }

    fun getString(key: String): String? {
        return preferences.getString(key, "")
    }

    private fun getListString(key: String): ArrayList<String> {
        return ArrayList<String>(listOf(*TextUtils.split(preferences.getString(key, ""), "‚‗‚")))
    }

    fun getBoolean(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }

    fun getListBoolean(key: String): ArrayList<Boolean> {
        val myList = getListString(key)
        val newList = ArrayList<Boolean>()

        for (item in myList) {
            if (item == "true") {
                newList.add(true)
            } else {
                newList.add(false)
            }
        }

        return newList
    }

    fun getListObject(key: String): ArrayList<Foods> {
        val gson = Gson1()

        val objStrings = getListString(key)
        val playerList = ArrayList<Foods>()

        for (jObjString in objStrings) {
            val player = gson.fromJson(jObjString, Foods::class.java)
            playerList.add(player)
        }
        return playerList
    }

    fun <T> getObject(key: String, classOfT: Class<T>): T {
        val json = getString(key)
        return Gson1().fromJson(json, classOfT) ?: throw NullPointerException()
    }
    fun putString(key: String, value: String?) {
        checkForNullKey(key)
        checkForNullValue(value)
        preferences.edit().putString(key, value).apply()
    }
    fun putObject(key: String, obj: Any) {
        checkForNullKey(key)
        val gson = Gson1()
        putString(key, gson.toJson(obj))
    }
    fun putListInt(key: String, intList: ArrayList<Int>) {
        checkForNullKey(key)
        val myIntList = intList.joinToString("‚‗‚")
        preferences.edit().putString(key, myIntList).apply()
    }
    fun putListString(key: String, stringList: ArrayList<String>) {
        checkForNullKey(key)
        val myStringList = stringList.joinToString("‚‗‚")
        preferences.edit().putString(key, myStringList).apply()
    }
    fun putListObject(key: String, playerList: ArrayList<Foods>) {
        checkForNullKey(key)
        val gson = Gson1()
        val objStrings = ArrayList<String>()
        for (player in playerList) {
            objStrings.add(gson.toJson(player))
        }
        putListString(key, objStrings)
    }

    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    fun deleteImage(path: String): Boolean {
        return File(path).delete()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    fun getAll(): Map<String, *> {
        return preferences.all
    }

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        fun isExternalStorageWritable(): Boolean {
            return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
        }

        fun isExternalStorageReadable(): Boolean {
            val state = Environment.getExternalStorageState()

            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }
    }

    private fun checkForNullKey(key: String) {
    }

    private fun checkForNullValue(value: String?) {
        if (value == null) {
            throw NullPointerException()
        }
    }
}
