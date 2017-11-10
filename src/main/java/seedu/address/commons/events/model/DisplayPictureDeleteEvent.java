//@@author tshradheya
package seedu.address.commons.events.model;

import seedu.address.commons.events.BaseEvent;

/**
 * Triggers event to delete image when a person is deleted
 */
public class DisplayPictureDeleteEvent extends BaseEvent {

    public final String path;

    public DisplayPictureDeleteEvent(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path + " to be deleted";
    }

}
