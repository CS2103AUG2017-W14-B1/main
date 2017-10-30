package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

//@@author justinpoh
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
