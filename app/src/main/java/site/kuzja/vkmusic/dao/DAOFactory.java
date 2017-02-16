package site.kuzja.vkmusic.dao;

import android.content.Context;

import java.lang.reflect.Type;

/**
 * Created by user on 16.02.17.
 */

public class DAOFactory {
    public static DAOImpl create(Context context, Type type) {
        if (type == DAOSQLite.class)
            return new  DAOSQLite(context);
        return null;
    }
}
