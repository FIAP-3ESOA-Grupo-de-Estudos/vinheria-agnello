package br.com.vinheriaagnello.service;

import com.google.cloud.storage.*;
import com.google.cloud.storage.StorageOptions;

import java.io.InputStream;

public class StorageService {
    private static final String BUCKET = "vinheria-files";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public String uploadFile(InputStream fileStream, String fileName) throws Exception {
        BlobId blobId = BlobId.of(BUCKET, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.createFrom(blobInfo, fileStream);
        return "https://storage.googleapis.com/" + BUCKET + "/" + fileName;
    }
}
