package tech.orbfin.api.gateway.utils;

public class Patterns {
    public static String EMAIL_PATTERN = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z]+$";
    public static int NAME_MAX_LENGTH = 50;
    public static String NAME_PATTERN = "^[a-zA-Z]{1,20}$";
    public static int PASSWORD_MAX_LENGTH = 20;
    public static String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%&+!])(?!.*\\s)(?=.*[0-9a-zA-Z@#$%&+!]).{8,}$";
    public static int PHONE_MAX_LENGTH = 20;
    public static String PHONE_PATTERN = "^[+][0-9]{3,15}$";
    public static int USERNAME_MAX_LENGTH = 20;
    public static String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,20}$";
}