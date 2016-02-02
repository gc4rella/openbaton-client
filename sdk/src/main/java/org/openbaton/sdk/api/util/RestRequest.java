package org.openbaton.sdk.api.util;


import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.openbaton.sdk.api.exception.SDKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * OpenBaton api request request abstraction for all requester. Shares common data and methods.
 */
public abstract class RestRequest {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    protected final String baseUrl;

//	protected final String url;

    protected Gson mapper;
    private String username;
    private String password;
    private String authStr = "openbatonOSClient" + ":" + "secret";
    private String encoding = Base64.encodeBase64String(authStr.getBytes());
    private final String provider;
    private String token = null;
    private String bearerToken = null;

    /**
     * Create a request with a given url path
     */
    public RestRequest(String username, String password, final String nfvoIp, String nfvoPort, String path, String version) {
        this.baseUrl = "http://" + nfvoIp + ":" + nfvoPort + "/api/v" + version + path;
        this.provider = "http://" + nfvoIp + ":" + nfvoPort + "/oauth/token";
        this.username = username;
        this.password = password;

        GsonBuilder builder = new GsonBuilder();
        /*builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });*/
        this.mapper = builder.create();

    }

    /**
     * Does the POST Request
     * @param id
     * @return String
     * @throws SDKException
     */
    public String requestPost(final String id) throws SDKException {
        HttpResponse<JsonNode> jsonResponse = null;
        try {

            log.debug("baseUrl: " + baseUrl);
            log.debug("id: " + baseUrl + id);

            checkToken();

            // call the api here
            log.debug("Executing post on: " + this.baseUrl + id);
            if (token != null)
                jsonResponse = Unirest.post(this.baseUrl + id)
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("Authorization", bearerToken.replaceAll("\"", ""))
                        .asJson();
            else
                jsonResponse = Unirest.post(this.baseUrl + id)
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .asJson();
//            check response status
            checkStatus(jsonResponse, HttpURLConnection.HTTP_CREATED);
            // return the response of the request
            String result = jsonResponse.getBody().toString();
            log.trace("received: " + result);

            return result;
        } catch (IOException e) {
            // catch request exceptions here
            log.error(e.getMessage(), e);
            throw new SDKException("Could not http-post or open the object properly", e);
        } catch (UnirestException e) {
            // catch request exceptions here
            log.error(e.getMessage(), e);
            throw new SDKException("Could not http-post or open the object properly", e);

        } catch (SDKException e) {
            if (jsonResponse.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                token = null;
                return requestPost(id);
            } else throw new SDKException("Status is " + jsonResponse.getStatus());
        }
    }

    /**
     * Executes a http post with to a given url, while serializing the object content as json
     * and returning the response
     *
     * @param object the object content to be serialized as json
     * @return a string containing the response content
     */
    public Serializable requestPost(final Serializable object) throws SDKException {
    	return requestPost("",object);
    }
    
    
    public Serializable requestPost(final String id,final Serializable object) throws SDKException {
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            log.trace("Object is: " + object);
         
            String fileJSONNode = mapper.toJson(object);

            log.trace("sending: " + fileJSONNode.toString());
            log.trace("baseUrl: " + baseUrl + "/" +id);

            checkToken();

            // call the api here
            log.debug("Executing post on: " + this.baseUrl + "/" +id);
            if (token != null)
                jsonResponse = Unirest.post(this.baseUrl + "/" +id)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", bearerToken.replaceAll("\"", ""))
                    .body(fileJSONNode)
                    .asJson();
            else
                jsonResponse = Unirest.post(this.baseUrl + "/" +id)
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .body(fileJSONNode)
                        .asJson();
//            check response status
            checkStatus(jsonResponse, HttpURLConnection.HTTP_CREATED);
            // return the response of the request
            String result2 = jsonResponse.getBody().toString();
            log.trace("received2: " + result2);

            if (jsonResponse.getStatus() != HttpURLConnection.HTTP_NO_CONTENT) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(jsonResponse.getBody().toString());
                String result = gson.toJson(je);
                log.trace("received: " + result);


                log.trace("Casting it into: " + object.getClass());
