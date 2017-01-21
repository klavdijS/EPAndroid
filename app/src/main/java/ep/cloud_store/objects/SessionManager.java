package ep.cloud_store.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Klavdij on 20/01/2017.
 */

public class SessionManager {

    private static String logTag = SessionManager.class.getSimpleName();

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "CloudStore";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String LOGGEDIN_USER = "loggedInUser";

    private static final String LAST_ACTIVITY = "lastActivity";

    public SessionManager (Context context) {
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setLogin (boolean loggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN,loggedIn);

        editor.commit();

        Log.d(logTag, "User login session modified!");
    }

    public void setUser(String userObject) {
        editor.putString(LOGGEDIN_USER,userObject);

        editor.commit();

        Log.d(logTag, "User object added to shared preferences!");
    }

    public void setLastActivity(String className) {
        editor.putString(LAST_ACTIVITY,className);

        editor.commit();
    }

    public String getLastActivity () {
        return sharedPreferences.getString(LAST_ACTIVITY,null);
    }

    public String getUserInfo () {
        return sharedPreferences.getString(LOGGEDIN_USER,null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGEDIN,false);
    }
}
