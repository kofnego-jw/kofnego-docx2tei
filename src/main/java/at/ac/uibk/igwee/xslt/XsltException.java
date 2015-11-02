package at.ac.uibk.igwee.xslt;
/**
 * This exception should capture all xslt exceptions.
 * @author Joseph
 *
 */
public class XsltException extends Exception {
	
	private static final long serialVersionUID = 201310220609L;
	
	public XsltException() {
		super();
	}
	
	public XsltException(Throwable cause) {
		super(cause);
	}
	
	public XsltException(String msg) {
		super(msg);
	}
	
	public XsltException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
