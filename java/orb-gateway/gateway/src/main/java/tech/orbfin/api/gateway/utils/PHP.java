package tech.orbfin.api.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PHP {
    public <T> T unserialize(String sdata) {
        if (sdata == null) {
            return null;
        }

        int sdataLength = sdata.length();
        int fbIndex = sdata.indexOf('{');
        int lbIndex = sdata.indexOf('}');
        int fcIndex = sdata.indexOf(':');
        int scIndex = sdata.indexOf(':', fcIndex + 1);

        if (sdata.startsWith("N;") && scIndex == -1) {
            log.info(null);
            return null;
        }

        if (sdata.equals("i:") && scIndex == -1) {
            return (T) uInteger(sdata);
        }

        if (sdata.equals("d:") && scIndex == -1) {
            return (T) uFloat(sdata);
        }

        if (sdata.equals("b:") && sdataLength == 3) {
            return (T) uBoolean(sdata);
        }

        if (sdata.equals("s:") && scIndex != -1) {
            return (T) uString(sdata);
        }

        if (sdata.startsWith("a:") || sdata.startsWith("O:")) {
            int items = Integer.parseInt(sdata.substring(fcIndex + 1, scIndex));
            String data = sdata.substring(fbIndex + 1, lbIndex);
            log.info(String.valueOf(items));
            if (sdata.startsWith("a:")) {
                Collection<T> array = new ArrayList<>(items);

                if (items > 1) {
                    List<String> newDataList = splitOutsideCurlyBraces(data, ";{");
                    var newDataListSize = newDataList.size();

                    for (String newData : newDataList) {
                        if (data.startsWith("s:")) {
                            var s = (T) uString(newData);
                            array.add(s);
                        }
                    }

                    log.info(String.valueOf(newDataListSize));
                }

                return (T) array;
            }
        }

        return null;
    }

    private Integer uInteger(String sdata) {
        if (sdata.startsWith("i:")) {
            String integerValue = sdata.substring(2);
            try {
                return Integer.parseInt(integerValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    private Double uFloat(String sdata) {
        if (sdata.startsWith("d:")) {
            String floatValue = sdata.substring(2);
            try {
                return Double.parseDouble(floatValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    private Boolean uBoolean(String sdata) {
        if (sdata.equals("b:1")) {
            return true;
        }

        if (sdata.equals("b:0")) {
            return false;
        }

        return null;
    }

    private String uString(String sdata) {
        int fcIndex = sdata.indexOf(':');
        int scIndex = sdata.indexOf(':', fcIndex + 1);
        int chars = Integer.parseInt(sdata.substring(fcIndex + 1, scIndex));
        int fComaIndex = sdata.indexOf('"');
        int lComaIndex = sdata.lastIndexOf('"');
        String data = sdata.substring(fComaIndex, lComaIndex);
        int dataLength = data.length();
        log.info(String.valueOf(chars));
        log.info(String.valueOf(dataLength));
        if (chars != dataLength) {
            log.info("true");
        }

        return data;
    }

    public static List<String> splitOutsideCurlyBraces(String input, String delimiter) {
        List<String> parts = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?![^{]*\\})(?=" + Pattern.quote(delimiter) + ")");
        Matcher matcher = pattern.matcher(input);
        int start = 0;
        while (matcher.find()) {
            String part = input.substring(start, matcher.end());
            log.info(part.trim());
            parts.add(part.trim());
            start = matcher.end();
        }
        if (start < input.length()) {
            parts.add(input.substring(start).trim());
        }
        return parts;
    }
}
