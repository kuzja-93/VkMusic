package site.kuzja.vkmusic.interfaces;

import site.kuzja.vkmusic.Media.MusicItem;

/**
 * Интерфейс для воспроизведения следующего трека
 */

public interface OnPlayNextItem {
    void playNextItem(MusicItem item);
}
