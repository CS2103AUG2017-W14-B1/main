package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATH;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RANGE;

import java.io.IOException;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.storage.AddressBookStorage;
import seedu.address.storage.XmlAddressBookStorage;

/**
 * Export selected person/s by the index number or range in the last person listing.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Export selected person/s\n"
            + "by the index number or range in the last person listing\n"
            + "Parameters: " + PREFIX_RANGE + "[RANGE] "
            + PREFIX_PATH + "[PATH]\n"
            + "Example 1: " + COMMAND_WORD + " " + PREFIX_RANGE + "all " + PREFIX_PATH + "C:\\Exports\n"
            + "Example 2: " + COMMAND_WORD + " " + PREFIX_RANGE + "1 " + PREFIX_PATH + "C:\\Exports\n"
            + "Example 3: " + COMMAND_WORD + " " + PREFIX_RANGE + "1,2 " + PREFIX_PATH + "C:\\Exports\n"
            + "Example 4: " + COMMAND_WORD + " " + PREFIX_RANGE + "1-5 " + PREFIX_PATH + "C:\\Exports";

    public static final String MESSAGE_ARGUMENTS = "Range: %1$s, Path: %2$s";

    public static final String MESSAGE_EXPORT_FAIL = "Export Failed";
    public static final String MESSAGE_EXPORT_SUCCESS = "Export Successful";

    private final String range;
    private final String path;
    private AddressBook exportBook;

    public ExportCommand(String range, String path) {
        requireNonNull(range);
        requireNonNull(path);

        this.range = range;
        this.path = path;
        exportBook = new AddressBook();
    }

    @Override
    public CommandResult execute() throws CommandException {

        String[] multipleRange = getRangeFromInput();

        if (multipleRange[0].equals("all")) {
            exportAll();
        } else {
            for (int i = 0; i < multipleRange.length; i++) {
                if (multipleRange[i].contains("-")) {
                    String[] rangeToExport = multipleRange[i].split("-");
                    exportRange(Integer.parseInt(rangeToExport[0]), Integer.parseInt(rangeToExport[1]));
                } else {
                    exportSpecific(Integer.parseInt(multipleRange[i]));
                }
            }
        }

        try {
            AddressBookStorage storage = new XmlAddressBookStorage(path + ".xml");
            storage.saveAddressBook(exportBook);
        } catch (IOException ioe) {
            return new CommandResult(MESSAGE_EXPORT_FAIL);
        }
        return new CommandResult(MESSAGE_EXPORT_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ExportCommand)) {
            return false;
        }

        ExportCommand e = (ExportCommand) other;
        return range.equals(e.range) && path.equals(e.path);
    }

    /**
     *Export all contacts from last shown list
     */
    private void exportAll() {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        try {
            exportBook.setPersons(lastShownList);
        } catch (DuplicatePersonException e) {
            e.printStackTrace();
        }
    }

    /**
     *Export a specific contact from last shown list
     */
    private void exportSpecific(int index) {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        try {
            exportBook.addPerson(lastShownList.get(index - 1));
        } catch (DuplicatePersonException e) {
            e.printStackTrace();
        }
    }

    /**
     *Export a range of contacts from last shown list
     */
    private void exportRange(int start, int end) {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        try {
            for (int i = start - 1; i <= end - 1; i++) {
                exportBook.addPerson(lastShownList.get(i));
            }
        } catch (DuplicatePersonException e) {
            e.printStackTrace();
        }
    }

    private String[] getRangeFromInput() {
        String[] splitStringComma = this.range.split(",");

        return splitStringComma;
    }
}
