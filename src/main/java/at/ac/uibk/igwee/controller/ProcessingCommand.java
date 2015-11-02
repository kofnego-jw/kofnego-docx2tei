package at.ac.uibk.igwee.controller;

import java.util.Map;

/**
 * Created by Joseph on 29.10.2015.
 */
public class ProcessingCommand {

    private final String xsltName;

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
