package com.citcd.demo.storage;

public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 5703994142051128130L;

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
