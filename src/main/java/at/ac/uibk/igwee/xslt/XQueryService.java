package at.ac.uibk.igwee.xslt;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Defines the api for a XQueryService
 * @author Joseph
 *
 */
public interface XQueryService {
	
	static final String DEFAULT_ENCODING = "utf-8";
	
	/**
	 * 
	 * Performs an XQuery and writes the result to the resultOutputStream.
	 * The BaseURI will not be set.
	 * 
	 * @param xqueryInputStream
	 * @param resultOutputStream
	 * @throws XsltException
	 */
	public void doXQuery(InputStream xqueryInputStream, OutputStream resultOutputStream) throws XsltException;
	
	/**
	 * Performs an XQuery and writes the result to the resultOutputStream.
	 * Uses default encoding
	 * @param xqueryUri
	 * @param resultOutputStream
	 * @throws XsltException
	 */
	public default void doXQuery(URI xqueryUri, OutputStream resultOutputStream) throws XsltException {
		doXQuery(xqueryUri, DEFAULT_ENCODING, resultOutputStream);
	}
	
	
	/**
	 * Performs an XQuery and returns an InputStream containing the result.
	 * @param xqueryUri
	 * @param encoding
	 * @param resultOutputStream
	 * @throws XsltException
	 */
	public void doXQuery(URI xqueryUri, String encoding, OutputStream resultOutputStream) throws XsltException;
	
	/**
	 * 
	 * @param xqueryAsString The xquery as a string.
	 * @param resultOutputStream
	 * @throws XsltException
	 */
	public void doXQuery(String xqueryAsString, OutputStream resultOutputStream) throws XsltException;
	
	
	/**
	 * Performs an XQuery and writes the result to the resultOutputStream
	 * @param xqueryAsString
	 * @param baseURI
	 * @param resultOutputStream
	 * @throws XsltException
	 */
	public void doXQuery(String xqueryAsString, URI baseURI, OutputStream resultOutputStream) throws XsltException;
	
	/**
	 * Performs an XQuery and writes the result to the OutputStream
	 * @param xqueryInputStream
	 * @param baseURI
	 * @param resultOutputStream
	 * @throws XsltException
	 */
	public void doXQuery(InputStream xqueryInputStream, URI baseURI, OutputStream resultOutputStream) throws XsltException;
	
	
	/**
	 * Performs an XQuery and returns an InputStream containing the result.
	 * Uses default encoding (UTF-8)
	 * @param xqueryUri URI of the xquery file.
	 * @return InputStream, never null.
	 * @throws XsltException
	 */
	public default InputStream doXQuery(URI xqueryUri) throws XsltException {
		return doXQuery(xqueryUri, DEFAULT_ENCODING);
	}
	
	
	
	
	/**
	 * Performs an XQuery and returns an InputStream containing the result.
	 * @param xqueryUri
	 * @param encoding
	 * @return
	 * @throws XsltException
	 */
	public InputStream doXQuery(URI xqueryUri, String encoding) throws XsltException;
	
	/**
	 * Performs an XQuery and returns an InputStream containing the result
	 * @param xqueryString
	 * @return
	 * @throws XsltException
	 */
	public InputStream doXQuery(String xqueryString) throws XsltException;
	
	/**
	 * Performs an XQuery and returns an inputStream containing the result.
	 * The BaseURI will not be set.
	 * @param xqueryInputStream
	 * @return InputStream, never null.
	 * @throws XsltException
	 */
	public InputStream doXQuery(InputStream xqueryInputStream) throws XsltException;
	
	/**
	 * Performs an XQuery and returns a result InputStream.
	 * @param xqueryAsString
	 * @param baseURI
	 * @return
	 * @throws XsltException
	 */
	public InputStream doXQuery(String xqueryAsString, URI baseURI) throws XsltException;

	/**
	 * Performs an XQuery and returns a result inputstream.
	 * @param xqueryInputStream
	 * @param baseURI
	 * @return
	 * @throws XsltException
	 */
	public InputStream doXQuery(InputStream xqueryInputStream, URI baseURI) throws XsltException;
}
