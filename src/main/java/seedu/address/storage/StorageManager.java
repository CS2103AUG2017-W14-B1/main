package seedu.address.storage;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.RemindersChangedEvent;
import seedu.address.commons.events.storage.DataSavingExceptionEvent;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.reminders.UniqueReminderList;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager extends ComponentManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private UserPrefsStorage userPrefsStorage;
    private RemindersStorage remindersStorage;


    public StorageManager(AddressBookStorage addressBookStorage, RemindersStorage remindersStorage,
                          UserPrefsStorage userPrefsStorage) {
        super();
        this.remindersStorage = remindersStorage;
        this.addressBookStorage = addressBookStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public String getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(UserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public String getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return addressBookStorage.readAddressBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    @Override
    @Subscribe
    public void handleAddressBookChangedEvent(AddressBookChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
        try {
            saveAddressBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }

    //@@author justinpoh
    @Override
    @Subscribe
    public void handleRemindersChangedEvent(RemindersChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local reminders data changed,"
                + " saving to file"));

        try {
            saveReminders(event.reminderList);
        } catch (IOException e) {
            raise (new DataSavingExceptionEvent(e));
        }
    }
    //@@author

    @Override
    public void backupAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath() + "-backup.xml");
    }

    // ================ Reminders methods ==============================

    //@@author justinpoh
    @Override
    public String getRemindersFilePath() {
        return remindersStorage.getRemindersFilePath();
    }

    @Override
    public Optional<XmlSerializableReminders> readReminders() throws DataConversionException, IOException {
        return readReminders(remindersStorage.getRemindersFilePath());
    }

    @Override
    public Optional<XmlSerializableReminders> readReminders(String filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return remindersStorage.readReminders(filePath);
    }

    @Override
    public void saveReminders(UniqueReminderList reminderList) throws IOException {
        saveReminders(reminderList, remindersStorage.getRemindersFilePath());
    }

    @Override
    public void saveReminders(UniqueReminderList reminderList, String filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        remindersStorage.saveReminders(reminderList, filePath);
    }
    //@@author
}
