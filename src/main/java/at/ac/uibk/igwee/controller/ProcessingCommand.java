package at.ac.uibk.igwee.controller;

import java.util.Map;

/**
 * Created by Joseph on 29.10.2015.
 *
 * A command
 *
 * @author Joseph
 */
public class ProcessingCommand {

    /**
     * Name of the option
     */
    private final String xsltName;

    /**
     * The parameters that are to be passed to the xslt
     */
    private final Map<String,String> parameters;

    public ProcessingCommand(String xsltName, Map<String, String> parameters) {
        this.xsltName = xsltName;
        this.parameters = parameters;
    }

    public String getXsltName() {
        return xsltName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "ProcessingCommand{" +
                "xsltName='" + xsltName + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
