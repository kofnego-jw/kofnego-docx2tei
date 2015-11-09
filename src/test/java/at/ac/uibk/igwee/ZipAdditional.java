package at.ac.uibk.igwee;

import at.ac.uibk.igwee.ziputils.api.Zipper;
import org.junit.Test;

import java.io.File;

/**
 * Created by Joseph on 05.11.2015.
 */
public class ZipAdditional {

    private static final File ADDITIONAL_FOLDER =
            new File("./src/main/resources/additional");

    private static final File ADDITIONAL_ZIP =
            new File("./src/main/resources/docx2tei_add.zip");

    @Test
    public void test() throws Exception {
        Zipper.zipDir(ADDITIONAL_FOLDER, ADDITIONAL_ZIP);
    }

}
