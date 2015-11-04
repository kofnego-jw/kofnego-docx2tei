package at.ac.uibk.igwee.web;

import at.ac.uibk.igwee.controller.ConversionOption;

/**
 * Created by Joseph on 31.10.15.
 *
 * Flyweight Object for ConversionOption
 *
 * @author Joseph
 */
public class ConversionOptionFW {

    /**
     * Name
     */
    public final String name;

    /**
     * Description
     */
    public final String description;

    public ConversionOptionFW(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Creates a ConversionOptionFW from a ConversionOption
     * @param co The conversion Option to be converted to ConversionOptionFW
     * @return the converted Flyweight object
     */
    public static ConversionOptionFW create(ConversionOption co) {
        if (co==null) return null;
        return new ConversionOptionFW(co.getName(), co.getDescription());
    }
}
