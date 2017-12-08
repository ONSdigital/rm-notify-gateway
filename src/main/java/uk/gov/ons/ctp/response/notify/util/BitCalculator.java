package uk.gov.ons.ctp.response.notify.util;

import lombok.ToString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger;

/**
 * This is a simple class to take a gov.notify key or other arbitrary string representing a number and a radix and
 * return the number of bits of data in the key. This is primarily for working out the validity of notify keys.
 * @author innesm
 */
@ToString
public class BitCalculator {

    private static final Pattern KEY_REGEXP = Pattern.compile("^([^-]+)-(.*)$");

    private static final int MINIMUM_KEY_BITS = 256;
    private static final int UUID_BITS = 128;
    private static final int HEX_RADIX = 16;

    /**
     * A bag of stuff holding details of the supplied data.  Could probably boil this down to just bits and valid.
     */
    public static class KeyInfo {
        public final String inputString;
        public final BigInteger value;
        public final String valueStr;
        public final String keyName;
        public final int bits;
        public final int radix;
        public final boolean valid;

        /**
         * Constructor for immutable object
         * @param aInputString String to analyse
         * @param aValue Value represented by input string
         * @param aValueStr Value portion of input string
         * @param aKeyName Name of a key
         * @param aBits Number of bits represented by data in input string
         * @param aRadix Radix of number in input string
         * @param aValid Validity flag
         */
        public KeyInfo(
                final String aInputString,
                final BigInteger aValue,
                final String aValueStr,
                final String aKeyName,
                final int aBits,
                final int aRadix,
                final boolean aValid) {
            this.inputString = aInputString;
            this.value = aValue;
            this.valueStr = aValueStr;
            this.keyName = aKeyName;
            this.bits = aBits;
            this.radix = aRadix;
            this.valid = aValid;
        }
    }

    /**
     * Parses a String into a KeyInfo structure.  If the isKey flag is set the String is a gov.notify key and the text
     * up to the first hyphen is assumed to be the name of the key.  If the isKey flag is not set, the string is a
     * number in base radix with optional hyphens to break it up.
     * @param str string to parse
     * @param isKey key flag to indicate whether the string is a gov.notify key
     * @param radix the radix of the number portion
     * @return a KeyInfo object showing the number of bits of data contained within the number portion of the supplied string
     */
    public KeyInfo analyse(final String str, final boolean isKey, final int radix) {
        KeyInfo info = cleanString(str, isKey, radix);

        if (info.valid) {
            int numDigits = info.valueStr.length();
            int bitsPerDigit = new Double(getBitsPerDigit(radix)).intValue();
            int bits = ((numDigits * bitsPerDigit) >>> radix);
            boolean valid = (isKey == false) || bits >= MINIMUM_KEY_BITS;

            return new KeyInfo(info.inputString, info.value, info.valueStr, info.keyName, bits, info.radix, valid);
        } else {
            return info;
        }
    }

    /**
     * Analyse a number encoded as a hexadecimal string
     * @param hexString A number encoded as a hex string
     * @return an object showing the number of bits of data in the supplied string
     */
    public KeyInfo analyseHexNumber(final String hexString) {
        return analyse(hexString, false, HEX_RADIX);

    }

    /**
     * Analyse a potential UUID. If it's a valid hex number and there are exactly 128 bits, the returned structure
     * will be have valid = true (otherwise false)
     * @param hexString A number encoded as a hex string
     * @return an object showing the number of bits of data in the supplied string and a flag showing whether it's a valid #
     * UUID
     */
    public KeyInfo analyseUUID(final String hexString) {
        final KeyInfo info = analyse(hexString, false, HEX_RADIX);

        if (info.bits != UUID_BITS && info.valid == true) {
            return new KeyInfo(info.inputString, info.value, info.valueStr, info.keyName, info.bits, info.radix, false);
        } else {
            return info;
        }
    }

    /**
     * Analyse a gov.notify key - a gov.notify key is a hex number with an alphanumeric prefix delimited by a hyphen
     * @param key The string of the key
     * @return an object showing the number of bits of data in the supplied key
     */
    public KeyInfo analyseNotifyKey(final String key) {
        return analyse(key, true, HEX_RADIX);
    }

    /**
     * A helper method to parse and tokenise a supplied string.  If it's a key it'll strip of everything to the first
     * hyphen and use it as keyName otherwise it will parse the whole number
     * @param str the string to clean
     * @param isKey does the string represent a gov.notify key?
     * @param radix teh radix for the number
     * @return an object representing the cleaned and tokenised string
     */
    private KeyInfo cleanString(final String str, final boolean isKey, final int radix) {
        final String keyName;
        final String valueStr;

        if (isKey) {
            Matcher matcher = KEY_REGEXP.matcher(str);

            if (matcher.find()) {
                keyName = matcher.group(1);
                valueStr = matcher.group(2);
            } else {
                return new KeyInfo(str, null, null, null, -1, radix, false);
            }
        } else {
            keyName = null;
            valueStr = str;
        }

        if (valueStr != null) {
            String cleanValueStr = valueStr.replaceAll("-", "");

            try {
                BigInteger value = new BigInteger(cleanValueStr, radix);

                return new KeyInfo(str, value, cleanValueStr, keyName, -1, radix,true);
            } catch(NumberFormatException e) {
                return new KeyInfo(str, null, cleanValueStr, keyName, -1, radix, false);
            }
        } else {
            return new KeyInfo(str, null, null, keyName, -1, radix, false);
        }
    }

    /**
     * Method to get the bits per digit for a given radix
     * @param radix The radix to calculate
     * @return bits per digit
     */
    private static double getBitsPerDigit(int radix) {
        return Math.log(radix) / Math.log(2) * Math.pow(2, radix);
    }
}
