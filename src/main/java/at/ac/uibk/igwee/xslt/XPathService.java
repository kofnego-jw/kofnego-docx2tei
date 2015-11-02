package at.ac.uibk.igwee.xslt;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This class uses XmlStream and XPath-Expression to create results. The
 * implementation should return DOM-Results.
 * 
 * @author Apple
 * 
 */
public interface XPathService {

	/**
	 * Evaluates an InputStream and returns a list of Strings.
	 * 
	 * @param xmlIn XML-InputStream
	 * @param xpath XPath-Expression
	 * @param namespaceDeclarations declaration of prefix --> namespace
	 * @return a List of Strings
	 * @throws XsltException
	 */
	public List<String> evaluateAsStringList(InputStream xmlIn, String xpath,
			Map<String, String> namespaceDeclarations) throws XsltException;
	
	/**
	 * Evaluates a source given with an URI and returns a list of String.
	 * @param uri Url to the xml-resource
	 * @param xpath
	 * @param nsDecl
	 * @return a list of String
	 * @throws XsltException
	 */
	public List<String> evaluateAsStringList(String uri, String xpath, 
			Map<String,String> nsDecl) throws XsltException;
	
	/**
	 * Evaluates a document in cache. Returns a list of String.
	 * @param cacheName name of the document in Cache
	 * @param xpath xpath
	 * @param nsDecl namespace declarations
	 * @return a List of String, will never be null.
	 * @throws XsltException
	 */
	public List<String> evaluateFromCacheAsStringList(String cacheName, String xpath, 
			Map<String,String> nsDecl) throws XsltException;
	

	/**
	 * Bounds an InputStream for xml-source to the cache.
	 * 
	 * @param xml
	 * @return the name of the internal docID, used for unbounding and for
	 *         evaluating xpath.
	 * @throws XsltException
	 *             if any error happens
	 */
	public String bound(InputStream xml) throws XsltException;

	/**
	 * Bounds an URI-Resource for xml-source to the cache.
	 * 
	 * @param xmlUri
	 *            uri
	 * @return the name of the internal docID, used for unbounding and for
	 *         evaluating xpath.
	 * @throws XsltException
	 *             if any exception happens.
	 */
	public String bound(String xmlUri) throws XsltException;

	/**
	 * Detache a document from the cache.
	 * 
	 * @param docName
	 */
	public void unbound(String docName);

}
