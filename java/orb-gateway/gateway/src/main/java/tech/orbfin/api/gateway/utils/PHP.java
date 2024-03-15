package tech.orbfin.api.gateway.utils;

import de.ailis.pherialize.Pherialize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PHP {
    private static final String regexInteger = "^([i]):([0-9]+);$";
    private static final String regexDouble = "^([d]):([0-9.]+);$";
    private static final String regexBoolean = "^([b]):([0|1]);$";
    private static final String regexString = "^([s]):([0-9]+):\\\\?\"?(\\w+)\\\\?\"?;?";

    public String unserialize(String sdata) {
        if (sdata == null || sdata.startsWith("N;")) {
            return null;
        }

        return String.valueOf(Pherialize.unserialize(sdata));
    }

    public static Integer uInteger(String sdata) {
        try {
            if (!sdata.startsWith("i:")) {
                return null;
            }

            Pattern pattern = Pattern.compile(regexInteger);
            Matcher matcher = pattern.matcher(sdata);

            String data = null;

            while (matcher.find()) {
                data = matcher.group(2);
            }

            return Integer.parseInt(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Double uDouble(String sdata) {
        try {
            if (!sdata.startsWith("d:")) {
                return null;
            }

            Pattern pattern = Pattern.compile(regexDouble);
            Matcher matcher = pattern.matcher(sdata);

            String data = null;

            while (matcher.find()) {
                data = matcher.group(2);
            }

            return Double.parseDouble(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean uBoolean(String sdata) {
        if (!sdata.startsWith("b:")) {
            return null;
        }

        Pattern pattern = Pattern.compile(regexBoolean);
        Matcher matcher = pattern.matcher(sdata);

        String data = null;

        while (matcher.find()) {
            data = matcher.group(2);
        }

        return data.equals("1");
    }

    public static String uString(int chars, String sdata) {
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(sdata);

        String data = null;

        while (matcher.find()) {
            data = matcher.group(3);
        }

        return data;
    }


}
