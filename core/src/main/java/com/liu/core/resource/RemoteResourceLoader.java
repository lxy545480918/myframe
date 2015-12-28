package com.liu.core.resource;

import org.springframework.core.io.Resource;

import java.io.IOException;

public interface RemoteResourceLoader {
	public Resource load(String path, boolean isCache) throws IOException;

	public Resource load(String path) throws IOException;
}
