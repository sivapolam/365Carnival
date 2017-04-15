package com.techplicit.mycarnival.data;

import com.techplicit.mycarnival.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class ServiceHandler implements Constants {

    String charset = "UTF-8";
    HttpURLConnection conn;
    DataOutputStream wr;
    StringBuilder result;
    URL urlObj;
    JSONObject jObj = null;
    StringBuilder sbParams;
    String paramsString;
    String responseStatus;

    public String makeHttpRequest(String url, String method,
                                  HashMap<String, String> params) {

        sbParams = new StringBuilder();
        int i = 0;

        if (params != null) {
            for (String key : params.keySet()) {
                try {
                    if (i != 0) {
                        sbParams.append("&");
                    }

                    sbParams.append(key).append("=")
                            .append(URLEncoder.encode(params.get(key), charset));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    responseStatus = ERROR;
                }
                i++;
            }
        }


        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept-Charset", charset);

                conn.setReadTimeout(30000);
                conn.setConnectTimeout(35000);

                conn.connect();

                JSONObject object = new JSONObject();

                try {
                    object.put("firstName", "Siva");
                    object.put("lastName", "Polam");
                    object.put("email", "test@gmail.com");
                    object.put("image", "Siva.jpg");
                    object.put("userAccessToken", "userAccessToken");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                paramsString = "" + object.toString();
                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(URLEncoder.encode(paramsString.toString(),"UTF-8"));
                wr.flush();
                wr.close();

            } catch (IOException e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

        } else if (method.equals("GET")) {
            // request method is GET

            if (params != null) {
                if (sbParams.length() != 0) {
                    url += "?" + sbParams.toString();
                }
            }


            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(false);

                conn.setRequestMethod("GET");

                conn.setRequestProperty("Accept-Charset", charset);

                conn.setConnectTimeout(35000);

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            responseStatus = ERROR;
        }

        conn.disconnect();

        if (responseStatus != null && responseStatus.equalsIgnoreCase(ERROR)) {
            return responseStatus;
        }

        // return JSON Object
        return result.toString();
    }

    public String makePostRequest(String url, JSONObject jsonObject){

        HttpURLConnection conn = null;
        OutputStream os = null;
        String message = null;
        //can catch a variety of wonderful things
        try {
            //constants
            URL urlPOST = null;
            try {
                urlPOST = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (jsonObject != null) {
                message = jsonObject.toString();
            }

            try {
                conn = (HttpURLConnection) urlPOST.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            conn.setReadTimeout(30000 /*milliseconds*/);
            conn.setConnectTimeout(35000 /* milliseconds */);
            try {
                conn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);
            conn.setRequestProperty("Connection", "close");

            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
//                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //open
            try {
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //setup send
            try {
                os = new BufferedOutputStream(conn.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //clean up
            try {
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } finally {
            //clean up
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        StringBuilder result;
        try {
            //Receive the response from the server
            InputStream is = conn.getInputStream();
            InputStream in = new BufferedInputStream(is);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            responseStatus = result.toString();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            responseStatus = ERROR;
        }

        return responseStatus;
    }


    public String makePUTRequest(String url, JSONObject jsonObject){

        HttpURLConnection conn = null;
        OutputStream os = null;
        String message = null;
        //can catch a variety of wonderful things
        try {
            //constants
            URL urlPUT = null;
            try {
                urlPUT = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (jsonObject != null) {
                message = jsonObject.toString();
            }

            try {
                conn = (HttpURLConnection) urlPUT.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            conn.setReadTimeout(30000 /*milliseconds*/);
            conn.setConnectTimeout(35000 /* milliseconds */);
            try {
                conn.setRequestMethod("PUT");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);

            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
//                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //open
            try {
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //setup send
            try {
                os = new BufferedOutputStream(conn.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //clean up
            try {
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //do somehting with response
//                    is = conn.getInputStream();
//                    String contentAsString = readIt(is,len);
        } finally {
            //clean up
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

//                    is.close();
//                    conn.disconnect();
        }

        StringBuilder result;
        try {
            //Receive the response from the server
            InputStream is = conn.getInputStream();
            InputStream in = new BufferedInputStream(is);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            responseStatus = result.toString();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            responseStatus = ERROR;
        }

        return responseStatus;
    }

}