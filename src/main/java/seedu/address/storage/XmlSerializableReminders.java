package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.reminders.Reminder;

//@@author justinpoh
/**
 * A List of reminders that is serializable to XML format.
 */
@XmlRootElement(name = "reminders")
public class XmlSerializableReminders {

    @XmlElement
    private List<XmlAdaptedReminder> reminders;

    /**
     * Creates an empty XmlSerializableReminders.
     * This empty constructor is required for marshalling.
     */
    public XmlSerializableReminders() {
        reminders = new ArrayList<>();
    }

    /**
     * Conversion
     */
    public XmlSerializableReminders(List<Reminder> source) {
        this();
        reminders.addAll(source.stream().map(XmlAdaptedReminder::new).collect(Collectors.toList()));
    }

    /**
     * Converts this jaxb-friendly list of XmlAdaptedReminder into a list of
     * the model's Reminder objects.
     */
    public List<Reminder> toModelType() {
        final List<Reminder> listOfReminders = new ArrayList<>();
        try {
            for (XmlAdaptedReminder reminder : reminders) {
                listOfReminders.add(reminder.toModelType());
            }
        } catch (IllegalValueException ive) {
            throw new AssertionError("Date in storage should not be problematic!");
        }
        return listOfReminders;
    }
}
