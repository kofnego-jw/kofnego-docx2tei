package at.ac.uibk.igwee;

import at.ac.uibk.igwee.controller.ProgramSetup;
import at.ac.uibk.igwee.docx2tei.Docx2TeiService;
import at.ac.uibk.igwee.docx2tei.impl.Docx2TeiServiceImpl;
import at.ac.uibk.igwee.xslt.XsltService;
import at.ac.uibk.igwee.xslt.impl.SaxonXsltServiceImpl;
import at.ac.uibk.igwee.ziputils.api.Unzipper;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.InputStream;

/**
 * Main Application. Scans controller and web folder for components.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"at.ac.uibk.igwee.web", "at.ac.uibk.igwee.controller"})
public class KofnegoTei2docxApplication {

    /**
     * Name of the additional stylehseet directory
     */
    private static final String ADDITIONAL_FOLDER = "docx2tei_add";

    /**
     *
     * @return XsltService
     */
    @Bean
    public XsltService xsltService() {
        return new SaxonXsltServiceImpl();
    }

    /**
     *
     * @return Docx2TeiService
     * @throws Exception
     */
    @Bean
    public Docx2TeiService docx2TeiService() throws Exception{
        Docx2TeiServiceImpl impl = new Docx2TeiServiceImpl();
        impl.setXsltService(xsltService());
        return impl;
    }

    /**
     *
     * This method will create a temporary directory for additional stylesheets,
     * temporary directory for other purposes, and load the programSetup.xml
     * from the class path.
     *
     * @return Default ProgramSetup
     * @throws Exception
     */
    @Bean
    public ProgramSetup programmSetup() throws Exception {

        File tmpLck = File.createTempFile("tempStylesheets", ".lck");
        File tmpDir = tmpLck.getParentFile();
        File additionalDir = new File(tmpDir, ADDITIONAL_FOLDER);
        File addFile = new File(tmpDir, "docx2tei_add.zip");
        if (!additionalDir.mkdirs() && !additionalDir.exists()) {
            throw new Exception("Cannot create temporary directory: " + additionalDir.getAbsolutePath());
        }
        InputStream addStream = getClass().getResourceAsStream("/docx2tei_add.zip");
        if (addStream==null) {
            throw new Exception("Cannot find additional stylesheets!");
        }
        FileUtils.copyInputStreamToFile(addStream, addFile);
        Unzipper.unpack(addFile, additionalDir);

        InputStream psSetup = getClass().getResourceAsStream("/programSetup.xml");

        ProgramSetup oldPS = ProgramSetup.loadProgramSetup(psSetup);
        oldPS.setAdditionalStylesheetDir(additionalDir);
        oldPS.setTmpDir(tmpDir);
        oldPS.setSavingLocation("./programSetup.xml");

        return oldPS;
    }

    /**
     * Starts the spring application
     * @param args The parameters will be ignored :)
     */
    public static void main(String[] args) {
        SpringApplication.run(KofnegoTei2docxApplication.class, args);
    }
}
