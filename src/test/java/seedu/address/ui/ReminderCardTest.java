package seedu.address.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.ui.testutil.GuiTestAssert.assertReminderCardDisplaysReminder;

import org.junit.Test;

import guitests.guihandles.ReminderCardHandle;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.reminders.DueDate;
import seedu.address.model.reminders.Reminder;
import seedu.address.testutil.ReminderBuilder;

public class ReminderCardTest extends GuiUnitTest {

    @Test
    public void display() {

        Reminder reminder = new ReminderBuilder().build();
        ReminderCard reminderCard = new ReminderCard(reminder, 1);
        uiPartRule.setUiPart(reminderCard);
        assertCardDisplay(reminderCard, reminder, 1);

        // changes made to Person reflects on card
        guiRobot.interact(() -> {
            reminder.setReminder("New reminder");
            try {
                reminder.setDueDate(new DueDate("01/02/1909", "1000"));
            } catch (IllegalValueException ive) {
                throw new AssertionError("Manually written due date values cannot be wrong");
            }
        });
        assertCardDisplay(reminderCard, reminder, 1);
    }

    @Test
    public void equals() {
        Reminder reminder = new ReminderBuilder().build();
        ReminderCard reminderCard = new ReminderCard(reminder, 0);

        // same person, same index -> returns true
        ReminderCard copy = new ReminderCard(reminder, 0);
        assertTrue(reminderCard.equals(copy));

        // same object -> returns true
        assertTrue(reminderCard.equals(reminderCard));

        // null -> returns false
        assertFalse(reminderCard.equals(null));

        // different types -> returns false
        assertFalse(reminderCard.equals(0));

        // different person, same index -> returns false
        Reminder differentReminder = new ReminderBuilder().withReminder("different reminder").build();
        assertFalse(reminderCard.equals(new ReminderCard(differentReminder, 0)));

        // same person, different index -> returns false
        assertFalse(reminderCard.equals(new ReminderCard(reminder, 1)));
    }

    /**
     * Asserts that {@code reminderCard} displays the details of {@code expectedReminder} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(ReminderCard reminderCard, Reminder expectedReminder, int expectedId) {
        guiRobot.pauseForHuman();

        ReminderCardHandle reminderCardHandle = new ReminderCardHandle(reminderCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", reminderCardHandle.getId());

        // verify person details are displayed correctly
        assertReminderCardDisplaysReminder(expectedReminder, reminderCardHandle);
    }
}