package site.kuzja.vkmusic.api.objects;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;
import java.util.Objects;

/**
 * Обект хранящий информацию о аудиозаписи
 */

public class Audio {

    /**
     * идентификатор аудиозаписи
     */
    @SerializedName("id")
    private int id;
    /**
     * идентификатор владельца аудиозаписи
     */
    @SerializedName("owner_id")
    private int ownerId;
    /**
     * исполнитель
     */
    @SerializedName("artist")
    private String artist;
    /**
     * название композиции
     */
    @SerializedName("title")
    private String title;
    /**
     * длительность аудиозаписи в секундах
     */
    @SerializedName("duration")
    private int duration;
    /**
     * ссылка на mp3
     */
    @SerializedName("url")
    private String url;
    /**
     *	идентификатор текста аудиозаписи (если доступно)
     */
    @SerializedName("lyrics_id")
    private int lyricsId;
    /**
     * идентификатор альбома, в котором находится аудиозапись (если присвоен)
     */
    @SerializedName("album_id")
    private int albumId;
    /**
     * дентификатор жанра из списка аудио жанров
     */
    @SerializedName("genre_id")
    private int genreId;
    /**
     * дата добавления
     */
    @SerializedName("date")
    private int date;
    /**
     * 1, если включена опция «Не выводить при поиске». Если опция отключена, поле не возвращается.
     */
    @SerializedName("no_search")
    private int noSearch;

    public int getAlbumId() {
        return albumId;
    }

    public int getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }

    public int getGenreId() {
        return genreId;
    }

    public int getId() {
        return id;
    }

    public int getLyricsId() {
        return lyricsId;
    }

    public int getNoSearch() {
        return noSearch;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, artist, title, duration,
                url, lyricsId, albumId, genreId, date, noSearch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audio actor = (Audio) o;
        return Objects.equals(id, actor.id) &&
                Objects.equals(ownerId, actor.ownerId) &&
                Objects.equals(artist, actor.artist) &&
                Objects.equals(title, actor.title) &&
                Objects.equals(duration, actor.duration) &&
                Objects.equals(url, actor.url) &&
                Objects.equals(lyricsId, actor.lyricsId) &&
                Objects.equals(albumId, actor.albumId) &&
                Objects.equals(genreId, actor.genreId) &&
                Objects.equals(date, actor.date) &&
                Objects.equals(noSearch, actor.noSearch);
    }

    @Override
    public String toString() {
        return "Audio{" + "id=" + id +
                ", owner_id='" + ownerId + "'" +
                ", artist='" + artist + "'" +
                ", title='" + title + "'" +
                ", duration='" + duration + "'" +
                ", url='" + url + "'" +
                ", lyrics_id='" + lyricsId + "'" +
                ", album_id='" + albumId + "'" +
                ", genre_id='" + genreId + "'" +
                ", date='" + date + "'" +
                ", no_search='" + noSearch + "'" +
                '}';

    }
}
