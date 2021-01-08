package com.mynews.common.core.browser.agent;

import androidx.collection.ArrayMap;

import java.util.Map;


public  class HttpHeaders {


    public static HttpHeaders create(){
       return new HttpHeaders();
    }

    private Map<String,String> headers=null;

    HttpHeaders(){
        headers= new ArrayMap<>();
    }

    public Map<String,String> getHeaders(){
        return headers;
    }

    public void additionalHttpHeader(String k, String v){
        headers.put(k,v);
    }
    public void removeHttpHeader(String k){
        headers.remove(k);
    }

    public boolean isEmptyHeaders(){
        return headers==null||headers.isEmpty();
    }


    @Override
    public String toString() {
        return "HttpHeaders{" +
                "headers=" + headers +
                '}';
    }
}
