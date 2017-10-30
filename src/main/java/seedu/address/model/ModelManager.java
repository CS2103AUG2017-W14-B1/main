package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.RemindersChangedEvent;
import seedu.address.commons.events.ui.SendingEmailEvent;
import seedu.address.commons.events.ui.ShowLocationEvent;
import seedu.address.model.email.Body;
import seedu.address.model.email.Service;
import seedu.address.model.email.Subject;
import seedu.address.model.person.BirthdayInCurrentMonthPredicate;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.reminders.Reminder;
import seedu.address.model.reminders.UniqueReminderList;
import seedu.address.model.reminders.exceptions.DuplicateReminderException;
import seedu.address.model.reminders.exceptions.ReminderNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * Represents the in-memory model of the address book data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final FilteredList<ReadOnlyPerson> filteredPersons;
    private final FilteredList<ReadOnlyPerson> filteredPersonsForBirthdayListPanel;
    private final FilteredList<ReadOnlyPerson> filteredPersonsForEmail;
    private final UniqueReminderList reminderList;

    private SortedList<ReadOnlyPerson> sortedfilteredPersons;
    private SortedList<ReadOnlyPerson> sortedFilteredPersonsForBirthdayListPanel;
    private SortedList<Reminder> sortedReminderList;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UniqueReminderList reminders, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, reminders, userPrefs);

        logger.fine("Initializing with address book: " + addressBook +  " and reminders " + reminders
                + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.reminderList = new UniqueReminderList(reminders);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        //@@author justinpoh
        filteredPersonsForBirthdayListPanel = new FilteredList<>(this.addressBook.getPersonList());
        filteredPersonsForBirthdayListPanel.setPredicate(new BirthdayInCurrentMonthPredicate());
        //@@author
        filteredPersonsForEmail = new FilteredList<>(this.addressBook.getPersonList());
        sortedfilteredPersons = new SortedList<>(filteredPersons);
        //@@author justinpoh
        sortedFilteredPersonsForBirthdayListPanel = new SortedList<>(filteredPersonsForBirthdayListPanel,
                Comparator.comparingInt(birthday -> birthday.getBirthday().getDayOfBirthday()));
        sortedReminderList = new SortedList<>(reminderList.asObservableList(),
                Comparator.comparing(reminder -> reminder.getLocalDateTime()));
        //@@author

    }

    public ModelManager() {
        this(new AddressBook(), new UniqueReminderList(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        addressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(addressBook));
    }

    //@@author justinpoh
    /** Raises an event to indicate the reminders have changed */
    private void indicateRemindersChanged() {
        raise(new RemindersChangedEvent(reminderList));
    }
    //@@author

    @Override
    public synchronized void deletePerson(ReadOnlyPerson target) throws PersonNotFoundException {
        addressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    @Override
    public void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireAllNonNull(target, editedPerson);

        addressBook.updatePerson(target, editedPerson);
        indicateAddressBookChanged();
    }

    @Override
    public void deleteTag(Tag tagToRemove) throws DuplicatePersonException, PersonNotFoundException {
        for (int i = 0; i < addressBook.getPersonList().size(); i++) {
            ReadOnlyPerson originalPerson = addressBook.getPersonList().get(i);

            Person personWithTagRemoved = new Person(originalPerson);
            Set<Tag> updatedTags = personWithTagRemoved.getTags();
            updatedTags.remove(tagToRemove);

            addressBook.updatePerson(originalPerson, personWithTagRemoved);
        }


    }

    /**
     * Shows location of the given person
     */
    @Override
    public void showLocation(ReadOnlyPerson person) throws PersonNotFoundException {
        raise(new ShowLocationEvent(person));
    }

    /**
     * Raises event for processing Email
     */
    @Override
    public void processEmailEvent(String recipients, Subject subject, Body body, Service service) {
        raise(new SendingEmailEvent(recipients, subject, body, service));
    }

    /**
     * Makes a string of all intended recipient through the given @predicate
     */
    @Override
    public String createEmailRecipients(Predicate<ReadOnlyPerson> predicate) {
        requireNonNull(predicate);

        filteredPersonsForEmail.setPredicate(predicate);

        List<String> validEmails = new ArrayList<>();
        for (ReadOnlyPerson person : filteredPersonsForEmail) {
            if (Email.isValidEmail(person.getEmail().value)) {
                validEmails.add(person.getEmail().value);
            }
        }
        return String.join(",", validEmails);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code ReadOnlyPerson} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<ReadOnlyPerson> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(sortedfilteredPersons);
    }

    @Override
    public void sortFilteredPersonList() {

        Comparator<ReadOnlyPerson> sortByName = (o1, o2) -> o1.getName().fullName.compareTo(o2.getName().fullName);
        sortedfilteredPersons.setComparator(sortByName);
        indicateAddressBookChanged();
    }

    @Override
    public void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void updateFilteredListToShowAll() {
        filteredPersons.setPredicate(null);
    }

    @Override
    public ObservableList<ReadOnlyPerson> getBirthdayPanelFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(sortedFilteredPersonsForBirthdayListPanel);
    }

    //=========== UniqueReminderList Accessors =================================================================

    //@@author justinpoh
    @Override
    public ObservableList<Reminder> getSortedReminderList() {
        return sortedReminderList;
    }

    @Override
    public UniqueReminderList getUniqueReminderList() {
        return reminderList;
    }

    @Override
    public void addReminder(Reminder toAdd) throws DuplicateReminderException {
        reminderList.add(toAdd);
        indicateRemindersChanged();
    }

    @Override
    public void deleteReminder(Reminder target) throws ReminderNotFoundException {
        reminderList.remove(target);
        indicateRemindersChanged();
    }

    @Override
    public void resetReminders(UniqueReminderList newReminders) {
        reminderList.setReminders(newReminders);
        indicateRemindersChanged();
    }

    @Override
    public void updateReminder(Reminder target, Reminder editedReminder)
            throws DuplicateReminderException, ReminderNotFoundException {
        requireAllNonNull(target, editedReminder);

        reminderList.setReminder(target, editedReminder);
        indicateRemindersChanged();
    }
    //@@author

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && filteredPersons.equals(other.filteredPersons)
                //@@author justinpoh
                && reminderList.equals(other.reminderList);
                //@@author
    }

}
