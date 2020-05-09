package com.zhize.exception;

public class UserException extends RuntimeException {

    private String id;

    public UserException (String id){
        super("user not exits");
        this.id =id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
