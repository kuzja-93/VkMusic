package site.kuzja.vkmusic.api.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Error {

    /**
     * Error code
     */
    @SerializedName("error_code")
    private Integer errorCode;

    /**
     * Error message
     */
    @SerializedName("error_msg")
    private String errorMsg;

    /**
     * Captcha id
     */
    @SerializedName("captcha_sid")
    private String captchaSid;

    /**
     * Captcha image url
     */
    @SerializedName("captcha_img")
    private String captchaImg;

    /**
     * Confirmation text for user
     */
    @SerializedName("confirmation_text")
    private String confirmationText;

    /**
     * Redirect uri for validation request
     */
    @SerializedName("redirect_uri")
    private String redirectUri;

    @SerializedName("request_params")
    private List<RequestParam> requestParams;

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }


    public String getCaptchaImg() {
        return captchaImg;
    }

    public String getCaptchaSid() {
        return captchaSid;
    }

    public String getConfirmationText() {
        return confirmationText;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public List<RequestParam> getRequestParams() {
        return requestParams;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode, requestParams, errorMsg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return Objects.equals(errorCode, error.errorCode) &&
                Objects.equals(errorMsg, error.errorMsg) &&
                Objects.equals(requestParams, error.requestParams);
    }

    @Override
    public String toString() {
        return "Error{" + "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + "'" +
                ", captchaSid='" + captchaSid + "'" +
                ", captchaImg='" + captchaImg + "'" +
                ", confirmationText='" + confirmationText + "'" +
                ", redirectUri='" + redirectUri + "'" +
                ", requestParams=" + requestParams +
                '}';

    }
}
