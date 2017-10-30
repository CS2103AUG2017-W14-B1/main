# justinpoh
###### /java/guitests/guihandles/BirthdayReminderCardHandle.java
``` java
/**
 * Provides a handle to a birthday reminder card in the birthday reminder list panel.
 */
public class BirthdayReminderCardHandle extends NodeHandle<Node> {
    private static final String ID_FIELD_ID = "#id";
    private static final String NAME_FIELD_ID = "#name";
    private static final String NICKNAME_FIELD_ID = "#nickname";
    private static final String BIRTHDAY_FIELD_ID = "#birthday";

    private final Label idLabel;
    private final Label nameLabel;
    private final Label nicknameLabel;
    private final Label birthdayLabel;

    public BirthdayReminderCardHandle(Node cardNode) {
        super(cardNode);

        this.idLabel = getChildNode(ID_FIELD_ID);
        this.nameLabel = getChildNode(NAME_FIELD_ID);
        this.nicknameLabel = getChildNode(NICKNAME_FIELD_ID);
        this.birthdayLabel = getChildNode(BIRTHDAY_FIELD_ID);
    }

    public String getId() {
        return idLabel.getText();
    }

    public String getName() {
        return nameLabel.getText();
    }

    public String getNickname() {
        return nicknameLabel.getText();
    }
    public String getBirthday() {
        return birthdayLabel.getText();
    }

}
```
###### /java/guitests/guihandles/ReminderCardHandle.java
``` java
/**
 * Provides a handle to a reminder card in the reminder list panel.
 */
public class ReminderCardHandle extends NodeHandle<Node> {
    private static final String ID_FIELD_ID = "#id";
    private static final String REMINDER_FIELD_ID = "#reminder";
    private static final String DATE_FIELD_ID = "#date";
    private static final String TIME_FIELD_ID = "#time";

    private final Label idLabel;
    private final Label reminderLabel;
    private final Label dateLabel;
    private final Label timeLabel;

    public ReminderCardHandle(Node cardNode) {
        super(cardNode);

        this.idLabel = getChildNode(ID_FIELD_ID);
        this.reminderLabel = getChildNode(REMINDER_FIELD_ID);
        this.dateLabel = getChildNode(DATE_FIELD_ID);
        this.timeLabel = getChildNode(TIME_FIELD_ID);
    }

    public String getId() {
        return idLabel.getText();
    }

    public String getReminder() {
        return reminderLabel.getText();
    }

    public String getDate() {
        return dateLabel.getText();
    }

    public String getTime() {
        return timeLabel.getText();
    }
}
```
###### /java/seedu/address/logic/commands/AddReminderCommandTest.java
``` java
public class AddReminderCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructor_nullReminder_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new AddReminderCommand(null);
    }


    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingReminderAdded modelStub = new ModelStubAcceptingReminderAdded();
        Reminder validReminder = new ReminderBuilder().build();

        CommandResult commandResult = getAddReminderCommandForReminder(validReminder, modelStub).execute();

        assertEquals(String.format(AddReminderCommand.MESSAGE_SUCCESS, validReminder), commandResult.feedbackToUser);
        assertEquals(Arrays.asList(validReminder), modelStub.remindersAdded);
    }

    @Test
    public void execute_duplicateReminder_throwsCommandException() throws Exception {
        ModelStub modelStub = new ModelStubThrowingDuplicateReminderException();
        Reminder validReminder = new ReminderBuilder().build();

        thrown.expect(CommandException.class);
        thrown.expectMessage(AddReminderCommand.MESSAGE_DUPLICATE_REMINDER);

        getAddReminderCommandForReminder(validReminder, modelStub).execute();
    }

    @Test
    public void equals() {
        Reminder coffeeReminder = new ReminderBuilder().build();
        Reminder assignmentReminder = new ReminderBuilder().withReminder("Assignment").build();
        AddReminderCommand addCoffeeCommand = new AddReminderCommand(coffeeReminder);
        AddReminderCommand addAssignmentCommand = new AddReminderCommand(assignmentReminder);

        // same object -> returns true
        assertTrue(addCoffeeCommand.equals(addCoffeeCommand));

        // same values -> returns true
        AddReminderCommand addCoffeeCommandCopy = new AddReminderCommand(coffeeReminder);
        assertTrue(addCoffeeCommand.equals(addCoffeeCommandCopy));

        // different types -> returns false
        assertFalse(addCoffeeCommand.equals(1));

        // null -> returns false
        assertFalse(addCoffeeCommand.equals(null));

        // different reminder -> returns false
        assertFalse(addCoffeeCommand.equals(addAssignmentCommand));
    }

    /**
     * Generates a new AddReminderCommand with the details of the given reminder.
     */
    private AddReminderCommand getAddReminderCommandForReminder(Reminder reminder, Model model) {
        AddReminderCommand command = new AddReminderCommand(reminder);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }
```
###### /java/seedu/address/logic/commands/AddReminderCommandTest.java
``` java
    /**
     * A Model stub that always accept the reminder being added.
     */
    private class ModelStubAcceptingReminderAdded extends ModelStub {
        final ArrayList<Reminder> remindersAdded = new ArrayList<>();

        @Override
        public void addReminder(Reminder reminder) throws DuplicateReminderException {
            remindersAdded.add(new Reminder(reminder));
        }
    }

    /**
     * A Model stub that always throw a DuplicateReminderException when trying to add a reminder.
     */
    private class ModelStubThrowingDuplicateReminderException extends ModelStub {
        @Override
        public void addReminder(Reminder reminder) throws DuplicateReminderException {
            throw new DuplicateReminderException();
        }
    }
}
```
###### /java/seedu/address/logic/commands/DeleteReminderCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteReminderCommand}.
 */
