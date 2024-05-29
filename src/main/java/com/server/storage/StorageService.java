package com.server.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

	void init();

	Path getLocation();

	void store(MultipartFile file);
	void store(MultipartFile file, String dir);

	Stream<Path> loadAll();
	List<InputStream> loadAll(String dir);

	Path load(String filename);

	Resource loadAsResource(String filename);

	void deleteAll();

}
