package com.liu.core.resource;

import com.liu.util.ApplicationContextHolder;
import com.liu.util.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URISyntaxException;

public class ResourceCenter implements ResourceLoaderAware {
	private static ResourceLoader loader = new DefaultResourceLoader();
	private static RemoteResourceLoader remoteLoader;

	public static Resource load(String path) throws IOException {
		Resource r = loader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + path);
		if (r.exists()) {
			return r;
		}
		r = loader.getResource(path);
		if (r.exists()) {
			return r;
		}
		if (remoteLoader != null) {
			return remoteLoader.load(path, !ApplicationContextHolder.isDevMode());
		} else {
			throw new FileNotFoundException("file not found:" + path);
		}
	}

	public static Resource load(String pathPrefix, String path) throws IOException {
		Resource r = loader.getResource(pathPrefix + path);
		if (r.exists()) {
			return r;
		} else {
			throw new FileNotFoundException("file not found:" + path);
		}
	}

	/*
	public static Resource load(String pathPrefix, String path, boolean isCache) throws IOException {
		Resource r = loader.getResource(pathPrefix + path);
		if (r.exists()) {
			if (!isCache) {
				r = loader.getResource(r.getURL().toString());
			}
			return r;
		}
		if (remoteLoader != null) {
			return remoteLoader.load(path, !ApplicationContextHolder.isDevMode());
		} else {
			throw new FileNotFoundException("file not found:" + path);
		}
	}
	*/

	public static void write(Resource r, OutputStream output) throws IOException {
		String protocol = r.getURL().getProtocol();
		boolean isFileSystem = protocol.startsWith(ResourceUtils.URL_PROTOCOL_FILE);

		if (ApplicationContextHolder.isDevMode() && isFileSystem) {
			File f = r.getFile();
			InputStream input = new FileInputStream(f);
			try {
				IOUtils.write(input, output);
			} finally {
				input.close();
			}
		} else {
			InputStream input = r.getInputStream();
			try {
				IOUtils.write(input, output);
			} finally {
				input.close();
			}
		}
	}

	@Override
	public void setResourceLoader(ResourceLoader appContextLoader) {
		loader = appContextLoader;
	}

	public void setRemoteResourceLoader(RemoteResourceLoader loader) {
		remoteLoader = loader;
	}

	public static String getAbstractClassPath() throws URISyntaxException {
		return new File(loader.getClassLoader().getResource("").toURI()).getAbsolutePath();
	}

	public static String getAbstractClassPath(String path) throws URISyntaxException {
		return StringUtils.join(getAbstractClassPath(), "/", path);
	}

}