public class DeleteReminderCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getUniqueTypicalReminders(), new UserPrefs());

    @Test
    public void execute_validIndexSortedReminderList_success() throws Exception {
        Reminder reminderToDelete = model.getSortedReminderList().get(INDEX_FIRST_PERSON.getZeroBased());

        DeleteReminderCommand deleteReminderCommand = prepareCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteReminderCommand.MESSAGE_DELETE_REMINDER_SUCCESS, reminderToDelete);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), model.getUniqueReminderList(),
                new UserPrefs());
        expectedModel.deleteReminder(reminderToDelete);

        assertCommandSuccess(deleteReminderCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexSortedReminderList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getSortedReminderList().size() + 1);
        DeleteReminderCommand deleteReminderCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(deleteReminderCommand, model, Messages.MESSAGE_INVALID_REMINDER_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteReminderCommand deleteFirstReminderCommand = new DeleteReminderCommand(INDEX_FIRST_PERSON);
        DeleteReminderCommand deleteSecondReminderCommand = new DeleteReminderCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstReminderCommand.equals(deleteFirstReminderCommand));

        // same values -> returns true
        DeleteReminderCommand deleteFirstReminderCommandCopy = new DeleteReminderCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstReminderCommand.equals(deleteFirstReminderCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstReminderCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstReminderCommand.equals(null));

        // different reminder -> returns false
        assertFalse(deleteFirstReminderCommand.equals(deleteSecondReminderCommand));
    }

    /**
     * Returns a {@code DeleteReminderCommand} with the parameter {@code index}.
     */
    private DeleteReminderCommand prepareCommand(Index index) {
        DeleteReminderCommand deleteReminderCommand = new DeleteReminderCommand(index);
        deleteReminderCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return deleteReminderCommand;
    }
}
```
###### /java/seedu/address/logic/commands/EditReminderCommandTest.java
``` java
public class EditReminderCommandTest {


    private Model model = new ModelManager(getTypicalAddressBook(), getUniqueTypicalReminders(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecified_success() throws Exception {
        Reminder editedReminder = new ReminderBuilder().build();
        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder(editedReminder).build();
        EditReminderCommand editCommand = prepareCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditReminderCommand.MESSAGE_EDIT_REMINDER_SUCCESS, editedReminder);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getUniqueReminderList(), new UserPrefs());
        expectedModel.updateReminder(model.getSortedReminderList().get(0), editedReminder);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecified_success() throws Exception {
        // time field not changed
        Index indexLastReminder = Index.fromOneBased(model.getSortedReminderList().size());
        Reminder lastReminder = model.getSortedReminderList().get(indexLastReminder.getZeroBased());

        ReminderBuilder reminderInList = new ReminderBuilder(lastReminder);
        Reminder editedReminder = reminderInList.withReminder(VALID_REMINDER_ASSIGNMENT)
                .withDate(VALID_REMINDER_DATE_ASSIGNMENT).build();

        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder().withReminder(VALID_REMINDER_ASSIGNMENT)
                .withDate(VALID_REMINDER_DATE_ASSIGNMENT).build();
        EditReminderCommand editReminderCommand = prepareCommand(indexLastReminder, descriptor);

        String expectedMessage = String.format(EditReminderCommand.MESSAGE_EDIT_REMINDER_SUCCESS, editedReminder);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getUniqueReminderList(), new UserPrefs());
        expectedModel.updateReminder(lastReminder, editedReminder);

        assertCommandSuccess(editReminderCommand, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_noFieldSpecified_success() throws Exception {
        EditReminderCommand editReminderCommand = prepareCommand(INDEX_FIRST_PERSON, new EditReminderDescriptor());
        Reminder editedReminder = model.getSortedReminderList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditReminderCommand.MESSAGE_EDIT_REMINDER_SUCCESS, editedReminder);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getUniqueReminderList(), new UserPrefs());

        assertCommandSuccess(editReminderCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateReminder_failure() throws Exception {
        Reminder firstReminder = new Reminder(model.getSortedReminderList().get(INDEX_FIRST_PERSON.getZeroBased()));
        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder(firstReminder).build();
        EditReminderCommand editReminderCommand = prepareCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editReminderCommand, model, EditReminderCommand.MESSAGE_DUPLICATE_REMINDER);
    }

    @Test
    public void execute_invalidReminderIndex_failure() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getSortedReminderList().size() + 1);
        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder()
                .withReminder(VALID_REMINDER_ASSIGNMENT).build();
        EditReminderCommand editReminderCommand = prepareCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editReminderCommand, model, Messages.MESSAGE_INVALID_REMINDER_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditReminderCommand standardCommand = new EditReminderCommand(INDEX_FIRST_PERSON, REMINDER_COFFEE);

