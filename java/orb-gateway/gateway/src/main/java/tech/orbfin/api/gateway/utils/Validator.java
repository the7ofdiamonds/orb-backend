package tech.orbfin.api.gateway.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static boolean validate(String string, String pattern, int maxLength) {
        if (string.length() > maxLength) {
            return false;
        }

        Matcher matcher = Pattern.compile(pattern).matcher(string);

        return matcher.matches();
    }
}

