package site.kuzja.vkmusic.api.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class UserActor implements Serializable {
    @SerializedName("user_id")
    private int userID;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private int expiresIn;

    public UserActor(int userID, String accessToken, int expiresIn) {
        this.userID = userID;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public int getUserID() {
        return userID;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public int getExpiresIn() {
        return expiresIn;
    }
    @Override
    public int hashCode() {
        return Objects.hash(userID, accessToken, expiresIn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserActor actor = (UserActor) o;
        return Objects.equals(userID, actor.userID) &&
                Objects.equals(accessToken, actor.accessToken) &&
                Objects.equals(expiresIn, actor.expiresIn);
    }

    @Override
    public String toString() {
        return "UserActor{" + "access_token=" + accessToken +
                ", expires_in='" + expiresIn + "'" +
                ", user_id='" + userID + "'" +
                '}';

    }
}