        // same values -> returns true
        EditReminderDescriptor copyDescriptor = new EditReminderDescriptor(REMINDER_COFFEE);
        EditReminderCommand commandWithSameValues = new EditReminderCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditReminderCommand(INDEX_SECOND_PERSON, REMINDER_COFFEE)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditReminderCommand(INDEX_FIRST_PERSON, REMINDER_ASSIGNMENT)));
    }

    /**
     * Returns an {@code EditReminderCommand} with parameters {@code index} and {@code descriptor}
     */
    private EditReminderCommand prepareCommand(Index index, EditReminderDescriptor descriptor) {
        EditReminderCommand editReminderCommand = new EditReminderCommand(index, descriptor);
        editReminderCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return editReminderCommand;
    }
}
```
###### /java/seedu/address/logic/commands/EditReminderDescriptorTest.java
``` java
public class EditReminderDescriptorTest {
    @Test
    public void equals() {
        // same values -> returns true
        EditReminderDescriptor descriptorWithSameValues = new EditReminderDescriptor(REMINDER_COFFEE);
        assertTrue(REMINDER_COFFEE.equals(descriptorWithSameValues));

        // same objects -> returns true
        assertTrue(REMINDER_COFFEE.equals(REMINDER_COFFEE));

        // null -> returns false
        assertFalse(REMINDER_COFFEE.equals(null));

        // different types -> returns false
        assertFalse(REMINDER_COFFEE.equals(5));

        // different values -> returns false
        assertFalse(REMINDER_COFFEE.equals(REMINDER_ASSIGNMENT));

        // different reminder -> returns false
        EditReminderDescriptor editedCoffee = new EditReminderDescriptorBuilder(REMINDER_COFFEE)
                .withReminder(VALID_REMINDER_ASSIGNMENT).build();
        assertFalse(REMINDER_COFFEE.equals(editedCoffee));

        // different date -> returns false;
        editedCoffee = new EditReminderDescriptorBuilder(REMINDER_COFFEE)
                .withDate(VALID_REMINDER_DATE_ASSIGNMENT).build();
        assertFalse(REMINDER_COFFEE.equals(editedCoffee));

        // different time -> returns false;
        editedCoffee = new EditReminderDescriptorBuilder(REMINDER_COFFEE)
                .withTime(VALID_REMINDER_TIME_ASSIGNMENT).build();
        assertFalse(REMINDER_COFFEE.equals(editedCoffee));
    }
}
```
###### /java/seedu/address/logic/commands/ToggleCommandTest.java
``` java
public class ToggleCommandTest {
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    @Test
    public void execute_toggle_success() {
        CommandResult result = new ToggleCommand().execute();
        assertEquals(MESSAGE_TOGGLE_SUCCESS, result.feedbackToUser);
        assertTrue(eventsCollectorRule.eventsCollector.getMostRecent() instanceof BrowserAndRemindersPanelToggleEvent);
        assertTrue(eventsCollectorRule.eventsCollector.getSize() == 1);
    }
}
```
###### /java/seedu/address/logic/LogicManagerTest.java
``` java
    @Test
    public void getBirthdayPanelFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        logic.getBirthdayPanelFilteredPersonList().remove(0);
    }

    @Test
    public void getReminderList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        logic.getReminderList().remove(0);
    }
```
###### /java/seedu/address/logic/parser/AddReminderCommandParserTest.java
``` java
public class AddReminderCommandParserTest {

    private AddReminderCommandParser parser = new AddReminderCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        Reminder expectedReminder = new ReminderBuilder().build();
        assertParseSuccess(parser, REMINDER_DESC_COFFEE + REMINDER_DESC_DATE_COFFEE
                + REMINDER_DESC_TIME_COFFEE, new AddReminderCommand(expectedReminder));
    }

    @Test
    public void parse_fieldMissing_failure() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddReminderCommand.MESSAGE_USAGE);

        // missing reminder
        assertParseFailure(parser, REMINDER_DESC_DATE_COFFEE + REMINDER_DESC_TIME_COFFEE, expectedMessage);

        // missing date
        assertParseFailure(parser, REMINDER_DESC_COFFEE + REMINDER_DESC_TIME_COFFEE, expectedMessage);

        // missing time
        assertParseFailure(parser, REMINDER_DESC_COFFEE + REMINDER_DESC_DATE_COFFEE, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddReminderCommand.MESSAGE_USAGE);

        //invalid reminder
        assertParseFailure(parser, INVALID_REMINDER_DESC + REMINDER_DESC_DATE_COFFEE + REMINDER_DESC_TIME_COFFEE,
                expectedMessage);

        //invalid date
        assertParseFailure(parser, REMINDER_DESC_COFFEE + INVALID_REMINDER_DESC_DATE + REMINDER_DESC_TIME_COFFEE,
                Date.MESSAGE_DATE_CONSTRAINTS);

        //invalid time
        assertParseFailure(parser, REMINDER_DESC_COFFEE + REMINDER_DESC_DATE_COFFEE + INVALID_REMINDER_DESC_TIME,
                Time.MESSAGE_TIME_CONSTRAINTS);
    }
}
```
###### /java/seedu/address/logic/parser/AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_addReminder() throws Exception {
        Reminder reminder = new ReminderBuilder().build();
        AddReminderCommand command = (AddReminderCommand) parser.parseCommand(AddReminderCommand.COMMAND_WORD
                + REMINDER_DESC_COFFEE + REMINDER_DESC_DATE_COFFEE + REMINDER_DESC_TIME_COFFEE);
        assertEquals(new AddReminderCommand(reminder), command);
    }
```
###### /java/seedu/address/logic/parser/AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_deleteReminder() throws Exception {
        DeleteReminderCommand command = (DeleteReminderCommand) parser.parseCommand(
                DeleteReminderCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteReminderCommand(INDEX_FIRST_PERSON), command);
    }
```
###### /java/seedu/address/logic/parser/AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_editReminder() throws Exception {
        Reminder reminder = new ReminderBuilder().build();
        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder(reminder).build();
        EditReminderCommand command = (EditReminderCommand) parser
                .parseCommand(EditReminderCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased()
                + " " + REMINDER_DESC_COFFEE + REMINDER_DESC_DATE_COFFEE + REMINDER_DESC_TIME_COFFEE);
        assertEquals(new EditReminderCommand(INDEX_FIRST_PERSON, descriptor), command);
    }
```
###### /java/seedu/address/logic/parser/AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_toggle() throws Exception {
        assertTrue(parser.parseCommand(ToggleCommand.COMMAND_WORD) instanceof ToggleCommand);
        assertTrue(parser.parseCommand(ToggleCommand.COMMAND_WORD + " 3") instanceof ToggleCommand);
    }
