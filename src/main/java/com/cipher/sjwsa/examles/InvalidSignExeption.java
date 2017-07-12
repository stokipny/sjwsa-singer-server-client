package com.cipher.sjwsa.examles;

import javax.json.JsonArray;

/**
 * Created by a.stopkipny on 12.07.2017.
 */
public class InvalidSignExeption extends Exception {
    private final JsonArray signerInfo;

    public InvalidSignExeption(String message, JsonArray aSignerInfo) {
        super(message);
        signerInfo = aSignerInfo;
    }

    public JsonArray getSignerInfo() {
        return signerInfo;
    }

}
