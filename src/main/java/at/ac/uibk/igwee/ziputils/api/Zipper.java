package at.ac.uibk.igwee.ziputils.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
/**
 * This class zips a file. If the passed file is a directory, all the file in the directory will be zipped recursively.
 * @author Joseph
 *
 */
public class Zipper {
	/**
	 * Input
	 */
	private File inputFile;
	/**
	 * Output
	 */
	private File outputZipFile;
	
	/**
	 * 
	 * @param inputFile
	 * @param output
	 */
	protected Zipper(File inputFile, File output) {
		this.inputFile = inputFile;
		this.outputZipFile = output;
	}
	/**
	 * 
	 * @throws IOException
	 */
	protected void execute() throws IOException {
		if (outputZipFile==null)
			throw new IOException("No output file provided.");
		
		ZipOutputStream zos = null;
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(outputZipFile));
			zos = new ZipOutputStream(os);
			File baseDir = inputFile.isDirectory() ? inputFile : inputFile.getParentFile();
			traverseDir(zos, inputFile, baseDir);
		} finally {
			if (zos!=null)
				zos.close();
		}
		
	}
	/**
	 * Traverse a dir and add each file into the zos.
	 * @param zos
	 * @param now
	 * @param baseDir
	 * @throws IOException
	 */
	private void traverseDir(ZipOutputStream zos, File now, File baseDir) throws IOException {
		if (now.isFile()) {
			addToZip(zos, now, baseDir);
		} else {
			for (File file: now.listFiles()) {
				traverseDir(zos, file, baseDir);
			}
		}
	}
	/**
	 * Adds a fiel into a zip.
	 * @param zos
	 * @param file
	 * @param baseDir
	 * @throws IOException
	 */
	private static void addToZip(ZipOutputStream zos, File file, File baseDir) throws IOException {
		String zipName;
		if (file.getAbsolutePath().startsWith(baseDir.getAbsolutePath()))
			zipName = file.getAbsolutePath().substring(baseDir.getAbsolutePath().length());
		else 
			zipName = file.getName();
		while (zipName.startsWith(File.separator))
			zipName = zipName.substring(1);
		
		
		ZipEntry entry = new ZipEntry(zipName);
		zos.putNextEntry(entry);
		FileUtils.copyFile(file, zos);
	}

	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the outputZipFile
	 */
	public File getOutputZipFile() {
		return outputZipFile;
	}

	/**
	 * @param outputZipFile the outputZipFile to set
	 */
	public void setOutputZipFile(File outputZipFile) {
		this.outputZipFile = outputZipFile;
	}
	
	/**
	 * Main method.
	 * @param inputFile
	 * @param output
	 * @throws IOException
	 */
	public static void zipDir(File inputFile, File output) throws IOException {
		Zipper z = new Zipper(inputFile, output);
		z.execute();
	}
	
	

}
