package seedu.address.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.TurnLabelsOffEvent;
import seedu.address.commons.events.ui.TurnLabelsOnEvent;

//@@author justinpoh
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
