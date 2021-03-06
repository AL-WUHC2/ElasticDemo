package org.n3r.core.text;

import java.security.SecureRandom;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomStringUtils;
import org.n3r.core.date4j.DateTime;
import org.n3r.core.joou.ULong;

public class RRand {
    /*
     * Thread-safe. It uses synchronization to protect the integrity of its state.
     * See SecureRandom.nextBytes with synchronized keyword.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    public static boolean randBoolean() {
        return RANDOM.nextBoolean();
    }

    public static double randDouble() {
        return RANDOM.nextDouble();
    }

    public static float randFloat() {
        return RANDOM.nextFloat();
    }

    public static int randInt() {
        return RANDOM.nextInt();
    }

    public static int randInt(int n) {
        return RANDOM.nextInt(n);
    }

    public static long randLong() {
        return RANDOM.nextLong();
    }

    public static String randNum(int count) {
        StringBuilder sb = new StringBuilder(count);
        while (sb.length() < count) {
            sb.append(new ULong(randLong()));
        }

        return sb.replace(count, sb.length(), "").toString();
    }

    public static String randAscii(int count) {
        return RandomStringUtils.random(count, 32, 127, false, false, null, RANDOM);
    }

    public static String randLetters(int count) {
        return RandomStringUtils.random(count, 0, 0, true, false, null, RANDOM);
    }

    public static String randAlphanumeric(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, RANDOM);
    }

    private static final long LAST_DATE_TIME = 2524535999000L;

    public static DateTime randFutureDate() {
        return randDateBetween(DateTime.now(TIME_ZONE), DateTime.forInstant(LAST_DATE_TIME, TIME_ZONE));
    }

    public static DateTime randPastDate() {
        return randDateBetween(DateTime.forInstant(0L, TIME_ZONE), DateTime.now(TIME_ZONE));
    }

    private static final TimeZone TIME_ZONE = TimeZone.getDefault();

    public static DateTime randDateBetween(String start, String end) {
        return randDateBetween(new DateTime(start), new DateTime(end));
    }

    public static DateTime randDateBetween(DateTime start, DateTime end) {
        long startMs = start.getMilliseconds(TIME_ZONE);
        long interval = end.getMilliseconds(TIME_ZONE) - startMs;
        if (interval < 1) return DateTime.forInstant(startMs, TIME_ZONE);

        long increment = randLong() & interval;
        return DateTime.forInstant(startMs + increment, TIME_ZONE);
    }

    private final static char[] CHINESE_LEVELONE = RChinese.levelOne().toCharArray();

    public static String randChinese(int count) {
        return random(count, 0, 0, false, false, CHINESE_LEVELONE, RANDOM);
    }

    public static String rand(int count, String allowedChars) {
        return random(count, 0, 0, false, false, allowedChars.toCharArray(), RANDOM);
    }

    /**
     * <p>Creates a random string based on a variety of options, using
     * supplied source of randomness.</p>
     *
     * <p>If start and end are both {@code 0}, start and end are set
     * to {@code ' '} and {@code 'z'}, the ASCII printable
     * characters, will be used, unless letters and numbers are both
     * {@code false}, in which case, start and end are set to
     * {@code 0} and {@code Integer.MAX_VALUE}.
     *
     * <p>If set is not {@code null}, characters between start and
     * end are chosen.</p>
     *
     * <p>This method accepts a user-supplied {@link Random}
     * instance to use as a source of randomness. By seeding a single
     * {@link Random} instance with a fixed seed and using it for each call,
     * the same random sequence of strings can be generated repeatedly
     * and predictably.</p>
     *
     * @param count  the length of random string to create
     * @param start  the position in set of chars to start at
     * @param end  the position in set of chars to end before
     * @param letters  only allow letters?
     * @param numbers  only allow numbers?
     * @param chars  the set of chars to choose randoms from, must not be empty.
     *  If {@code null}, then it will use the set of all chars.
     * @param random  a source of randomness.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     *  {@code (end - start) + 1} characters in the set array.
     * @throws IllegalArgumentException if {@code count} &lt; 0 or the provided chars array is empty.
     * @since 2.0
     */
    public static String random(int count, int start, int end, boolean letters, boolean numbers,
            char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }

        if (start == 0 && end == 0) {
            if (chars != null) {
                end = chars.length;
            } else {
                if (!letters && !numbers) {
                    end = Integer.MAX_VALUE;
                } else {
                    end = 'z' + 1;
                    start = ' ';
                }
            }
        } else {
            if (end <= start) {
                throw new IllegalArgumentException("Parameter end (" + end + ") must be greater than start (" + start + ")");
            }
        }

        char[] buffer = new char[count];
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if (letters && Character.isLetter(ch)
                    || numbers && Character.isDigit(ch)
                    || !letters && !numbers) {
                if(ch >= 56320 && ch <= 57343) {
                    if(count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if(ch >= 55296 && ch <= 56191) {
                    if(count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if(ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }
}
