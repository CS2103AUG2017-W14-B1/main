package seedu.address.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import seedu.address.MainApp;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.BrowserAndRemindersPanelToggleEvent;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.address.commons.events.ui.SendingEmailEvent;
import seedu.address.commons.events.ui.ShowLocationEvent;
import seedu.address.commons.events.ui.TurnLabelsOffEvent;
import seedu.address.commons.events.ui.TurnLabelsOnEvent;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.reminders.Reminder;

/**
 * The Browser Panel of the App.
 */
public class BrowserAndRemindersPanel extends UiPart<Region> {

    /**
     * An Enumeration to differentiate between the child nodes and to keep track of which is
     * in front.
     */
    private enum Node {
        BROWSER, REMINDERS
    }

    public static final String DEFAULT_PAGE = "default.html";
    public static final String GOOGLE_SEARCH_URL_PREFIX = "https://www.google.com.sg/search?safe=off&q=";
    public static final String GOOGLE_SEARCH_URL_SUFFIX = "&cad=h";
    public static final String GOOGLE_MAPS_URL = "https://www.google.com.sg/maps/place/";

    public static final String EMAIL_SERVICE_GMAIL = "gmail";
    public static final String EMAIL_SERVICE_OUTLOOK = "outlook";

    public static final String GMAIL_EMAIL_URL =
            "https://mail.google.com/mail/?view=cm&fs=1&tf=1&source=mailto&to=%1$s&su=%2$s&body=%3$s";

    public static final String OUTLOOK_EMAIL_URL =
            "https://outlook.office.com/?path=/mail/action/compose&to=%1$s&subject=%2$s&body=%3$s";

    private static final String FXML = "BrowserAndRemindersPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());
    private BirthdayAndReminderListPanel birthdayAndReminderListPanel;
    private Node currentlyInFront = Node.REMINDERS;

    @FXML
    private WebView browser;

    @FXML
    private StackPane remindersPanel;

    public BrowserAndRemindersPanel(ObservableList<ReadOnlyPerson> birthdayPanelFilteredPersonList,
                                    ObservableList<Reminder> reminderList) {
        super(FXML);

        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);

        loadDefaultPage();

        //@@author justinpoh
        birthdayAndReminderListPanel = new BirthdayAndReminderListPanel(birthdayPanelFilteredPersonList, reminderList);

        //remindersPanel should be displayed first so no need to shift it to the back.
        remindersPanel.getChildren().add(birthdayAndReminderListPanel.getRoot());
        //@@author
        registerAsAnEventHandler(this);
    }

    private void loadPersonPage(ReadOnlyPerson person) {
        loadPage(GOOGLE_SEARCH_URL_PREFIX + person.getName().fullName.replaceAll(" ", "+")
                + GOOGLE_SEARCH_URL_SUFFIX);
    }

    /**
     * Loads the specified url with cookies and javascript enabled
     * @param url of specified web page
     */
    public void loadPage(String url) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        URI uri = URI.create(url);
        Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
        headers.put("Set-Cookie", Arrays.asList("name=value"));
        try {
            java.net.CookieHandler.getDefault().put(uri, headers);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        browser.getEngine().setJavaScriptEnabled(true);
        Platform.runLater(() -> browser.getEngine().load(url));
    }

    /**
     * Loads a default HTML file with a background that matches the general theme.
     */
    private void loadDefaultPage() {
        URL defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE);
        loadPage(defaultPage.toExternalForm());
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

    //@@author justinpoh
    /**
     * Check which child is currently at the front, and do the appropriate toggling between the children nodes.
     */
    private void toggleBrowserPanel() {
        switch(currentlyInFront) {
        case BROWSER:
            remindersPanel.toFront();
            currentlyInFront = Node.REMINDERS;
            raise(new TurnLabelsOnEvent());
            break;
        case REMINDERS:
            browser.toFront();
            currentlyInFront = Node.BROWSER;
            raise(new TurnLabelsOffEvent());
            break;
        default:
            throw new AssertionError("It should not be possible to land here");
        }
    }

    private void bringBrowserToFront() {
        browser.toFront();
        currentlyInFront = Node.BROWSER;
    }
    //@@author

    /**
     * Set's up the UI to bring browser to front and show location
     */
    private void setUpToShowLocation() {
        if (currentlyInFront == Node.REMINDERS) {
            browser.toFront();
            currentlyInFront = Node.BROWSER;
            raise(new TurnLabelsOffEvent());
        }
    }

    /**
     * Creates url from given address
     * @param address of the specified person
     */
    public String loadPersonLocation(String address) {

        String[] splitAddressByWords = address.split("\\s");

        String keywordsOfUrl = "";

        for (String word: splitAddressByWords) {
            keywordsOfUrl += word;
            keywordsOfUrl += "+";
        }

        loadPage(GOOGLE_MAPS_URL + keywordsOfUrl);
        return GOOGLE_MAPS_URL + keywordsOfUrl;
    }

    /**
     * Sets up email Url for processing Email in Browser panel
     * @param service mentioned email service
     * @param recipients formed recipients string
     * @param subject
     * @param body
     */
    private void setUpEmailUrl(String service, String recipients, String subject, String body) {
        if (service.equalsIgnoreCase(EMAIL_SERVICE_GMAIL)) {
            loadEmailUrlGmail(recipients, subject, body);
        } else if (service.equalsIgnoreCase(EMAIL_SERVICE_OUTLOOK)) {
            loadEmailUrlOutlook(recipients, subject, body);
        }
    }

    /**
     * Loads page to send email through gmail
     * @param recipients
     * @param subject
     * @param body
     */
    public void loadEmailUrlGmail(String recipients, String subject, String body) {
        try {
            Desktop.getDesktop().browse(new URI(String.format(GMAIL_EMAIL_URL, recipients, subject, body)));
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Loads page to send email through outlook
     * @param recipients
     * @param subject
     * @param body
     */
    private void loadEmailUrlOutlook(String recipients, String subject, String body) {
        try {
            Desktop.getDesktop().browse(new URI(String.format(OUTLOOK_EMAIL_URL, recipients, subject, body)));
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadPersonPage(event.getNewSelection().person);
        bringBrowserToFront();
        raise(new TurnLabelsOffEvent());
    }

    //@@author justinpoh
    @Subscribe
    private void handleBrowserPanelToggleEvent(BrowserAndRemindersPanelToggleEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        toggleBrowserPanel();
    }
    //@@author

    @Subscribe
    private void handleShowLocationEvent(ShowLocationEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event,
                "Processing Location of " + event.person.getName().fullName));
        setUpToShowLocation();
        String url = loadPersonLocation(event.person.getAddress().value);
    }

    @Subscribe
    private void handleSendingEmailEvent(SendingEmailEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event,
                "Processing email through service of " + event.service.service));
        setUpEmailUrl(event.service.service, event.recipients, event.subject.subject, event.body.body);
    }

}
