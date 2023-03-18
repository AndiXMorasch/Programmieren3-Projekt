package hsos.prog3.projektarbeit.bitlocker.logik;

/**
 * The password generator interface for the PasswordGenerator class.
 * More information about the methods can be found in the PasswordGenerator class.
 *
 * @author Andreas Morasch
 * @see PasswordGenerator
 */

public interface PasswordGeneratorInterface {
    String generatePassword(int passwordLength, int specialCharPortion);
}
