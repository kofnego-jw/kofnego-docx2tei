package at.ac.uibk.igwee.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 29.10.2015.
 *
 * Configuration for ONE conversion
 *
 * @author Joseph
 */
public class ConversionConfig {

    /**
     * List of ProcessingCommand
     */
    private final List<ProcessingCommand> postProcessing = new ArrayList<>();

    public ConversionConfig(List<ProcessingCommand> postProcessing) {

        if (postProcessing!=null)
            this.postProcessing.addAll(postProcessing);
    }

    public List<ProcessingCommand> getPostProcessing() {
        return postProcessing;
    }

    @Override
    public String toString() {
        return "ConversionConfig{" +
                "postProcessing=" + postProcessing +
                '}';
    }
}
