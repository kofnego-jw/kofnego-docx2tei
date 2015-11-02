package playground.testserver.web;

/**
 * Created by Joseph on 29.10.2015.
 */
public class NameAndContentResponse {

    public final String name;

    public final byte[] content;

    public NameAndContentResponse(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }
}
