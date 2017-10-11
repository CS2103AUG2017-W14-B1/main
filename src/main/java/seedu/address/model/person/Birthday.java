package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.exceptions.IllegalValueException;
import sun.jvm.hotspot.utilities.AssertionFailure;

/**
 * Represents a Person's birthday in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidBirthday(String)}
 */
public class Birthday {

    public static final String MESSAGE_BIRTHDAY_CONSTRAINTS = "Birthday must be in the following format: "
            + "dd/mm/yyyy, dd.mm.yyyy or dd-mm-yyyy.\n"
            + "Leading zeroes are allowed for the day and month field. The year field must have 4 digits.\n"
            + "Example: 21/10/1995, 21-05-1996. 8.10.1987";
    private static final int[] MONTH_TO_DAY_MAPPING = {31, 28, 31, 30, 31, 30, 31, 31,
        30, 31, 30, 31};

    private static final String EMPTY_STRING = "";

    private static final String BIRTHDAY_DASH_SEPARATOR = "-";

    private static final int ZERO_BASED_ADJUSTMENT = 1;

    private static final int SMALLEST_POSSIBLE_DAY = 1;

    private static final int LEAP_YEAR_MONTH_FEBRUARY = 2;
    private static final int LEAP_YEAR_DAY = 29;
    private static final int LEAP_YEAR_REQUIREMENT_FIRST = 4;
    private static final int LEAP_YEAR_REQUIREMENT_SECOND = 100;
    private static final int LEAP_YEAR_REQUIREMENT_THIRD = 400;

    private static final int DATE_DAY_INDEX = 0;
    private static final int DATE_MONTH_INDEX = 1;
    private static final int DATE_YEAR_INDEX = 2;

    private static final String BIRTHDAY_VALIDATION_REGEX = "(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[01])[///./-]"
            + "(0[1-9]|1[0-2]|[1-9])[///./-](19|20)[0-9][0-9]";
    private static final String BIRTHDAY_SPLIT_REGEX = "[///./-]";

    public final String value;

    /**
     * Validates the given birthday.
     *
     * @throws IllegalValueException if given birthday string is invalid.
     */
    public Birthday(String birthday) throws IllegalValueException {
        requireNonNull(birthday);

        if (!isValidBirthday(birthday)) {
            throw new IllegalValueException(MESSAGE_BIRTHDAY_CONSTRAINTS);
        }
        String trimmedBirthday = birthday.trim();
        if (trimmedBirthday.isEmpty()) {
            this.value = EMPTY_STRING;
        } else {
            this.value = convertToDefaultDateFormat(birthday);
        }
    }

    public static boolean isValidBirthday(String birthday) {
        String trimmedBirthday = birthday.trim();
        if (trimmedBirthday.isEmpty()) {
            return true;
        }
        if (!trimmedBirthday.matches(BIRTHDAY_VALIDATION_REGEX)) {
            return false;
        }

        String[] splitDate = getSplitDate(trimmedBirthday);

        if (!isValidDate(splitDate)) {
            return false;
        }
        return true;
    }

    private static boolean isValidDate(String[] splitDate) {
        if (isValidLeapDay(splitDate)) {
            return true;
        }

        try {
            final int day = Integer.parseInt(splitDate[DATE_DAY_INDEX]);
            final int month = Integer.parseInt(splitDate[DATE_MONTH_INDEX]);
            final int dayUpperLimitForMonth = MONTH_TO_DAY_MAPPING[month - ZERO_BASED_ADJUSTMENT];
            if (day < SMALLEST_POSSIBLE_DAY || day > dayUpperLimitForMonth) {
                return false;
            }
        } catch (NumberFormatException nfe) {
            throw new AssertionError("Not possible as birthday has passed through the regex");
        }

        return true;
    }

    private static boolean isValidLeapDay(String[] splitDate) {
        try {
            final int day = Integer.parseInt(splitDate[DATE_DAY_INDEX]);
            final int month = Integer.parseInt(splitDate[DATE_MONTH_INDEX]);
            final int year = Integer.parseInt(splitDate[DATE_YEAR_INDEX]);
            if (!isLeapYear(year) || day != LEAP_YEAR_DAY || month != LEAP_YEAR_MONTH_FEBRUARY) {
                return false;
            }
        } catch (NumberFormatException nfe) {
            throw new AssertionError("Not possible as birthday has passed through the regex");
        }
        return true;
    }


    private static String[] getSplitDate(String trimmedBirthday) {
       return trimmedBirthday.split(BIRTHDAY_SPLIT_REGEX);
    }

    /**
     * Converts date in integer array into dd-mm-yyyy String format.
     */
    private static String convertToDefaultDateFormat(String birthday) {
        String[] splitDate = getSplitDate(birthday);

        StringBuilder builder = new StringBuilder();
        builder.append(splitDate[DATE_DAY_INDEX]);
        builder.append(BIRTHDAY_DASH_SEPARATOR);
        builder.append(splitDate[DATE_MONTH_INDEX]);
        builder.append(BIRTHDAY_DASH_SEPARATOR);
        builder.append(splitDate[DATE_YEAR_INDEX]);
        return builder.toString();
    }


    /**
     * Returns true if the year is a valid leap year.
     * Algorithm to determine is a year is a valid leap year:
     * https://support.microsoft.com/en-us/help/214019/method-to-determine-whether-a-year-is-a-leap-year
     */
    private static boolean isLeapYear(int year) {
        if (year % LEAP_YEAR_REQUIREMENT_FIRST == 0 && year % LEAP_YEAR_REQUIREMENT_SECOND != 0) {
            return true;
        } else if (year % LEAP_YEAR_REQUIREMENT_FIRST == 0 && year % LEAP_YEAR_REQUIREMENT_SECOND == 0
                && year % LEAP_YEAR_REQUIREMENT_THIRD == 0) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof Birthday
                && this.value.equals(((Birthday) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
