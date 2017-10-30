package seedu.address.model.person;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import seedu.address.testutil.PersonBuilder;

//@@author justinpoh
public class BirthdayInCurrentMonthPredicateTest {

    private final int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    private final int notCurrentMonth = Calendar.getInstance().get(Calendar.MONTH);

    @Test
    public void equals() {
        BirthdayInCurrentMonthPredicate firstPredicate = new BirthdayInCurrentMonthPredicate();
        BirthdayInCurrentMonthPredicate secondPredicate = new BirthdayInCurrentMonthPredicate(currentMonth);
        BirthdayInCurrentMonthPredicate thirdPredicate = new BirthdayInCurrentMonthPredicate(notCurrentMonth);

        // same object -> return true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same value -> return true
        assertTrue(firstPredicate.equals(secondPredicate));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different month -> returns false
        assertFalse(firstPredicate.equals(thirdPredicate));
    }

    @Test
    public void test_personContainsBirthdayCorrectMonth_returnsTrue() {
        // testing for current month
        BirthdayInCurrentMonthPredicate predicateCurrentMonth = new BirthdayInCurrentMonthPredicate();
        final String birthdayInCurrentMonth = "01/" + currentMonth + "/1990";
        assertTrue(predicateCurrentMonth.test(new PersonBuilder().withBirthday(birthdayInCurrentMonth).build()));

        // testing for correct month, but not current month
        BirthdayInCurrentMonthPredicate predicateNotCurrentMonth = new BirthdayInCurrentMonthPredicate(notCurrentMonth);
        final String birthdayNotInCurrentMonth = "01/" + notCurrentMonth + "/1990";
        assertTrue(predicateNotCurrentMonth.test(new PersonBuilder().withBirthday(birthdayNotInCurrentMonth).build()));
    }

    @Test
    public void test_personContainsBirthdayWrongMonth_returnsFalse() {
        // use current month as comparison
        BirthdayInCurrentMonthPredicate predicateCurrentMonth = new BirthdayInCurrentMonthPredicate();
        final String birthdayNotInCurrentMonth = "01/" + notCurrentMonth + "/1990";
        assertFalse(predicateCurrentMonth.test(new PersonBuilder().withBirthday(birthdayNotInCurrentMonth).build()));

        // use non-current month as comparison
        BirthdayInCurrentMonthPredicate predicateNotCurrentMonth = new BirthdayInCurrentMonthPredicate(notCurrentMonth);
        final String birthdayInCurrentMonth = "01/" + currentMonth + "/1990";
        assertFalse(predicateNotCurrentMonth.test(new PersonBuilder().withBirthday(birthdayInCurrentMonth).build()));
    }

    @Test
    public void test_personContainsNoBirthday_returnsFalse() {
        BirthdayInCurrentMonthPredicate predicate = new BirthdayInCurrentMonthPredicate();
        assertFalse(predicate.test(new PersonBuilder().withBirthday("").build()));
    }
}
