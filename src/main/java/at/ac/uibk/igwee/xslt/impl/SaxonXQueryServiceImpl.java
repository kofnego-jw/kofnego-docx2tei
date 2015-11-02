package at.ac.uibk.igwee.xslt.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.uibk.igwee.xslt.XQueryService;
import at.ac.uibk.igwee.xslt.XsltException;

/**
 * Saxon implementation
 * @author Joseph
 *
 */
public class SaxonXQueryServiceImpl implements XQueryService {
	
	public static final boolean USE_LICENSED_SAXON = false;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SaxonXQueryServiceImpl.class);
	
	private Processor processor;
	
	public SaxonXQueryServiceImpl() {
		super();
		this.processor = new Processor(USE_LICENSED_SAXON);
	}
	
	private XQueryCompiler createXQueryCompiler(URI baseUri) {
		XQueryCompiler xc = this.processor.newXQueryCompiler();
		if (baseUri!=null) 
			xc.setBaseURI(baseUri);
		return xc;
	}
	
	private XQueryExecutable createXQueryExecutable(InputStream query, URI baseURI) throws XsltException {
		XQueryCompiler xc = createXQueryCompiler(baseURI);
		try {
			return xc.compile(query);
		} catch (Exception e) {
			LOGGER.error("Cannot compile XQuery input from InputStream.", e);
			throw new XsltException("Cannot compile XQuery input from InputStream.", e);
		}
	}
	
	private XQueryExecutable createXQueryExecutable(String queryString, URI baseURI) throws XsltException {
		XQueryCompiler xc = createXQueryCompiler(baseURI);
		try {
			return xc.compile(queryString);
		} catch (SaxonApiException e) {
			LOGGER.error("Cannot compile XQuery input from URI.", e);
			throw new XsltException("Cannot compile XQuery input from URI.", e);
		}
	}
	
	private void performXQuery(XQueryExecutable xe, OutputStream os) throws XsltException {
		Serializer result = processor.newSerializer(os);
		XQueryEvaluator eval = xe.load();
		eval.setDestination(result);
		try {
			eval.run();
		} catch (Exception e) {
			LOGGER.error("Exception while performing the XQuery evaluation.", e);
			throw new XsltException("Exception while performing the XQuery evaluation.", e);
		}
	}
	
	private void performXQuery(InputStream in, URI baseURI, OutputStream os) throws XsltException {
		XQueryExecutable xe = createXQueryExecutable(in, baseURI);
		performXQuery(xe, os);
	}
	
	private void performXQuery(String queryString, URI baseURI, OutputStream os) throws XsltException {
		XQueryExecutable xe = createXQueryExecutable(queryString, baseURI);
		performXQuery(xe, os);
	}
	
	@Override
	public InputStream doXQuery(InputStream xqueryInputStream)
			throws XsltException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		performXQuery(xqueryInputStream, null, baos);
		
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	@Override
	public void doXQuery(InputStream xqueryInputStream,
			OutputStream resultOutputStream) throws XsltException {
		
		performXQuery(xqueryInputStream, null, resultOutputStream);
		
	}
	
	@Override
	public InputStream doXQuery(InputStream xqueryInputStream, URI baseURI)
			throws XsltException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		performXQuery(xqueryInputStream, baseURI, baos);
		
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	@Override
	public void doXQuery(InputStream xqueryInputStream, URI baseURI,
			OutputStream resultOutputStream) throws XsltException {
		performXQuery(xqueryInputStream, baseURI, resultOutputStream);
	}
	
	@Override
	public InputStream doXQuery(String xqueryString) throws XsltException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		performXQuery(xqueryString, null, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	@Override
	public void doXQuery(String xqueryString, OutputStream resultOutputStream)
			throws XsltException {
		performXQuery(xqueryString, null, resultOutputStream);
	}
	
	@Override
	public InputStream doXQuery(String xqueryString, URI baseURI)
			throws XsltException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		performXQuery(xqueryString, baseURI, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	@Override
	public void doXQuery(String xqueryString, URI baseURI,
			OutputStream resultOutputStream) throws XsltException {
		performXQuery(xqueryString, baseURI, resultOutputStream);
	}
	
	
	@Override
	public void doXQuery(URI xqueryUri, String encoding, OutputStream resultOutputStream)
			throws XsltException {
		String xqueryString = readFromURI(xqueryUri, encoding);
		performXQuery(xqueryString, xqueryUri, resultOutputStream);
	}
	
	@Override
	public InputStream doXQuery(URI xqueryUri, String encoding)
			throws XsltException {
		String xqueryString = readFromURI(xqueryUri, encoding);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		performXQuery(xqueryString, xqueryUri, baos);
		
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	private static String readFromURI(URI uri, String encoding) throws XsltException {
		if (encoding==null)
			encoding = DEFAULT_ENCODING;
		try {
			return IOUtils.toString(uri, encoding);
		} catch (Exception e) {
			throw new XsltException("Cannot read from URI.", e);
		}
	}
}
