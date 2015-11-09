package at.ac.uibk.igwee;

import at.ac.uibk.igwee.ziputils.api.Zipper;
import org.junit.Test;

import java.io.File;

/**
 * Created by Joseph on 05.11.2015.
 *
 * This class zips the content of "./src/main/resources/additional"
 * to the docx2tei_add.zip file. After developing the XSLT stylesheets
 * in the "additional" directory, this class zips the files and
 * make it available for shipping.
 *
 * @author joseph
 */
public class ZipAdditional {

    private static final File ADDITIONAL_FOLDER =
            new File("./src/main/resources/additional");

    private static final File ADDITIONAL_ZIP =
            new File("./src/main/resources/docx2tei_add.zip");

    public static void main(String[] args) throws Exception {
        System.out.println("Zipping the additional directory...");
        Zipper.zipDir(ADDITIONAL_FOLDER, ADDITIONAL_ZIP);
        System.out.println("Zipping finished.");
    }

}
