# justinpoh
###### /java/seedu/address/commons/events/model/RemindersChangedEvent.java
``` java
/** Indicates the reminders have changed*/
public class RemindersChangedEvent extends BaseEvent {
    public final UniqueReminderList reminderList;

    public RemindersChangedEvent(UniqueReminderList reminderList) {
        this.reminderList = reminderList;
    }

    @Override
    public String toString() {
        return "number of reminders " + reminderList.size();
    }
}
```
###### /java/seedu/address/commons/events/ui/BrowserAndRemindersPanelToggleEvent.java
``` java
/**
 * Represents a toggling between the browser and reminders panels.
 */

public class BrowserAndRemindersPanelToggleEvent extends BaseEvent {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### /java/seedu/address/commons/events/ui/TurnLabelsOffEvent.java
``` java
/**
 * Turn birthday and reminders labels off whenever the browser is brought to the front.
 */
public class TurnLabelsOffEvent extends BaseEvent {
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### /java/seedu/address/commons/events/ui/TurnLabelsOnEvent.java
``` java
/**
 * Turn birthday and reminders labels on whenever the birthday and reminder lists
 * are brought to the front.
 */

public class TurnLabelsOnEvent extends BaseEvent {
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### /java/seedu/address/logic/commands/AddReminderCommand.java
``` java
/**
 *  Adds a reminder to the program.
 */

public class AddReminderCommand extends Command {

    public static final String COMMAND_WORD = "addReminder";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a reminder.\n"
            + "Parameters: "
            + PREFIX_REMINDER + "REMINDER "
            + PREFIX_DATE + "DATE "
            + PREFIX_TIME + "TIME\n"
            + "REMINDER must be non-empty. DATE must be in the format dd-mm-yyyy, dd/mm/yyyy or dd.mm.yyyy, "
            + "and must be a valid date. TIME is in 24-hour format.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_REMINDER + "Dinner with Family "
            + PREFIX_DATE + "22-11-2017 "
            + PREFIX_TIME + "1700\n";

    public static final String MESSAGE_SUCCESS = "New reminder added!";
    public static final String MESSAGE_DUPLICATE_REMINDER = "This reminder already exists!";

    private Reminder toAdd;

    /**
     * Creates an AddReminderCommand to add the specified {@code Reminder}
     */
    public AddReminderCommand(Reminder toAdd) {
        requireNonNull(toAdd);

        this.toAdd = toAdd;
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);
        try {
            model.addReminder(toAdd);
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (DuplicateReminderException dre) {
            throw new CommandException(MESSAGE_DUPLICATE_REMINDER);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddReminderCommand // instanceof handles nulls
                && toAdd.equals(((AddReminderCommand) other).toAdd));
    }
}
```
###### /java/seedu/address/logic/commands/DeleteReminderCommand.java
``` java
/**
 * Deletes a reminder identified using it's index.
 */
public class DeleteReminderCommand extends Command {

    public static final String COMMAND_WORD = "deleteReminder";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the reminder identified by the index number used in the reminder listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_REMINDER_SUCCESS = "Deleted reminder: %1$s";

    private final Index targetIndex;

    public DeleteReminderCommand(Index index) {
        this.targetIndex = index;
    }

    @Override
    public CommandResult execute() throws CommandException {
        List<Reminder> reminderListing = model.getSortedReminderList();

        if (targetIndex.getZeroBased() >= reminderListing.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_REMINDER_DISPLAYED_INDEX);
        }

        Reminder reminderToDelete = reminderListing.get(targetIndex.getZeroBased());

        try {
            model.deleteReminder(reminderToDelete);
        } catch (ReminderNotFoundException rnfe) {
            assert false : "The target reminder cannot be missing";
        }

        return new CommandResult(String.format(MESSAGE_DELETE_REMINDER_SUCCESS, reminderToDelete.toString()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteReminderCommand // instanceof handles nulls
                && this.targetIndex.equals(((DeleteReminderCommand) other).targetIndex)); // state check
    }
}
```
###### /java/seedu/address/logic/commands/EditReminderCommand.java
``` java
/**
 * Edits the details of an existing reminder in the application.
 */
public class EditReminderCommand extends Command {

    public static final String COMMAND_WORD = "editReminder";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the reminder identified "
            + "by the index number. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_REMINDER + "REMINDER (cannot be empty)]"
            + "[" + PREFIX_DATE + "DATE]"
            + "[" + PREFIX_TIME + "TIME]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_REMINDER + "Changed reminder";


    public static final String MESSAGE_EDIT_REMINDER_SUCCESS = "Edited Reminder: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_REMINDER = "This reminder already exists in iContacts.";

    public static final String MESSAGE_REMINDER_FORMAT = "Reminder can be of any value, and cannot be empty.";

    private final Index index;
    private final EditReminderDescriptor editReminderDescriptor;

