package at.ac.uibk.igwee.xslt.impl;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XsltUtils {
	
	/**
	 * Creates a Result from an OutputStream.
	 * @param os the outputStream
	 * @return a Result
	 */
	public static Result createResult(OutputStream os) {
		return new StreamResult(os);
	}
	
	/**
	 * Creates a Source from an InputStream.
	 * @param is InputStream of the Source.
	 * @return the source.
	 */
	public static Source createSource(InputStream is) {
		return new StreamSource(is);
	}
	/**
	 * Creates a Source from an URI.
	 * @param uri Uri for the source.
	 * @return the source.
	 */
	public static Source createSource(String uri) {
		return new StreamSource(uri);
	}
	
	public static String nullOrEmpty(String s) {
		if (s==null) return "";
		return s;
	}
	
}
