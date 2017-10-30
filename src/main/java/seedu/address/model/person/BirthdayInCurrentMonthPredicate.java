package seedu.address.model.person;

import java.util.Calendar;
import java.util.function.Predicate;

//@@author justinpoh
/**
 * Tests that a {@code ReadOnlyPerson}'s birthday month is the current month.
 * If a {@code ReadOnlyPerson} does not have a birthday recorded, return false.
 */
public class BirthdayInCurrentMonthPredicate implements Predicate<ReadOnlyPerson> {
    private final int currentMonth;

    public BirthdayInCurrentMonthPredicate() {
        // Added 1 because Calendar.getInstance().get(Calendar.MONTH) is zero-based.
        this.currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * This constructor is made for the purpose of testing this predicate class.
     * It also might have further use in filtering the birthdays in the future.
     */
    public BirthdayInCurrentMonthPredicate(int monthToFilter) {
        this.currentMonth = monthToFilter;
    }

    @Override
    public boolean test(ReadOnlyPerson person) {
        final int month = person.getBirthday().getMonthOfBirthday();
        if (month == Birthday.EMPTY_BIRTHDAY_FIELD_MONTH) {
            return false;
        }
        return currentMonth == month;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof BirthdayInCurrentMonthPredicate // instanceof handles nulls
                && this.currentMonth == ((BirthdayInCurrentMonthPredicate) other).currentMonth); // state check
    }
}
