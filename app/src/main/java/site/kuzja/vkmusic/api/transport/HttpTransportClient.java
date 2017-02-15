package site.kuzja.vkmusic.api.transport;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import site.kuzja.vkmusic.api.exceptions.ClientException;

public class HttpTransportClient {
    private static final String ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    //private static final ConnectionsSupervisor SUPERVISOR = new ConnectionsSupervisor();
    private static HttpTransportClient instance;
    private static HttpClient httpClient;

    public HttpTransportClient() {
        httpClient = new DefaultHttpClient();
    }

    public static HttpTransportClient getInstance() {
        if (instance == null) {
            instance = new HttpTransportClient();
        }

        return instance;
    }

    private static Map<String, String> getHeaders(Header[] headers) {
        Map<String, String> result = new HashMap<>();
        for (Header header : headers) {
            result.put(header.getName(), header.getValue());
        }

        return result;
    }

    private String call(HttpPost request) throws ClientException {
        try {
            HttpResponse response = httpClient.execute(request);

            String result = EntityUtils.toString(response.getEntity(), ENCODING);

            if (response.getStatusLine().getStatusCode() != 200)
                throw new ClientException("Internal API server error. Wrong status code("
                        + response.getStatusLine().getStatusCode() + "): "
                        + response.getStatusLine() + ". Content: " + result);

            Map<String, String> headers = getHeaders(response.getAllHeaders());

            if (!headers.containsKey("Content-Type"))
                throw new ClientException("No content type header");
            if (!headers.get("Content-Type").contains("application/json"))
                throw new ClientException("Invalid content type");

            return result;
        } catch (IOException e) {
            throw new ClientException("I/O exception");
        }

    }

    public String post(String url, String body) throws ClientException {
        HttpPost request = new HttpPost(url);
        request.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);

        if (body != null) {
            try {
                request.setEntity(new StringEntity(body));
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("UnsupportedEncodingException");
            }
        }

        return call(request);
    }
}