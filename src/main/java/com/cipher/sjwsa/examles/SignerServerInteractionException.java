package com.cipher.sjwsa.examles;

/**
 * Created by a.stopkipny on 11.07.2017.
 */
public class SignerServerInteractionException extends Exception {
    public SignerServerInteractionException() {
    }

    public SignerServerInteractionException(String message) {
        super(message);
    }

    public SignerServerInteractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignerServerInteractionException(Throwable cause) {
        super(cause);
    }
}
