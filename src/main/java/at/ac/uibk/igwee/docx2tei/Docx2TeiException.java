package at.ac.uibk.igwee.docx2tei;

/**
 * Exception Wrapper for Docx2Tei bundle.
 * @author Joseph
 *
 */
public class Docx2TeiException extends Exception {
	
	private static final long serialVersionUID = 201402180938L;
	
	public Docx2TeiException() {
		super();
	}
	
	public Docx2TeiException(String msg) {
		super(msg);
	}
	
	public Docx2TeiException(Throwable cause) {
		super(cause);
	}
	
	public Docx2TeiException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
