# chuaweiwen
###### \java\seedu\address\commons\events\ui\ChangeThemeRequestEvent.java
``` java
package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;
import seedu.address.logic.parser.Theme;

/**
 * Indicates a request to change the theme
 */
public class ChangeThemeRequestEvent extends BaseEvent {

    public final Theme theme;

    public ChangeThemeRequestEvent(Theme theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### \java\seedu\address\logic\commands\FilterCommand.java
``` java
package seedu.address.logic.commands;

import seedu.address.model.person.NameAndTagsContainsKeywordsPredicate;

/**
 * Finds and lists all persons in address book whose name and/or tags contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names and tags contain"
            + "any of the specified keywords and displays them as a list with index numbers.\n"
            + "Parameters: [n/NAME] [t/TAG]...\n"
            + "Note: At least one of the parameters must be specified.\n"
            + "Example: " + COMMAND_WORD + " n/Alex t/friends";

    private final NameAndTagsContainsKeywordsPredicate predicate;

    public FilterCommand(NameAndTagsContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute() {
        model.updateFilteredPersonList(predicate);
        return new CommandResult(getMessageForPersonListShownSummary(model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FilterCommand // instanceof handles nulls
                && this.predicate.equals(((FilterCommand) other).predicate)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\NicknameCommand.java
``` java
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Nickname;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Sets the nickname of an existing person in the address book.
 */
public class NicknameCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "nickname";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets the nickname to the person identified "
            + "by the index number used in the last person listing. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[NICKNAME]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "Adam";

    public static final String MESSAGE_SET_NICKNAME_SUCCESS = "Nickname successfully set to Person: %1$s";
    public static final String MESSAGE_REMOVE_NICKNAME_SUCCESS = "Nickname successfully removed from Person: %1$s";
    public static final String MESSAGE_UNCHANGED_NICKNAME = "Nickname remains unchanged for Person: %1$s";

    private final Index index;
    private final Nickname nickname;

    /**
     * @param index of the person in the filtered person list to edit
     * @param nickname details to edit the person with
     */
    public NicknameCommand(Index index, Nickname nickname) {
        requireNonNull(index);
        requireNonNull(nickname);

        this.index = index;
        this.nickname = nickname;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = (Person) lastShownList.get(index.getZeroBased());
        Nickname previousNickname;

        try {
            previousNickname = personToEdit.getNickname();
        } catch (NullPointerException npe) {
            throw new AssertionError("Nickname cannot be null");
        }

        personToEdit.setNickname(nickname);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        try {
            model.updatePerson(personToEdit, personToEdit);
        } catch (DuplicatePersonException dpe) {
            throw new AssertionError("The target person cannot be duplicated");
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }

        if (nickname.equals(previousNickname)) {
            return new CommandResult(String.format(MESSAGE_UNCHANGED_NICKNAME, personToEdit.getAsText()));
        } else if (nickname.value.equals("")) {
            return new CommandResult(String.format(MESSAGE_REMOVE_NICKNAME_SUCCESS, personToEdit.getAsText()));
        } else {
            return new CommandResult(String.format(MESSAGE_SET_NICKNAME_SUCCESS, personToEdit.getAsText()));
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NicknameCommand)) {
            return false;
        }

        // state check
        NicknameCommand e = (NicknameCommand) other;
        return index.equals(e.index) && nickname.equals(e.nickname);
    }
}
```
###### \java\seedu\address\logic\commands\ThemeCommand.java
``` java
package seedu.address.logic.commands;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.ChangeThemeRequestEvent;
import seedu.address.logic.parser.Theme;
import seedu.address.logic.parser.ThemeList;

/**
 * Changes the theme of the address book.
 */
public class ThemeCommand extends Command {

    public static final String COMMAND_WORD = "theme";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Changes the theme of the address book\n"
            + "Parameter: THEME\n"
            + "List of available themes: "
            + ThemeList.THEME_DARK + ", "
            + ThemeList.THEME_DAY + ", "
            + ThemeList.THEME_NIGHT + ", "
            + ThemeList.THEME_SKY + "\n"
            + "Example: " + COMMAND_WORD + " dark";

    public static final String MESSAGE_SET_THEME_SUCCESS = "Successfully set theme: %1$s";

    private final Theme theme;

    public ThemeCommand(Theme theme) {
        this.theme = theme;
    }

    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new ChangeThemeRequestEvent(theme));
        return new CommandResult(String.format(MESSAGE_SET_THEME_SUCCESS, theme.getTheme()));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ThemeCommand)) {
            return false;
        }

        // state check
        ThemeCommand e = (ThemeCommand) other;
        return theme.equals(e.theme);
    }
}
```
###### \java\seedu\address\logic\parser\AddressBookParser.java
``` java
        case NicknameCommand.COMMAND_WORD:
            return new NicknameCommandParser().parse(arguments);

        case ThemeCommand.COMMAND_WORD:
            return new ThemeCommandParser().parse(arguments);
```
###### \java\seedu\address\logic\parser\AddressBookParser.java
``` java
        case FilterCommand.COMMAND_WORD:
            return new FilterCommandParser().parse(arguments);