//            return mapper.readValue(jsonResponse.getBody().toString(), object.getClass());

                return mapper.fromJson(result, object.getClass());
            }
            return null;
        } catch (IOException e) {
            // catch request exceptions here
            log.error(e.getMessage(), e);
            throw new SDKException("Could not http-post or open the object properly", e);
        } catch (UnirestException e) {
            // catch request exceptions here
            log.error(e.getMessage(), e);
            throw new SDKException("Could not http-post or open the object properly", e);
        } catch (SDKException e) {
            if (jsonResponse.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                token = null;
                return requestPost(object);
            } else
                throw new SDKException("Status is " + jsonResponse.getStatus(), e);
        }
    }

    private void checkToken() throws IOException, SDKException {
        if (!(this.username == null || this.password == null))
            if (token == null && (!this.username.equals("") || !this.password.equals(""))) {
                getAccessToken();
            }
    }

    private JsonNode getJsonNode(Serializable object) throws IOException {
        return new JsonNode(mapper.toJson(object));
    }

    /**
     * Executes a http delete with to a given id
     *
     * @param id the id path used for the api request
     */
    public void requestDelete(final String id) throws SDKException {
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            // call the api here
            checkToken();
            log.debug("Executing delete on: " + this.baseUrl + "/" + id);
            if (token != null)
                jsonResponse = Unirest.delete(this.baseUrl + "/" + id)
                    .header("Authorization", bearerToken.replaceAll("\"", ""))
                    .asJson();
            else
                jsonResponse = Unirest.delete(this.baseUrl + "/" + id).asJson();
//            check response status
            checkStatus(jsonResponse, HttpURLConnection.HTTP_NO_CONTENT);

        } catch (UnirestException e) {
            // catch request exceptions here
            throw new SDKException("Could not http-delete or the api response was wrong", e);
        } catch (SDKException e) {
            // catch request exceptions here
            if (jsonResponse.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                token = null;
                requestDelete(id);
                return;
            }
            throw new SDKException("Could not http-delete or the api response was wrong", e);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new SDKException("Could not get token", e);
        }
    }
    

    /**
     * Executes a http get with to a given id
     *
     * @param id the id path used for the api request
     * @return a string containing he response content
     */
    public Object requestGet(final String id, Class type) throws SDKException {
        String url = this.baseUrl;
        if (id != null){
            url += "/" + id;
            return requestGetWithStatus(url, null, type);
        }
        else return requestGetAll(url, type, null);
    }

    protected Object requestGetAll(String url, Class type) throws SDKException {
        url = this.baseUrl + "/" + url;
        return requestGetAll(url, type, null);
    }
    private Object requestGetAll(String url, Class type, final Integer httpStatus) throws SDKException {
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            // call the api here
            try {
                checkToken();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new SDKException("Could not get token", e);
            }
            log.debug("Executing get on: " + url);

            if (token != null)
                jsonResponse = Unirest.get(url)
                        .header("Authorization", bearerToken.replaceAll("\"", ""))
                        .asJson();
            else
                jsonResponse = Unirest.get(url).asJson();

            // check response status
            if (httpStatus != null) {
                checkStatus(jsonResponse, httpStatus);
            } else {
                checkStatus(jsonResponse, HttpURLConnection.HTTP_OK);
            }
            // return the response of the request
            log.trace("result is: " + jsonResponse.getBody().toString());

            //log.trace("result is: " + result);

            Class<?> aClass = Array.newInstance(type, 3).getClass();
            log.trace("class is: " + aClass);
            Object[] o = (Object[]) mapper.fromJson(jsonResponse.getBody().toString(), aClass);
            log.trace("deserialized is: " + o);

            return o;

        } catch (UnirestException e) {
            // catch request exceptions here
            throw new SDKException("Could not http-get properly", e);
        } catch (SDKException e) {
            if (jsonResponse != null) {
                if (jsonResponse.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                    token = null;
                    return requestGetAll(url, type, httpStatus);
                } else {
                    log.error(e.getMessage(), e);
                    throw new SDKException("Could not authorize", e);
                }
            }else {
                log.error(e.getMessage(), e);
                throw e;
            }
        }
    }


    /**
     * Executes a http get with to a given id, and possible executed an http (accept) status check of the response if an httpStatus is delivered.
     * If httpStatus is null, no check will be executed.
     *
     * @param url        the id path used for the api request
     * @param httpStatus the http status to be checked.
     * @param type
     * @return a string containing the response content
     */
    private Object requestGetWithStatus(final String url, final Integer httpStatus, Class type) throws SDKException {
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            // call the api here
            try {
                checkToken();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new SDKException("Could not get token", e);

            }
            log.debug("Executing get on: " +url);

            if (token != null)
                jsonResponse = Unirest.get(url)
                    .header("Authorization", bearerToken.replaceAll("\"", ""))
                    .asJson();
            else
                jsonResponse = Unirest.get(url).asJson();

            // check response status
            if (httpStatus != null) {
                checkStatus(jsonResponse, httpStatus);
            } else {
                checkStatus(jsonResponse, HttpURLConnection.HTTP_OK);
            }
            // return the response of the request
            log.trace("result is: " + jsonResponse.getBody().toString());

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jsonResponse.getBody().toString());
            String result = gson.toJson(je);
            log.trace("result is: " + result);

            Class<?> aClass = Array.newInstance(type, 1).getClass();
            log.trace("class is: " + aClass);

            return mapper.fromJson(jsonResponse.getBody().toString(), type);

        } catch (UnirestException e) {
            // catch request exceptions here
            throw new SDKException("Could not http-get properly", e);
        } catch (SDKException e) {
            if (jsonResponse.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                token = null;
                return requestGetWithStatus(url, httpStatus, type);
            } else {
                log.error(e.getMessage(), e);
                throw new SDKException("Could not authorize", e);
            }
        }
    }

    /**
     * Executes a http get with to a given url, in contrast to the normal get it uses an http (accept) status check of the response
     *
     * @param url the url path used for the api request
     * @return a string containing the response content
     */
    public Object requestGetWithStatusAccepted(String url, Class type) throws SDKException {
        url = this.baseUrl + "/" + url;
        return requestGetWithStatus(url, new Integer(HttpURLConnection.HTTP_ACCEPTED), type);
    }

    /**
     * Executes a http put with to a given id, while serializing the object content as json
     * and returning the response
     *
     * @param id     the id path used for the api request
     * @param object the object content to be serialized as json
     * @return a string containing the response content
     */
    public Serializable requestPut(final String id, final Serializable object) throws SDKException {
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            JsonNode fileJSONNode = getJsonNode(object);
            try {
                checkToken();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new SDKException("Could not get token", e);
            }
            // call the api here
            log.debug("Executing put on: " + this.baseUrl + "/" + id);
            if (token != null)
                jsonResponse = Unirest.put(this.baseUrl + "/" + id)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", bearerToken.replaceAll("\"", ""))
                    .body(fileJSONNode)
                    .asJson();
            else
                jsonResponse = Unirest.put(this.baseUrl + "/" + id)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(fileJSONNode)
                    .asJson();

