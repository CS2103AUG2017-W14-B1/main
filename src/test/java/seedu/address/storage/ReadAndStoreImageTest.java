package seedu.address.storage;

import static org.junit.Assert.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.commons.events.storage.ReadAndStoreImage;
import seedu.address.logic.parser.exceptions.ImageException;

public class ReadAndStoreImageTest {

    private static final String IMAGE_NAME = "testDisplaypic";
    private static final String INVALID_IMAGE_NAME = "testDisplaypicWrong";

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void test_execute() throws IOException {
        String initialPath = "src\\test\\resources\\pictures\\" + IMAGE_NAME + ".jpg";

        ReadAndStoreImage readAndStoreImage = new ReadAndStoreImage();

        String finalName = readAndStoreImage.execute(initialPath, ALICE.getEmail().hashCode());

        assertEquals(finalName, Integer.toString(ALICE.getEmail().hashCode()));
    }

    @Test
    public void throwsImageException() throws IOException {

        String initialPath = "src\\test\\resources\\pictures\\" + INVALID_IMAGE_NAME + ".jpg";

        ReadAndStoreImage readAndStoreImage = new ReadAndStoreImage();
        thrown.expect(ImageException.class);
        String finalName = readAndStoreImage.execute(initialPath, ALICE.getEmail().hashCode());
    }

}
