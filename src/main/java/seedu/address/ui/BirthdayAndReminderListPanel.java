package seedu.address.ui;

import java.util.logging.Logger;

import org.fxmisc.easybind.EasyBind;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.reminders.Reminder;

/**
 * Panel containing the list of persons with birthday in the current month.
 */
public class BirthdayAndReminderListPanel extends UiPart<Region> {
    private static final String FXML = "BirthdayAndReminderListPanel.fxml";
    private static final String REMINDER_TODAY_STYLE = "nightThemeReminderToday";
    private static final String REMINDER_THREE_DAYS_STYLE = "nightThemeReminderThreeDays";
    private static final String REMINDER_REST_STYLE = "nightThemeReminderRest";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<BirthdayReminderCard> birthdayListView;
    @FXML
    private ListView<ReminderCard> reminderListView;

    public BirthdayAndReminderListPanel(ObservableList<ReadOnlyPerson> birthdayList,
                                        ObservableList<Reminder> reminderList) {
        super(FXML);
        setConnections(birthdayList, reminderList);
        registerAsAnEventHandler(this);
    }

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
                if (reminder.isEventToday()) {
                    this.getStyleClass().add(REMINDER_TODAY_STYLE);
                } else if (reminder.isEventWithinThreeDays()) {
                    this.getStyleClass().add(REMINDER_THREE_DAYS_STYLE);
                } else if (!reminder.hasEventPast()) {
                    this.getStyleClass().add(REMINDER_REST_STYLE);
                }
                setGraphic(reminder.getRoot());
            }
        }
    }

}
