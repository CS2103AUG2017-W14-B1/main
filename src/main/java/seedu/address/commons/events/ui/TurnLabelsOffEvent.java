package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

//@@author justinpoh
/**
 * Turn birthday and reminders labels off whenever the browser is brought to the front.
 */
public class TurnLabelsOffEvent extends BaseEvent {
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
