package at.ac.uibk.igwee.xslt.impl;

import static at.ac.uibk.igwee.xslt.impl.XsltUtils.createResult;
import static at.ac.uibk.igwee.xslt.impl.XsltUtils.createSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import net.sf.saxon.TransformerFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import at.ac.uibk.igwee.xslt.XsltException;
import at.ac.uibk.igwee.xslt.XsltService;

/**
 * Implementation of XsltService using Saxon API.
 * @author Apple
 *
 */
@Component
public class SaxonXsltServiceImpl implements XsltService, Closeable {
	
	private static final int TRANSFORMER_CACHE_SIZE = 10;
	
	/**
	 * Cache of the transformer. Uses a LinkedList internally.
	 */
	private LinkedList<CacheEntry> transformerCache = new LinkedList<CacheEntry>();

	/**
	 * A transformation factory, reused for any xslt-source.
	 */
	private final TransformerFactory transformerFactory = new TransformerFactoryImpl();
	
	/**
	 * Maximal size of the cache.
	 */
	private int maxCacheSize = TRANSFORMER_CACHE_SIZE;
	
	/**
	 * slf4j-Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SaxonXsltServiceImpl.class);
		
	public SaxonXsltServiceImpl() {
		super();
	}
	
	
	
	
	/**
	 * Creates a Transformer using a source.
	 * @param xsl Source of the XSL-File.
	 * @return a Transformer. Can be reused.
	 * @throws XsltException if any exception should happen.
	 */
	private Transformer createTransformer(Source xsl) throws XsltException {
		try {
			return transformerFactory.newTransformer(xsl);
		} catch (Exception e) {
			logger.error("Error while creating transformer", e);
			throw new XsltException("While creating transformer.", e);
		}
	}
	
	/**
	 * Main method for transformation.
	 * @param xml xml-Source
	 * @param tf transformer
	 * @param params parameters, can be null
	 * @param result the result
	 * @throws XsltException if any exception should happen.
	 */
	private void doXslt(Source xml, Transformer tf, Map<String,?> params, Result result) 
			throws XsltException {
		try {
			if (params!=null && !params.isEmpty()) {
				for (Map.Entry<String, ?> param: params.entrySet()) {
					tf.setParameter(param.getKey(), param.getValue());
				}
			}
			tf.transform(xml, result);
		} catch (Exception e) {
			logger.error("Error while doing the xslt.", e);
			throw new XsltException("While doing the xslt.", e);
		} finally {
			tf.clearParameters();
		}
	}
	
	/**
	 * Do the xslt. relegates to main xslt method.
	 * @param xml
	 * @param tf
	 * @param params
	 * @return
	 * @throws XsltException
	 */
	private InputStream doXslt(Source xml, Transformer tf, Map<String,?> params) 
			throws XsltException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		doXslt(xml, tf, params, createResult(baos));
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	@Override
	public InputStream doXslt(InputStream xml, InputStream xsl,
			Map<String, ?> parameters) throws XsltException {
		Source xmlSource = createSource(xml);
		Source xslSource = createSource(xsl);
		return doXslt(xmlSource, createTransformer(xslSource), parameters);
	}

	@Override
	public InputStream doXslt(String xmlUri, String xslUri,
			Map<String, ?> parameters) throws XsltException {
		Source xml = createSource(xmlUri);
		Source xsl = createSource(xslUri);
		return doXslt(xml, createTransformer(xsl), parameters);
	}
	@Override
	public InputStream doXslt(InputStream in, String xslUri, Map<String, ?> params) 
			throws XsltException {
		Source xml = createSource(in);
		Source xsl = createSource(xslUri);
		return doXslt(xml, createTransformer(xsl), params);
	}

	@Override
	public void doXslt(InputStream xml, InputStream xsl,
			Map<String, ?> parameters, OutputStream result)
			throws XsltException {
		Source xmlSource = createSource(xml);
		Source xslSource = createSource(xsl);
		Result res = createResult(result);
		doXslt(xmlSource, createTransformer(xslSource), parameters, res);
	}

	@Override
	public void doXslt(String xmlUri, String xslUri, Map<String, ?> parameters, 
			OutputStream result) throws XsltException {
		Source xml = createSource(xmlUri);
		Source xsl = createSource(xslUri);
		Result res = createResult(result);
		doXslt(xml, createTransformer(xsl), parameters, res);
	}
	@Override
	public void doXslt(InputStream in, String xslUri, Map<String,?> params, OutputStream os) 
			throws XsltException {
		Source xml = createSource(in);
		Source xsl = createSource(xslUri);
		Result res = createResult(os);
		doXslt(xml, createTransformer(xsl), params, res);
	}