//          check response status
            checkStatus(jsonResponse, HttpURLConnection.HTTP_ACCEPTED);

            // return the response of the request
            return mapper.fromJson(jsonResponse.getBody().toString(), object.getClass());

        } catch (IOException e) {
            // catch request exceptions here
            throw new SDKException("Could not http-put or the api response was wrong or open the object properly", e);
        } catch (UnirestException e) {
            // catch request exceptions here
            throw new SDKException("Could not http-put or the api response was wrong or open the object properly", e);
        } catch (SDKException e) {
            // catch request exceptions here
            if (jsonResponse.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                token = null;
                return requestPut(id, object);
            }
            throw new SDKException("Could not http-put or the api response was wrong or open the object properly", e);
        }
    }

    /**
     * Check wether a json repsonse has the right http status. If not, an SDKException is thrown.
     *
     * @param jsonResponse the http json response
     * @param httpStatus   the (desired) http status of the repsonse
     */
    private void checkStatus(HttpResponse<JsonNode> jsonResponse, final int httpStatus) throws SDKException {
        if (jsonResponse.getStatus() != httpStatus) {
            log.debug("Status expected: " + httpStatus + " obtained: " + jsonResponse.getStatus());
            throw new SDKException("Received wrong API HTTPStatus");
        }
    }

    private void getAccessToken() throws IOException, SDKException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost httpPost = new HttpPost(provider);

        httpPost.setHeader("Authorization", "Basic " + encoding);
        List<BasicNameValuePair> parametersBody = new ArrayList<>();
        parametersBody.add(new BasicNameValuePair("grant_type", "password"));
        parametersBody.add(new BasicNameValuePair("username", this.username));
        parametersBody.add(new BasicNameValuePair("password", this.password));

        log.debug("Username is: " + username);
        log.debug("Password is: " + password);

        httpPost.setEntity(new UrlEncodedFormEntity(parametersBody, StandardCharsets.UTF_8));

        org.apache.http.HttpResponse response = null;
        log.debug("httpPost is: " + httpPost.toString());
        response = httpClient.execute(httpPost);

        String responseString = null;
        responseString = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        log.trace(statusCode + ": " + responseString);

        if (statusCode != 200) {
            ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
            log.error("Status Code [" + statusCode + "]: Error signing-in [" + error.error + "] - " + error.error_description);
            throw new SDKException("Status Code [" + statusCode + "]: Error signing-in [" + error.error + "] - " + error.error_description);
        }
        JsonObject jobj = new Gson().fromJson(responseString, JsonObject.class);
        log.trace("JsonTokeAccess is: " + jobj.toString());
        try {
            String token = jobj.get("value").getAsString();
            log.trace(token);
            bearerToken = "Bearer " + token;
            this.token = token;
        }catch (NullPointerException e){
            String error = jobj.get("error").getAsString();
            if (error.equals("invalid_grant")){
                throw new SDKException("Error during authentication: " + jobj.get("error_description").getAsString(), e);
            }
        }

    }

    private class ParseComError implements Serializable {
        String error_description;
        String error;
    }
}
