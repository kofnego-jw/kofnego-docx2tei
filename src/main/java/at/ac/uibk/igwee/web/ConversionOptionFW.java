package at.ac.uibk.igwee.web;

import at.ac.uibk.igwee.controller.ConversionOption;

/**
 * Created by Joseph on 31.10.15.
 */
public class ConversionOptionFW {

    public final String name;

    public final String description;

    public ConversionOptionFW(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static ConversionOptionFW create(ConversionOption co) {
        if (co==null) return null;
        return new ConversionOptionFW(co.getName(), co.getDescription());
    }
}