	/**
	 * Creates a CacheEntry, will not add to cache automatically.
	 * @param xslName
	 * @param tf
	 * @return a CacheEntry object
	 */
	private CacheEntry cacheEntry(String xslName, Transformer tf) {
		return new CacheEntry(xslName, tf);
	}
	
	/**
	 * Removes a cacheEntry from the cache.
	 * @param ce
	 */
	private void removeFromXsltCache(CacheEntry ce) {
		if (transformerCache.contains(ce))
			transformerCache.remove(ce);
	}
	
	/**
	 * Removes the first entry from the cache. Can be used when cache is full.
	 */
	private void removeFromXsltCache() {
		if (transformerCache.size()>0)
			transformerCache.remove();
	}
	
	/**
	 * Adds a transformer to the cache. Will replace an old one with the same name.
	 * @param name Name of the transformer
	 * @param xsl XSL-Source
	 * @throws XsltException
	 */
	private void addToXslCache(String name, Source xsl) throws XsltException {
		try {
			Transformer tf = createTransformer(xsl);
			CacheEntry ce = cacheEntry(name, tf);
			if (transformerCache.contains(ce))
				removeFromXsltCache(ce);
			transformerCache.add(ce);
			if (transformerCache.size()>maxCacheSize)
				removeFromXsltCache();
		} catch (Exception e) {
			logger.error("Error while adding a transformer to the cache.", e);
			throw new XsltException("While adding to transformerCache", e);
		}
	}
	
	/**
	 * Gets a transformer with the xslName from the cache.
	 * @param xslName
	 * @return the transformer
	 * @throws XsltException if no transformer with the name can be found.
	 */
	private Transformer getFromCache(String xslName) throws XsltException {
		for (CacheEntry ce: transformerCache) {
			if (ce.getName().equals(xslName)) return ce.getTransformer();
		}
		throw new XsltException("Cannot find transformer '" + xslName + "' in the cache.");
	}
	
	@Override
	public void addToXslCache(String xslName, InputStream xsl)
			throws XsltException {
		addToXslCache(xslName, createSource(xsl));
	}

	@Override
	public void addToXslCache(String xslName, String xslUri)
			throws XsltException {
		addToXslCache(xslName, createSource(xslUri));
	}

	@Override
	public InputStream doXsltFromCache(String xslName, InputStream xml,
			Map<String, ?> parameters) throws XsltException {
		Transformer tf = getFromCache(xslName);
		return doXslt(createSource(xml), tf, parameters);
	}

	@Override
	public InputStream doXsltFromCache(String xslName, String xmlUri,
			Map<String,?> parameters) throws XsltException {
		Transformer tf = getFromCache(xslName);
		return doXslt(createSource(xmlUri), tf, parameters);
	}
	
	@Override
	public void doXsltFromCache(String xslName, InputStream xml, Map<String,?> parameters, 
			OutputStream os) throws XsltException {
		Transformer tf = getFromCache(xslName);
		doXslt(createSource(xml), tf, parameters, createResult(os));
	}
	
	@Override
	public void doXsltFromCache(String xslName, String xmlUri, Map<String,?> params, 
			OutputStream os) throws XsltException {
		Transformer tf = getFromCache(xslName);
		doXslt(createSource(xmlUri), tf, params, createResult(os));
	}
	
	/**
	 * clears the transformer cache.
	 */
	@Override
	public void close() {
		this.transformerCache.clear();
	}
	
	/**
	 * @return the size of the cache.
	 */
	public int getCacheSize() {
		return transformerCache.size();
	}

	/**
	 * sets the maximal size of the cache. If the cache already is bigger then 
	 * the cacheSize, the first elements of the cache will be removed.
	 * @param cacheSize
	 */
	public void setMaxCacheSize(int cacheSize) {
		if (cacheSize<0) return;
		this.maxCacheSize = cacheSize;
		while (transformerCache.size()>this.maxCacheSize)
			transformerCache.remove();
	}
	
	/**
	 * @return the maximal cache size.
	 */
	public int getMaxCacheSize() {
		return this.maxCacheSize;
	}


	/**
	 * Static ValueObject-class for the cache entry.
	 * @author Joseph
	 *
	 */
	private static final class CacheEntry {
		
		private final String name;
		private final Transformer transformer;
		
		public CacheEntry(String name, Transformer tf) {
			super();
			this.name = name;
			this.transformer = tf;
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
		public String getName() {
			return name;
		}
		public Transformer getTransformer() {
			return transformer;
		}
		
	}

}
