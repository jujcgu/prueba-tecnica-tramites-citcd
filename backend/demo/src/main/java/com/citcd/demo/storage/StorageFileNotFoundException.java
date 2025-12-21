package com.citcd.demo.storage;

public class StorageFileNotFoundException extends StorageException {

	private static final long serialVersionUID = -8758352137716297587L;

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
