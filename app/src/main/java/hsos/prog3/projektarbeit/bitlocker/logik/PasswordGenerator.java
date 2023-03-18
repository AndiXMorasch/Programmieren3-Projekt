package hsos.prog3.projektarbeit.bitlocker.logik;

import java.util.Random;

/**
 * The PasswordGenerator class implements the PasswordGeneratorInterface and its purpose is to generate
 * certain passwords based on given parameters.
 *
 * @author Andreas Morasch
 */

public class PasswordGenerator implements PasswordGeneratorInterface {

    private static final String upperLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lowerLetters = "abcdefghijklmnopqrstuvwxyz";
    private static final String digits = "0123456789";
    private static final String specialCharacters = "!\"'§$%&/()=?.,-_:;*#<>@+|[{]";

    /**
     * This method generates a random password based on a password length and a special char portion in percent.
     *
     * @param passwordLength     password length
     * @param specialCharPortion special character portion in percent
     * @return random password that meets the above mentioned criteria
     */

    public String generatePassword(int passwordLength, int specialCharPortion) {

        int specialCharNumber = (passwordLength * specialCharPortion) / 100;
        int specialCharRatio = passwordLength / specialCharNumber;

        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 1; i < passwordLength + 1; i++) {
            int decider = random.nextInt(3);
            if (i % specialCharRatio == 0) {
                decider = 3;
            }

            char charValue;

            if (decider == 0) {
                int column = random.nextInt(upperLetters.length());
                charValue = upperLetters.charAt(column);
            } else if (decider == 1) {
                int column = random.nextInt(lowerLetters.length());
                charValue = lowerLetters.charAt(column);
            } else if (decider == 2) {
                int column = random.nextInt(digits.length());
                charValue = digits.charAt(column);
            } else {
                int column = random.nextInt(specialCharacters.length());
                charValue = specialCharacters.charAt(column);
            }

            password.append(charValue);
        }
        return password.toString();
    }
}
