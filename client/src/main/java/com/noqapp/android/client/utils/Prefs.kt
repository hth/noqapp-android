package com.noqapp.android.client.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Build
import java.util.*


object Prefs {

    private lateinit var mPrefs: SharedPreferences

    /**
     * Returns an instance of the shared preference for this app.
     *
     * @return an Instance of the SharedPreference
     */
    val preferences: SharedPreferences
        get() {
            if (::mPrefs.isInitialized) {
                return mPrefs
            }
            throw RuntimeException(
                    "Prefs class not correctly instantiated please call Prefs.iniPrefs(context) in the Application class onCreate.")
        }

    /**
     * @return Returns a map containing a list of pairs key/value representing
     * the preferences.
     * @see SharedPreferences.getAll
     */
    val all: Map<String, *>
        get() = preferences.all

    /**
     * Initialize the Prefs helper class to keep a reference to the
     * SharedPreference for this application the SharedPreference will use the
     * package name of the application as the Key.
     *
     * @param context the Application context.
     */
    fun initPrefs(context: Context) {
        if (!::mPrefs.isInitialized) {
            val key = context.packageName+"_preferences" ?: throw NullPointerException("Prefs key may not be null")
            mPrefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        }
    }

    fun clearallPrefs() {
        preferences.edit().clear().commit()
    }

    /**
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that
     * is not an int.
     * @see SharedPreferences.getInt
     */
    fun getInt(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    /**
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that
     * is not a boolean.
     * @see SharedPreferences.getBoolean
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    /**
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that
     * is not a long.
     * @see SharedPreferences.getLong
     */
    fun getLong(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    /**
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that
     * is not a float.
     * @see SharedPreferences.getFloat
     */
    fun getFloat(key: String, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    /**
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that
     * is not a String.
     * @see SharedPreferences.getString
     */
    fun getString(key: String, defValue: String): String? {
        return preferences.getString(key, defValue)
    }

    /**
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference values if they exist, or defValues. Throws
     * ClassCastException if there is a preference with this name that
     * is not a Set.
     * @see SharedPreferences.getStringSet
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun getStringSet(key: String,
                     defValue: Set<String>): Set<String>? {
        val prefs = preferences
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return prefs.getStringSet(key, defValue)
        } else {
            if (prefs.contains("$key#LENGTH")) {
                val set = HashSet<String>()
                // Workaround for pre-HC's lack of StringSets
                val stringSetLength = prefs.getInt("$key#LENGTH", -1)
                if (stringSetLength >= 0) {
                    for (i in 0 until stringSetLength) {
                        prefs.getString("$key[$i]", null)
                    }
                }
                return set
            }
        }
        return defValue
    }

    /**
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see Editor.putLong
     */
    fun putLong(key: String, value: Long) {
        val editor = preferences.edit()
        editor.putLong(key, value)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    /**
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see Editor.putInt
     */
    fun putInt(key: String, value: Int) {
        val editor = preferences.edit()
        editor.putInt(key, value)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    /**
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see Editor.putFloat
     */
    fun putFloat(key: String, value: Float) {
        val editor = preferences.edit()
        editor.putFloat(key, value)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    /**
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see Editor.putBoolean
     */
    fun putBoolean(key: String, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    /**
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see Editor.putString
     */
    fun putString(key: String, value: String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    /**
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @see Editor.putStringSet
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun putStringSet(key: String, value: Set<String>) {
        val editor = preferences.edit()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            editor.putStringSet(key, value)
        } else {
            // Workaround for pre-HC's lack of StringSets
            var stringSetLength = 0
            if (mPrefs!!.contains("$key#LENGTH")) {
                // First read what the value was
                stringSetLength = mPrefs!!.getInt("$key#LENGTH", -1)
            }
            editor.putInt("$key#LENGTH", value.size)
            var i = 0
            for (aValue in value) {
                editor.putString("$key[$i]", aValue)
                i++
            }
            while (i < stringSetLength) {
                // Remove any remaining values
                editor.remove("$key[$i]")
                i++
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    /**
     * @param key The name of the preference to remove.
     * @see Editor.remove
     */
    fun remove(key: String) {
        val prefs = preferences
        val editor = prefs.edit()
        if (prefs.contains("$key#LENGTH")) {
            // Workaround for pre-HC's lack of StringSets
            val stringSetLength = prefs.getInt("$key#LENGTH", -1)
            if (stringSetLength >= 0) {
                editor.remove("$key#LENGTH")
                for (i in 0 until stringSetLength) {
                    editor.remove("$key[$i]")
                }
            }
        }
        editor.remove(key)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    /**
     * @param key The name of the preference to check.
     * @see SharedPreferences.contains
     */
    operator fun contains(key: String): Boolean {
        return preferences.contains(key)
    }


}