```
###### \java\seedu\address\logic\parser\FilterCommandParser.java
``` java
package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameAndTagsContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns an FilterCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_TAG)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        List<String> nameKeywordsList = new ArrayList<>();
        List<String> tagsKeywordsList = new ArrayList<>();

        String regex = "\\s+";

        // Extracting name
        if (!argMultimap.getAllValues(PREFIX_NAME).isEmpty()) {
            List<String> unprocessedNames = argMultimap.getAllValues(PREFIX_NAME);
            nameKeywordsList = Arrays.asList(getKeywordsFromList(unprocessedNames, regex));
        }

        if (!argMultimap.getAllValues(PREFIX_TAG).isEmpty()) {
            List<String> unprocessedTags = argMultimap.getAllValues(PREFIX_TAG);
            tagsKeywordsList = Arrays.asList(getKeywordsFromList(unprocessedTags, regex));
        }

        return new FilterCommand(new NameAndTagsContainsKeywordsPredicate(nameKeywordsList, tagsKeywordsList));
    }

    private boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    private String[] getKeywordsFromList(List<String> list, String regex) throws ParseException {
        String keywords = "";
        for (String string : list) {
            // string cannot be empty
            if (string.length() == 0) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
            }
            keywords = keywords + " " + string;
        }
        return keywords.trim().split(regex);
    }
}
```
###### \java\seedu\address\logic\parser\NicknameCommandParser.java
``` java
package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.NicknameCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Nickname;

/**
 * Parses input arguments and creates a new NicknameCommand object
 */
public class NicknameCommandParser implements Parser<NicknameCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the NicknameCommand
     * and returns an NicknameCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public NicknameCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String regex = "[\\s]+";
        String[] splitArgs = args.trim().split(regex, 2);

        Index index;
        try {
            index = ParserUtil.parseIndex(splitArgs[0]);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NicknameCommand.MESSAGE_USAGE));
        }

        String nickname;
        if (splitArgs.length > 1) {
            nickname = splitArgs[1];
        } else {
            nickname = "";
        }

        return new NicknameCommand(index, new Nickname(nickname));
    }
}
```
###### \java\seedu\address\logic\parser\Theme.java
``` java
package seedu.address.logic.parser;

/**
 * A value used to specify the theme of the address book.
 */
public class Theme {
    private final String theme;
    private final String filePath;

    public Theme(String theme, String filePath) {
        this.theme = theme;
        this.filePath = filePath;
    }

    public String getTheme() {
        return theme;
    }

    public String getFilePath() {
        return filePath;
    }

    public String toString() {
        return getTheme();
    }

    @Override
    public int hashCode() {
        return theme == null ? 0 : theme.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Theme)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Theme otherTheme = (Theme) obj;
        return otherTheme.getTheme().equals(getTheme()) && otherTheme.getFilePath().equals(getFilePath());
    }
}
```
###### \java\seedu\address\logic\parser\ThemeCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_THEME;

import seedu.address.logic.commands.ThemeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ThemeCommand object
 */
public class ThemeCommandParser implements Parser<ThemeCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ThemeCommand
     * and returns an ThemeCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ThemeCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ThemeCommand.MESSAGE_USAGE));
        }

        switch(trimmedArgs) {
        case ThemeList.THEME_DARK:
            return new ThemeCommand(new Theme(trimmedArgs, ThemeList.THEME_DARK_PATH));

        case ThemeList.THEME_DAY:
            return new ThemeCommand(new Theme(trimmedArgs, ThemeList.THEME_DAY_PATH));

        case ThemeList.THEME_NIGHT:
            return new ThemeCommand(new Theme(trimmedArgs, ThemeList.THEME_NIGHT_PATH));

        case ThemeList.THEME_SKY:
            return new ThemeCommand(new Theme(trimmedArgs, ThemeList.THEME_SKY_PATH));

        default:
            throw new ParseException(
                String.format(MESSAGE_UNKNOWN_THEME, ThemeCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\parser\ThemeList.java
``` java
package seedu.address.logic.parser;

/**
 * List of available themes
 */
public class ThemeList {
    public static final String DEFAULT_PATH = "view/";

    public static final String THEME_DARK = "dark";
    public static final String THEME_DAY = "day";
    public static final String THEME_NIGHT = "night";
    public static final String THEME_SKY = "sky";

    public static final String THEME_DARK_PATH = DEFAULT_PATH + "DarkTheme.css";
    public static final String THEME_DAY_PATH = DEFAULT_PATH + "DayTheme.css";
    public static final String THEME_NIGHT_PATH = DEFAULT_PATH + "NightTheme.css";
    public static final String THEME_SKY_PATH = DEFAULT_PATH + "SkyTheme.css";
}
```
###### \java\seedu\address\MainApp.java
``` java
    @Subscribe
    public void handleChangeThemeRequestEvent(ChangeThemeRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        userPrefs.setThemeFilePath(event.theme.getFilePath());
    }
```
###### \java\seedu\address\model\person\NameAndTagsContainsKeywordsPredicate.java
``` java
package seedu.address.model.person;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code ReadOnlyPerson}'s {@code Name} matches any of the keywords given.
 */
public class NameAndTagsContainsKeywordsPredicate implements Predicate<ReadOnlyPerson> {
    private final List<String> nameKeywords;
    private final List<String> tagKeywords;

    public NameAndTagsContainsKeywordsPredicate(List<String> nameKeywords, List<String> tagKeywords) {
        this.nameKeywords = nameKeywords;
        this.tagKeywords = tagKeywords;
    }

