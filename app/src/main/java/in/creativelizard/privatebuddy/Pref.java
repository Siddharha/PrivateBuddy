package in.creativelizard.privatebuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by siddharthamaji on 30/12/16.
 */

public class Pref {

    private SharedPreferences spref;
    //private Activity _activity;
    private static final String PREF_FILE = "in.creativelizard.privatebuddy";
    private SharedPreferences.Editor _editorSpref;

    public SharedPreferences getSharedPreferencesInstance() {
        return spref;
    }

    public SharedPreferences.Editor getSharedPreferencesEditorInstance() {
        return _editorSpref;
    }

    @SuppressLint("CommitPrefEdits")
    public Pref(Context _thisContext) {
        // TODO Auto-generated constructor stub
        //this._activity = (Activity)_thisContext;
        spref = _thisContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        _editorSpref = spref.edit();
    }

    public String getSession(String key) {
        String value = spref.getString(key, "");
        return value;
    }

    public int getIntegerSession(String key) {
        int value = spref.getInt(key, Integer.MIN_VALUE);
        return value;
    }

    public void setSession(String key, String value) {
        if (key != null && value != null) {
            _editorSpref.putString(key, value);
            _editorSpref.commit();
        }
    }

    public void setSession(String key, int value) {
        if (key != null) {
            _editorSpref.putInt(key, value);
            _editorSpref.commit();
        }
    }
}