```
###### /java/seedu/address/logic/parser/DeleteReminderCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteReminderCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteReminderCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteReminderCommandParserTest {
    private DeleteReminderCommandParser parser = new DeleteReminderCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteReminderCommand() throws IOException {
        assertParseSuccess(parser, "1", new DeleteReminderCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() throws IOException {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteReminderCommand.MESSAGE_USAGE));
    }
}
```
###### /java/seedu/address/logic/parser/EditReminderCommandParserTest.java
``` java
public class EditReminderCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditReminderCommand.MESSAGE_USAGE);

    private EditReminderCommandParser parser = new EditReminderCommandParser();

    @Test
    public void parse_missingParts_failure() throws IOException {
        // no index specified
        assertParseFailure(parser, VALID_REMINDER_COFFEE, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditReminderCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() throws IOException {
        // negative index
        assertParseFailure(parser, "-5" + REMINDER_DESC_COFFEE, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + REMINDER_DESC_COFFEE, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() throws IOException {
        assertParseFailure(parser, "1" + INVALID_REMINDER_DESC, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditReminderCommand.MESSAGE_REMINDER_FORMAT)); // invalid reminder
        assertParseFailure(parser, "1" + INVALID_REMINDER_DESC_DATE, Date.MESSAGE_DATE_CONSTRAINTS); // invalid date
        assertParseFailure(parser, "1" + INVALID_REMINDER_DESC_TIME, Time.MESSAGE_TIME_CONSTRAINTS); // invalid time

        // invalid date followed by valid time
        assertParseFailure(parser, "1" + INVALID_REMINDER_DESC_DATE + VALID_REMINDER_TIME_COFFEE,
                Date.MESSAGE_DATE_CONSTRAINTS);

        // valid date followed by invalid date
        assertParseFailure(parser, "1" + REMINDER_DESC_COFFEE + INVALID_REMINDER_DESC_DATE,
                Date.MESSAGE_DATE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_REMINDER_DESC + INVALID_REMINDER_DESC_TIME
                + INVALID_REMINDER_DESC_DATE, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditReminderCommand.MESSAGE_REMINDER_FORMAT));
    }

    @Test
    public void parse_allFieldsSpecified_success() throws IOException {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + REMINDER_DESC_COFFEE + REMINDER_DESC_DATE_COFFEE
                + REMINDER_DESC_TIME_COFFEE;

        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder().withReminder(VALID_REMINDER_COFFEE)
                .withDate(VALID_REMINDER_DATE_COFFEE).withTime(VALID_REMINDER_TIME_COFFEE).build();
        EditReminderCommand expectedCommand = new EditReminderCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() throws IOException {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + REMINDER_DESC_COFFEE + REMINDER_DESC_DATE_COFFEE;

        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder().withReminder(VALID_REMINDER_COFFEE)
                .withDate(VALID_REMINDER_DATE_COFFEE).build();
        EditReminderCommand expectedCommand = new EditReminderCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() throws IOException {
        // reminder
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + REMINDER_DESC_COFFEE;
        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder()
                .withReminder(VALID_REMINDER_COFFEE).build();
        EditReminderCommand expectedCommand = new EditReminderCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // date
        userInput = targetIndex.getOneBased() + REMINDER_DESC_DATE_COFFEE;
        descriptor = new EditReminderDescriptorBuilder().withDate(VALID_REMINDER_DATE_COFFEE).build();
        expectedCommand = new EditReminderCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // time
        userInput = targetIndex.getOneBased() + REMINDER_DESC_TIME_COFFEE;
        descriptor = new EditReminderDescriptorBuilder().withTime(VALID_REMINDER_TIME_COFFEE).build();
        expectedCommand = new EditReminderCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() throws IOException {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased()  + REMINDER_DESC_ASSIGNMENT + REMINDER_DESC_DATE_COFFEE
                + REMINDER_DESC_DATE_ASSIGNMENT + REMINDER_DESC_TIME_ASSIGNMENT + REMINDER_DESC_TIME_COFFEE
                + REMINDER_DESC_COFFEE;

        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder().withReminder(VALID_REMINDER_COFFEE)
                .withDate(VALID_REMINDER_DATE_ASSIGNMENT).withTime(VALID_REMINDER_TIME_COFFEE).build();
        EditReminderCommand expectedCommand = new EditReminderCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() throws IOException {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_REMINDER_DESC_DATE + REMINDER_DESC_DATE_COFFEE;
        EditReminderDescriptor descriptor = new EditReminderDescriptorBuilder()
                .withDate(VALID_REMINDER_DATE_COFFEE).build();
        EditReminderCommand expectedCommand = new EditReminderCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // other valid values specified
        userInput = targetIndex.getOneBased() + REMINDER_DESC_ASSIGNMENT + INVALID_REMINDER_DESC_DATE
                + REMINDER_DESC_DATE_COFFEE + REMINDER_DESC_TIME_ASSIGNMENT;
        descriptor = new EditReminderDescriptorBuilder().withReminder(VALID_REMINDER_ASSIGNMENT)
                .withDate(VALID_REMINDER_DATE_COFFEE).withTime(VALID_REMINDER_TIME_ASSIGNMENT).build();
        expectedCommand = new EditReminderCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
```
###### /java/seedu/address/model/ModelManagerTest.java
``` java
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
```
###### /java/seedu/address/model/ModelManagerTest.java
``` java
        // different list of reminders -> return false
        XmlSerializableReminders differentReminders = new XmlSerializableReminders();
        UniqueReminderList uniqueDifferentReminders = new UniqueReminderList(differentReminders);
        assertFalse(modelManager.equals(new ModelManager(addressBook, uniqueDifferentReminders, userPrefs)));
```
###### /java/seedu/address/model/person/BirthdayInCurrentMonthPredicateTest.java
``` java
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
```
###### /java/seedu/address/model/person/BirthdayTest.java
``` java
public class BirthdayTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void isValidBirthday() {
        // day out of bounds -> returns false
        assertFalse(Birthday.isValidBirthday("32.3.1995"));
        assertFalse(Birthday.isValidBirthday("31.4.1995"));
        assertFalse(Birthday.isValidBirthday("0.1.1995"));

        // month out of bounds -> returns false
        assertFalse(Birthday.isValidBirthday("21.13.1995"));
        assertFalse(Birthday.isValidBirthday("21.0.1995"));

        // year too small -> returns false
        assertFalse(Birthday.isValidBirthday("01.01.999"));
        assertFalse(Birthday.isValidBirthday("01.01.1899"));

        // year too big -> returns false
        assertFalse(Birthday.isValidBirthday("01.01.10000"));
        assertFalse(Birthday.isValidBirthday("01.01.2100"));

        // input format not correct -> returns false
        assertFalse(Birthday.isValidBirthday("1 January 1999"));
        assertFalse(Birthday.isValidBirthday("10/ 10 / 1992"));

        // input values are not integers -> returns false
        assertFalse(Birthday.isValidBirthday("3.0/10.0/1995"));

        // not even a date -> returns false
        assertFalse(Birthday.isValidBirthday("Not a date"));

        // wrong leap day date -> returns false
        assertFalse(Birthday.isValidBirthday("29/2/2017"));

        // space -> returns true
        assertTrue(Birthday.isValidBirthday(" "));

        // empty string -> returns true
        assertTrue(Birthday.isValidBirthday(""));

        // valid dates using / separator -> returns true
        assertTrue(Birthday.isValidBirthday("31/3/1995"));
        assertTrue(Birthday.isValidBirthday("31/03/1995"));
        assertTrue(Birthday.isValidBirthday("01/03/1995"));

        // valid dates using - separator -> returns true
        assertTrue(Birthday.isValidBirthday("01-03-1995"));
        assertTrue(Birthday.isValidBirthday("31-03-1995"));
        assertTrue(Birthday.isValidBirthday("01-03-1995"));

        // valid dates using . separator -> returns true
        assertTrue(Birthday.isValidBirthday("01.03.1995"));
        assertTrue(Birthday.isValidBirthday("31.03.1995"));
        assertTrue(Birthday.isValidBirthday("01.03.1995"));

        // valid leap year day -> returns true
        assertTrue(Birthday.isValidBirthday("29-2-2016"));
        assertTrue(Birthday.isValidBirthday("29-2-2000"));

        // flexible day-month-year separator -> returns true
        assertTrue(Birthday.isValidBirthday("01/03.1995"));
        assertTrue(Birthday.isValidBirthday("31.03/1995"));
        assertTrue(Birthday.isValidBirthday("01/03-1995"));
    }

}
```
###### /java/seedu/address/model/reminder/DateTest.java
``` java
public class DateTest {

    @Test
    public void isValidDate() {
        // day out-of-bounds -> returns false
        assertFalse(Date.isValidDate("0/01/2017"));
        assertFalse(Date.isValidDate("31/04/2017"));

        // month out-of-bounds -> returns false
        assertFalse(Date.isValidDate("01/13/2017"));
        assertFalse(Date.isValidDate("01/00/2017"));

        // year out-of-bounds -> returns false
        assertFalse(Date.isValidDate("01/13/2100"));
        assertFalse(Date.isValidDate("01/13/1899"));

        // incorrect date input format -> returns false
        assertFalse(Date.isValidDate("1 Mar 2017"));

        // input is not even a date -> returns false
        assertFalse(Date.isValidDate("Not a date"));

        // invalid leap day -> returns false
        assertFalse(Date.isValidDate("29/02/2017"));

        // valid leap day -> returns true
        assertTrue(Date.isValidDate("29/02/2016"));

        // valid date -> returns true
        assertTrue(Date.isValidDate("01/01/2017"));
        assertTrue(Date.isValidDate("01.01.2017"));
        assertTrue(Date.isValidDate("01-01-2017"));
    }

    @Test
    public void equals() throws Exception {

        Date date1 = new Date("01/01/2017");

        // different date -> returns false
        Date date2 = new Date("02/01/2017");
        assertFalse(date1.equals(date2));

        // different type -> returns false
        assertFalse(date1.equals(100));

        // null -> returns false
        assertFalse(date1.equals(null));

        // same object -> returns true
        assertTrue(date1.equals(date1));

        // same value -> returns true
        Date date3 = new Date("01/01/2017");
        assertTrue(date1.equals(date3));
    }
}
```
###### /java/seedu/address/model/reminder/TimeTest.java
``` java
public class TimeTest {

    @Test
    public void isValidTime() {
        // invalid 24-hr time -> returns false
        assertFalse(Time.isValidTime("24:00"));
        assertFalse(Time.isValidTime("08:60"));

        // incorrect time input format -> returns false
        assertFalse(Time.isValidTime("8AM"));
        assertFalse(Time.isValidTime("0800"));

        // input is not even a time -> returns false
        assertFalse(Time.isValidTime("Not a time"));

        // valid time -> returns true
        assertTrue(Time.isValidTime("00:00"));
        assertTrue(Time.isValidTime("08:00"));
        assertTrue(Time.isValidTime("23:59"));
    }

    @Test
    public void equals() throws Exception {

        Time time1 = new Time("08:00");

        // different time -> returns false
        Time time2 = new Time("08:01");
        assertFalse(time1.equals(time2));

        // different type -> returns false
        assertFalse(time1.equals(100));

        // null -> returns false
        assertFalse(time1.equals(null));

        // same object -> returns true
        assertTrue(time1.equals(time1));

        // same value -> returns true
        Time time3 = new Time("08:00");
        assertTrue(time1.equals(time3));
    }
}
```
###### /java/seedu/address/model/reminder/UniqueReminderListTest.java
``` java
public class UniqueReminderListTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final UniqueReminderList reminderList = new UniqueReminderList();

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        UniqueReminderList uniqueReminderList = new UniqueReminderList();
        thrown.expect(UnsupportedOperationException.class);
        uniqueReminderList.asObservableList().remove(0);
    }

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), reminderList.asObservableList());
    }

    @Test
    public void constructor_withDuplicateReminders_throwsAssertionError() {
        // repeat coffee reminder twice
        List<Reminder> newReminders = Arrays.asList(COFFEE_REMINDER, COFFEE_REMINDER);
        XmlSerializableReminders serializableReminders = new XmlSerializableReminders(newReminders);

        thrown.expect(AssertionError.class);
        new UniqueReminderList(serializableReminders);
    }

    @Test
    public void contains_withReminderInside_returnTrue() throws Exception {
        reminderList.add(COFFEE_REMINDER);
        assertTrue(reminderList.contains(COFFEE_REMINDER));
    }

    @Test
    public void contains_withoutReminderInside_returnFalse() {
        assertFalse(reminderList.contains(COFFEE_REMINDER));
    }

    @Test
    public void add_null_throwsNullPointerException() throws DuplicateReminderException {
        thrown.expect(NullPointerException.class);
        reminderList.add(null);
    }

    @Test
    public void size_initialSizeZero() throws Exception {
        assertEquals(0, reminderList.size());

        // after adding one Reminder, size should increase by 1
        reminderList.add(COFFEE_REMINDER);
        assertEquals(1, reminderList.size());
    }

    @Test
    public void remove_null_throwsNullPointerException() throws ReminderNotFoundException {
        thrown.expect(NullPointerException.class);
        reminderList.remove(null);
    }

    @Test
    public void setReminders_sameReminders() {
        reminderList.setReminders(getUniqueTypicalReminders());
        assertEquals(reminderList, getUniqueTypicalReminders());
    }

    @Test
    public void equals() throws Exception {

        // null -> returns false
        assertFalse(reminderList.equals(null));

        // different types -> returns false
        assertFalse(reminderList.equals(0));

        // different reminder -> return false
        reminderList.setReminders(getUniqueTypicalReminders());
        UniqueReminderList differentReminderList = new UniqueReminderList();
        differentReminderList.setReminders(getUniqueTypicalReminders());
        differentReminderList.add(MEETING_REMINDER);
        assertFalse(reminderList.equals(differentReminderList));

        // same object -> returns true
        assertTrue(reminderList.equals(reminderList));

        // same reminders -> returns true
        assertTrue(reminderList.equals(getUniqueTypicalReminders()));
    }
}
```
###### /java/seedu/address/storage/StorageManagerTest.java
``` java
    @Test
    public void remindersReadSave() throws Exception {
        UniqueReminderList original = getUniqueTypicalReminders();
        storageManager.saveReminders(original);
        UniqueReminderList retrieved = new UniqueReminderList(storageManager.readReminders().get());
        assertEquals(original, retrieved);
    }
```
###### /java/seedu/address/storage/StorageManagerTest.java
``` java
    @Test
    public void getRemindersFilePath() {
        assertNotNull(storageManager.getRemindersFilePath());
    }
```
###### /java/seedu/address/storage/StorageManagerTest.java
``` java
    @Test
    public void handleRemindersChangedEvent_exceptionThrown_eventRaised() {
        // Create a StorageManager while injecting a stub that  throws an exception when the save method is called
        Storage storage = new StorageManager(new XmlAddressBookStorage("dummy"),
                new XmlRemindersStorageExceptionThrowingStub("dummy"),
                new JsonUserPrefsStorage("dummy"));
        storage.handleRemindersChangedEvent(new RemindersChangedEvent(new UniqueReminderList()));
        assertTrue(eventsCollectorRule.eventsCollector.getMostRecent() instanceof DataSavingExceptionEvent);
    }


    /**
     * A Stub class to throw an exception when the save method is called
     */
    class XmlRemindersStorageExceptionThrowingStub extends XmlRemindersStorage {

        public XmlRemindersStorageExceptionThrowingStub(String filePath) {
            super(filePath);
        }

        @Override
        public void saveReminders(UniqueReminderList reminderList, String filePath) throws IOException {
            throw new IOException("dummy exception");
        }
    }
```
###### /java/seedu/address/storage/XmlRemindersStorageTest.java
``` java
public class XmlRemindersStorageTest {
    private static final String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/XmlRemindersStorageTest/");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        readReminders(null);
    }

    private java.util.Optional<XmlSerializableReminders> readReminders(String filePath) throws Exception {
        return new XmlRemindersStorage(filePath).readReminders(addToTestDataPathIfNotNull(filePath));
    }

    private String addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER + prefsFileInTestDataFolder
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readReminders("NonExistentFile.xml").isPresent());
    }

    @Test
    public void read_notXmlFormat_exceptionThrown() throws Exception {
        thrown.expect(DataConversionException.class);
        readReminders("NotXmlFormatReminders.xml");
    }

    @Test
    public void readAndSaveReminders_allInOrder_success() throws Exception {
        String filePath = testFolder.getRoot().getPath() + "TempReminders.xml";
        UniqueReminderList original = getUniqueTypicalReminders();
        XmlRemindersStorage xmlRemindersStorage = new XmlRemindersStorage(filePath);

        //Save in new file and read back
        xmlRemindersStorage.saveReminders(original, filePath);
        XmlSerializableReminders readBack = xmlRemindersStorage.readReminders(filePath).get();
        assertEquals(original, new UniqueReminderList(readBack));

        //Modify data, overwrite exiting file, and read back
        original.add(new Reminder(MEETING_REMINDER));
        original.remove(new Reminder(HOMEWORK_REMINDER));
        xmlRemindersStorage.saveReminders(original, filePath);
        readBack = xmlRemindersStorage.readReminders(filePath).get();
        assertEquals(original, new UniqueReminderList(readBack));

        //Save and read without specifying file path
        original.add(new Reminder(DENTIST_REMINDER));
        xmlRemindersStorage.saveReminders(original); //file path not specified
        readBack = xmlRemindersStorage.readReminders().get(); //file path not specified
        assertEquals(original, new UniqueReminderList(readBack));

    }

    @Test
    public void saveReminders_nullReminders_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        saveReminders(null, "SomeFile.xml");
    }

    @Test
    public void getReminderList_modifyList_throwsUnsupportedOperationException() {
        UniqueReminderList reminderList = new UniqueReminderList();
        thrown.expect(UnsupportedOperationException.class);
        reminderList.asObservableList().remove(0);
    }

    /**
     * Saves {@code reminders} at the specified {@code filePath}.
     */
    private void saveReminders(UniqueReminderList reminders, String filePath) {
        try {
            new XmlRemindersStorage(filePath).saveReminders(reminders, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveReminders_nullFilePath_throwsNullPointerException() throws IOException {
        thrown.expect(NullPointerException.class);
        saveReminders(new UniqueReminderList(), null);
    }

}

```
###### /java/seedu/address/testutil/EditReminderDescriptorBuilder.java
``` java
/**
 * A utility class to help with building EditReminderDescriptor objects.
 */
public class EditReminderDescriptorBuilder {

    private EditReminderDescriptor descriptor;

    public EditReminderDescriptorBuilder() {
        descriptor = new EditReminderDescriptor();
    }

    public EditReminderDescriptorBuilder(EditReminderDescriptor descriptor) {
        this.descriptor = new EditReminderDescriptor(descriptor);
    }

    public EditReminderDescriptorBuilder(Reminder reminder) {
        descriptor = new EditReminderDescriptor();
        descriptor.setReminder(reminder.getReminder());
        descriptor.setDate(reminder.getDate());
        descriptor.setTime(reminder.getTime());
    }

    /**
     * Sets the reminder of the {@code EditReminderDescriptor} that we are building.
     */
    public EditReminderDescriptorBuilder withReminder(String reminder) {
        if (reminder.trim().isEmpty()) {
            throw new IllegalArgumentException("reminder is expected to be unique.");
        }
        descriptor.setReminder(reminder);
        return this;
    }

    /**
     * Sets the {@code Date} of the {@code EditReminderDescriptor} that we are building.
     */
    public EditReminderDescriptorBuilder withDate(String date) {
        try {
            ParserUtil.parseDate(Optional.of(date)).ifPresent(descriptor::setDate);
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("date is expected to be unique.");
        }
        return this;
    }

    /**
     * Sets the {@code Date} of the {@code EditReminderDescriptor} that we are building.
     */
    public EditReminderDescriptorBuilder withTime(String time) {
        try {
            ParserUtil.parseTime(Optional.of(time)).ifPresent(descriptor::setTime);
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("time is expected to be unique.");
        }
        return this;
    }

    public EditReminderDescriptor build() {
        return descriptor;
    }
}
```
###### /java/seedu/address/testutil/ReminderBuilder.java
``` java
/**
 * A utility class to help with building Reminder objects.
 */
public class ReminderBuilder {

    public static final String DEFAULT_REMINDER = "Drink coffee";
    public static final String DEFAULT_DATE = "01/11/2017";
    public static final String DEFAULT_TIME = "08:00";

    private Reminder reminder;

    public ReminderBuilder() {
        try {
            Date date = new Date(DEFAULT_DATE);
            Time time = new Time(DEFAULT_TIME);
            this.reminder = new Reminder(DEFAULT_REMINDER, date, time);
        } catch (IllegalValueException ive) {
            throw new AssertionError("Default values cannot be wrong!");
        }
    }

    /**
     * Initializes the ReminderBuilder with the data of {@code reminderToCopy}.
     */
    public ReminderBuilder(Reminder reminderToCopy) {
        this.reminder = new Reminder(reminderToCopy);
    }

    /**
     * Sets the {@code reminder} of the {@code Reminder} that we are building.
     */
    public ReminderBuilder withReminder(String reminder) {
        this.reminder.setReminder(reminder);
        return this;
    }

    /**
     * Sets the {@code Time} of the {@code Reminder} that we are building.
     */
    public ReminderBuilder withTime(String time) {
        try {
            this.reminder.setTime(new Time(time));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("name is expected to be unique.");
        }
        return this;
    }

    /**
     * Sets the {@code Date} of the {@code Reminder} that we are building.
     */
    public ReminderBuilder withDate(String date) {
        try {
            this.reminder.setDate(new Date(date));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("date is expected to be unique.");
        }
        return this;
    }

    public Reminder build() {
        return this.reminder;
    }
}
```
###### /java/seedu/address/testutil/TypicalReminders.java
``` java
/**
 * A utility class containing a list of {@code Reminder} objects to be used in tests.
 */
public class TypicalReminders {

    public static final Reminder COFFEE_REMINDER = new ReminderBuilder().build();
    public static final Reminder HOMEWORK_REMINDER = new ReminderBuilder().withReminder("Do homework")
                                                        .withDate("01/01/2018").withTime("07:30").build();
    public static final Reminder DINNER_REMINDER = new ReminderBuilder().withReminder("Dinner with family")
                                                        .withDate("25/12/2017").withTime("18:00").build();

    // Manually added
    public static final Reminder MEETING_REMINDER = new ReminderBuilder().withReminder("Meet with CS2103 group")
                                                  .withDate("09/09/2017").withTime("12:00").build();
    public static final Reminder DENTIST_REMINDER = new ReminderBuilder().withReminder("Go for dental checkup")
            .withDate("10/10/2017").withTime("14:00").build();

    private TypicalReminders() {} //prevents instantiation

    public static List<Reminder> getTypicalReminders() {
        return new ArrayList<>(Arrays.asList(COFFEE_REMINDER, HOMEWORK_REMINDER, DINNER_REMINDER));
    }

    /**
     * Utility method to return a UniqueReminderList containing a list of {@code Reminder} objects
     * to be used in tests.
     */
    public static UniqueReminderList getUniqueTypicalReminders() {
        return new UniqueReminderList(new XmlSerializableReminders(getTypicalReminders()));
    }
}
```
###### /java/seedu/address/ui/BirthdayReminderCardTest.java
``` java
public class BirthdayReminderCardTest extends GuiUnitTest {

    @Test
    public void display() {
        // no nickname
        Person personWithNoNickName = new PersonBuilder().withNickname("").build();
        BirthdayReminderCard birthdayCard = new BirthdayReminderCard(personWithNoNickName, 1);
        uiPartRule.setUiPart(birthdayCard);
        assertCardDisplay(birthdayCard, personWithNoNickName, 1);

        // with nickname
        Person personWithNickname = new PersonBuilder().withNickname("nickname").build();
        birthdayCard = new BirthdayReminderCard(personWithNickname, 2);
        uiPartRule.setUiPart(birthdayCard);
        assertCardDisplay(birthdayCard, personWithNickname, 2);

        //changes made to Person reflects on card
        guiRobot.interact(() -> {
            personWithNickname.setName(ALICE.getName());
            personWithNickname.setNickname(ALICE.getNickname());
        });
        assertCardDisplay(birthdayCard, personWithNickname, 2);
    }

    @Test
    public void equals() {
        Person person = new PersonBuilder().build();
        BirthdayReminderCard birthdayCard = new BirthdayReminderCard(person, 0);

        // same person, same index -> returns true
        BirthdayReminderCard copy = new BirthdayReminderCard(person, 0);
        assertTrue(birthdayCard.equals(copy));

        // same object -> returns true
        assertTrue(birthdayCard.equals(birthdayCard));

        // null -> returns false
        assertFalse(birthdayCard.equals(null));

        // different types -> returns false
        assertFalse(birthdayCard.equals(0));

        // different person, same index -> returns false
        Person differentPerson = new PersonBuilder().withName("different name").build();
        assertFalse(birthdayCard.equals(new BirthdayReminderCard(differentPerson, 0)));

        // same person, different index -> returns false
        assertFalse(birthdayCard.equals(new BirthdayReminderCard(person, 1)));
    }

    /**
     * Asserts that {@code personCard} displays the details of {@code expectedPerson} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(BirthdayReminderCard personCard, ReadOnlyPerson expectedPerson, int expectedId) {
        guiRobot.pauseForHuman();

        BirthdayReminderCardHandle birthdayReminderCardHandle = new BirthdayReminderCardHandle(personCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", birthdayReminderCardHandle.getId());

        // verify person details are displayed correctly
        assertBirthdayReminderCardDisplaysPerson(expectedPerson, birthdayReminderCardHandle);
    }

}
```
###### /java/seedu/address/ui/PersonCardTest.java
``` java
public class PersonCardTest extends GuiUnitTest {

    @Test
    public void display() {
        // no tags
        Person personWithNoTags = new PersonBuilder().withTags(new String[0]).build();
        PersonCard personCard = new PersonCard(personWithNoTags, 1);
        uiPartRule.setUiPart(personCard);
        assertCardDisplay(personCard, personWithNoTags, 1);

        // with tags
        Person personWithTags = new PersonBuilder().build();
        personCard = new PersonCard(personWithTags, 2);
        uiPartRule.setUiPart(personCard);
        assertCardDisplay(personCard, personWithTags, 2);

        // changes made to Person reflects on card
        guiRobot.interact(() -> {
            personWithTags.setName(ALICE.getName());
            personWithTags.setAddress(ALICE.getAddress());
            personWithTags.setEmail(ALICE.getEmail());
            personWithTags.setPhone(ALICE.getPhone());
            personWithTags.setNickname(ALICE.getNickname());
            personWithNoTags.setDisplayPicture(ALICE.getDisplayPicture());
            personWithTags.setTags(ALICE.getTags());
        });
        assertCardDisplay(personCard, personWithTags, 2);
    }

    @Test
    public void equals() {
        Person person = new PersonBuilder().build();
        PersonCard personCard = new PersonCard(person, 0);

        // same person, same index -> returns true
        PersonCard copy = new PersonCard(person, 0);
        assertTrue(personCard.equals(copy));

        // same object -> returns true
        assertTrue(personCard.equals(personCard));

        // null -> returns false
        assertFalse(personCard.equals(null));

        // different types -> returns false
        assertFalse(personCard.equals(0));

        // different person, same index -> returns false
        Person differentPerson = new PersonBuilder().withName("differentName").build();
        assertFalse(personCard.equals(new PersonCard(differentPerson, 0)));

        // same person, different index -> returns false
        assertFalse(personCard.equals(new PersonCard(person, 1)));
    }

    /**
     * Asserts that {@code personCard} displays the details of {@code expectedPerson} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(PersonCard personCard, ReadOnlyPerson expectedPerson, int expectedId) {
        guiRobot.pauseForHuman();

        PersonCardHandle personCardHandle = new PersonCardHandle(personCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", personCardHandle.getId());

        // verify person details are displayed correctly
        assertCardDisplaysPerson(expectedPerson, personCardHandle);
    }
}
```
###### /java/seedu/address/ui/ReminderCardTest.java
``` java
public class ReminderCardTest extends GuiUnitTest {

    @Test
    public void display() {

        Reminder reminder = new ReminderBuilder().build();
        ReminderCard reminderCard = new ReminderCard(reminder, 1);
        uiPartRule.setUiPart(reminderCard);
        assertCardDisplay(reminderCard, reminder, 1);

        // changes made to Person reflects on card
        guiRobot.interact(() -> {
            reminder.setReminder(HOMEWORK_REMINDER.getReminder());
            reminder.setDate(HOMEWORK_REMINDER.getDate());
            reminder.setTime(HOMEWORK_REMINDER.getTime());
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
```
###### /java/seedu/address/ui/testutil/GuiTestAssert.java
``` java
    /**
     * Asserts that {@code actualCard} displays the details of {@code expectedPerson}.
     */
    public static void assertBirthdayReminderCardDisplaysPerson(ReadOnlyPerson expectedPerson,
                                                                BirthdayReminderCardHandle actualCard) {
        assertEquals(expectedPerson.getName().fullName, actualCard.getName());
        assertEquals(expectedPerson.getBirthday().value, actualCard.getBirthday());
        assertEquals(expectedPerson.getNickname().value, actualCard.getNickname());
    }

    /**
     * Asserts that {@code actualCard} displays the details of {@code expectedReminder}.
     */
    public static void assertReminderCardDisplaysReminder(Reminder expectedReminder,
                                                                ReminderCardHandle actualCard) {
        assertEquals(expectedReminder.getReminder(), actualCard.getReminder());
        assertEquals(expectedReminder.getDate().toString(), actualCard.getDate());
        assertEquals(expectedReminder.getTime().toString(), actualCard.getTime());
    }
```
