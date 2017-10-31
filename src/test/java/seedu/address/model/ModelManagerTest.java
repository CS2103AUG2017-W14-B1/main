package seedu.address.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalReminders.getUniqueTypicalReminders;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.commons.events.model.DisplayPictureChangedEvent;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PersonContainsTagPredicate;
import seedu.address.model.reminders.UniqueReminderList;
import seedu.address.model.tag.Tag;
import seedu.address.storage.XmlSerializableReminders;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.ui.testutil.EventsCollectorRule;

public class ModelManagerTest {

    private static final String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/ImageDisplayPicture/");

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        ModelManager modelManager = new ModelManager();
        thrown.expect(UnsupportedOperationException.class);
        modelManager.getFilteredPersonList().remove(0);
    }

    @Test
    public void getBirthdayPanelFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        ModelManager modelManager = new ModelManager();
        thrown.expect(UnsupportedOperationException.class);
        modelManager.getBirthdayPanelFilteredPersonList().remove(0);
    }

    @Test
    public void getSortedReminderList_modifyList_throwsUnsupportedOperationException() {
        ModelManager modelManager = new ModelManager();
        thrown.expect(UnsupportedOperationException.class);
        modelManager.getSortedReminderList().remove(0);
    }

    @Test
    public void addDisplayPicture_eventRaised() throws IOException {
        ModelManager modelManager = new ModelManager();
        modelManager.addDisplayPicture(TEST_DATA_FOLDER + "1137944384.png", 1137944384);
        assertTrue(eventsCollectorRule.eventsCollector.getMostRecent() instanceof DisplayPictureChangedEvent);
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();
        UniqueReminderList uniqueReminders = getUniqueTypicalReminders();

        // same values -> returns true
        ModelManager modelManager = new ModelManager(addressBook, uniqueReminders, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, uniqueReminders, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, uniqueReminders, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, uniqueReminders, userPrefs)));

        // different list of reminders -> return false
        XmlSerializableReminders differentReminders = new XmlSerializableReminders();
        UniqueReminderList uniqueDifferentReminders = new UniqueReminderList(differentReminders);
        assertFalse(modelManager.equals(new ModelManager(addressBook, uniqueDifferentReminders, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different filtered list -> returns false
        Set<Tag> tagsOfBenson = BENSON.getTags();
        final String[] keyword = new String[1];
        tagsOfBenson.stream()
                .findFirst().ifPresent(s -> keyword[0] = s.getTagName());

        modelManager.updateFilteredPersonList(new PersonContainsTagPredicate(keyword[0]));
        assertFalse(modelManager.equals(new ModelManager(addressBook, uniqueReminders, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns true
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookName("differentName");
        assertTrue(modelManager.equals(new ModelManager(addressBook, uniqueReminders, differentUserPrefs)));

    }
}
