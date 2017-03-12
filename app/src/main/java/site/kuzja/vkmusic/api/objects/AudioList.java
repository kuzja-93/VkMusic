package site.kuzja.vkmusic.api.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * Created by user on 14.02.17.
 */

public class AudioList {

    /**
     * Количество элементов
     */
    @SerializedName("count")
    private int count;

    @SerializedName("items")
    private List<Audio> items;

    public List<Audio> getItems() {
        return items;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioList audioList = (AudioList) o;
        return Objects.equals(count, audioList.count) &&
                Objects.equals(items, audioList.items);
    }

    @Override
    public String toString() {
        return "AudioList{" + "count=" + count +
                ", items=" + items +
                '}';
    }
}
