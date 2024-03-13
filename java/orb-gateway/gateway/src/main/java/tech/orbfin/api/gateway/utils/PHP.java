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

        if (sdata.equals("i:") && scIndex == -1 && fbIndex == -1 && lbIndex == -1) {
            return (T) uInteger(sdata);
        }

        if (sdata.equals("d:") && scIndex == -1 && fbIndex == -1 && lbIndex == -1) {
            return (T) uDouble(sdata);
        }

        if (sdata.equals("b:") && sdataLength == 3) {
            return (T) uBoolean(sdata);
        }

        if (sdata.equals("s:") && scIndex != -1 && fbIndex == -1 && lbIndex == -1) {
            return (T) uString(sdata);
        }

        if (sdata.startsWith("a:")) {
            return (T) uArray(sdata);
        }

        if (sdata.startsWith("O:")) {
            return (T) uObject(sdata);
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

    public static String uString(String sdata) {
        int fColIndex = sdata.indexOf(':');
        int sColIndex = sdata.indexOf(':', fColIndex +1);
        int chars = Integer.parseInt(sdata.substring(fColIndex + 1, sColIndex));
        int fSemiIndex = sdata.indexOf(';');

        String data = sdata.substring(sColIndex + 2, fSemiIndex - 1); // Adjust substring indices to exclude quotes

        int dataLength = data.length();

        Pattern pattern = Pattern.compile("[^a-z]");
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            System.out.println("Invalid characters found in the extracted data.");
        }

        if (chars != dataLength){
            System.out.println("There has been an error number of characters expected " + chars + " and length of string "+ dataLength + " should be the same.");
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

    private <T> T uArray(String sdata) {
        int sdataLength = sdata.length();
        int fbIndex = sdata.indexOf('{');
        int lbIndex = sdata.indexOf('}');
        int fcIndex = sdata.indexOf(':');
        int scIndex = sdata.indexOf(':', fcIndex + 1);

        if (sdata.startsWith("a:")) {
            int items = Integer.parseInt(sdata.substring(fcIndex + 1, scIndex));
            String data = sdata.substring(fbIndex + 1, lbIndex);
            log.info(String.valueOf(items));

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

        return null;
    }

    private <T> T uObject(String sdata) {
        int sdataLength = sdata.length();
        int fbIndex = sdata.indexOf('{');
        int lbIndex = sdata.indexOf('}');
        int fcIndex = sdata.indexOf(':');
        int scIndex = sdata.indexOf(':', fcIndex + 1);

        if (sdata.startsWith("O:")) {
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
}