    @Override
    public boolean test(ReadOnlyPerson person) {
        boolean tagFound = false;

        int numTagKeywords = tagKeywords.size();
        int tagsMatchedCount = 0;
        if (!tagKeywords.isEmpty()) {
            tagsMatchedCount = countTagMatches(person);
        }

        if (tagsMatchedCount == numTagKeywords) {
            tagFound = true;
        }

        boolean nameFound = false;
        if (!nameKeywords.isEmpty()) {
            nameFound = nameKeywords.stream().allMatch(nameKeywords -> StringUtil
                    .containsWordIgnoreCase(person.getName().fullName, nameKeywords));
        }

        if (nameKeywords.isEmpty() && tagKeywords.isEmpty()) {
            throw new AssertionError("Either name or tag must be non-empty");
        } else if (nameKeywords.isEmpty()) {
            return tagFound;
        } else if (tagKeywords.isEmpty()) {
            return nameFound;
        }

        return nameFound && tagFound;
    }

    /**
     * Counts the number of matching tags of a person and returns the count
     */
    public int countTagMatches(ReadOnlyPerson person) {
        int tagsMatchedCount = 0;

        for (String keywords : tagKeywords) {
            if (hasTag(keywords, person)) {
                tagsMatchedCount++;
            }
        }
        return tagsMatchedCount;
    }

    /**
     * Returns true if the person's tag can be found in the keywords. Otherwise returns false.
     */
    public boolean hasTag(String keywords, ReadOnlyPerson person) {
        Set<Tag> tagsOfPerson = person.getTags();
        for (Tag tag : tagsOfPerson) {
            if (tag.tagName.equalsIgnoreCase(keywords)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof NameAndTagsContainsKeywordsPredicate // instanceof handles nulls
                && this.nameKeywords.equals(((NameAndTagsContainsKeywordsPredicate) other)
                .nameKeywords)
                && this.tagKeywords.equals(((NameAndTagsContainsKeywordsPredicate) other)
                .tagKeywords)); // state check
    }

}
```
###### \java\seedu\address\model\person\Nickname.java
``` java
package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's nickname in the address book.
 * Guarantees: immutable; is valid
 */
public class Nickname {

    public static final String MESSAGE_ADDRESS_CONSTRAINTS =
            "Person's nickname can take any values.";

    public final String value;

