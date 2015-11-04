package at.ac.uibk.igwee.web;

/**
 * Created by Joseph on 29.10.2015.
 *
 * A Flyweight object for the response of the conversion
 *
 * @author Joseph
 */
@Deprecated
public class NameAndContentResponse {

    /**
     * Name of the file
     */
    public final String name;

    /**
     * The content
     */
    public final String  content;

    public NameAndContentResponse(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
