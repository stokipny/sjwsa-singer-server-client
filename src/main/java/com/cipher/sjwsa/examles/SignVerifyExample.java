package com.cipher.sjwsa.examles;

import javax.json.JsonArray;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a.stopkipny on 11.07.2017.
 */
public class SignVerifyExample {

    private final static String BASE_SIGN_SERVER_API_URL = "http://localhost:9092/api/v1";

    public static String signTextData(String aTextToSign, boolean isAttached) throws Exception {
        SimpleOperationHelper oh = new SimpleOperationHelper(BASE_SIGN_SERVER_API_URL);
        String uuid = oh.createTicket();
        try {
            oh.uploadTextData(aTextToSign, uuid);
            Map<String, String> options = new HashMap<>();
            if (isAttached) {
                options.put("signatureType", "attached ");
            }
            options.put("embedCertificateType", "signerAndCaCert");
            oh.setOptions(options, uuid);
            oh.createDigitalSign(uuid);
            String ds = oh.getBase64DigitalSign(uuid);
            oh.deleteTicket(uuid);
            return ds;
        } finally {
            oh.deleteTicket(uuid);
        }
    }

    public JsonArray verifyStringDataWithDetachedDS(String aDataToCheck, String aDigitalSign) throws Exception {
        SimpleOperationHelper oh = new SimpleOperationHelper(BASE_SIGN_SERVER_API_URL);
        String uuid = oh.createTicket();
        try {
            uuid = oh.createTicket();
            oh.uploadTextData(aDataToCheck, uuid);
            if (aDigitalSign != null) {
                oh.uploadDsBase64Data(aDigitalSign, uuid);
            } else {
                Map<String, String> options = new HashMap<>();
                options.put("signatureType", "attached");
                oh.setOptions(options, uuid);
            }
            oh.verifyDigitalSign(uuid);
            return oh.getDigitalSignVerifyingResult(uuid);
        } finally {
            oh.deleteTicket(uuid);
        }
    }

    public JsonArray verifyStringDataWithAttachedDS(String aDataToCheck) throws Exception {
        return verifyStringDataWithDetachedDS(aDataToCheck, null);
    }

    public static void main(String[] args) {
        SimpleOperationHelper oh = new SimpleOperationHelper("http://localhost:9092/api/v1");
        try {
            final String textToSign = "111";
            String uuid;
            Map<String, String> options;
            String ds;

            uuid = oh.createTicket();
            oh.uploadTextData(textToSign, uuid);
            options = new HashMap<>();
            options.put("signatureType", "detached");
            options.put("embedCertificateType", "signerAndCaCert");
            oh.setOptions(options, uuid);
            oh.createDigitalSign(uuid);
            ds = oh.getBase64DigitalSign(uuid);
            System.out.println(ds);
            oh.deleteTicket(uuid);

            uuid = oh.createTicket();
            oh.uploadTextData(textToSign, uuid);
            oh.uploadDsBase64Data(ds, uuid);
            options = new HashMap<>();
            options.put("signatureType", "detached");
            oh.setOptions(options, uuid);
            oh.verifyDigitalSign(uuid);
            oh.getDigitalSignVerifyingResult(uuid);
            oh.deleteTicket(uuid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
