package com.cipher.sjwsa.examles;

import javax.json.JsonArray;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a.stopkipny on 11.07.2017.
 */
public class SignVerifyExample {

    private final static String BASE_SIGN_SERVER_API_URL = "http://localhost:9092/api/v1";

    public static void main(String[] args) {
        SimpleOperationHelper oh = new SimpleOperationHelper(BASE_SIGN_SERVER_API_URL);
        try {
            final String textToSign = "111";
            String uuid;
            Map<String, String> options;
            String ds;

            // ------- Создание ЭЦП для текстовых данных
            // Создание сессии
            uuid = oh.createTicket();
            try {
                // Загрузка данных сессии
                oh.uploadTextData(textToSign, uuid);
                // Установка параметров сессии
                options = new HashMap<>();
                options.put("signatureType", "detached");
                options.put("embedCertificateType", "signerAndCaCert");
                oh.setOptions(options, uuid);
                // Создание ЭЦП
                oh.createDigitalSign(uuid);
                // Получение данных ЭЦП
                ds = oh.getBase64DigitalSign(uuid);
            } finally {
                // Удаление сесии
                oh.deleteTicket(uuid);
            }

            // ------- Проверка ЭЦП
            // Создание сессии
            uuid = oh.createTicket();
            try {
                // Загрузка данных сессии
                oh.uploadTextData(textToSign, uuid);
                // Загрузка данных ЭЦП
                oh.uploadDsBase64Data(ds, uuid);
                // Установка параметров сессии
                options = new HashMap<>();
                options.put("signatureType", "detached");
                oh.setOptions(options, uuid);
                // Проверка ЭЦП
                oh.verifyDigitalSign(uuid);
                // Получение результата проверки ЭЦП
                JsonArray jar = oh.getDigitalSignVerifyingResult(uuid);
                System.out.println("Цифровая подпись действительна.");
                System.out.println("Информация о подписантах: ");
                System.out.println(jar);
            } catch (InvalidSignExeption e) {
                System.out.println(e.getMessage());
                System.out.println("Информация о подписантах: ");
                System.out.println(e.getSignerInfo());
            } finally {
                // Удаление сесии
                oh.deleteTicket(uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
