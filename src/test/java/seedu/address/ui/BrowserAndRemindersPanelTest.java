package seedu.address.ui;

import static guitests.guihandles.WebViewUtil.waitUntilBrowserLoaded;
import static org.junit.Assert.assertEquals;
import static seedu.address.testutil.EventsUtil.postNow;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.ui.BrowserAndRemindersPanel.DEFAULT_PAGE;
import static seedu.address.ui.BrowserAndRemindersPanel.GOOGLE_SEARCH_URL_PREFIX;
import static seedu.address.ui.BrowserAndRemindersPanel.GOOGLE_SEARCH_URL_SUFFIX;
import static seedu.address.ui.UiPart.FXML_FILE_FOLDER;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import guitests.guihandles.BrowserAndRemindersPanelHandle;
import seedu.address.MainApp;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;

public class BrowserAndRemindersPanelTest extends GuiUnitTest {
    private PersonPanelSelectionChangedEvent selectionChangedEventStub;

    private BrowserAndRemindersPanel browserAndRemindersPanel;
    private BrowserAndRemindersPanelHandle browserAndRemindersPanelHandle;

    @Before
    public void setUp() {
        selectionChangedEventStub = new PersonPanelSelectionChangedEvent(new PersonCard(ALICE, 0));

        guiRobot.interact(() -> browserAndRemindersPanel = new BrowserAndRemindersPanel());
        uiPartRule.setUiPart(browserAndRemindersPanel);

        browserAndRemindersPanelHandle = new BrowserAndRemindersPanelHandle(browserAndRemindersPanel.getRoot());
    }

    @Test
    public void display() throws Exception {
        // default web page
        URL expectedDefaultPageUrl = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE);
        assertEquals(expectedDefaultPageUrl, browserAndRemindersPanelHandle.getLoadedUrl());

        // associated web page of a person
        postNow(selectionChangedEventStub);
        URL expectedPersonUrl = new URL(GOOGLE_SEARCH_URL_PREFIX
                + ALICE.getName().fullName.replaceAll(" ", "+") + GOOGLE_SEARCH_URL_SUFFIX);

        waitUntilBrowserLoaded(browserAndRemindersPanelHandle);
        assertEquals(expectedPersonUrl, browserAndRemindersPanelHandle.getLoadedUrl());
    }
}
