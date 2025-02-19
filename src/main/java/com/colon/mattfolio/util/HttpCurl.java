package com.colon.mattfolio.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.colon.mattfolio.model.common.HttpCurlResponseDto;

/**
 * HTTP 통신을 위한 클래스
 */
public class HttpCurl {

    /**
     * HTTP 통신을 수행하는 메소드
     *
     * @param targetUrl 통신하고자 하는 대상의 URL
     * @param method HTTP 통신에 사용될 메소드
     * @param header Request의 Header에 삽입될 항목, KEY/VALUE 형식의 Hashtable 객체
     * @param body Request의 Body에 삽입될 항목
     * @return HTTP 통신의 결과의 HttpResponse 객체로 반환
     * @exception IOException HTTP 통신 실패 시 해당 예외가 발생
     */
    public HttpCurlResponseDto httpConnection(String targetUrl, String method, Map<String, String> header, String body) throws IOException {
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        OutputStreamWriter streamWriter = null;
        StringBuilder stringBuilder = new StringBuilder();
        String charset = "UTF-8";
        String tMethod = method;

        if (targetUrl == null || targetUrl.isEmpty()) {
            throw new IllegalArgumentException("Parameter targetUrl is null.");
        }

        try {
            url = new URL(targetUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true); // POST, PUT
            conn.setDoInput(true); // GET

            // header 설정
            if (header != null) {
                Iterator<String> headerKeys = header.keySet()
                    .iterator();
                while (headerKeys.hasNext()) {
                    String key = headerKeys.next();
                    conn.setRequestProperty(key, header.get(key));
                }
            }

            if (header == null || header.get("Content-Type") == null) {
                conn.setRequestProperty("Content-Type", "application/json");
            }

            if (header == null || header.get("Accept") == null) {
                conn.setRequestProperty("Accept", "application/json");
            }

            if (header == null || header.get("Cache-Control") == null) {
                conn.setRequestProperty("Cache-Control", "no-cache");
            }

            if (tMethod == null || tMethod.isEmpty()) {
                tMethod = "GET";
            }

            conn.setRequestMethod(tMethod);

            // method가 POST인 경우에만 실행
            // POST인 경우에 대해서만 Request Body에 전송하고자 하는 데이터를 실어서 보낼 수 있음 (HTTP 규약)
            if (tMethod.equalsIgnoreCase("POST") && body != null && !body.isEmpty()) {
                streamWriter = new OutputStreamWriter(conn.getOutputStream(), charset);
                streamWriter.write(body);
                streamWriter.flush();
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
                String tempString = null;
                while ((tempString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(tempString + "\n");
                }
            }

            HttpCurlResponseDto response = new HttpCurlResponseDto();
            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());
            response.setResponseBody(StringUtils.defaultString(stringBuilder.toString()));

            return response;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (streamWriter != null) {
                streamWriter.close();
            }

            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * HTTP 통신을 수행하는 메소드
     *
     * @param targetUrl 통신하고자 하는 대상의 URL
     * @param method HTTP 통신에 사용될 메소드
     * @param header Request의 Header에 삽입될 항목, KEY/VALUE 형식의 Hashtable 객체
     * @param body Request의 Body에 삽입될 항목
     * @param charSet HTTP 통신 시 사용되는 문자 집합
     * @return HTTP 통신의 결과의 HttpCurlResponseDto 객체로 반환
     * @exception IOException HTTP 통신 실패 시 해당 예외가 발생
     */
    public HttpCurlResponseDto httpConnection(String targetUrl, String method, Map<String, String> header, String body, String charSet) throws IOException {
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        OutputStreamWriter streamWriter = null;
        StringBuilder stringBuilder = new StringBuilder();
        String tMethod = method;

        if (targetUrl == null || targetUrl.isEmpty()) {
            throw new IllegalArgumentException("Parameter targetUrl is null.");
        }

        try {
            url = new URL(targetUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true); // POST, PUT
            conn.setDoInput(true); // GET

            // header 설정
            if (header != null) {
                Iterator<String> headerKeys = header.keySet()
                    .iterator();
                while (headerKeys.hasNext()) {
                    String key = headerKeys.next();
                    conn.setRequestProperty(key, header.get(key));
                }
            }

            if (header == null || header.get("Content-Type") == null) {
                conn.setRequestProperty("Content-Type", "application/json");
            }

            if (header == null || header.get("Accept") == null) {
                conn.setRequestProperty("Accept", "application/json");
            }

            if (header == null || header.get("Cache-Control") == null) {
                conn.setRequestProperty("Cache-Control", "no-cache");
            }

            if (tMethod == null || tMethod.isEmpty()) {
                tMethod = "GET";
            }

            conn.setRequestMethod(tMethod);

            // method가 POST인 경우에만 실행
            // POST인 경우에 대해서만 Request Body에 전송하고자 하는 데이터를 실어서 보낼 수 있음 (HTTP 규약)
            if ((tMethod.equalsIgnoreCase("PUT") || tMethod.equalsIgnoreCase("POST")) && body != null && !body.isEmpty()) {
                streamWriter = new OutputStreamWriter(conn.getOutputStream(), charSet);
                streamWriter.write(body);
                streamWriter.flush();
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
                String tempString = null;
                while ((tempString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(tempString + "\n");
                }
            }

            HttpCurlResponseDto httpCurlResponse = new HttpCurlResponseDto();
            httpCurlResponse.setResponseCode(conn.getResponseCode());
            httpCurlResponse.setResponseMessage(conn.getResponseMessage());
            httpCurlResponse.setResponseBody(StringUtils.defaultString(stringBuilder.toString()));

            return httpCurlResponse;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (streamWriter != null) {
                streamWriter.close();
            }

            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}