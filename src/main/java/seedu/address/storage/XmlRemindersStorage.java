package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.reminders.UniqueReminderList;

//@@author justinpoh
/**
 * A class to access reminder data stored as an xml file.
 */

public class XmlRemindersStorage implements RemindersStorage {

    private static final Logger logger = LogsCenter.getLogger(XmlRemindersStorage.class);

    private String filePath;

    public XmlRemindersStorage(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getRemindersFilePath() {
        return filePath;
    }

    @Override
    public Optional<XmlSerializableReminders> readReminders() throws DataConversionException, IOException {
        return readReminders(filePath);
    }

    /**
     * Similar to {@link #readReminders()}
     * @param filePath location of the data. Cannot be null
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<XmlSerializableReminders> readReminders(String filePath) throws DataConversionException,
                                                                                    FileNotFoundException {
        requireNonNull(filePath);

        File remindersFile = new File(filePath);

        if (!remindersFile.exists()) {
            logger.info("Reminders file "  + remindersFile + " not found");
            return Optional.empty();
        }

        XmlSerializableReminders remindersOptional = XmlFileStorage.loadRemindersFromSaveFile(new File(filePath));

        return Optional.of(remindersOptional);
    }

    @Override
    public void saveReminders(UniqueReminderList reminderList) throws IOException {
        saveReminders(reminderList, filePath);
    }

    /**
     * Similar to {@link #saveReminders(UniqueReminderList)}
     * @param filePath location of the data. Cannot be null
     */
    public void saveReminders(UniqueReminderList reminderList, String filePath) throws IOException {
        requireNonNull(reminderList);
        requireNonNull(filePath);

        File file = new File(filePath);
        FileUtil.createIfMissing(file);
        XmlFileStorage.saveRemindersToFile(file, new XmlSerializableReminders(reminderList.asObservableList()));
    }
}