    public Nickname(String nickname) {
        requireNonNull(nickname);
        this.value = nickname;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Nickname // instanceof handles nulls
                && this.value.equals(((Nickname) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
```
###### \java\seedu\address\model\UserPrefs.java
``` java
    public String getThemeFilePath() {
        return themeFilePath;
    }

    public void setThemeFilePath(String themeFilePath) {
        this.themeFilePath = themeFilePath;
    }
```
###### \java\seedu\address\ui\MainWindow.java
``` java
    @Subscribe
    private void handleChangeThemeEvent(ChangeThemeRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        changeTheme(event.theme);
    }

    /**
     * Changes the theme
     */
    private void changeTheme(Theme theme) {
        Scene scene = primaryStage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(theme.getFilePath());
        scene.getStylesheets().add(STYLE);
    }
```
###### \resources\view\DayTheme.css
``` css
.background {
    -fx-background-color: #f2f8ff;
    background-color: #f2f8ff; /* Used in the default.html file */
}

.label {
    -fx-font-size: 15pt;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: derive(#383838, +30%);
    -fx-opacity: 1;
}

.label-bright {
    -fx-font-size: 11pt;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

.label-header {
    -fx-font-size: 32pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

.text-field {
    -fx-font-size: 12pt;
    -fx-font-family: "Segoe UI Semibold";
}

.tab-pane {
    -fx-padding: 0 0 0 1;
}

.tab-pane .tab-header-area {
    -fx-padding: 0 0 0 0;
    -fx-min-height: 0;
    -fx-max-height: 0;
}

.table-view {
    -fx-base: #1d1d1d;
    -fx-control-inner-background: #1d1d1d;
    -fx-background-color: #1d1d1d;
    -fx-table-cell-border-color: transparent;
    -fx-table-header-border-color: transparent;
    -fx-padding: 5;
}

.table-view .column-header-background {
    -fx-background-color: transparent;
}

.table-view .column-header, .table-view .filler {
    -fx-size: 35;
    -fx-border-width: 0 0 1 0;
    -fx-background-color: transparent;
    -fx-border-color:
        transparent
        transparent
        derive(-fx-base, 80%)
        transparent;
    -fx-border-insets: 0 10 1 0;
}

.table-view .column-header .label {
    -fx-font-size: 20pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-alignment: center-left;
    -fx-opacity: 1;
}

.table-view:focused .table-row-cell:filled:focused:selected {
    -fx-background-color: -fx-focus-color;
}

.split-pane:horizontal .split-pane-divider {
    -fx-background-color: #f2f8ff;
    -fx-border-color: #f2f8ff;
}

.split-pane {
    -fx-border-radius: 0;
    -fx-border-width: 0;
    -fx-background-color: #f2f8ff;
}

.list-view {
    -fx-background-insets: 0;
    -fx-padding: 0;
    -fx-background-color: #f2f8ff;
    -fx-border-width: 5;
    -fx-border-radius: 18 18 18 18;
}

.list-cell {
    -fx-label-padding: 0 0 0 0;
    -fx-graphic-text-gap : 0;
    -fx-background-radius: 18 18 18 18;
    -fx-border-radius: 18 18 18 18;
    -fx-padding: 10px;
    -fx-background-insets: 10px, 10px;
    -fx-background-color: transparent, -fx-background;
}

.list-cell:filled {
    -fx-background-color: derive(#d6e5ff, +15%);
}

.list-cell:filled:selected {
    -fx-background-color: derive(#d6e5ff, +40%);
}

.list-cell:filled:selected #cardPane {
    -fx-border-color: derive(#d6e5ff, -10%);
    -fx-border-width: 5;
    -fx-border-radius: 18 18 18 18;
}

.list-cell:filled:selected #popularContactPane {
    -fx-border-color: derive(#d6e5ff, -10%);
    -fx-border-width: 2;
    -fx-border-radius: 18 18 18 18;
}

.list-cell .label {
    -fx-text-fill: derive(#383838, -40%);
}

.list-cell:empty {
    /* Empty cells will not have alternating colours */
    -fx-background: #f2f8ff;
}

.cell_big_label {
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 16px;
    -fx-text-fill: white;
}

.cell_small_label {
    -fx-font-family: "Segoe UI";
    -fx-font-size: 13px;
    -fx-text-fill: white;
}

.anchor-pane {
     -fx-background-color: #f2f8ff;
}

.pane-with-border {
     -fx-background-color: #f2f8ff;
     -fx-border-color: #f2f8ff;
     -fx-border-top-width: 1px;
}

.status-bar {
    -fx-background-color: #f2f8ff;
    -fx-text-fill: black;
}

.result-display {
    -fx-background-color: #f2f8ff;
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 13pt;
    -fx-text-fill: derive(#383838, +30%);
}

.result-display .label {
    -fx-text-fill: black !important;
}

.scroll-bar:vertical .thumb,
.scroll-bar:horizontal .thumb {
    -fx-background-color: derive(#383838, +30%);
    -fx-background-insets: 2 2 2 2;
}

.scroll-bar:vertical .track-background,
.scroll-bar:horizontal .track-background {
    -fx-background-color: #f2f8ff;
}

.scroll-bar:vertical > .increment-button,
.scroll-bar:vertical > .decrement-button,
.scroll-bar:horizontal > .increment-button,
.scroll-bar:horizontal > .decrement-button {
    -fx-padding: 3px;
}

.scroll-bar:vertical > .increment-button > .increment-arrow {
    -fx-background-color: grey;
    -fx-shape: "M 0 0 L 4 8 L 8 0 Z";
    -fx-padding: 0.30em;
    -fx-rotate: 0;
}

.scroll-bar:horizontal > .increment-button > .increment-arrow {
    -fx-background-color: grey;
    -fx-shape: "M 0 0 L 4 8 L 8 0 Z";
    -fx-padding: 0.30em;
    -fx-rotate: -90;
}

.scroll-bar:vertical > .decrement-button > .decrement-arrow {
    -fx-background-color: grey;
    -fx-shape: "M 0 0 L 4 8 L 8 0 Z";
    -fx-padding: 0.30em;
    -fx-rotate: -180;
}

.scroll-bar:horizontal > .decrement-button > .decrement-arrow {
    -fx-background-color: grey;
    -fx-shape: "M 0 0 L 4 8 L 8 0 Z";
    -fx-padding: 0.30em;
    -fx-rotate: 90;
}

.status-bar .label {
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: derive(#383838, +30%);
}

.status-bar-with-border {
    -fx-background-color: derive(#1d1d1d, 30%);
    -fx-border-color: derive(#1d1d1d, 25%);
    -fx-border-width: 1px;
}

.status-bar-with-border .label {
    -fx-text-fill: white;
}

.grid-pane {
    -fx-background-color: #f2f8ff;
    -fx-border-color: #f2f8ff;
    -fx-border-width: 0px;
}

.grid-pane .anchor-pane {
    -fx-background-color: #f2f8ff;
}

.context-menu {
    -fx-background-color: derive(#1d1d1d, 50%);
}

.context-menu .label {
    -fx-text-fill: white;
}

.menu-bar {
    -fx-background-color: #f2f8ff;
}

.menu-bar .label {
    -fx-font-size: 14pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: derive(#383838, +30%);
    -fx-opacity: 0.9;
}

.menu .left-container {
    -fx-background-color: black;
}

/*
 * Metro style Push Button
 * Author: Pedro Duque Vieira
 * http://pixelduke.wordpress.com/2012/10/23/jmetro-windows-8-controls-on-java/
 */
.button {
    -fx-padding: 5 22 5 22;
    -fx-border-color: #e2e2e2;
    -fx-border-width: 2;
    -fx-background-radius: 0;
    -fx-background-color: #1d1d1d;
    -fx-font-family: "Segoe UI", Helvetica, Arial, sans-serif;
    -fx-font-size: 11pt;
    -fx-text-fill: #d8d8d8;
    -fx-background-insets: 0 0 0 0, 0, 1, 2;
}

.button:hover {
    -fx-background-color: #3a3a3a;
}

.button:pressed, .button:default:hover:pressed {
  -fx-background-color: #f2f8ff;
  -fx-text-fill: #1d1d1d;
}

.button:focused {
    -fx-border-color: #f2f8ff, #f2f8ff;
    -fx-border-width: 1, 1;
    -fx-border-style: solid, segments(1, 1);
    -fx-border-radius: 0, 0;
    -fx-border-insets: 1 1 1 1, 0;
}

.button:disabled, .button:default:disabled {
    -fx-opacity: 0.4;
    -fx-background-color: #1d1d1d;
    -fx-text-fill: white;
}

.button:default {
    -fx-background-color: -fx-focus-color;
    -fx-text-fill: #ffffff;
}

.button:default:hover {
    -fx-background-color: derive(-fx-focus-color, 30%);
}

.dialog-pane {
    -fx-background-color: #1d1d1d;
}

.dialog-pane > *.button-bar > *.container {
    -fx-background-color: #1d1d1d;
}

.dialog-pane > *.label.content {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: white;
}

.dialog-pane:header *.header-panel {
    -fx-background-color: derive(#1d1d1d, 25%);
}

.dialog-pane:header *.header-panel *.label {
    -fx-font-size: 18px;
    -fx-font-style: italic;
    -fx-fill: white;
    -fx-text-fill: white;
}

#cardPane {
    -fx-background-color: transparent;
    -fx-border-width: 0;
}

#commandTypeLabel {
    -fx-font-size: 11px;
    -fx-text-fill: #F70D1A;
}

#commandTextField {
    -fx-background-color: transparent #383838 transparent #383838;
    -fx-background-insets: 0;
    -fx-border-color: derive(#383838, +30%);
    -fx-border-insets: 0;
    -fx-border-width: 2;
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 13pt;
    -fx-text-fill: derive(#383838, +30%);
}

#filterField, #personListPanel, #personWebpage {
    -fx-effect: innershadow(gaussian, black, 10, 0, 0, 0);
}

#resultDisplay {
    -fx-border-color : derive(#383838, +30%);
}

#resultDisplay .content {
    -fx-background-color: #f2f8ff;
    -fx-background-radius: 0;
}

#tags {
    -fx-hgap: 7;
    -fx-vgap: 3;
}

#tags .label {
    -fx-text-fill: white;
    -fx-background-color: #3e7b91;
    -fx-padding: 1 3 1 3;
    -fx-border-radius: 18 18 18 18;
    -fx-background-radius: 18 18 18 18;
    -fx-font-size: 16;
}

#birthdayListHolder {
    -fx-background-color: #f2f8ff;
}

#birthdayListHolder > .label {
    -fx-background-color: #f2f8ff;
}

#reminderListHolder {
    -fx-background-color: #f2f8ff;
}

#reminderListHolder > .label {
    -fx-background-color: #f2f8ff;
}

#placeHolder {
    -fx-border-color: derive(#383838, +30%);
    -fx-border-width: 2;
}

.error {
    -fx-text-fill: derive(#dc143c, 0%) !important; /* The error class should always override the default text-fill style */
}

```
###### \resources\view\NightTheme.css
``` css
.background {
    -fx-background-color: derive(#383838, -40%);
    background-color: #383838; /* Used in the default.html file */
}

.label {
    -fx-font-size: 15pt;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: #f0ffff;
    -fx-opacity: 1;
}

.label-bright {
    -fx-font-size: 11pt;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

.label-header {
    -fx-font-size: 32pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

.text-field {
    -fx-font-size: 12pt;
    -fx-font-family: "Segoe UI Semibold";
}

.tab-pane {
    -fx-padding: 0 0 0 1;
}

.tab-pane .tab-header-area {
    -fx-padding: 0 0 0 0;
    -fx-min-height: 0;
    -fx-max-height: 0;
}

.table-view {
    -fx-base: #1d1d1d;
    -fx-control-inner-background: #1d1d1d;
    -fx-background-color: #1d1d1d;
    -fx-table-cell-border-color: transparent;
    -fx-table-header-border-color: transparent;
    -fx-padding: 5;
}

.table-view .column-header-background {
    -fx-background-color: transparent;
}

.table-view .column-header, .table-view .filler {
    -fx-size: 35;
    -fx-border-width: 0 0 1 0;
    -fx-background-color: transparent;
    -fx-border-color:
        transparent
        transparent
        derive(-fx-base, 80%)
        transparent;
    -fx-border-insets: 0 10 1 0;
}

.table-view .column-header .label {
    -fx-font-size: 20pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-alignment: center-left;
    -fx-opacity: 1;
}

.table-view:focused .table-row-cell:filled:focused:selected {
    -fx-background-color: -fx-focus-color;
}

.split-pane:horizontal .split-pane-divider {
    -fx-background-color: derive(#383838, -40%);
    -fx-border-color: derive(#383838, -40%);
}

.split-pane {
    -fx-border-radius: 0;
    -fx-border-width: 0;
    -fx-background-color: derive(#383838, -40%);
}

.list-view {
    -fx-background-insets: 0;
    -fx-padding: 0;
    -fx-background-color: derive(#383838, -40%);
    -fx-border-width: 5;
    -fx-border-radius: 18 18 18 18;
}

.list-cell {
    -fx-label-padding: 0 0 0 0;
    -fx-graphic-text-gap : 0;
    -fx-background-radius: 18 18 18 18;
    -fx-border-radius: 18 18 18 18;
    -fx-padding: 10px;
    -fx-background-insets: 10px, 10px;
    -fx-background-color: transparent, -fx-background;
}

.list-cell:filled {
    -fx-background-color: derive(#383838, 20%);
}

.list-cell:filled:selected {
    -fx-background-color: derive(#383838, +50%);
}

.list-cell:filled:selected #cardPane {
    -fx-border-color: derive(#383838, +100%);
    -fx-border-width: 3;
    -fx-border-radius: 18 18 18 18;
}

.list-cell:filled:selected #popularContactPane {
    -fx-border-color: derive(#383838, +100%);
    -fx-border-width: 2;
    -fx-border-radius: 18 18 18 18;
}

.list-cell .label {
    -fx-text-fill: white;
}

.list-cell:empty {
    /* Empty cells will not have alternating colours */
    -fx-background: derive(#383838, -40%);
}

.cell_big_label {
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 16px;
    -fx-text-fill: white;
}

.cell_small_label {
    -fx-font-family: "Segoe UI";
    -fx-font-size: 13px;
    -fx-text-fill: white;
}

.anchor-pane {
     -fx-background-color: derive(#383838, -40%);
}

.pane-with-border {
     -fx-background-color: derive(#383838, -40%);
     -fx-border-color: derive(#383838, -40%);
     -fx-border-top-width: 1px;
}

.status-bar {
    -fx-background-color: derive(#1d1d1d, 20%);
    -fx-text-fill: black;
}

.result-display {
    -fx-background-color: derive(#383838, -40%);
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 13pt;
    -fx-text-fill: white;
}

.result-display .label {
    -fx-text-fill: black !important;
}

.scroll-bar:vertical .thumb,
.scroll-bar:horizontal .thumb {
    -fx-background-color: rgb(211,211,211);
    -fx-background-insets: 2 2 2 2;
}

.scroll-bar:vertical .track-background,
.scroll-bar:horizontal .track-background {
    -fx-background-color: derive(#383838, -40%);
}

.scroll-bar:vertical > .increment-button,
.scroll-bar:vertical > .decrement-button,
.scroll-bar:horizontal > .increment-button,
.scroll-bar:horizontal > .decrement-button {
    -fx-padding: 3px;
}

.scroll-bar:vertical > .increment-button > .increment-arrow {
    -fx-background-color: grey;
    -fx-shape: "M 0 0 L 4 8 L 8 0 Z";
    -fx-padding: 0.30em;
    -fx-rotate: 0;
}

.scroll-bar:horizontal > .increment-button > .increment-arrow {
    -fx-background-color: grey;
    -fx-shape: "M 0 0 L 4 8 L 8 0 Z";
    -fx-padding: 0.30em;
    -fx-rotate: -90;
}

.scroll-bar:vertical > .decrement-button > .decrement-arrow {
    -fx-background-color: grey;
    -fx-shape: "M 0 0 L 4 8 L 8 0 Z";
    -fx-padding: 0.30em;
    -fx-rotate: -180;
}

.scroll-bar:horizontal > .decrement-button > .decrement-arrow {
    -fx-background-color: grey;
    -fx-shape: "M 0 0 L 4 8 L 8 0 Z";
    -fx-padding: 0.30em;
    -fx-rotate: 90;
}

.status-bar .label {
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
}

.status-bar-with-border {
    -fx-background-color: derive(#1d1d1d, 30%);
    -fx-border-color: derive(#1d1d1d, 25%);
    -fx-border-width: 1px;
}

.status-bar-with-border .label {
    -fx-text-fill: white;
}

.grid-pane {
    -fx-background-color: derive(#383838, -40%);
    -fx-border-color: derive(#383838, -40%);
    -fx-border-width: 0px;
}

.grid-pane .anchor-pane {
    -fx-background-color: derive(#383838, -40%);
}

.context-menu {
    -fx-background-color: derive(#1d1d1d, 50%);
}

.context-menu .label {
    -fx-text-fill: white;
}

.menu-bar {
    -fx-background-color: derive(#383838, -40%);
}

.menu-bar .label {
    -fx-font-size: 14pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-opacity: 0.9;
}

.menu .left-container {
    -fx-background-color: black;
}

/*
 * Metro style Push Button
 * Author: Pedro Duque Vieira
 * http://pixelduke.wordpress.com/2012/10/23/jmetro-windows-8-controls-on-java/
 */
.button {
    -fx-padding: 5 22 5 22;
    -fx-border-color: #e2e2e2;
    -fx-border-width: 2;
    -fx-background-radius: 0;
    -fx-background-color: #1d1d1d;
    -fx-font-family: "Segoe UI", Helvetica, Arial, sans-serif;
    -fx-font-size: 11pt;
    -fx-text-fill: #d8d8d8;
    -fx-background-insets: 0 0 0 0, 0, 1, 2;
}

.button:hover {
    -fx-background-color: #3a3a3a;
}

.button:pressed, .button:default:hover:pressed {
  -fx-background-color: white;
  -fx-text-fill: #1d1d1d;
}

.button:focused {
    -fx-border-color: white, white;
    -fx-border-width: 1, 1;
    -fx-border-style: solid, segments(1, 1);
    -fx-border-radius: 0, 0;
    -fx-border-insets: 1 1 1 1, 0;
}

.button:disabled, .button:default:disabled {
    -fx-opacity: 0.4;
    -fx-background-color: #1d1d1d;
    -fx-text-fill: white;
}

.button:default {
    -fx-background-color: -fx-focus-color;
    -fx-text-fill: #ffffff;
}

.button:default:hover {
    -fx-background-color: derive(-fx-focus-color, 30%);
}

.dialog-pane {
    -fx-background-color: #1d1d1d;
}

.dialog-pane > *.button-bar > *.container {
    -fx-background-color: #1d1d1d;
}

.dialog-pane > *.label.content {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: white;
}

.dialog-pane:header *.header-panel {
    -fx-background-color: derive(#1d1d1d, 25%);
}

.dialog-pane:header *.header-panel *.label {
    -fx-font-size: 18px;
    -fx-font-style: italic;
    -fx-fill: white;
    -fx-text-fill: white;
}

#cardPane {
    -fx-background-color: transparent;
    -fx-border-width: 0;
}

#commandTypeLabel {
    -fx-font-size: 11px;
    -fx-text-fill: #F70D1A;
}

#commandTextField {
    -fx-background-color: transparent #383838 transparent #383838;
    -fx-background-insets: 0;
    -fx-border-color: derive(#383838, +100%);
    -fx-border-insets: 0;
    -fx-border-width: 2;
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 13pt;
    -fx-text-fill: white;
}

#filterField, #personListPanel, #personWebpage {
    -fx-effect: innershadow(gaussian, black, 10, 0, 0, 0);
}

#resultDisplay .content {
    -fx-background-color: derive(#383838, -40%);
    -fx-background-radius: 0;
}

#tags {
    -fx-hgap: 7;
    -fx-vgap: 3;
}

#tags .label {
    -fx-text-fill: white;
    -fx-background-color: #3e7b91;
    -fx-padding: 1 3 1 3;
    -fx-border-radius: 18 18 18 18;
    -fx-background-radius: 18 18 18 18;
    -fx-font-size: 16;
}

#birthdayListHolder {
    -fx-background-color: derive(#383838, -40%);
}

#birthdayListHolder > .label {
    -fx-background-color: derive(#383838, -40%);
}

#reminderListHolder {
    -fx-background-color: derive(#383838, -40%);
}

#reminderListHolder > .label {
    -fx-background-color: derive(#383838, -40%);
}

.error {
    -fx-text-fill: #d06651 !important; /* The error class should always override the default text-fill style */
}

```
###### \resources\view\SkyTheme.css
``` css
.background {
    -fx-background-color: derive(#9bd8ff, 20%);
    background-color: #215c7a; /* Used in the default.html file */
}

.label {
    -fx-font-size: 15pt;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: #555555;
    -fx-opacity: 0.9;
}

.label-bright {
    -fx-font-size: 11pt;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

.label-header {
    -fx-font-size: 32pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

.text-field {
    -fx-font-size: 12pt;
    -fx-font-family: "Segoe UI Semibold";
}

.tab-pane {
    -fx-padding: 0 0 0 1;
}

.tab-pane .tab-header-area {
    -fx-padding: 0 0 0 0;
    -fx-min-height: 0;
    -fx-max-height: 0;
}

.table-view {
    -fx-base: #9bd8ff;
    -fx-control-inner-background: #9bd8ff;
    -fx-background-color: #9bd8ff;
    -fx-table-cell-border-color: transparent;
    -fx-table-header-border-color: transparent;
    -fx-padding: 5;
}

.table-view .column-header-background {
    -fx-background-color: transparent;
}

.table-view .column-header, .table-view .filler {
    -fx-size: 35;
    -fx-border-width: 0 0 1 0;
    -fx-background-color: transparent;
    -fx-border-color:
        transparent
        transparent
        derive(-fx-base, 80%)
        transparent;
    -fx-border-insets: 0 10 1 0;
}

.table-view .column-header .label {
    -fx-font-size: 20pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-alignment: center-left;
    -fx-opacity: 1;
}

.table-view:focused .table-row-cell:filled:focused:selected {
    -fx-background-color: -fx-focus-color;
}

.split-pane:horizontal .split-pane-divider {
    -fx-background-color: derive(#9bd8ff, 20%);
    -fx-border-color: transparent transparent transparent #4d4d4d;
}

.split-pane {
    -fx-border-radius: 1;
    -fx-border-width: 1;
    -fx-background-color: derive(#9bd8ff, 20%);
}

.list-view {
    -fx-background-color: derive(#9bd8ff, 20%);
    -fx-background-insets: 0;
    -fx-padding: 0;
}

.list-cell {
    -fx-label-padding: 0 0 0 0;
    -fx-graphic-text-gap : 0;
    -fx-padding: 0 0 0 0;
}

.list-cell:filled:even {
    -fx-background-color: #e0f4ff;
}

.list-cell:filled:odd {
    -fx-background-color: #f2faff;
}

.list-cell:filled:selected {
    -fx-background-color: #54bcff;
}

.list-cell:filled:selected #cardPane {
    -fx-border-color: #0852b2;
    -fx-border-width: 1;
}

.list-cell .label {
    -fx-text-fill: black;
}

.list-cell:empty {
    /* Empty cells will not have alternating colours */
    -fx-background: derive(#9bd8ff, 20%);
}

.cell_big_label {
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 16px;
    -fx-text-fill: #010504;
}

.cell_small_label {
    -fx-font-family: "Segoe UI";
    -fx-font-size: 13px;
    -fx-text-fill: #010504;
}

.anchor-pane {
     -fx-background-color: derive(#9bd8ff, 20%);
}

.pane-with-border {
     -fx-background-color: derive(#9bd8ff, 20%);
     -fx-border-color: derive(#9bd8ff, 10%);
     -fx-border-top-width: 1px;
}

.status-bar {
    -fx-background-color: derive(#9bd8ff, 20%);
    -fx-text-fill: black;
}

.result-display {
    -fx-background-color: transparent;
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 13pt;
    -fx-text-fill: white;
}

.result-display .label {
    -fx-text-fill: black !important;
}

.status-bar .label {
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: black;
}

.status-bar-with-border {
    -fx-background-color: derive(#9bd8ff, 30%);
    -fx-border-color: derive(#9bd8ff, 25%);
    -fx-border-width: 1px;
}

.status-bar-with-border .label {
    -fx-text-fill: black;
}

.grid-pane {
    -fx-background-color: derive(#9bd8ff, 30%);
    -fx-border-color: derive(#9bd8ff, 30%);
    -fx-border-width: 1px;
}

.grid-pane .anchor-pane {
    -fx-background-color: derive(#9bd8ff, 30%);
}

.context-menu {
    -fx-background-color: derive(#9bd8ff, 50%);
}

.context-menu .label {
    -fx-text-fill: white;
}

.menu-bar {
    -fx-background-color: derive(#006eff, 20%);
}

.menu-bar .label {
    -fx-font-size: 14pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-opacity: 0.9;
}

.menu .left-container {
    -fx-background-color: black;
}

/*
 * Metro style Push Button
 * Author: Pedro Duque Vieira
 * http://pixelduke.wordpress.com/2012/10/23/jmetro-windows-8-controls-on-java/
 */
.button {
    -fx-padding: 5 22 5 22;
    -fx-border-color: #e2e2e2;
    -fx-border-width: 2;
    -fx-background-radius: 0;
    -fx-background-color: #9bd8ff;
    -fx-font-family: "Segoe UI", Helvetica, Arial, sans-serif;
    -fx-font-size: 11pt;
    -fx-text-fill: #d8d8d8;
    -fx-background-insets: 0 0 0 0, 0, 1, 2;
}

.button:hover {
    -fx-background-color: #3a3a3a;
}

.button:pressed, .button:default:hover:pressed {
  -fx-background-color: white;
  -fx-text-fill: #9bd8ff;
}

.button:focused {
    -fx-border-color: white, white;
    -fx-border-width: 1, 1;
    -fx-border-style: solid, segments(1, 1);
    -fx-border-radius: 0, 0;
    -fx-border-insets: 1 1 1 1, 0;
}

.button:disabled, .button:default:disabled {
    -fx-opacity: 0.4;
    -fx-background-color: #9bd8ff;
    -fx-text-fill: white;
}

.button:default {
    -fx-background-color: -fx-focus-color;
    -fx-text-fill: #ffffff;
}

.button:default:hover {
    -fx-background-color: derive(-fx-focus-color, 30%);
}

.dialog-pane {
    -fx-background-color: #9bd8ff;
}

.dialog-pane > *.button-bar > *.container {
    -fx-background-color: #9bd8ff;
}

.dialog-pane > *.label.content {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: white;
}

.dialog-pane:header *.header-panel {
    -fx-background-color: derive(#9bd8ff, 25%);
}

.dialog-pane:header *.header-panel *.label {
    -fx-font-size: 18px;
    -fx-font-style: italic;
    -fx-fill: white;
    -fx-text-fill: white;
}

.scroll-bar {
    -fx-background-color: derive(#9bd8ff, 20%);
}

.scroll-bar .thumb {
    -fx-background-color: derive(#9bd8ff, 50%);
    -fx-background-insets: 3;
}

.scroll-bar .increment-button, .scroll-bar .decrement-button {
    -fx-background-color: transparent;
    -fx-padding: 0 0 0 0;
}

.scroll-bar .increment-arrow, .scroll-bar .decrement-arrow {
    -fx-shape: " ";
}

.scroll-bar:vertical .increment-arrow, .scroll-bar:vertical .decrement-arrow {
    -fx-padding: 1 8 1 8;
}

.scroll-bar:horizontal .increment-arrow, .scroll-bar:horizontal .decrement-arrow {
    -fx-padding: 8 1 8 1;
}

#cardPane {
    -fx-background-color: transparent;
    -fx-border-width: 0;
}

#commandTypeLabel {
    -fx-font-size: 11px;
    -fx-text-fill: #F70D1A;
}

#commandTextField {
    -fx-background-color: transparent #215c7a transparent #215c7a;
    -fx-background-insets: 0;
    -fx-border-color: #215c7a #215c7a #348cba #215c7a;
    -fx-border-insets: 0;
    -fx-border-width: 1;
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 13pt;
    -fx-text-fill: black;
}

#filterField, #personListPanel, #personWebpage {
    -fx-effect: innershadow(gaussian, black, 10, 0, 0, 0);
}

#resultDisplay .content {
    -fx-background-color: transparent, #215c7a, transparent, #215c7a;
    -fx-background-radius: 0;
}

#tags {
    -fx-hgap: 7;
    -fx-vgap: 3;
}

#tags .label {
    -fx-text-fill: white;
    -fx-background-color: #3e7b91;
    -fx-padding: 1 3 1 3;
    -fx-border-radius: 2;
    -fx-background-radius: 2;
    -fx-font-size: 11;
}

#birthdayListHolder {
    -fx-background-color: derive(#9bd8ff, 20%);
}

#birthdayListHolder > .label {
    -fx-background-color: derive(#9bd8ff, 20%);
}

#reminderListHolder {
    -fx-background-color: derive(#9bd8ff, 20%);
}

#reminderListHolder > .label {
    -fx-background-color: derive(#9bd8ff, 20%);
}

```
