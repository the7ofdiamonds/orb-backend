package tech.orbfin.api.gateway.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Validator {
    public static boolean validate(String string, String pattern) {
        try {
            Matcher matcher = Pattern.compile(pattern).matcher(string);
            return matcher.matches();
        } catch (PatternSyntaxException e) {
            // Log or report the details of the exception
            System.err.println("Error compiling regular expression pattern: " + pattern);
            e.printStackTrace();

            // Rethrow the exception with a more descriptive error message
            throw new IllegalArgumentException("Invalid regular expression pattern: " + pattern, e);
        }
    }
}
