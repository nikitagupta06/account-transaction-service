package com.dws.challenge.exception;

public class TransactionFailedException extends Exception{
    public TransactionFailedException(String message) {
        super(message);
    }
}