    /**
     * @param index of the reminder in the sorted reminder list to edit
     * @param editReminderDescriptor details to edit the reminder with
     */
    public EditReminderCommand(Index index, EditReminderDescriptor editReminderDescriptor) {
        requireNonNull(index);
        requireNonNull(editReminderDescriptor);

        this.index = index;
        this.editReminderDescriptor = editReminderDescriptor;
    }


    @Override
    public CommandResult execute() throws CommandException {
        List<Reminder> reminderList = model.getSortedReminderList();

        if (index.getZeroBased() >= reminderList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_REMINDER_DISPLAYED_INDEX);
        }

        Reminder reminderToEdit = reminderList.get(index.getZeroBased());
        Reminder editedReminder = createEditedReminder(reminderToEdit, editReminderDescriptor);

        try {
            model.updateReminder(reminderToEdit, editedReminder);
        } catch (DuplicateReminderException dre) {
            throw new CommandException(MESSAGE_DUPLICATE_REMINDER);
        } catch (ReminderNotFoundException pnfe) {
            throw new AssertionError("The target reminder cannot be missing");
        }
        return new CommandResult(String.format(MESSAGE_EDIT_REMINDER_SUCCESS, editedReminder));
    }

    /**
     * Creates and returns a {@code Reminder} with the details of {@code reminderToEdit}
     * edited with {@code editReminderDescriptor}.
     */
    private static Reminder createEditedReminder (Reminder reminderToEdit,
                                                  EditReminderDescriptor editReminderDescriptor) {
        assert reminderToEdit != null;

        String updatedReminder = editReminderDescriptor.getReminder().orElse(reminderToEdit.getReminder());
        Date updatedDate = editReminderDescriptor.getDate().orElse(reminderToEdit.getDate());
        Time updatedTime = editReminderDescriptor.getTime().orElse(reminderToEdit.getTime());

        return new Reminder(updatedReminder, updatedDate, updatedTime);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditReminderCommand)) {
            return false;
        }

        // state check
        EditReminderCommand e = (EditReminderCommand) other;
        return index.equals(e.index)
                && editReminderDescriptor.equals(e.editReminderDescriptor);
    }

    /**
     * Stores the details to edit the reminder with. Each non-empty field value will replace the
     * corresponding field value of the reminder.
     */
    public static class EditReminderDescriptor {
        private String reminder;
        private Time time;
        private Date date;

        public EditReminderDescriptor() {}

        public EditReminderDescriptor(EditReminderDescriptor toCopy) {
            this.reminder = toCopy.reminder;
            this.time = toCopy.time;
            this.date = toCopy.date;
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(this.reminder, this.time, this.date);
        }

        public void setReminder(String reminder) {
            this.reminder = reminder;
        }

        public Optional<String> getReminder() {
            return Optional.ofNullable(reminder);
        }

        public Optional<Date> getDate() {
            return Optional.ofNullable(date);
        }

        public Optional<Time> getTime() {
            return Optional.ofNullable(time);
        }

        public void setTime(Time time) {
            this.time = time;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditReminderDescriptor)) {
                return false;
            }

            // state check
            EditReminderDescriptor e = (EditReminderDescriptor) other;

            return getReminder().equals(e.getReminder())
                    && getDate().equals(e.getDate())
                    && getTime().equals(e.getTime());
        }
    }
}
```
###### /java/seedu/address/logic/commands/ToggleCommand.java
``` java
/**
 * Toggles between the browser and reminders.
 */

public class ToggleCommand extends Command {

    public static final String COMMAND_WORD = "toggle";
    public static final String MESSAGE_TOGGLE_SUCCESS = "Toggle successful.";

    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new BrowserAndRemindersPanelToggleEvent());
        return new CommandResult(MESSAGE_TOGGLE_SUCCESS);
    }


}
```
###### /java/seedu/address/logic/Logic.java
``` java
    /** Returns an unmodifiable view of the birthday panel filtered person list */
    ObservableList<ReadOnlyPerson> getBirthdayPanelFilteredPersonList();

    /** Returns an unmodifiable view of the reminder list */
    ObservableList<Reminder> getReminderList();
```
###### /java/seedu/address/logic/LogicManager.java
``` java
    @Override
    public ObservableList<ReadOnlyPerson> getBirthdayPanelFilteredPersonList() {
        return model.getBirthdayPanelFilteredPersonList();
    }

    @Override
    public ObservableList<Reminder> getReminderList() {
        return model.getSortedReminderList();
    }
```
###### /java/seedu/address/logic/parser/AddReminderCommandParser.java
``` java
/**
 * Parses input arguments and creates a new AddReminderCommand object
 */
