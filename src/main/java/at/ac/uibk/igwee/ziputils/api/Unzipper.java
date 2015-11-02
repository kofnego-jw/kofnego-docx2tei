/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.uibk.igwee.ziputils.api;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

/**
 * This class is used to unzip the docx files.
 *
 * @author jw
 */
public class Unzipper extends Object {
    /**
     * Output-Directory.
     */
    private File outputDir;
    
    /**
     * Input-Docx-File.
     */
    private File inputZipFile;
    
    protected Unzipper() {
        super();
    }
    
    protected Unzipper(File in, File outputDir) {
        this();
        this.inputZipFile = in;
        this.outputDir = outputDir;
    }
    
    /**
     * Unpacks the zip file to the output directory.
     * @throws IOException 
     * @throws NullPointerException if the output directory is not set.
     */
    public void unpack() throws IOException {
    	ZipFile zipFile = null;
        try {
            if (outputDir==null)
                throw new NullPointerException("The output directory is null.");
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("Cannot create the output directory \"" + outputDir.getAbsolutePath() + "\".");
            }
            zipFile = new ZipFile(inputZipFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    createDirectory(entry);
                    continue;
                }
                String filename = entry.getName();
                createDestFile(filename, zipFile.getInputStream(entry));
            }
            
        } finally {
        	if (zipFile!=null) {
        		try {
        			zipFile.close();
        		} catch (Exception ignored) {}
        	}
        }
    }
    /**
     * Writes the content of the inputStream to the file called filename under the output directory.
     * @param filename the filename
     * @param in the inputStream
     * @throws IOException 
     * @ee writeToFile(java.io.File, InputStream)
     */
    protected void createDestFile(String filename, InputStream in) throws IOException {
        File out = createOutputFile(filename);
        FileUtils.copyInputStreamToFile(in, out);
    }
    
    /**
     * Creates the directory using the entry-name. Calls createDirectory(String).
     * @param entry
     * @throws IOException if the directory cannot be created.
     * @see createDirectory(java.lang.String)
     */
    protected void createDirectory(ZipEntry entry) throws IOException {
        String name = entry.getName();
        createDirectory(name);
    }
    
    /**
     * Creates a directory with the given name under the outputDir. 
     * Trims the filename first.
     * @param name
     * @throws IOException if the directory cannot be created. Or if the given name is empty.
     */
    protected void createDirectory(String name) throws IOException {
        File destDir = createOutputFile(name);
        if (!destDir.exists() && !destDir.mkdirs())
            throw new IOException("Cannot create directory \"" + name + "\".");
    }
    
    /**
     * Trims the filename. Removes the relative path "../" and starting "/".
     * Normally calling createOutputFile(java.lang.String) is sufficient.
     * @param name the filename.
     * @return trimmed filename.
     */
    protected String trimFilename(String name) {
        if (name==null) return "";
        name = name.replace("../", "");
        while (name.startsWith("/"))
            name = name.substring(1);
        return name;
    }
    /**
     * @param trimmedName
     * @return a file under the output directory with the given name. The name will be trimmed first.
     * @throws IOException if the name is empty.
     * @throws NullPointerException if the output directory is not set.
     */
    protected File createOutputFile(String name) throws IOException {
        name = trimFilename(name);
        if (name.isEmpty())
            throw new IOException("Cannot create output file if the given name is an empty string.");
        if (outputDir==null)
            throw new NullPointerException("The output directory is null.");
        return new File(outputDir, name);
    }

    /**
     * @return the outputDir
     */
    public File getOutputDir() {
        return outputDir;
    }

    /**
     * @param outputDir the outputDir to set
     */
    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * @return the docxFile
     */
    public File getDocxFile() {
        return inputZipFile;
    }

    /**
     * @param docxFile the docxFile to set
     */
    public void setDocxFile(File docxFile) {
        this.inputZipFile = docxFile;
    }
    
    /**
     * Unpacks a docx (or any zip) file to the output directory.
     * @param docxFile the zipfile or the docx file
     * @param outputDir the output directory
     * @throws IOException 
     */
    public static void unpack(File docxFile, File outputDir) throws IOException {
        Unzipper unzip = new Unzipper(docxFile, outputDir);
        unzip.unpack();
    }
    /**
     * Unpacks a docx (or any zip) file to the output directory.
     * @param docxFile
     * @param outputPath the path to the output directory
     * @throws IOException 
     */
    public static void unpack(File docxFile, String outputPath) throws IOException {
        File outputDir = new File(outputPath);
        unpack(docxFile, outputDir);
    }
    /**
     * Unpacks a docx (or any zip) file to the output directory.
     * @param docxPath
     * @param outputPath
     * @throws IOException 
     */
    public static void unpack(String docxPath, String outputPath) throws IOException {
        File docxFile = new File(docxPath);
        unpack(docxFile, outputPath);
    }
    
} 