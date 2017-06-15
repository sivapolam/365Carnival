package com.techplicit.mycarnival.data;

import android.os.Build;
import android.util.Log;

import com.techplicit.mycarnival.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class ServiceHandler implements Constants {

    private static final String LOG_TAG = ServiceHandler.class.getSimpleName();
    String charset = "UTF-8";
    Object conn;
    DataOutputStream wr;
    StringBuilder result;
    URL urlObj;
    JSONObject jObj = null;
    StringBuilder sbParams;
    String paramsString;
    String responseStatus;
    boolean isSecured;
    public String makeHttpRequest(String url, String method,
                                  HashMap<String, String> params) {
        isSecured = this.isSecuredUrl(url);
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

                if (isSecured) {
                    conn = (HttpsURLConnection) urlObj.openConnection();
                } else {
                    conn = (HttpURLConnection) urlObj.openConnection();
                }

                ((HttpURLConnection)conn).setDoOutput(true);

                ((HttpURLConnection)conn).setRequestMethod("POST");
                ((HttpURLConnection)conn).setRequestProperty("Content-Type", "application/json");
                ((HttpURLConnection)conn).setRequestProperty("Accept-Charset", charset);

                ((HttpURLConnection)conn).setReadTimeout(30000);
                ((HttpURLConnection)conn).setConnectTimeout(35000);

                ((HttpURLConnection)conn).connect();

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
                wr = new DataOutputStream(((HttpURLConnection)conn).getOutputStream());
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

                /*// Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)
                CertificateFactory cf = null;
                try {
                    cf = CertificateFactory.getInstance("X.509");
                } catch (CertificateException e) {
                    e.printStackTrace();
                }
// From https://www.washington.edu/itconnect/security/ca/load-der.crt
                Certificate ca = null;
                String keyStoreType;
                KeyStore keyStore = null;
                String tmfAlgorithm;
                TrustManagerFactory tmf = null;
                SSLContext context = null;
                try (InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"))) {
                    try {
                        try {
                            ca = cf.generateCertificate(caInput);
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        }
                        System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                    } finally {
                        caInput.close();
                    }
                }

// Create a KeyStore containing our trusted CAs
                keyStoreType = KeyStore.getDefaultType();
                try {
                    keyStore = KeyStore.getInstance(keyStoreType);
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
                try {
                    keyStore.load(null, null);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                }
                try {
                    keyStore.setCertificateEntry("ca", ca);
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }

// Create a TrustManager that trusts the CAs in our KeyStore
                tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                try {
                    tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    tmf.init(keyStore);
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }

// Create an SSLContext that uses our TrustManager
                try {
                    context = SSLContext.getInstance("TLS");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    context.init(null, tmf.getTrustManagers(), null);
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }*/

                Log.e("Siva", "url:"+url);
                urlObj = new URL(url);

                if (isSecured) {
                    conn = (HttpsURLConnection) urlObj.openConnection();
                } else {
                    conn = (HttpURLConnection) urlObj.openConnection();
                }
//                this.enableTLSProtocol(conn);
//                conn.setSSLSocketFactory(context.getSocketFactory());

                ((HttpURLConnection)conn).setDoOutput(false);

                ((HttpURLConnection)conn).setRequestMethod("GET");

                ((HttpURLConnection)conn).setRequestProperty("Accept-Charset", charset);

                ((HttpURLConnection)conn).setConnectTimeout(35000);



                ((HttpURLConnection)conn).connect();

            } catch (IOException e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(((HttpURLConnection)conn).getInputStream());
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

        ((HttpURLConnection)conn).disconnect();

        if (responseStatus != null && responseStatus.equalsIgnoreCase(ERROR)) {
            return responseStatus;
        }

        // return JSON Object
        return result.toString();
    }

    private void enableTLSProtocol(HttpsURLConnection httpsConnection) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
            Object e;
            if(Build.VERSION.SDK_INT <= 19) {
                e = new TLSSocketFactory(sslContext.getSocketFactory());
            } else {
                e = sslContext.getSocketFactory();
            }

            httpsConnection.setSSLSocketFactory((SSLSocketFactory)e);
        } catch (NoSuchAlgorithmException var4) {
            Log.e(LOG_TAG, var4.getMessage(), var4);
        } catch (KeyManagementException var5) {
            Log.e(LOG_TAG, var5.getMessage(), var5);
        }

    }

    public String makePostRequest(String url, JSONObject jsonObject){

        HttpsURLConnection conn = null;
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
                conn = (HttpsURLConnection) urlPOST.openConnection();
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

        HttpsURLConnection conn = null;
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
                conn = (HttpsURLConnection) urlPUT.openConnection();
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

    private boolean isSecuredUrl(String url) {
        return url.startsWith("https:") || url.startsWith("HTTPS:");
    }

}