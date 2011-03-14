/**
 * 
 */
package com.mycallstation.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * @author Jack
 * 
 */
public abstract class ClassUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(ClassUtils.class);

	public static final String CLASSPATH_PROPERTY_KEY = "java.class.path";

	public static final String CLASS_SUFFIX = ".class";

	/** URL protocol for an entry from a jar file: "jar" */
	public static final String URL_PROTOCOL_JAR = "jar";

	/** URL protocol for an entry from a zip file: "zip" */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** URL protocol for an entry from a WebSphere jar file: "wsjar" */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/** URL protocol for an entry from an OC4J jar file: "code-source" */
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

	/** Separator between JAR URL and file path within the JAR */
	public static final String JAR_URL_SEPARATOR = "!/";

	private static void recursivelyListDir(Collection<String> dirListing,
			File dir, StringBuilder relativePath) {
		int prevLen; // used to undo append operations to the StringBuffer

		// if the dir is really a directory
		if (dir.isDirectory()) {
			// get a list of the files in this directory
			File[] files = dir.listFiles();
			// for each file in the present dir
			for (int i = 0; i < files.length; i++) {
				// store our original relative path string length
				prevLen = relativePath.length();
				// call this function recursively with file list from present
				// dir and relateveto appended with present dir
				recursivelyListDir(
						dirListing,
						files[i],
						relativePath.append(prevLen == 0 ? "" : "/").append(
								files[i].getName()));
				// delete subdirectory previously appended to our relative path
				relativePath.delete(prevLen, relativePath.length());
			}
		} else {
			// this dir is a file; append it to the relativeto path and add it
			// to the directory listing
			dirListing.add(relativePath.toString());
		}
	}

	public static Collection<Package> findPackages(String basePackage,
			Class<? extends Annotation> annotationClass) {
		Collection<Package> ret = new HashSet<Package>();
		Package[] packages = Package.getPackages();
		for (Package p : packages) {
			if (p.getName().startsWith(basePackage)
					&& p.isAnnotationPresent(annotationClass)) {
				ret.add(p);
			}
		}
		return ret;
	}

	public static Collection<Class<?>> findClasses(String basePackage,
			Class<? extends Annotation> annotationClass) {
		ClassLoader classLoader = getDefaultClassLoader();
		if (logger.isTraceEnabled()) {
			logger.trace(
					"Looking for classes by using class loader: \"{}\" within base package: \"{}\" and annotation: \"{}\" present.",
					new Object[] { classLoader, basePackage,
							annotationClass.getName() });
		}
		String packageDir = basePackage.replace('.', '/');
		Enumeration<URL> urls = null;
		try {
			urls = classLoader.getResources(packageDir);
		} catch (IOException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Cannot load packages.", e);
			}
			return Collections.emptyList();
		}
		Set<JarFile> jarFiles = new HashSet<JarFile>();
		Set<File> fsFiles = new HashSet<File>();
		Collection<Class<?>> ret = new HashSet<Class<?>>();
		try {
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (logger.isTraceEnabled()) {
					logger.trace(
							"Get URL for base package: \"{}\", url: \"{}\"",
							basePackage, url);
				}
				if (isJarUrl(url)) {
					if (logger.isTraceEnabled()) {
						logger.trace("Url: \"{}\" is a jar file.", url);
					}
					JarFile jarFile = null;
					String urlFile = url.getFile();
					int separatorIndex = urlFile
							.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
					if (separatorIndex != -1) {
						String jarFileUrl = urlFile
								.substring(0, separatorIndex);
						try {
							jarFile = getJarFile(jarFileUrl);
						} catch (IOException e) {
							if (logger.isWarnEnabled()) {
								logger.warn("Cannot get jar file.", e);
							}
							continue;
						}
					} else {
						try {
							jarFile = new JarFile(urlFile);
						} catch (IOException e) {
							if (logger.isWarnEnabled()) {
								logger.warn("Cannot get jar file.", e);
							}
							continue;
						}
					}
					if (jarFile != null) {
						if (logger.isTraceEnabled()) {
							logger.trace(
									"Adding jar file into jar file list. Jar file name: \"{}\"",
									jarFile.getName());
						}
						jarFiles.add(jarFile);
					}
				} else {
					File fs = new File(url.getFile());
					if (logger.isTraceEnabled()) {
						logger.trace(
								"Adding file system file into list. File name: \"{}\"",
								fs.getAbsolutePath());
					}
					fsFiles.add(fs);
				}
			}

			for (JarFile jf : jarFiles) {
				Enumeration<JarEntry> entries = jf.entries();
				while (entries != null && entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					addClass(ret, entry.getName(), basePackage, annotationClass);
				}
			}
			for (File f : fsFiles) {
				Collection<String> dirListing = new ArrayList<String>();
				recursivelyListDir(dirListing, f, new StringBuilder());
				Enumeration<String> files = Collections.enumeration(dirListing);
				while (files != null && files.hasMoreElements()) {
					String file = packageDir + "/" + files.nextElement();
					addClass(ret, file, basePackage, annotationClass);
				}
			}
		} finally {
			if (jarFiles != null) {
				for (JarFile jf : jarFiles) {
					try {
						jf.close();
					} catch (IOException e) {
						if (logger.isWarnEnabled()) {
							logger.warn(
									"Cannot close jar file: \"" + jf.getName()
											+ "\"", e);
						}
					}
				}
			}
		}

		return ret;
	} // end method

	private static void addClass(Collection<Class<?>> klasses, String fileName,
			String basePackage, Class<? extends Annotation> annotationClass) {
		ClassLoader classLoader = getDefaultClassLoader();
		// we only want the class files
		if (fileName.endsWith(CLASS_SUFFIX)) {
			// convert our full filename to a fully qualified class name
			String className = fileName.replaceAll("/", ".").substring(0,
					fileName.length() - CLASS_SUFFIX.length());
			// skip any classes in packages not explicitly requested in
			// our package filter
			if (!className.startsWith(basePackage)) {
				return;
			}
			// get the class for our class name
			Class<?> theClass = null;
			try {
				theClass = Class.forName(className, false, classLoader);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Skipping class \"" + className + "\"", e);
				}
				return;
			}
			if (theClass.isAnnotationPresent(annotationClass)) {
				if (logger.isTraceEnabled()) {
					logger.trace("Class: \"{}\" been added.",
							theClass.getName());
				}
				klasses.add(theClass);
			}
		}
	}

	private static boolean isJarUrl(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol)
				|| URL_PROTOCOL_ZIP.equals(protocol)
				|| URL_PROTOCOL_WSJAR.equals(protocol) || (URL_PROTOCOL_CODE_SOURCE
				.equals(protocol) && url.getPath().indexOf(JAR_URL_SEPARATOR) != -1));
	}

	/**
	 * Resolve the given jar file URL into a JarFile object.
	 */
	protected static JarFile getJarFile(String jarFileUrl) throws IOException {
		if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
			try {
				return new JarFile(ResourceUtils.toURI(jarFileUrl)
						.getSchemeSpecificPart());
			} catch (URISyntaxException ex) {
				// Fallback for URLs that are not valid URIs (should hardly ever
				// happen).
				return new JarFile(
						jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX
								.length()));
			}
		} else {
			return new JarFile(jarFileUrl);
		}
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			logger.debug(
					"Cannot access thread context ClassLoader - falling back to system class loader",
					ex);
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
		}
		return cl;
	}
}
