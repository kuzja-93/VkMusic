package site.kuzja.vkmusic.api.metods;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import site.kuzja.vkmusic.api.exceptions.ApiException;
import site.kuzja.vkmusic.api.exceptions.ClientException;
import site.kuzja.vkmusic.api.exceptions.ExceptionMapper;
import site.kuzja.vkmusic.api.transport.HttpTransportClient;
import site.kuzja.vkmusic.api.objects.Error;

/**
 * Класс для выполнения запросов к серверу vk
 * @param <U> - Тип возвращаемого результата
 */
public abstract class AbstractMethod <U> {
    private final Map<String, String> params = new HashMap<>();
    private String url;
    private Gson gson;
    private Type responseClass;
    AbstractMethod(String url, Type responseClass) {
        this.url = url;
        this.gson = new Gson();
        this.responseClass = responseClass;
    }
    private String mapToGetString() {
        String result = "";
        for (Map.Entry<String, String> entry: params.entrySet()) {
            if (!result.isEmpty())
                result += "&";
            result += entry.getKey() + "=" + (entry.getValue() != null ?
                    entry.getValue() : "");
        }
        return result;
    }

    void addParam(String key, String value) {
        params.put(key, value);
    }
    void addParam(String key, int value) {
        addParam(key, Integer.toString(value));
    }
    /*void addParam(String key, boolean value) {
        addParam(key, value ? "1" : "0");
    }*/
    void accessToken(String value) {
        addParam("access_token", value);
    }
    protected void version(String value) {
        addParam("v", value);
    }

    public U execute() throws ClientException, ApiException {
        String textResponse = HttpTransportClient.getInstance().post(url, mapToGetString());
        JsonObject json = (JsonObject) new JsonParser().parse(textResponse);

        if (json.has("error")) {
            JsonElement errorElement = json.get("error");
            Error error;
            try {
                error = gson.fromJson(errorElement, Error.class);
            } catch (JsonSyntaxException e) {
                throw new ClientException("Некорректный ответ");
            }

            throw ExceptionMapper.parseException(error);
        }

        JsonElement response = json;
        if (json.has("response")) {
            response = json.get("response");
        }

        try {
            return gson.fromJson(response, responseClass);
        } catch (JsonSyntaxException e) {
            throw new ClientException("Некорректный ответ");
        }
    }
}