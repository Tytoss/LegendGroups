package de.tytoss.core.utils;

public class DurationParser {
    public static long parseDuration(String input) {
        long total = 0;

        StringBuilder number = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                long value = Long.parseLong(number.toString());
                switch (c) {
                    case 'd' -> total += value * 24 * 60 * 60 * 1000L;
                    case 'h' -> total += value * 60 * 60 * 1000L;
                    case 'm' -> total += value * 60 * 1000L;
                    case 's' -> total += value * 1000L;
                }
                number = new StringBuilder();
            }
        }

        return total;
    }
}
