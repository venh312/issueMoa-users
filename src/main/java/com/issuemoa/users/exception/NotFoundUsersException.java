package com.issuemoa.users.exception;

public class NotFoundUsersException extends RuntimeException{
    public NotFoundUsersException(String message) {
        super(message);
    }
}
