package site.kuzja.vkmusic.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class UserActor {
    @SerializedName("user_id")
    private String userID;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private int expiresIn;

    public UserActor() {
    }
    public UserActor(String userID, String accessToken, int expiresIn) {
        this.userID = userID;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getUserID() {
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
        final StringBuilder sb = new StringBuilder("UserActor{");
        sb.append("access_token=").append(accessToken);
        sb.append(", expires_in='").append(expiresIn).append("'");
        sb.append(", user_id='").append(userID).append("'");
        sb.append('}');
        return sb.toString();

    }
}
