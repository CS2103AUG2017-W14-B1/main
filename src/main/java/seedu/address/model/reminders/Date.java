package seedu.address.model.reminders;

import static java.util.Objects.requireNonNull;

import java.time.DateTimeException;
import java.time.LocalDate;

import seedu.address.commons.exceptions.IllegalValueException;

//@@author justinpoh
/**
 * Represents a Reminder's date in the program.
 * Guarantees: immutable; is valid as declared in {@link #isValidDate(String)}
 */
public class Date {
    public static final String MESSAGE_DATE_CONSTRAINTS = "Date must be in the format dd-mm-yyyy,"
            + " dd/mm/yyyy or dd.mm.yyyy, and must be a valid date.\n"
            + "Example: 22-10-2019, 23.12.1997, 24/12/1989.\n";
    private static final String DATE_VALIDATION_REGEX = "(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[01])[///./-]"
            + "(0[1-9]|1[0-2]|[1-9])[///./-](19|20)[0-9][0-9]";
    private static final String DATE_SPLIT_REGEX = "[///./-]";
    private static final String DATE_SEPARATOR = "-";

    private static final int DATE_DAY_INDEX = 0;
    private static final int DATE_MONTH_INDEX = 1;
    private static final int DATE_YEAR_INDEX = 2;

    public final String value;

    /**
     * Validates given date.
     *
     * @throws IllegalValueException if given date string is invalid.
     */
    public Date(String date) throws IllegalValueException {
        requireNonNull(date);
        if (!isValidDate(date)) {
            throw new IllegalValueException(MESSAGE_DATE_CONSTRAINTS);
        }
        this.value = convertToPresentableForm(date);
    }

    /**
     * Returns true if a given string is a valid reminder date.
     */
    public static boolean isValidDate(String date) {
        if (!date.matches(DATE_VALIDATION_REGEX)) {
            return false;
        }
        String[] splitDate = date.split(DATE_SPLIT_REGEX);

        final int day = Integer.parseInt(splitDate[DATE_DAY_INDEX]);
        final int month = Integer.parseInt(splitDate[DATE_MONTH_INDEX]);
        final int year = Integer.parseInt(splitDate[DATE_YEAR_INDEX]);

        try {
            LocalDate.of(year, month, day);
        } catch (DateTimeException dte) {
            return false;
        }

        return true;
    }

    /**
     * Returns the date as a LocalDate object for easy comparison of chronology between reminders.
     */
    public LocalDate toLocalDate() {
        String[] splitDate = value.split(DATE_SPLIT_REGEX);
        final int year = Integer.parseInt(splitDate[DATE_YEAR_INDEX]);
        final int month = Integer.parseInt(splitDate[DATE_MONTH_INDEX]);
        final int day = Integer.parseInt(splitDate[DATE_DAY_INDEX]);
        return LocalDate.of(year, month, day);
    }

    /**
     * Converts the date String from user into a standard and presentable form: dd-mm-yyyy.
     * @return the converted date.
     */
    private static String convertToPresentableForm(String date) {
        String[] splitDate = date.split(DATE_SPLIT_REGEX);
        StringBuilder builder = new StringBuilder();
        builder.append(splitDate[DATE_DAY_INDEX]);
        builder.append(DATE_SEPARATOR);
        builder.append(splitDate[DATE_MONTH_INDEX]);
        builder.append(DATE_SEPARATOR);
        builder.append(splitDate[DATE_YEAR_INDEX]);
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof Date
                && this.value.equals(((Date) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
