package at.ac.uibk.igwee.controller;

import at.ac.uibk.igwee.docx2tei.Docx2TeiService;
import at.ac.uibk.igwee.xslt.XsltService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Joseph on 29.10.2015.
 */
@Service
public class MainConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainConverter.class);

    @Autowired
    private Docx2TeiService docx2TeiService;

    @Autowired
    private XsltService xsltService;

    @Autowired
    private ProgramSetup setup;

    public InputStream convert(String docxName, InputStream docxStream, List<String> addNames) throws Exception {
        File tmpFile = File.createTempFile(docxName, ".docx");
        FileUtils.copyInputStreamToFile(docxStream, tmpFile);

        List<ProcessingCommand> pss = addNames==null ? Collections.emptyList() :
                addNames.stream()
                .map(x -> new ProcessingCommand(x, setup.getDefaultParameters(x)))
                .collect(Collectors.toList());
        ConversionConfig cc = new ConversionConfig(pss);
        return convert(tmpFile,cc);
    }

    public InputStream convert(File docxFile, ConversionConfig conversionConfig) throws Exception {
        LOGGER.info("Converting {} with {}.", docxFile, conversionConfig);
        InputStream tei = docx2TeiService.doDocx2Tei(docxFile);
        LOGGER.debug("TEI 2 DOCX finished.");
        if (conversionConfig==null) return tei;
        for (ProcessingCommand command: conversionConfig.getPostProcessing()) {
            List<String> xslts = setup.getXsltURIs(command.getXsltName());
            for (String xsl: xslts) {
                LOGGER.debug("FURTHER CONVERSION: {}", xsl);
                tei = xsltService.doXslt(tei, xsl, command.getParameters());
            }
        }
        return tei;
    }

}
