package site.kuzja.vkmusic.dao;

import android.content.Context;
import android.support.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * Фабрика для ДАО
 */

public class DAOFactory {
    @Nullable
    public static DAOImpl create(Context context, Type type) {
        if (type == DAOSQLite.class)
            return new  DAOSQLite(context);
        return null;
    }
}
