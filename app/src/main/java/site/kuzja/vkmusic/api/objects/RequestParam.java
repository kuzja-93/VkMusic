package site.kuzja.vkmusic.api.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * RequestParam object
 */
public class RequestParam {
    /**
     * Parameter name
     */
    @SerializedName("key")
    private String key;

    /**
     * Parameter value
     */
    @SerializedName("value")
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestParam requestParam = (RequestParam) o;
        return Objects.equals(key, requestParam.key) &&
                Objects.equals(value, requestParam.value);
    }

    @Override
    public String toString() {
        return "RequestParam{" + "key='" + key + "'" +
                ", value='" + value + "'" +
                '}';
    }
}
