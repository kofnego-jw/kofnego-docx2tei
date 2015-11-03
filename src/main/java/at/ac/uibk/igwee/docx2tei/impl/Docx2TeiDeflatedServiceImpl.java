package at.ac.uibk.igwee.docx2tei.impl;

import at.ac.uibk.igwee.docx2tei.Docx2TeiException;
import at.ac.uibk.igwee.docx2tei.Docx2TeiService;
import at.ac.uibk.igwee.xslt.XsltService;
import at.ac.uibk.igwee.ziputils.api.Unzipper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of Docx2TeiService, uses a default XSLT-URL for 
 * the service. No stylehseet.zip needed.
 * @author Apple
 *
 */
public class Docx2TeiDeflatedServiceImpl implements Docx2TeiService {
	
	/**
	 * DEFAULT URL for the xslt stylesheet
	 */
	public static final String STYLESHEET_URL = "file:///Databases"
			+ "/igwee/docx2tei/igwee-stylesheets/docx/from/docxtotei.xsl";
	
	/**
	 * URL for the stylehsheet, pointing to "docxtotei.xsl"
	 */
	private String xsltUrl = STYLESHEET_URL;
	
	/**
	 * xsltService
	 */
	private XsltService xsltService;
	
	/**
	 * The temporary directory
	 */
	private File tempDir;
	
	
	
	public void setXsltService(XsltService service) throws Docx2TeiException {
		this.xsltService = service;
	}
	
	/**
	 * Default constructor will unzip the stylesheets to a temporary directory
	 * and use it also for the conversion
	 * @throws Docx2TeiException
	 */
	public Docx2TeiDeflatedServiceImpl() throws Docx2TeiException{
		try {
			File tmp = File.createTempFile("docx2tei_deflated", ".lck");
			tempDir = new File(tmp.getParentFile(), "docx2tei_deflated");
			if (!tempDir.exists() && !tempDir.mkdirs())
				throw new Docx2TeiException("Cannot create temporary directory '" + tempDir + "'.");
			tmp.delete();
		} catch (Docx2TeiException e) {
			throw e;
		} catch (IOException e) {
			throw new Docx2TeiException("Cannot create temporary directory '" + tempDir + "'.");
		}
	}
	


	@Override
	public InputStream doDocx2Tei(File docxFile, String xslUri, Map<String, ?> parameters) throws Docx2TeiException {
		if (parameters==null)
			parameters = new HashMap<String,Object>();
		File tmpDir;
		try {
			Map<String,Object> params = new HashMap<String,Object>();
			params.putAll(parameters);
			tmpDir = createTempOutputDir();
			Unzipper.unpack(docxFile, tmpDir);
			String wordDirUri = tmpDir.toURI().toASCIIString();
			params.put(Docx2TeiServiceImpl.WORD_DIRECTORY_PARAMNAME, wordDirUri);
			File start = new File(tmpDir, Docx2TeiServiceImpl.WORD_DOCUMENT_PATH);
			return xsltService.doXslt(start.toURI().toString(), xslUri, params);
		} catch (Exception e) {
			throw new Docx2TeiException("Exception while transforming: " + e.getMessage(), e);
		}
	}
	
	@Override
	public InputStream doDocx2Tei(InputStream docxStream, String customXsltUri,
			Map<String, ?> params) throws Docx2TeiException {
		File tmpDocx;
		
		try {
			tmpDocx = File.createTempFile("tempDocx", ".docx");
			FileUtils.copyInputStreamToFile(docxStream, tmpDocx);
		} catch (Exception e) {
			throw new Docx2TeiException("Cannot create temporary docx file.", e);
		}
		
		
		return doDocx2Tei(tmpDocx, customXsltUri, params);
	}

	@Override
	public InputStream doDocx2Tei(File docxFile, Map<String,?> params)
			throws Docx2TeiException {
		
		return doDocx2Tei(docxFile, this.xsltUrl, params);
	}
	
	/**
	 * 
	 * @return a File object pointing to an empty temp directory.
	 * @throws IOException
	 */
	private File createTempOutputDir() throws IOException {
		String dirname = UUID.randomUUID().toString();
		File result;
		int i = 0;
		do {
			result = new File(tempDir, "docx2tei-" + dirname + "-" +Integer.toString(i++));
		} while (result.exists());
		
		if (!result.exists() && !result.mkdirs())
			throw new IOException("Cannot create temporary directory for processing '" + result.getAbsolutePath() + "'.");
		Docx2TeiServiceImpl.deleteOldDirectory(tempDir, "docx2tei-", 1, TimeUnit.HOURS);
		
		return result;
	}

	@Override
	public void close() throws IOException {
		FileUtils.deleteDirectory(tempDir);
	}

	@Override
	public InputStream doDocx2Tei(File docxFile) throws Docx2TeiException {
		return doDocx2Tei(docxFile, this.xsltUrl, null);
	}

	@Override
	public InputStream doDocx2Tei(File docxFile, String customXsltUri)
			throws Docx2TeiException {
		return doDocx2Tei(docxFile, customXsltUri, null);
	}
	
	

}
