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
    private HttpTransportClient client = new HttpTransportClient();
    private String url;
    private Gson gson;
    private Type responseClass;
    public AbstractMethod(String url, Type responseClass) {
        this.url = url;
        this.gson = new Gson();
        this.responseClass = responseClass;
    }
    private String mapToGetString() {
        String result = new String();
        for (Map.Entry<String, String> entry: params.entrySet()) {
            if (!result.isEmpty())
                result += "&";
            result += entry.getKey() + "=" + (entry.getValue() != null ?
                    entry.getValue() : "");
        }
        return result;
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }
    public void addParam(String key, int value) {
        addParam(key, Integer.toString(value));
    }
    public void addParam(String key, boolean value) {
        addParam(key, value ? "1" : "0");
    }
    protected void accessToken(String value) {
        addParam("access_token", value);
    }
    protected void version(String value) {
        addParam("v", value);
    }

    public U execute() throws ClientException, ApiException {
        String textResponse = client.post(url, mapToGetString());
        JsonObject json = (JsonObject) new JsonParser().parse(textResponse);

        if (json.has("error")) {
            JsonElement errorElement = json.get("error");
            Error error;
            try {
                error = gson.fromJson(errorElement, Error.class);
            } catch (JsonSyntaxException e) {
                throw new ClientException("Can't parse json response");
            }

            ApiException exception = ExceptionMapper.parseException(error);

            throw exception;
        }

        JsonElement response = json;
        if (json.has("response")) {
            response = json.get("response");
        }

        try {
            return gson.fromJson(response, responseClass);
        } catch (JsonSyntaxException e) {
            throw new ClientException("Can't parse json response");
        }
    }
}