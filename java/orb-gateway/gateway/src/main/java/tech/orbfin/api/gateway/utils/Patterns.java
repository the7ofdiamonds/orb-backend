package tech.orbfin.api.gateway.utils;

public class Patterns {
    public static String EMAIL_PATTERN = "^[a-zA-Z0-9._-]+@[a-z]+\\.[a-zA-Z]+$";
    public static int NAME_MAX_LENGTH = 50;
    public static String NAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
    public static int PASSWORD_MAX_LENGTH = 20;
    public static String PASSWORD_PATTERN = "^[a-zA-Z0-9_-]{6,20}$";
    public static int PHONE_MAX_LENGTH = 20;
    public static String PHONE_PATTERN = "^[0-9]{3,15}$";
    public static int USERNAME_MAX_LENGTH = 20;
    public static String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,100}$";
}