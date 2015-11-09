package at.ac.uibk.igwee.controller;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Joseph on 29.10.2015.
 *
 * ProgramSetup
 *
 * @author Joseph
 */
public class ProgramSetup {

    /**
     * XML Prologue
     */
    private static final String XML_PROLOGUE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramSetup.class);

    /**
     * A Set of ConversionOption
     */
    private List<ConversionOption> conversionOptions = new ArrayList<>();

    /**
     * Saving Location of the program setup
     */
    private String savingLocation;

    /**
     * Additional Stylesheets are kept here. Transient, because these files are unpacked into a
     * temporary directory
     */
    transient private File additionalStylesheetDir;

    /**
     * Temporary directory, for saving files.
     */
    transient private File tmpDir;

    public ProgramSetup(File tmpDir, File additionalStylesheetDir, Collection<ConversionOption> conversionOptions) {
        if (conversionOptions!=null)
            this.conversionOptions.addAll(conversionOptions);
        this.additionalStylesheetDir = additionalStylesheetDir;
        this.tmpDir = tmpDir;
    }

    public ProgramSetup(File tmpDir, File additionalStylesheetDir, String savingLocation, Collection<ConversionOption> conversionOptions) {
        this(tmpDir, additionalStylesheetDir, conversionOptions);
        this.savingLocation = savingLocation;
        loadProgramSetup();
    }

    /**
     * Adds an option
     * @param co The conversion option to be added. If there is one option already with the name,
     *           the old one will be removed first.
     */
    public void addConversionOption(ConversionOption co) {
        if (co==null || co.getName()==null || co.getName().isEmpty()) {
            LOGGER.warn("Cannot add ConversionOption without a name.");
            return;
        }
        removeConversionOption(co);
        this.conversionOptions.add(co);
        LOGGER.info("A ConversionOption {} has been added.", co.getName());
    }

    /**
     * Removes a option
     * @param co the option to be removed
     */
    public void removeConversionOption(ConversionOption co) {
        if (co==null || co.getName()==null) {
            LOGGER.warn("Trying to remove a ConversionOption with no name.");
            return;
        }
        ConversionOption old;
        try {
            old = getConversionOption(co.getName());
        } catch (Exception e) {
            LOGGER.info("Cannot find a ConversionOption with name {}. No ConversionOption will be removed.", co.getName());
            return;
        }
        this.conversionOptions.remove(old);
        LOGGER.debug("ConversionOption with name {} has been removed.", old.getName());
    }

    /**
     *
     * @param name the name of the option
     * @return the ConversionOption with this name
     * @throws NoSuchElementException if no option with this name can be found.
     */
    public ConversionOption getConversionOption(String name) throws NoSuchElementException {
        if (name==null) throw new NoSuchElementException();
        return this.conversionOptions.stream()
                .filter(x -> name.equals(x.getName()))
                .findAny()
                .get();
    }

    public boolean containsConversionOption(ConversionOption co) {
        return this.conversionOptions.contains(co);
    }

    /**
     *
     * @param conversionOptionName Name of the option
     * @return the XSLT Strings (as relative path to the additionalStylesheetDirectory) as a List
     */
    public List<String> getXsltURIs(String conversionOptionName) {
        ConversionOption co;
        try {
            co = getConversionOption(conversionOptionName);
        } catch (Exception e) {
            LOGGER.warn("Cannot find ConversionOption with name {}.", conversionOptionName);
            return Collections.emptyList();
        }
        return co.getXsltStylesheets()
                .stream()
                .map(x -> additionalStylesheetDir.getAbsolutePath() + "/" + x)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param optionName Name of the option
     * @return the default parameters set in the ProgramSetup, or null if none is present.
     */
    public Map<String,String> getDefaultParameters(String optionName) {
        ConversionOption co;
        try {
            co = getConversionOption(optionName);
        } catch(Exception e) {
            return null;
        }
        return co.getDefaultParameters();
    }

    /**
     * Joins two ProgramSetups together. The ConversionOptions are merged.
     * If there are options with the same name, then the option of the
     * original (not of ps) is used.
     * @param ps another ProgramSetup.
     */
    protected void joinConversionOptions(ProgramSetup ps) {
        ps.conversionOptions.stream()
                .forEach(co -> {
                    if (!containsConversionOption(co))
                        addConversionOption(co);
                });
    }

    public String getSavingLocation() {
        return savingLocation;
    }

    public void setSavingLocation(String savingLocation) {
        this.savingLocation = savingLocation;
    }

    protected File getSaveFile() {
        return new File(savingLocation);
    }

    public File getAdditionalStylesheetDir() {
        return additionalStylesheetDir;
    }

    public void setAdditionalStylesheetDir(File additionalStylesheetDir) {
        this.additionalStylesheetDir = additionalStylesheetDir;
    }

    public List<ConversionOption> getConversionOptions() {
        return conversionOptions.stream().collect(Collectors.toList());
    }

    public File getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    /**
     * XStream Object for XML Serialization
     */
    public static final XStream X;
    static {
        X = new XStream();
        X.alias("ProgrammSetup", ProgramSetup.class);
        X.alias("ConversionOption", ConversionOption.class);
    }

    /**
     * Loads the programSetup from the saving location, if the savingLocation is set.
     */
    @PostConstruct
    public void loadProgramSetup() {
        if (savingLocation!=null && !savingLocation.isEmpty()) {
            LOGGER.info("Try to load old setup in {}.", savingLocation);
            try {
                ProgramSetup oldSetup = (ProgramSetup) X.fromXML(getSaveFile());
                if (oldSetup!=null)
                    joinConversionOptions(oldSetup);
            } catch (Exception e) {
                // ignored
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the programSetup in the given saving location.
     */
    @PreDestroy
    public void saveProgramSetup() {
        if (savingLocation!=null && !savingLocation.isEmpty()) {
            LOGGER.info("Storing ProgramSetup in {}.", savingLocation);
            String content = XML_PROLOGUE + X.toXML(this);
            try {
                FileUtils.write(getSaveFile(), content, "UTF-8");
            } catch (Exception e) {
                LOGGER.error("Cannot write old ProgramSetup to " + savingLocation + ".", e);
                return;
            }
            LOGGER.info("ProgramSetup successfully stored in {}.", savingLocation);
        }
    }

    /**
     *
     * @param in the inputStream containing the XML content for the programSetup
     * @return a programSetup with the content
     * @throws Exception if deserialization fails.
     */
    public static ProgramSetup loadProgramSetup(InputStream in) throws Exception {
        return (ProgramSetup) X.fromXML(in);
    }

}
