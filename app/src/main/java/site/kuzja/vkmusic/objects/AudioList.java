package site.kuzja.vkmusic.objects;

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

    public int getCount() {
        return count;
    }

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
        final StringBuilder sb = new StringBuilder("AudioList{");
        sb.append("count=").append(count);
        sb.append(", items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
