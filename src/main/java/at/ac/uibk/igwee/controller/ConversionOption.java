package at.ac.uibk.igwee.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joseph on 31.10.15.
 *
 * A ConversionOption represents an option in the conversion.
 *
 * @author Joseph
 */
public class ConversionOption {

    /**
     * name of the option
     */
    private String name;

    /**
     * description of the option
     */
    private String description;

    /**
     * List of Strings: The URL of the xsltStylesheets
     */
    private List<String> xsltStylesheets;

    /**
     * The default parameters that will be passed to the stylesheets.
     * Every stylesheet will get these parameters.
     */
    private Map<String,String> defaultParameters;

    public ConversionOption() {
        super();
        this.xsltStylesheets = new ArrayList<>();
        this.defaultParameters = new HashMap<>();
    }

    public ConversionOption(String name, String description, List<String> xsltStylesheets, Map<String, String> defaultParameters) {
        this.name = name;
        this.description = description;
        this.xsltStylesheets = xsltStylesheets;
        this.defaultParameters = defaultParameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getXsltStylesheets() {
        return xsltStylesheets;
    }

    public void setXsltStylesheets(List<String> xsltStylesheets) {
        this.xsltStylesheets = xsltStylesheets;
    }

    public Map<String, String> getDefaultParameters() {
        return defaultParameters;
    }

    public void setDefaultParameters(Map<String, String> defaultParameters) {
        this.defaultParameters = defaultParameters;
    }

    private Object readResolve() {
        if (this.defaultParameters==null)
            this.defaultParameters = new HashMap<>();
        if (this.xsltStylesheets==null)
            this.xsltStylesheets = new ArrayList<>();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversionOption)) return false;

        ConversionOption that = (ConversionOption) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
