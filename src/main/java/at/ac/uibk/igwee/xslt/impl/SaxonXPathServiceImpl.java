package at.ac.uibk.igwee.xslt.impl;

import static at.ac.uibk.igwee.xslt.impl.XsltUtils.createSource;
import static at.ac.uibk.igwee.xslt.impl.XsltUtils.nullOrEmpty;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.uibk.igwee.xslt.XPathService;
import at.ac.uibk.igwee.xslt.XsltException;

/**
 * Implementation of XPathService using Saxon
 * @author Apple
 *
 */
public class SaxonXPathServiceImpl implements XPathService {

	/**
	 * Default maximum size of the cache.
	 */
	private static final int DEFAULT_MAX_CACHE_SIZE = 20;
	/**
	 * Cache.
	 */
	private LinkedList<CacheEntry> cache = new LinkedList<CacheEntry>();
	/**
	 * maximal cache size.
	 */
	private int maxCacheSize = DEFAULT_MAX_CACHE_SIZE;
	/**
	 * Document builder used for building documents to XmdNode.
	 */
	private DocumentBuilder docBuilder;
	/**
	 * xpathCompiler. 
	 */
	private XPathCompiler xpathCompiler;

	/**
	 * Slf4j-Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SaxonXPathServiceImpl.class);
		
	public SaxonXPathServiceImpl() {
		super();
		Processor proc = new Processor(false);
		docBuilder = proc.newDocumentBuilder();
		xpathCompiler = proc.newXPathCompiler();
	}

	/**
	 * main method for evaluating the xpath.
	 * @param xpath xpath-expression
	 * @param context the document
	 * @param prefixes namespace declarations
	 * @return a XdmValue of the result
	 * @throws XsltException
	 */
	private XdmValue evaluate(String xpath, XdmNode context, Map<String,String> prefixes) 
			throws XsltException {
		try {
			if (prefixes!=null && !prefixes.isEmpty()) {
				for (Map.Entry<String, String> ns: prefixes.entrySet()) {
					xpathCompiler.declareNamespace(nullOrEmpty(ns.getKey()), nullOrEmpty(ns.getValue()));
				}
			}
			return xpathCompiler.evaluate(xpath, context);
		} catch (Exception e) {
			logger.error("Error while evaluating xpath", e);
			throw new XsltException("Exception while evaluating xpath.", e);
		}
	}
	
	/**
	 * Static method, converts a XdmValue to a list of String.
	 * @param val XdmValue, result of xpath evaluation
	 * @return a list of string, can be empty but never null.
	 */
	private static List<String> convertToStringList(XdmValue val) {
		List<String> result = new ArrayList<String>(val.size());
		for (int i=0; i<val.size(); i++) {
			XdmItem item = val.itemAt(i);
			result.add(item.getStringValue());
		}
		return result;
	}

	@Override
	public List<String> evaluateAsStringList(InputStream xmlIn, String xpath,
			Map<String, String> namespaceDeclarations) throws XsltException {
		XdmNode doc = createNode(xmlIn);
		XdmValue result = evaluate(xpath, doc, namespaceDeclarations);
		return convertToStringList(result);
	}
	
	@Override
	public List<String> evaluateFromCacheAsStringList(String name, String xpath,
			Map<String,String> nsDecl) throws XsltException {
		XdmNode doc = getCacheEntry(name).getNode();
		XdmValue res = evaluate(xpath, doc, nsDecl);
		return convertToStringList(res);
	}
	
	@Override
	public List<String> evaluateAsStringList(String url, String xpath, 
			Map<String,String> nsDecl) throws XsltException {
		XdmNode doc = createNode(url);
		XdmValue result = evaluate(xpath, doc, nsDecl);
		return convertToStringList(result);
	}

	@Override
	public String bound(InputStream xml) throws XsltException {
		XdmNode doc = createNode(xml);
		CacheEntry ce = createCacheEntry(doc);
		return ce.getName();
	}

	@Override
	public String bound(String xmlUri) throws XsltException {
		XdmNode doc = createNode(xmlUri);
		CacheEntry ce = createCacheEntry(doc);
		return ce.getName();
	}

	@Override
	public void unbound(String docName) {
		CacheEntry ce = new CacheEntry(docName, null);
		cache.remove(ce);
	}

	/**
	 * returns the cache-entry for a given name.
	 * @param name the name of the cache.
	 * @return the CacheEntry with the name.
	 * @throws XsltException if no CacheEntry is found.
	 */
	private CacheEntry getCacheEntry(String name) throws XsltException {
		for (CacheEntry now : cache) {
			if (now.getName().equals(name))
				return now;
		}
		logger.error("Cannot find Entry with name '{}'", new Object[] {name});
		throw new XsltException("Cannot find Entry with the name '" + name
				+ "'.");
	}

	/**
	 * Helper method to read the document.
	 * @param in InputStream of the xml.
	 * @return a XdmNode, can be used for the evaluation.
	 * @throws XsltException
	 */
	private XdmNode createNode(InputStream in) throws XsltException {
		Source s = createSource(in);
		XdmNode node;
		try {
			node = docBuilder.build(s);
		} catch (Exception e) {
			logger.error("Exception while parsing the document", e);
			throw new XsltException("Exception while building the document.", e);
		}
		return node;
	}

	/**
	 * Converts an uri to XdmNode.
	 * @param uri Uri
	 * @return a XdmNode
	 * @throws XsltException
	 */
	private XdmNode createNode(String uri) throws XsltException {
		Source s = createSource(uri);
		XdmNode node;
		try {
			node = docBuilder.build(s);
		} catch (Exception e) {
			logger.error("Exception while parsing the document.", e);
			throw new XsltException("Exception while building the document.", e);
		}
		return node;
	}

	/**
	 * Adds a node into the cache.
	 * @param node
	 * @return the name of the node in cache.
	 */
	private synchronized CacheEntry createCacheEntry(XdmNode node) {
		String name = Long.toHexString(System.currentTimeMillis());
		String root = name;
		CacheEntry test = new CacheEntry(name, node);
		int i = 0;
		while (cache.contains(test)) {
			name = root + "_" + Integer.toHexString(++i);
			test = new CacheEntry(name, node);
		}
		cache.add(test);
		if (cache.size() > maxCacheSize)
			cache.remove();
		return test;
	}

	
	/**
	 * Cache-Entry Helper class.
	 * @author Joseph
	 *
	 */
	private static final class CacheEntry {
		/**
		 * Name
		 */
		private final String name;
		/**
		 * The stored node.
		 */
		private final XdmNode node;

		/**
		 * Default constructor.
		 * @param name name of the entry.
		 * @param node node of the entry.
		 */
		public CacheEntry(String name, XdmNode node) {
			super();
			this.name = name;
			this.node = node;
		}

		public String getName() {
			return name;
		}

		public XdmNode getNode() {
			return node;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheEntry other = (CacheEntry) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

}
