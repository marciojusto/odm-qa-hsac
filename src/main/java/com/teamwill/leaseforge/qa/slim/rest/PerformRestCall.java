package com.teamwill.leaseforge.qa.slim.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.teamwill.leaseforge.qa.slim.AbstractScript;
import com.teamwill.leaseforge.qa.slim.ContextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.X509TrustManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class PerformRestCall extends AbstractScript {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[]{}, null);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectionSpecs(Arrays.asList(
                    ConnectionSpec.MODERN_TLS,
                    ConnectionSpec.CLEARTEXT,
                    ConnectionSpec.COMPATIBLE_TLS))
            .sslSocketFactory(TrustAllCerts.trustAllSslSocketFactory, (X509TrustManager) TrustAllCerts.trustAllCerts[0])
            .hostnameVerifier((hostname, session) -> true)
            .build();
    private Method method;
    private String url;
    private String messageBody;
    private final Map<String, String> bodyVars = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private MediaType mediaType = JSON;
    private String returnCode = "NOT STARTED";
    private String responseBody;
    private JsonNode responseBodyAsJson;
    private JsonNode messageBodyAsJson;
    private EditableBody editableRequestBody;
    private long responseTime = Integer.MAX_VALUE;

    public String responseBody() {
        return responseBody;
    }

    public String url() {
        return url;
    }

    public String returnCode() {
        return returnCode;
    }

    public void setMethodAs(String method) {
        if (this.method != null) {
            throw new IllegalArgumentException("method already set");
        }
        this.method = Method.valueOf(method);
    }

    public void setEndpointAs(String endpoint) {
        if (this.url != null) {
            throw new IllegalArgumentException("url already set");
        }
        this.url = ContextHolder.INSTANCE.get("PROTOCOL")
                + "://"
                + ContextHolder.INSTANCE.get("SERVER")
                + (endpoint.contains("$") ? merge(endpoint) : endpoint);

        log.info("url " + this.url);
    }

    public void setAlternativeEndpointAs(String alternativeEndpoint) {
        if (this.url != null) {
            throw new IllegalArgumentException("url already set");
        }
        this.url = "http"
                + "://"
                +alternativeEndpoint;
        log.info("url " + this.url);
    }

    public void setHeaderAs(String key, String value) {
        headers.put(key, value);
    }

    public void setHeaderAsContextVat(String key, String contextKey) {
        headers.put(key, ContextHolder.INSTANCE.get(contextKey));
    }

    public void setSecurityTokenAsContextVar(String key) {
        headers.put("Authorization", "Bearer " + ContextHolder.INSTANCE.get(key));
    }

    public void setBodyAs(String body) {
        if (this.messageBody != null) {
            throw new IllegalArgumentException("body already set");
        }
        this.messageBody = (body.contains("$") ? merge(body) : body.trim());
    }

    public void setMediaAsType(String mediaType) {
        this.mediaType = MediaType.get(mediaType);
    }

    public String mediaType() {
        return mediaType.type();
    }

    @SneakyThrows
    public void performCall() {
        log.info("\n".repeat(3));
        log.info(method + " " + url);
        if (messageBody != null) {
            log.info(messageBody);
        }
        log.info("\n".repeat(3));

        if (method == null) {
            throw new IllegalArgumentException("Method is not set");
        }
        if (url == null) {
            throw new IllegalArgumentException("url is not set");
        }

        Request.Builder builder = new Request
                .Builder()
                .url(url);

        leadRequestBody(builder, messageBody);

        headers.forEach(builder::addHeader);

        Request request = builder.build();

        long startTime = System.currentTimeMillis();
        try (Response response = client.newCall(request)
                                       .execute()) {
            responseTime = System.currentTimeMillis() - startTime;
            responseBody = Objects.requireNonNull(response.body())
                                  .string();
            returnCode = "" + response.code();
            Thread.sleep(1000);
        } catch (Exception exception) {
            returnCode = buildErrorMessage("Error while performing rest call", exception);
            throw exception;
        }
    }

    private void leadRequestBody(Request.Builder builder, String messageBody) {
        if(method.equals(Method.GET)) {
            builder.get();
        }
        else if (messageBody != null) {
            builder.method(method.name(), buildBody());
        }
        else {
            builder.method(method.name(), EMPTY_BODY);
        }
    }

    String buildErrorMessage(String rootMessage, Throwable exception) {
        String errorMessage = rootMessage
                + "\n"
                + exception.getClass()
                           .getName()
                + ": "
                + exception.getLocalizedMessage();
        return exception.getCause() != null
                ? buildErrorMessage(errorMessage, exception.getCause())
                : errorMessage;
    }

    public long responseTime() {
        return responseTime;
    }

    public boolean responseTimeIsLessThanMs(long maxResponseTime) {
        return responseTime < maxResponseTime;
    }

    public RequestBody buildBody() {
        messageBody = messageBody.contains("$") ? merge(messageBody) : messageBody;
        return RequestBody.create(messageBody, mediaType);
    }

    public boolean responseTagIsNotEmpty(String tag) {
        JsonNode node = jsonResponseBody().get(tag);
        if (node instanceof ArrayNode) {
            return node.size() > 0;
        }
        return node.isEmpty();
    }

    @SneakyThrows
    public JsonNode jsonResponseBody() {
        if (responseBodyAsJson == null) {
            if (responseBody == null || responseBody.isEmpty()) {
                throw new IllegalArgumentException("Response body is empty");
            }
            responseBodyAsJson = mapper.readTree(responseBody);
        }
        return responseBodyAsJson;
    }

    public int responseTagIsArrayOfSize(String tag) {
        JsonNode node = find(jsonResponseBody(), tag);
        if (node == null) {
            log.error("Node not found " + tag);
        }
        if (node instanceof ArrayNode) {
            return node.size();
        }
        return 0;
    }

    public boolean responseTagEqualsContextVar(String tag, String var) {
        return responseTagEquals(tag, ContextHolder.INSTANCE.get(var));
    }

    public boolean responseTagEquals(String tag, String value) {
        JsonNode node = jsonResponseBody().get(tag);
        return node.asText()
                   .equals(value);
    }

    public void setJsonResponseAsContextVar(String var) {
        ContextHolder.INSTANCE.put(var, responseBodyAsJson());
    }

    public String responseBodyAsJson() {
        try {
            return jsonResponseBody().toPrettyString();
        } catch (Exception exception) {
            return responseBody;
        }
    }

    public String messageBodyAsJson() {
        try {
            return jsonMessageBody().toPrettyString();
        } catch (Exception exception) {
            return messageBody;
        }
    }

    @SneakyThrows
    public JsonNode jsonMessageBody() {
        if (messageBodyAsJson == null) {
            if (messageBody == null || messageBody.isEmpty()) {
                throw new IllegalArgumentException("Message body is empty");
            }
            messageBodyAsJson = mapper.readTree(messageBody);
        }
        return messageBodyAsJson;
    }

    public void retrieveFromResponseBodyAndStoreIn(String readKey, String writeKey) {
        ContextHolder.INSTANCE.put(writeKey, find(jsonResponseBody(), readKey).asText());
    }

    public void retrieveFromMessageBodyAndStoreIn(String readKey, String writeKey) {
        ContextHolder.INSTANCE.put(writeKey, jsonMessageBody().get(readKey)
                                                              .asText());
    }

    public String generateRedmineMessage() {
        return "Issue on " + method.name() + " " + url + "\n" +
                "## Request\n" +
                "\n" +
                "**Method:** " + method.name() + "\n" +
                "**URL:** " + url + "\n" +
                "**Body:**" +
                (messageBody == null
                        ? " empty\n"
                        : " \n``` javascript\n" +
                        messageBody + "\n" +
                        "```\n"
                ) +
                "\n" +
                "## Result\n" +
                "\n" +
                "**HTTP code:** " + returnCode + "\n" +
                "**Message:**\n" +
                "``` javascript\n" +
                responseBodyAsJson() + "\n" +
                "```\n" +
                "## Expected\n" +
                "\n" +
                "1. issue 1\n" +
                "1. issue 2";
    }

    /**
     *
     */
    public void initBodyWithContextVar(String var) {
        editableRequestBody = new EditableBody(ContextHolder.INSTANCE.get(var));
        messageBody = editableRequestBody.asString();
    }

    public void setBodyAttributeAs(String att, String value) {
        editableRequestBody.put(att, value);
        messageBody = editableRequestBody.asString();
    }

    public void removeBodyAttribute(String att) {
        editableRequestBody.remove(att);
        messageBody = editableRequestBody.asString();
    }

    public enum Method {
        POST,
        GET,
        PUT,
        DELETE
    }
}
