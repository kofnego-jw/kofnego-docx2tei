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
 */
public class ProgramSetup {

    private static final String XML_PROLOGUE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramSetup.class);

    private Set<ConversionOption> conversionOptionSet = new HashSet<>();

    private String savingLocation;

    transient private File additionalStylesheetDir;

    transient private File tmpDir;

    public ProgramSetup(File tmpDir, File additionalStylesheetDir, Collection<ConversionOption> conversionOptionSet) {
        if (conversionOptionSet!=null)
            this.conversionOptionSet.addAll(conversionOptionSet);
        this.additionalStylesheetDir = additionalStylesheetDir;
        this.tmpDir = tmpDir;
    }

    public ProgramSetup(File tmpDir, File additionalStylesheetDir, String savingLocation, Collection<ConversionOption> conversionOptions) {
        this(tmpDir, additionalStylesheetDir, conversionOptions);
        this.savingLocation = savingLocation;
        loadProgramSetup();
    }

    public void addConversionOption(ConversionOption co) {
        if (co==null || co.getName()==null || co.getName().isEmpty()) {
            LOGGER.warn("Cannot add ConversionOption without a name.");
            return;
        }
        removeConversionOption(co);
        this.conversionOptionSet.add(co);
        LOGGER.info("A ConversionOption {} has been added.", co.getName());
    }

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
        this.conversionOptionSet.remove(old);
        LOGGER.debug("ConversionOption with name {} has been removed.", old.getName());
    }

    public ConversionOption getConversionOption(String name) throws NoSuchElementException {
        if (name==null) throw new NoSuchElementException();
        return this.conversionOptionSet.stream()
                .filter(x -> name.equals(x.getName()))
                .findAny()
                .get();
    }

    public boolean containsConversionOption(ConversionOption co) {
        return this.conversionOptionSet.contains(co);
    }

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

    public Map<String,String> getDefaultParameters(String optionName) {
        ConversionOption co = null;
        try {
            co = getConversionOption(optionName);
        } catch(Exception e) {
            return null;
        }
        return co.getDefaultParameters();
    }

    protected void joinConversionOptions(ProgramSetup ps) {
        ps.conversionOptionSet.stream()
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
        return conversionOptionSet.stream().collect(Collectors.toList());
    }

    public File getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    public static final XStream X;
    static {
        X = new XStream();
        X.alias("ProgrammSetup", ProgramSetup.class);
        X.alias("ConversionOption", ConversionOption.class);
    }

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

    public static ProgramSetup loadProgramSetup(InputStream in) throws Exception {
        return (ProgramSetup) X.fromXML(in);
    }

}
