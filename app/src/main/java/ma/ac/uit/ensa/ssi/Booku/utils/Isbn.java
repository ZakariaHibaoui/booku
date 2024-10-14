package ma.ac.uit.ensa.ssi.Booku.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Isbn {
    static final Pattern isbn10_pattern = Pattern.compile("^(?:ISBN(?:-10)?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");
    static final Pattern isbn13_pattern = Pattern.compile("^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$");

    public static boolean is_valid(String match) {
        Matcher matcher10 = isbn10_pattern.matcher(match);
        Matcher matcher13 = isbn13_pattern.matcher(match);

        return matcher10.matches() || matcher13.matches();
    }

    public static String formatISBN10(String isbn10) {
        if (isbn10.length() != 10) {
            throw new IllegalArgumentException("ISBN-10 must be 10 digits long.");
        }

        return isbn10.substring(0, 1) + "-" +
                isbn10.substring(1, 4) + "-" +
                isbn10.substring(4, 9) + "-" +
                isbn10.substring(9);
    }

    public static String formatISBN13(String isbn13) {
        if (isbn13.length() != 13) {
            throw new IllegalArgumentException("ISBN-13 must be 13 digits long.");
        }

        return isbn13.substring(0, 3) + "-" +
                isbn13.substring(3, 4) + "-" +
                isbn13.substring(4, 8) + "-" +
                isbn13.substring(8, 12) + "-" +
                isbn13.substring(12);
    }
}