public class AddReminderCommandParser implements Parser<AddReminderCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddReminderCommand
     * and returns an AddReminderCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddReminderCommand parse(String args) throws ParseException {
        requireNonNull(args);
        final String reminder;

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_REMINDER, PREFIX_DATE, PREFIX_TIME);

        if (!arePrefixesPresent(argMultimap, PREFIX_REMINDER, PREFIX_DATE, PREFIX_TIME)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddReminderCommand.MESSAGE_USAGE));
        }

        reminder = argMultimap.getValue(PREFIX_REMINDER).get();
        if (reminder.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddReminderCommand.MESSAGE_USAGE));
        }

        try {
            Date date = ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE)).get();
            Time time = ParserUtil.parseTime(argMultimap.getValue(PREFIX_TIME)).get();
            Reminder toAdd = new Reminder(reminder, date, time);
            return new AddReminderCommand(toAdd);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage());
        }
    }
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case AddReminderCommand.COMMAND_WORD:
            return new AddReminderCommandParser().parse(arguments);
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case EditReminderCommand.COMMAND_WORD:
            return new EditReminderCommandParser().parse(arguments);
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case DeleteReminderCommand.COMMAND_WORD:
            return new DeleteReminderCommandParser().parse(arguments);
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case ToggleCommand.COMMAND_WORD:
            return new ToggleCommand();
```
###### /java/seedu/address/logic/parser/CliSyntax.java
``` java
    public static final Prefix PREFIX_BIRTHDAY = new Prefix("b/");
```
###### /java/seedu/address/logic/parser/CliSyntax.java
``` java
    public static final Prefix PREFIX_REMINDER = new Prefix("rd/");
    public static final Prefix PREFIX_DATE = new Prefix("d/");
    public static final Prefix PREFIX_TIME = new Prefix("ti/");
```
###### /java/seedu/address/logic/parser/DeleteReminderCommandParser.java
``` java
/**
 * Parses input arguments and create a new DeleteReminderCommand object.
 */
public class DeleteReminderCommandParser implements Parser<DeleteReminderCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteReminderCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteReminderCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteReminderCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteReminderCommand.MESSAGE_USAGE));
        }
    }
}
```
###### /java/seedu/address/logic/parser/EditReminderCommandParser.java
``` java
/**
 * Parses input arguments and creates a new EditReminderCommand object
 */
public class EditReminderCommandParser implements Parser<EditReminderCommand> {


    /**
     * Parses the given {@code String} of arguments in the context of the EditReminderCommand
     * and returns an EditReminderCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditReminderCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_REMINDER, PREFIX_DATE, PREFIX_TIME);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditReminderCommand.MESSAGE_USAGE));
        }

        EditReminderDescriptor editReminderDescriptor = new EditReminderDescriptor();
        Optional<String> optionalReminder = argMultimap.getValue(PREFIX_REMINDER);
        String reminder;
        if (optionalReminder.isPresent()) {
            reminder = optionalReminder.get();
            if (reminder.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        EditReminderCommand.MESSAGE_REMINDER_FORMAT));
            }
            editReminderDescriptor.setReminder(reminder);
        }

        try {
            ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE)).ifPresent(editReminderDescriptor::setDate);
            ParserUtil.parseTime(argMultimap.getValue(PREFIX_TIME)).ifPresent(editReminderDescriptor::setTime);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        if (!editReminderDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditReminderCommand.MESSAGE_NOT_EDITED);
        }

        return new EditReminderCommand(index, editReminderDescriptor);
    }
}
```
###### /java/seedu/address/model/Model.java
``` java
    /** Clears existing backing model and replaces with the provided new reminders. */
    void resetReminders(UniqueReminderList newReminders);
```
###### /java/seedu/address/model/Model.java
``` java
    /** Returns an unmodifiable view of the sorted list of reminders */
    ObservableList<Reminder> getSortedReminderList();

    /** Returns the reminders */
    UniqueReminderList getUniqueReminderList();
```
###### /java/seedu/address/model/Model.java
``` java
    /** Adds the given reminder */
    void addReminder(Reminder reminder) throws DuplicateReminderException;

    /** Deletes the given reminder. */
    void deleteReminder(Reminder target) throws ReminderNotFoundException;
```
###### /java/seedu/address/model/Model.java
``` java
    /**
     * Replaces the given reminder {@code target} with {@code editedReminder}.
     *
     * @throws DuplicateReminderException if updating the reminder's details causes the reminder to be equivalent to
     *      another existing reminder in the list.
     * @throws ReminderNotFoundException if {@code target} could not be found in the list.
     */
    void updateReminder(Reminder target, Reminder editedReminder)
            throws DuplicateReminderException, ReminderNotFoundException;
```
###### /java/seedu/address/model/ModelManager.java
``` java
        filteredPersonsForBirthdayListPanel = new FilteredList<>(this.addressBook.getPersonList());
        filteredPersonsForBirthdayListPanel.setPredicate(new BirthdayInCurrentMonthPredicate());
```
###### /java/seedu/address/model/ModelManager.java
``` java
        sortedFilteredPersonsForBirthdayListPanel = new SortedList<>(filteredPersonsForBirthdayListPanel,
                Comparator.comparingInt(birthday -> birthday.getBirthday().getDayOfBirthday()));
        sortedReminderList = new SortedList<>(reminderList.asObservableList(),
                Comparator.comparing(reminder -> reminder.getLocalDateTime()));
```
###### /java/seedu/address/model/ModelManager.java
``` java
    /** Raises an event to indicate the reminders have changed */
    private void indicateRemindersChanged() {
        raise(new RemindersChangedEvent(reminderList));
    }
```
###### /java/seedu/address/model/ModelManager.java
``` java
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
```
###### /java/seedu/address/model/ModelManager.java
``` java
                && reminderList.equals(other.reminderList);
```
###### /java/seedu/address/model/person/Birthday.java
``` java
/**
 * Represents a Person's birthday in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidBirthday(String)}
 */
public class Birthday {

