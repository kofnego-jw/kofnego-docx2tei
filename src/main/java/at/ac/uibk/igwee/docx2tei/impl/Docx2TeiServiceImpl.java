package at.ac.uibk.igwee.docx2tei.impl;

import at.ac.uibk.igwee.docx2tei.Docx2TeiException;
import at.ac.uibk.igwee.docx2tei.Docx2TeiService;
import at.ac.uibk.igwee.xslt.XsltService;
import at.ac.uibk.igwee.ziputils.api.Unzipper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of the Docx2TeiService. Uses a stylesheets.zip
 * contained in the jar-file as the base of the stylesheet. It also need an
 * XsltService for transforming.
 * 
 * @author Joseph
 * 
 */
public class Docx2TeiServiceImpl implements Docx2TeiService {
	/**
	 * "word-directory" The default paramname for the Word-Directory.
	 */
	public static final String WORD_DIRECTORY_PARAMNAME = "word-directory";
	/**
	 * The default starting Document for transformation.
	 */
	public static final String WORD_DOCUMENT_PATH = "word/document.xml";
	/**
	 * The standard zip-File-Path for the
	 */
	private static final String XSLT_ZIP_FILE = "/stylesheets.zip";
	/**
	 * Default relative XSLT-Location /docx/from/docxtotei.xsl
	 */
	private static final String DEFAULT_XSLT_LOCATION = "docx/from/docxtotei.xsl";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Docx2TeiServiceImpl.class);

	/**
	 * The temporary dir, used to unzip docx-files and store the unpacked
	 * stylesheets.zip
	 */
	private File tempDir;
	/**
	 * The default xslt file.
	 */
	private File stylesheetFile;
	/**
	 * XsltService
	 */
	private XsltService xsltService;

	public Docx2TeiServiceImpl() throws Docx2TeiException {
		super();

		try {
			File tmp = File.createTempFile("docx2tei", ".lck");
			tempDir = new File(tmp.getParentFile(), "docx2tei");
			if (!tempDir.exists() && !tempDir.mkdirs())
				throw new Docx2TeiException(
						"Cannot create temporary directory '" + tempDir + "'.");
			tmp.delete();
		} catch (Docx2TeiException e) {
			throw e;
		} catch (IOException e) {
			throw new Docx2TeiException("Cannot create temporary directory '"
					+ tempDir + "'.");
		}
		copyStylesheets();

	}

	protected synchronized void copyStylesheets() throws Docx2TeiException {
		LOGGER.debug("Unzip stylesheets.zip");
		InputStream is;
		File out = new File(tempDir, "stylesheets.zip");
		try {
			is = getClass().getResourceAsStream(XSLT_ZIP_FILE);
			if (is == null) {
				LOGGER.warn("Use stylehsheet.zip in maven project.");
				is = new FileInputStream(new File(
						"src/main/resources/stylesheets.zip"));
			}
			FileUtils.copyInputStreamToFile(is, out);
		} catch (Exception e) {
			throw new Docx2TeiException(
					"Cannot copy stylesheets.zip to destination.", e);
		}

		File stylesheetDir = new File(tempDir, "stylesheets");

		try {
			Unzipper.unpack(out, stylesheetDir);
		} catch (Exception e) {
			throw new Docx2TeiException(
					"Cannot unpack stylesheets.zip to destination '"
							+ stylesheetDir.getAbsolutePath() + "'.", e);
		}

		this.stylesheetFile = new File(stylesheetDir, DEFAULT_XSLT_LOCATION);
	}

	public void setXsltService(XsltService service) {
		this.xsltService = service;
	}

	@Override
	public InputStream doDocx2Tei(File docxFile, String xslUri,
			Map<String, ?> parameters) throws Docx2TeiException {
		if (parameters == null)
			parameters = new HashMap<String, Object>();
		File tmpDir;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.putAll(parameters);
			tmpDir = createTempOutputDir();
			Unzipper.unpack(docxFile, tmpDir);
			String wordDirUri = tmpDir.toURI().toASCIIString();
			params.put(WORD_DIRECTORY_PARAMNAME, wordDirUri);
			File start = new File(tmpDir, WORD_DOCUMENT_PATH);
			return xsltService.doXslt(start.toURI().toString(), xslUri, params);
		} catch (Exception e) {
			throw new Docx2TeiException("Exception while transforming: "
					+ e.getMessage(), e);
		}
	}

	@Override
	public InputStream doDocx2Tei(InputStream docxStream, String customXsltUri,
			Map<String, ?> params) throws Docx2TeiException {

		File tmpDocFile;
		try {
			tmpDocFile = File.createTempFile("tempdocx", ".docx");
			FileUtils.copyInputStreamToFile(docxStream, tmpDocFile);
		} catch (Exception e) {
			throw new Docx2TeiException("Cannot create temporary docx file.", e);
		}

		return doDocx2Tei(tmpDocFile, customXsltUri, params);
	}

	@Override
	public InputStream doDocx2Tei(File docxFile, Map<String, ?> params)
			throws Docx2TeiException {

		return doDocx2Tei(docxFile,
				this.stylesheetFile.toURI().toASCIIString(), params);
	}

	private File createTempOutputDir() throws IOException {
		String dirname = UUID.randomUUID().toString();
		File result;
		int i = 0;
//		System.out.println("tempDir: " + tempDir.getAbsolutePath());
		do {
			result = new File(tempDir, "docx2tei-" + dirname + "-"
					+ Integer.toString(i++));
		} while (result.exists());
		
		
		deleteOldDirectory(tempDir ,"docx2tei", 1, TimeUnit.HOURS);
		
		if (!result.exists() && !result.mkdirs())
			throw new IOException(
					"Cannot create temporary directory for processing '"
							+ result.getAbsolutePath() + "'.");
		return result;
	}
	
	public static void deleteOldDirectory(File baseDir, String prefix, long time, TimeUnit unit) {
		long toDel =  System.currentTimeMillis() - unit.convert(time, TimeUnit.MILLISECONDS);
		if (baseDir==null || baseDir.listFiles()==null) return;
		for (File now: baseDir.listFiles()) {
			if (now.getName().startsWith(prefix) && now.lastModified() < toDel) {
				FileUtils.deleteQuietly(now);
			}
		}
	}

	@Override
	public void close() throws IOException {
		FileUtils.deleteDirectory(tempDir);
	}

	@Override
	public InputStream doDocx2Tei(File docxFile) throws Docx2TeiException {
		return doDocx2Tei(docxFile,
				this.stylesheetFile.toURI().toASCIIString(), null);
	}

	@Override
	public InputStream doDocx2Tei(File docxFile, String customXsltUri)
			throws Docx2TeiException {
		return doDocx2Tei(docxFile, customXsltUri, null);
	}

	/**
	 * @return the tempDir
	 */
	public File getTempDir() {
		return tempDir;
	}

	/**
	 * @param tempDir
	 *            the tempDir to set
	 */
	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * @return the stylesheetFile
	 */
	public File getStylesheetFile() {
		return stylesheetFile;
	}

	/**
	 * @param stylesheetFile
	 *            the stylesheetFile to set
	 */
	public void setStylesheetFile(File stylesheetFile) {
		this.stylesheetFile = stylesheetFile;
	}

}