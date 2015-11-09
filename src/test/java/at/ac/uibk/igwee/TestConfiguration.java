package at.ac.uibk.igwee;

import at.ac.uibk.igwee.controller.ProgramSetup;
import at.ac.uibk.igwee.docx2tei.Docx2TeiService;
import at.ac.uibk.igwee.docx2tei.impl.Docx2TeiServiceImpl;
import at.ac.uibk.igwee.xslt.XsltService;
import at.ac.uibk.igwee.xslt.impl.SaxonXsltServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Created by Joseph on 29.10.2015.
 *
 * This file is used as setting for TestServer. Instead of copying additional
 * stylesheets to the temporary directory. It uses the "./src/main/resources/additional"
 * as the base directory. By doing so, the changes in the stylesheets will automatically
 * be reflected in the TestServer. This eases the developing and debugging process for
 * the XSLT stylesheets.
 *
 * @author joseph
 */
@Configuration
@ComponentScan(basePackages = {"at.ac.uibk.igwee.controller"})
public class TestConfiguration {

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
        File additionalDir = new File("./src/main/resources/additional");

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
