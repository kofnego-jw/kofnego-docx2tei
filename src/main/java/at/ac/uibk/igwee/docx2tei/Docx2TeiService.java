package at.ac.uibk.igwee.docx2tei;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
/**
 * Main interface of a docx2tei service
 * @author Joseph
 *
 */
public interface Docx2TeiService extends Closeable {
	
	/**
	 * 
	 * @param docxFile
	 * @return an inputStream containing the result of the conversion.
	 * @throws Docx2TeiException
	 */
	public InputStream doDocx2Tei(File docxFile) throws Docx2TeiException;
	/**
	 * Note: the word-directory parameter will be passed by the implementation.
	 * @param docxFile
	 * @param parameters
	 * @return
	 * @throws Docx2TeiException
	 */
	public InputStream doDocx2Tei(File docxFile, Map<String, ?> parameters) throws Docx2TeiException;
	/**
	 * 
	 * @param docxFile
	 * @param customXsltUri URI for a custom stylesheet. Please note that all the sub xslts must be accessible through the 
	 * same URI-Type.
	 * @return
	 * @throws Docx2TeiException
	 */
	public InputStream doDocx2Tei(File docxFile, String customXsltUri) throws Docx2TeiException;
	/**
	 * 
	 * @param docxFile
	 * @param customXsltUri
	 * @param parameters
	 * @return
	 * @throws Docx2TeiException
	 */
	public InputStream doDocx2Tei(File docxFile, String customXsltUri, Map<String,?> parameters) throws Docx2TeiException;
	
	
	/**
	 * 
	 * @param docxStream
	 * @param customXsltUri
	 * @param params
	 * @return
	 * @throws Docx2TeiException
	 */
	public InputStream doDocx2Tei(InputStream docxStream, String customXsltUri, Map<String,?> params) throws Docx2TeiException;

}
