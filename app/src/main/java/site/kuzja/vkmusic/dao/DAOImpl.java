package site.kuzja.vkmusic.dao;

import site.kuzja.vkmusic.api.objects.UserActor;

/**
 * Интерфейс для DAO
 */

public interface DAOImpl {
    UserActor getUserActor();
    void saveUserActor(UserActor actor);
    void clear();
}
