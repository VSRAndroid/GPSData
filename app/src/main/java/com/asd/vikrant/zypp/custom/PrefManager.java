package com.asd.vikrant.zypp.custom;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // Shared preferences file name
    private static final String PREF_NAME = "Zypp";
 
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, _context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveData(String key,String value) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor .putString(key, value);
        prefsEditor.commit();
    }

    public String getData(String key) {
        if (pref!= null) {
            return pref.getString(key, "");
        }
        return "";
    }

    public void remove(String key){ // Delete only the shared preference that you want
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }
 
}