package at.ac.uibk.igwee;

import at.ac.uibk.igwee.controller.ConversionOption;
import at.ac.uibk.igwee.controller.ProgramSetup;
import at.ac.uibk.igwee.docx2tei.Docx2TeiService;
import at.ac.uibk.igwee.docx2tei.impl.Docx2TeiServiceImpl;
import at.ac.uibk.igwee.xslt.XsltService;
import at.ac.uibk.igwee.xslt.impl.SaxonXsltServiceImpl;
import at.ac.uibk.igwee.ziputils.api.Unzipper;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Joseph on 29.10.2015.
 */
@Configuration
@ComponentScan(basePackages = {"at.ac.uibk.igwee.controller"})
public class TestConfiguration {

    private static final String ADDITIONAL_FOLDER = "docx2tei_add";

    @Bean
    public XsltService xsltService() {
        return new SaxonXsltServiceImpl();
    }

    @Bean
    public Docx2TeiService docx2TeiService() throws Exception{
        Docx2TeiServiceImpl impl = new Docx2TeiServiceImpl();
        impl.setXsltService(xsltService());
        return impl;
    }

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


//        List<String> commentToRef = new ArrayList<>();
//        commentToRef.add("additional/igwee/commentToRs.xsl");
//        commentToRef.add("additional/igwee/rsToRef.xsl");
//
//        ConversionOption co1 = new ConversionOption("commentToRef", "Converts comments in DOCX using the commenting function to " +
//                "<rs>, <persName>, <placeName> or <subject>.", commentToRef, null);
//
//
//        List<String> addRN2P = new ArrayList<>();
//        addRN2P.add("additional/igwee/addRN2P.xsl");
//        ConversionOption co2 = new ConversionOption("addRN2P", "Adds a new line before each <div> and <p>.",
//                addRN2P, null);
//
//        List<ConversionOption> cos = Arrays.asList(co1, co2);

        ProgramSetup ps = new ProgramSetup(tmpDir, additionalDir, "./programSetup.xml",null);
        return ps;
    }

}
