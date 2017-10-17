package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DISPLAYPICTURE;

import java.io.IOException;
import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.storage.ReadAndStoreImage;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.DisplayPicture;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Adds a display picture to an existing person in address book
 */
public class DisplayPictureCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "displaypic";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds/Updates the profile picture of a person identified "
            + "by the index number used in the last person listing. "
            + "Existing Display picture will be updated by the image referenced in the input path. \n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_DISPLAYPICTURE + "[PATH]\n"
            + "Example: " + COMMAND_WORD + " 2 "
            + "C:\\Users\\Admin\\Desktop\\pic.jpg";

    public static final String MESSAGE_ADD_DISPLAYPICTURE_SUCCESS = "Added Display Picture to Person: %1$s";

    public static final String MESSAGE_DELETE_DISPLAYPICTURE_SUCCESS = "Removed Display Picture from Person: %1$s";

    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    public static final String MESSAGE_IMAGE_PATH_FAIL =
            "This specified path cannot be read. Please check it's validity and try again";


    private Index index;
    private DisplayPicture displayPicture;

    /**
     * @param index of the person in the filtered person list to edit the remark
     * @param displayPicture of the person
     */
    public DisplayPictureCommand(Index index, DisplayPicture displayPicture) {
        requireNonNull(index);
        requireNonNull(displayPicture);

        this.index = index;
        this.displayPicture = displayPicture;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException, IOException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        ReadOnlyPerson personToEdit = lastShownList.get(index.getZeroBased());

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadAndStoreImage readAndStoreImage = new ReadAndStoreImage();
        try {
            displayPicture.setPath(readAndStoreImage.execute(displayPicture.getPath(),
                    personToEdit.getEmail().hashCode()));
        } catch (IOException ioe) {
            displayPicture.setPath("");
        }
        if (displayPicture.getPath().equalsIgnoreCase("")) {
            return new CommandResult(generateFailureMessage());
        }


        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getBirthday(), personToEdit.getNickname(),
                displayPicture, personToEdit.getTags());

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredListToShowAll();

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates success message
     * @return String
     */
    private String generateFailureMessage() {
        return MESSAGE_IMAGE_PATH_FAIL;
    }

    /**
     * Generates failure message
     * @param personToEdit is checked
     * @return String
     */
    private String generateSuccessMessage(ReadOnlyPerson personToEdit) {
        if (!displayPicture.getPath().isEmpty()) {
            return String.format(MESSAGE_ADD_DISPLAYPICTURE_SUCCESS, personToEdit);
        } else {
            return String.format(MESSAGE_DELETE_DISPLAYPICTURE_SUCCESS, personToEdit);
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DisplayPictureCommand)) {
            return false;
        }

        // state check
        DisplayPictureCommand e = (DisplayPictureCommand) other;
        return index.equals(e.index)
                && displayPicture.equals(e.displayPicture);
    }
}
