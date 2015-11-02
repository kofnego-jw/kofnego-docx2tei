package at.ac.uibk.igwee.xslt;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Interface for XSL-Transformation-Service
 * 
 * 
 * @author Joseph
 *
 */
public interface XsltService {
	
	/**
	 * Do an XSL-Transformation.
	 * @param xml XML-Source
	 * @param xsl XSL-Source
	 * @param parameters Parameters should be set
	 * @return an InputStream containing the result
	 * @throws XsltException when any exception should happen
	 */
	public InputStream doXslt(InputStream xml, InputStream xsl, Map<String,? extends Object> parameters) 
			throws XsltException;
	/**
	 * Do an XSL-Transformation.
	 * @param xml XML-Source
	 * @param xsl XSL-Source
	 * @param parameters Parameters should be set
	 * @return an InputStream containing the result
	 * @throws XsltException when any exception should happen
	 */	
	public InputStream doXslt(String xmlUri, String xslUri, Map<String,?> parameters) 
			throws XsltException;
	/**
	 * 
	 * @param xmlStream
	 * @param xslUri
	 * @param params
	 * @return
	 * @throws XsltException
	 */
	public InputStream doXslt(InputStream xmlStream, String xslUri, Map<String,?> params) 
			throws XsltException;
	
	/**
	 * Do an XSL-Transformation.
	 * @param xml XML-Source
	 * @param xsl XSL-Source
	 * @param parameters Parameters should be set
	 * @param result OutputStream to be written
	 * @throws XsltException when any exception should happen
	 */
	public void doXslt(InputStream xml, InputStream xsl, Map<String,?> parameters, OutputStream result) 
			throws XsltException;
	/**
	 * Do an XSL-Transformation.
	 * @param xml XML-Source
	 * @param xsl XSL-Source
	 * @param parameters Parameters should be set
	 * @param result OutputStream to be written
	 * @throws XsltException when any exception should happen
	 */	
	public void doXslt(String xmlUri, String xslUri, Map<String,?> parameters, OutputStream result) 
			throws XsltException;
	
	/**
	 * Do a transformation
	 * @param xmlInput
	 * @param xslUri
	 * @param params
	 * @param result
	 * @throws XsltException
	 */
	public void doXslt(InputStream xmlInput, String xslUri, Map<String,?> params, OutputStream result)
			throws XsltException;
	
	/**
	 * Adds a transformer to the cache.
	 * @param xslName Name of the transformer, if already exists in cache, the old entry 
	 * should be replaced.
	 * @param xsl InputStream of the XSLT-File or Data
	 * @throws XsltException if any exception should happen
	 */
	public void addToXslCache(String xslName, InputStream xsl) throws XsltException;
	
	/**
	 * Adds a transformer to the cache
	 * @param xslName Name of the transformer, if already exists, replace the old one.
	 * @param xslUri an URI to the xslt-resource.
	 * @throws XsltException if any exception should happen.
	 */
	public void addToXslCache(String xslName, String xslUri) throws XsltException;
	
	/**
	 * Does a transformation from the cache.
	 * @param xslName name of the transformer
	 * @param xml xml-source
	 * @param parameters any transformation parameters
	 * @return an InputStream of the result
	 * @throws XsltException if any exception should happen
	 */
	public InputStream doXsltFromCache(String xslName, InputStream xml, Map<String,?> parameters) 
			throws XsltException;
	
	/**
	 * Does a transformation from the cache.
	 * @param xslName name of the transformer
	 * @param xmlUri xml-source
	 * @param parameters any transformation parameters
	 * @return an InputStream of the result
	 * @throws XsltException if any exception should happen
	 */
	public InputStream doXsltFromCache(String xslName, String xmlUri, Map<String,?> parameters) 
			throws XsltException;
	
	/**
	 * Does a transformation from the cache.
	 * @param xslName name of the transformer
	 * @param xml xml-source
	 * @param parameters any transformation parameters
	 * @param result the result outputstream
	 * @throws XsltException if any exception should happen
	 */
	public void doXsltFromCache(String xslName, InputStream xml, Map<String,?> parameters, 
			OutputStream result) throws XsltException;
	
	/**
	 * Does a transformation from the cache.
	 * @param xslName name of the transformer
	 * @param xmlUri xml-source
	 * @param parameters any transformation parameters
	 * @param result the result outputstream
	 * @throws XsltException if any exception should happen
	 */
	public void doXsltFromCache(String xslName, String xmlUri, Map<String,?> parameters, 
			OutputStream result) throws XsltException;
	
}
