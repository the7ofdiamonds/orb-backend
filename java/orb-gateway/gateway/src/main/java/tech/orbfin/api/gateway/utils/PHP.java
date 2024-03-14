package tech.orbfin.api.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PHP {
    private final String regexInteger = "^([i]):([0-9]+);$";
    private final String regexDouble = "^([d]):([0-9]+).([0-9]+);$";
    private final String regexBoolean = "^([b]):([0|1]);$";
    private final String regexString = "^([s]):([0-9]+):(.+);$";
    private final String regexArray = "^([a]):([0-9]+):\\{(.+)\\}$";
    private final String regexObject = "^([O]):([0-9]+):\\{(.+)\\}$";
    private Collection<String> delimiters = new ArrayList<>();

    public PHP() {
        delimiters.add(regexInteger);
        delimiters.add(regexDouble);
        delimiters.add(regexBoolean);
        delimiters.add(regexString);
        delimiters.add(regexArray);
        delimiters.add(regexObject);
    }

    public <T> T unserialize(String sdata) {
        if (sdata == null || sdata.startsWith("N;")) {
            return null;
        }

        for (String delimiter : delimiters) {
            Pattern pattern = Pattern.compile(delimiter);
            Matcher matcher = pattern.matcher(sdata);

            while (matcher.find()) {

                if (matcher.group(1).equals("i")) {
                    return (T) uInteger(sdata);
                }

                if (matcher.group(1).equals("d")) {
                    return (T) uDouble(sdata);
                }

                if (matcher.group(1).equals("b")) {
                    return (T) uBoolean(sdata);
                }

                if (matcher.group(1).equals("s")) {
                    return (T) uString(Integer.valueOf(matcher.group(2)), matcher.group(3));
                }

                if (matcher.group(1).equals("a")) {
                    return (T) uArray(Integer.valueOf(matcher.group(2)), matcher.group(3));
                }

                if (matcher.group(1).equals("O")) {
                    return (T) uObject(Integer.valueOf(matcher.group(2)), matcher.group(3));
                }
            }
        }

        return null;
    }

    public Integer uInteger(String sdata) {
        try {
            if (!sdata.startsWith("i:")) {
                return null;
            }

            int fSemiIndex = sdata.indexOf(';');
            String integerValue = sdata.substring(2, fSemiIndex);

            return Integer.parseInt(integerValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double uDouble(String sdata) {
        try {
            if (!sdata.startsWith("d:")) {
                return null;
            }

            int fSemiIndex = sdata.indexOf(';');
            String doubleValue = sdata.substring(2, fSemiIndex);

            return Double.parseDouble(doubleValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean uBoolean(String sdata) {
        if (!sdata.startsWith("b:")) {
            return null;
        }

        if (sdata.equals("b:1;")) {
            return true;
        }

        if (sdata.equals("b:0;")) {
            return false;
        }

        return null;
    }

    private static String uString(int chars, String sdata) {
        try {
            Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)");
            Matcher matcher = pattern.matcher(sdata);

            StringBuilder dataBuilder = new StringBuilder();

            while (matcher.find()) {
                dataBuilder.append(matcher.group());
            }

            String data = dataBuilder.toString();
            int dataLength = data.length();

            if (chars != dataLength) {
                throw new IllegalArgumentException("Number of characters expected " + chars + " does not match the length of the extracted string " + dataLength + ".");
            }

            return data;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> T uArray(int items, String sdata) {

        Collection<T> array = new ArrayList<>(items);
log.info(String.valueOf(sdata));
        return (T) array;
    }

    private <T> T uObject(int chars, String sdata) {
        Object object = new Object();
        return null;
    }
}
