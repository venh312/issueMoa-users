package com.issuemoa.users.domain.exception;

public class NotFoundUsersException extends RuntimeException{
    public NotFoundUsersException(String message) {
        super(message);
    }
}