    public static final String MESSAGE_BIRTHDAY_CONSTRAINTS = "Birthday must be a valid date"
            + " and in the following format:\n"
            + "'.', '-' and '/' can be used to seperate the day, month and year fields.\n"
            + "Day field: 1 - 31 (allows leading zeroes).\n"
            + "Month field: 1-12 (allows leading zeroes).\n"
            + "Year field: 1900 - 2099.\n"
            + "Example: 21/10/1995, 21-05-1996. 8.10.1987";
    public static final int EMPTY_BIRTHDAY_FIELD_MONTH = 0;
    public static final int EMPTY_BIRTHDAY_FIELD_DAY = 0;
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
            System.out.println(birthday);
            throw new IllegalValueException(MESSAGE_BIRTHDAY_CONSTRAINTS);
        }
        String trimmedBirthday = birthday.trim();
        if (trimmedBirthday.isEmpty()) {
            this.value = EMPTY_STRING;
        } else {
            this.value = convertToDefaultDateFormat(birthday);
        }
    }

    /**
     * Get the month of the birthday in this Birthday object.
     * If the birthday field is empty, return EMPTY_BIRTHDAY_FIELD_MONTH
     */
    public int getMonthOfBirthday() {
        if (value.isEmpty()) {
            return EMPTY_BIRTHDAY_FIELD_MONTH;
        }
        String[] splitDate = value.split(BIRTHDAY_DASH_SEPARATOR);
        try {
            final int month = Integer.parseInt(splitDate[DATE_MONTH_INDEX]);
            return month;
        } catch (NumberFormatException nfe) {
            throw new AssertionError("Should not happen");
        }
    }

    /**
     * Get the day of the birthday in this Birthday object.
     * If the birthday field is empty, return EMPTY_BIRTHDAY_FIELD_DAY
     */
    public int getDayOfBirthday() {
        if (value.isEmpty()) {
            return EMPTY_BIRTHDAY_FIELD_DAY;
        }
        String[] splitDate = value.split(BIRTHDAY_DASH_SEPARATOR);
        final int day = Integer.parseInt(splitDate[DATE_DAY_INDEX]);
        return day;
    }

    /**
     * Returns true if a given string is a valid person birthday.
     */
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

    /**
     * Returns true if a given date is a valid date.
     * A date is valid if it exists.
     */
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

    /**
     * Returns true if a given date is a valid leap day.
     */
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

    /**
     * Converts the date into logical segments.
     */
    private static String[] getSplitDate(String trimmedDate) {
        return trimmedDate.split(BIRTHDAY_SPLIT_REGEX);
    }

    /**
     * Converts date in integer array into dd-mm-yyyy String format.
     */
    private static String convertToDefaultDateFormat(String date) {
        String[] splitDate = getSplitDate(date);

        StringBuilder builder = new StringBuilder();
        builder.append(splitDate[DATE_DAY_INDEX]);
        builder.append(BIRTHDAY_DASH_SEPARATOR);
        builder.append(splitDate[DATE_MONTH_INDEX]);
        builder.append(BIRTHDAY_DASH_SEPARATOR);
        builder.append(splitDate[DATE_YEAR_INDEX]);
        return builder.toString();
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
```
###### /java/seedu/address/model/person/BirthdayInCurrentMonthPredicate.java
``` java
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
```
###### /java/seedu/address/model/person/Person.java
``` java
    public void setBirthday(Birthday birthday) {
        this.birthday.set(requireNonNull(birthday));
    }

    @Override
    public ObjectProperty<Birthday> birthdayProperty() {
        return birthday;
    }

    @Override
    public Birthday getBirthday() {
        return birthday.get();
    }
```
###### /java/seedu/address/model/reminders/Date.java
``` java
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
```
###### /java/seedu/address/model/reminders/exceptions/DuplicateReminderException.java
``` java
/**
 *  Signals that the operation will result in duplicate Reminder objects.
 */
public class DuplicateReminderException extends DuplicateDataException {
    public DuplicateReminderException() {
        super("Operation would result in duplicate reminders.");
    }
}
```
###### /java/seedu/address/model/reminders/exceptions/ReminderNotFoundException.java
``` java
/**
 * Signals that the operation is unable to find the specified reminder.
 */
public class ReminderNotFoundException extends Exception {
}
```
###### /java/seedu/address/model/reminders/Reminder.java
``` java
/**
 *  Represents a reminder.
 */

public class Reminder {

    private ObjectProperty<String> reminder;
    private ObjectProperty<Date> date;
    private ObjectProperty<Time> time;

    /**
     * Every field must be present and not null.
     */
    public Reminder(String reminder, Date date, Time time) {
        requireAllNonNull(reminder, date, time);

        this.reminder = new SimpleObjectProperty<>(reminder);
        this.date = new SimpleObjectProperty<>(date);
        this.time = new SimpleObjectProperty<>(time);
    }

    /**
     * Creates a copy of the given Reminder.
     */
    public Reminder(Reminder source) {
        this(source.getReminder(), source.getDate(), source.getTime());
    }

    public void setReminder(String reminder) {
        this.reminder.set(requireNonNull(reminder));
    }

    public ObjectProperty<String> reminderProperty() {
        return reminder;
    }

    public String getReminder() {
        return reminder.get();
    }

    public void setTime(Time time) {
        this.time.set(requireNonNull(time));
    }

    public ObjectProperty<Time> timeProperty() {
        return time;
    }

    public Time getTime() {
        return time.get();
    }

    public void setDate(Date date) {
        this.date.set(requireNonNull(date));
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public Date getDate() {
        return date.get();
    }

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.of(date.get().toLocalDate(), time.get().toLocalTime());
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof Reminder
                && this.getReminder().equals(((Reminder) other).getReminder())
                && this.getDate().equals(((Reminder) other).getDate())
                && this.getTime().equals(((Reminder) other).getTime()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(reminder.get(), date.get(), time.get());
    }

    @Override
    public String toString() {
        return reminder.get() + "\n" + date.get() + "\n" + time.get();
    }
}
```
###### /java/seedu/address/model/reminders/Time.java
``` java
/**
 * Represents a Reminder's time in the program.
 * Guarantees: immutable; is valid as declared in {@link #isValidTime(String)}
 */
public class Time {

    public static final String MESSAGE_TIME_CONSTRAINTS = "Time must be in 24-hour format,"
            + " with a colon separating the hour and minute.\n"
            + "Example: 09:00, 23:59, 17:56";

    private static final String TIME_VALIDATION_REGEX = "(0[0-9]|1[0-9]|2[0-3]):"
            + "(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])";
    private static final String HOUR_MIN_SEPARATOR = ":";

    private static final int TIME_HOUR_INDEX = 0;
    private static final int TIME_MIN_INDEX = 1;

    public final String value;

    /**
     * Validates given time.
     *
     * @throws IllegalValueException if given time string is invalid.
     */
    public Time(String time) throws IllegalValueException {
        requireNonNull(time);
        if (!isValidTime(time)) {
            throw new IllegalValueException(MESSAGE_TIME_CONSTRAINTS);
        }

        this.value = time;
    }

    /**
     * Returns true if a given string is a valid reminder time.
     */
    public static boolean isValidTime(String time) {
        if (!time.matches(TIME_VALIDATION_REGEX)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the time as a LocalTime object for easy comparison of chronology between reminders.
     */
    public LocalTime toLocalTime() {
        String[] splitTime = value.split(HOUR_MIN_SEPARATOR);
        final int hour = Integer.parseInt(splitTime[TIME_HOUR_INDEX]);
        final int min = Integer.parseInt(splitTime[TIME_MIN_INDEX]);
        return LocalTime.of(hour, min);
    }


    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof Time
                && this.value.equals(((Time) other).value));
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
```
###### /java/seedu/address/model/reminders/UniqueReminderList.java
``` java
/**
 * A list of reminders that enforces uniqueness between its elements and does not allow nulls.
 *
 * Supports a minimal set of list operations.
 *
 * @see Reminder#equals(Object)
 */
public class UniqueReminderList implements Iterable<Reminder> {
    private final ObservableList<Reminder> internalList = FXCollections.observableArrayList();

    public UniqueReminderList() {
    }

    public UniqueReminderList(UniqueReminderList uniqueReminderList) {
        this();
        setReminders(uniqueReminderList);
    }

    /**
     * Constructor used for loading reminder from storage file into program.
     */
    public UniqueReminderList(XmlSerializableReminders xmlReminders) {
        requireNonNull(xmlReminders);
        try {
            setReminders(xmlReminders.toModelType());
        } catch (DuplicateReminderException dre) {
            assert false : "Reminders from storage should not have duplicates";
        }
    }

    public int size() {
        return internalList.size();
    }

    /**
     * Returns true if the list contains an equivalent reminder as the given argument.
     */
    public boolean contains(Reminder toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }

    /**
     * Adds a reminder to the list.
     * @throws DuplicateReminderException if the reminder to add is a duplicate of an existing reminder in the list.
     */
    public void add(Reminder toAdd) throws DuplicateReminderException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateReminderException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the reminder {@code target} in the list with {@code editedReminder}.
     *
     * @throws DuplicateReminderException if the replacement is equivalent to another existing reminder in the list.
     * @throws ReminderNotFoundException if {@code target} could not be found in the list.
     */
    public void setReminder(Reminder target, Reminder editedReminder)
            throws DuplicateReminderException, ReminderNotFoundException {
        requireNonNull(editedReminder);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new ReminderNotFoundException();
        }

        if (!target.equals(editedReminder) && internalList.contains(editedReminder)) {
            throw new DuplicateReminderException();
        }

        internalList.set(index, new Reminder(editedReminder));
    }

    /**
     * Removes the equivalent reminder from the list.
     *
     * @throws ReminderNotFoundException if no such reminder could be found in the list.
     */
    public boolean remove(Reminder toRemove) throws ReminderNotFoundException {
        requireNonNull(toRemove);
        final boolean reminderFoundAndDeleted = internalList.remove(toRemove);
        if (!reminderFoundAndDeleted) {
            throw new ReminderNotFoundException();
        }
        return reminderFoundAndDeleted;
    }

    public void setReminders(UniqueReminderList replacement) {
        this.internalList.setAll(replacement.internalList);
    }

    public void setReminders(List<Reminder> reminders) throws DuplicateReminderException {
        final UniqueReminderList replacement = new UniqueReminderList();
        for (final Reminder reminder : reminders) {
            replacement.add(new Reminder(reminder));
        }
        setReminders(replacement);
    }

    /**
     * Returns the list as an unmodiafiable {@code ObservableList}.
     */
    public ObservableList<Reminder> asObservableList() {
        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<Reminder> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueReminderList // instanceof handles nulls
                && this.internalList.equals(((UniqueReminderList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
```
###### /java/seedu/address/model/UserPrefs.java
``` java
    public String getRemindersFilePath() {
        return remindersFilePath;
    }

    public void setRemindersFilePath(String remindersFilePath) {
        this.remindersFilePath = remindersFilePath;
    }
```
###### /java/seedu/address/storage/RemindersStorage.java
``` java
/**
 * Represents a storage for {@link seedu.address.model.reminders.UniqueReminderList}.
 */

public interface RemindersStorage {
    /**
     * Returns the file path of the data file for reminders.
     */
    String getRemindersFilePath();

    /**
     * Returns AddressBook data as a {@link XmlSerializableReminders}.
     *   Returns {@code Optional.empty()} if storage file is not found.
     * @throws DataConversionException if the data in storage is not in the expected format.
     * @throws IOException if there was any problem when reading from the storage.
     */
    Optional<XmlSerializableReminders> readReminders() throws DataConversionException, IOException;

    /**
     * @see #getRemindersFilePath()
     */
    Optional<XmlSerializableReminders> readReminders(String filePath) throws DataConversionException,
            IOException;

    /**
     * Saves the given {@link UniqueReminderList} to the storage.
     * @param reminderList cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveReminders(UniqueReminderList reminderList) throws IOException;

    /**
     * @see #saveReminders(UniqueReminderList)
     */
    void saveReminders(UniqueReminderList reminderList, String filePath) throws IOException;
}
```
###### /java/seedu/address/storage/Storage.java
``` java
    @Override
    void saveReminders(UniqueReminderList reminderList) throws IOException;
```
###### /java/seedu/address/storage/Storage.java
``` java
    /**
     * Saves the current version of reminders to the hard disk.
     *   Creates the data file if it is missing.
     * Raises {@link DataSavingExceptionEvent} if there was an error during saving.
     */
    void handleRemindersChangedEvent(RemindersChangedEvent rce);
```
###### /java/seedu/address/storage/StorageManager.java
``` java
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
```
###### /java/seedu/address/storage/StorageManager.java
``` java
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
```
###### /java/seedu/address/storage/XmlAdaptedReminder.java
``` java
/**
 * JAXB-friendly version of the Reminder.
 */
public class XmlAdaptedReminder {

    @XmlElement(required = true)
    private String reminder;
    @XmlElement(required = true)
    private String date;
    @XmlElement(required = true)
    private String time;

    /**
     * Constructs an XmlAdaptedReminder.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedReminder() {}

    /**
     * Converts a given Reminder into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedReminder
     */
    public XmlAdaptedReminder(Reminder source) {
        this.reminder = source.getReminder();
        this.date = source.getDate().value;
        this.time = source.getTime().value;
    }
    /**
     * Converts this jaxb-friendly adapted reminder object into the model's Reminder object.
     *
     * @throws DateTimeParseException if there were any data constraints violated in the adapted reminder
     */
    public Reminder toModelType() throws IllegalValueException {
        final Date date = new Date(this.date);
        final Time time = new Time(this.time);
        return new Reminder(this.reminder, date, time);
    }


}
```
###### /java/seedu/address/storage/XmlRemindersStorage.java
``` java
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
```
###### /java/seedu/address/storage/XmlSerializableReminders.java
``` java
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
```
###### /java/seedu/address/ui/BirthdayAndReminderListPanel.java
``` java
        setConnections(birthdayList, reminderList);
```
###### /java/seedu/address/ui/BirthdayAndReminderListPanel.java
``` java
    private void setConnections(ObservableList<ReadOnlyPerson> birthdayList, ObservableList<Reminder> reminderList) {
        ObservableList<BirthdayReminderCard> birthdayMappedList = EasyBind.map(
                birthdayList, (birthdayPerson) -> new BirthdayReminderCard(birthdayPerson,
                        birthdayList.indexOf(birthdayPerson) + 1));
        birthdayListView.setItems(birthdayMappedList);
        birthdayListView.setCellFactory(listView -> new BirthdayListViewCell());

        ObservableList<ReminderCard> reminderMappedList = EasyBind.map(
                reminderList, (reminder) -> new ReminderCard(reminder,
                        reminderList.indexOf(reminder) + 1));
        reminderListView.setItems(reminderMappedList);
        reminderListView.setCellFactory(listView -> new ReminderListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code BirthdayReminderCard}.
     */
    class BirthdayListViewCell extends ListCell<BirthdayReminderCard> {

        @Override
        protected void updateItem(BirthdayReminderCard person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(person.getRoot());
            }
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code ReminderCard}.
     */
    class ReminderListViewCell extends ListCell<ReminderCard> {

        @Override
        protected void updateItem(ReminderCard reminder, boolean empty) {
            super.updateItem(reminder, empty);

            if (empty || reminder == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(reminder.getRoot());
            }
        }
    }
```
###### /java/seedu/address/ui/BirthdayReminderCard.java
``` java
/**
 * An UI component that displays the name, nickname and birthday of a Person.
 */
public class BirthdayReminderCard extends UiPart<Region> {

    private static final String FXML = "BirthdayReminderCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final ReadOnlyPerson person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label nickname;
    @FXML
    private Label birthday;

    public BirthdayReminderCard(ReadOnlyPerson person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        bindListeners(person);
    }


    /**
     * Binds the individual UI elements to observe their respective {@code Person} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(ReadOnlyPerson person) {
        name.textProperty().bind(Bindings.convert(person.nameProperty()));
        nickname.textProperty().bind(Bindings.convert(person.nicknameProperty()));
        birthday.textProperty().bind(Bindings.convert(person.birthdayProperty()));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof BirthdayReminderCard)) {
            return false;
        }

        // state check
        BirthdayReminderCard card = (BirthdayReminderCard) other;
        return id.getText().equals(card.id.getText())
                && person.equals(card.person);
    }
}
```
###### /java/seedu/address/ui/BrowserAndRemindersPanel.java
``` java
        birthdayAndReminderListPanel = new BirthdayAndReminderListPanel(birthdayPanelFilteredPersonList, reminderList);

        //remindersPanel should be displayed first so no need to shift it to the back.
        remindersPanel.getChildren().add(birthdayAndReminderListPanel.getRoot());
```
###### /java/seedu/address/ui/BrowserAndRemindersPanel.java
``` java
    /**
     * Check which child is currently at the front, and do the appropriate toggling between the children nodes.
     */
    private void toggleBrowserPanel() {
        switch(currentlyInFront) {
        case BROWSER:
            remindersPanel.toFront();
            currentlyInFront = Node.REMINDERS;
            raise(new TurnLabelsOnEvent());
            break;
        case REMINDERS:
            browser.toFront();
            currentlyInFront = Node.BROWSER;
            raise(new TurnLabelsOffEvent());
            break;
        default:
            throw new AssertionError("It should not be possible to land here");
        }
    }

    private void bringBrowserToFront() {
        browser.toFront();
        currentlyInFront = Node.BROWSER;
    }
```
###### /java/seedu/address/ui/BrowserAndRemindersPanel.java
``` java
    @Subscribe
    private void handleBrowserPanelToggleEvent(BrowserAndRemindersPanelToggleEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        toggleBrowserPanel();
    }
```
###### /java/seedu/address/ui/HeaderPane.java
``` java
/**
 * The Header Pane of the App.
 */

public class HeaderPane extends UiPart<Region> {

    private static final String FXML = "HeaderPane.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private Label contacts;
    @FXML
    private Label birthdays;
    @FXML
    private Label reminders;

    public HeaderPane() {
        super(FXML);
        registerAsAnEventHandler(this);
    }

    @Subscribe
    private void handleTurnLabelsOffEvent(TurnLabelsOffEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        birthdays.setVisible(false);
        reminders.setVisible(false);
    }

    @Subscribe
    private void handleTurnLabelsOnEvent(TurnLabelsOnEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        birthdays.setVisible(true);
        reminders.setVisible(true);
    }
}
```
###### /java/seedu/address/ui/ReminderCard.java
``` java
/**
 * An UI component that displays the content, date and time of a Reminder.
 */
public class ReminderCard extends UiPart<Region> {
    private static final String FXML = "ReminderCard.fxml";
    public final Reminder source;

    @FXML
    private HBox cardPane;
    @FXML
    private Label reminder;
    @FXML
    private Label id;
    @FXML
    private Label date;
    @FXML
    private Label time;

    public ReminderCard(Reminder reminder, int displayedIndex) {
        super(FXML);
        source = reminder;
        id.setText(displayedIndex + ". ");
        bindListeners(reminder);
    }

    /**
     * Binds the individual UI elements to observe their respective {@code Reminder} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(Reminder source) {
        reminder.textProperty().bind(Bindings.convert(source.reminderProperty()));
        date.textProperty().bind(Bindings.convert(source.dateProperty()));
        time.textProperty().bind(Bindings.convert(source.timeProperty()));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ReminderCard)) {
            return false;
        }

        // state check
        ReminderCard card = (ReminderCard) other;
        return id.getText().equals(card.id.getText())
                && source.equals(card.source);
    }
}
```
###### /resources/view/BirthdayAndReminderListPanel.fxml
``` fxml
<?import javafx.scene.control.ListView?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.VBox?>

<VBox xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
   <children>
      <HBox VBox.vgrow="ALWAYS" alignment="CENTER">
         <children>
            <ListView fx:id="birthdayListView" VBox.vgrow="ALWAYS" HBox.hgrow="ALWAYS"/>
            <ListView fx:id="reminderListView" VBox.vgrow="ALWAYS" HBox.hgrow="ALWAYS"/>
         </children>
      </HBox>
   </children>
</VBox>
```
###### /resources/view/BirthdayReminderCard.fxml
``` fxml
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.layout.VBox?>

<HBox id="cardPane" fx:id="cardPane" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
    <GridPane HBox.hgrow="ALWAYS">
        <columnConstraints>
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
        </columnConstraints>
        <VBox alignment="CENTER_LEFT" minHeight="105" GridPane.columnIndex="0">
            <padding>
                <Insets bottom="5" left="15" right="5" top="5" />
            </padding>
            <HBox alignment="CENTER_LEFT" spacing="5">
                <Label fx:id="id" styleClass="cell_big_label">
                    <minWidth>
                        <!-- Ensures that the label text is never truncated -->
                        <Region fx:constant="USE_PREF_SIZE" />
                    </minWidth>
                </Label>
                <Label fx:id="name" styleClass="cell_big_label" text="\$first" />
                <Label fx:id="nickname" styleClass="cell_small_label" text="\$nickname" />
            </HBox>
            <Label fx:id="birthday" styleClass="cell_small_label" text="\$birthday" />
        </VBox>
        <rowConstraints>
            <RowConstraints />
        </rowConstraints>
    </GridPane>
</HBox>
```
###### /resources/view/BrowserAndRemindersPanel.fxml
``` fxml
  <StackPane fx:id="remindersPanel" maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308" minHeight="0.0" minWidth="0.0" prefHeight="600.0" prefWidth="800.0" />
```
###### /resources/view/HeaderPane.fxml
``` fxml
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<FlowPane maxHeight="30" minHeight="30" prefHeight="30" styleClass="pane-with-border" VBox.vgrow="NEVER"
          xmlns="http://javafx.com/javafx/8">
    <Label fx:id="contacts" text="Contacts" translateX="130.0" xmlns:fx="http://javafx.com/fxml/1"/>
    <Label fx:id="birthdays" text="Birthdays" translateX="550.0" xmlns:fx="http://javafx.com/fxml/1"/>
    <Label fx:id="reminders" text="Reminders" translateX="900.0" xmlns:fx="http://javafx.com/fxml/1"/>
</FlowPane>
```
###### /resources/view/ReminderCard.fxml
``` fxml
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.layout.VBox?>

<HBox id="cardPane" fx:id="cardPane" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
    <GridPane HBox.hgrow="ALWAYS">
        <columnConstraints>
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
        </columnConstraints>
        <VBox alignment="CENTER_LEFT" minHeight="105" GridPane.columnIndex="0">
            <padding>
                <Insets bottom="5" left="15" right="5" top="5" />
            </padding>
            <HBox alignment="CENTER_LEFT" spacing="5">
                <Label fx:id="id" styleClass="cell_big_label">
                    <minWidth>
                        <!-- Ensures that the label text is never truncated -->
                        <Region fx:constant="USE_PREF_SIZE" />
                    </minWidth>
                </Label>
                <Label fx:id="reminder" styleClass="cell_big_label" text="\$reminder" />
            </HBox>
            <Label fx:id="date" styleClass="cell_big_label" text="\$date" />
            <Label fx:id="time" styleClass="cell_big_label" text="\$time" />
        </VBox>
        <rowConstraints>
            <RowConstraints />
        </rowConstraints>
    </GridPane>
</HBox>
```
