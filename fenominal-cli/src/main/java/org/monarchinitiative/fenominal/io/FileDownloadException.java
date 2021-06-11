package org.monarchinitiative.fenominal.io;

import java.io.Serial;

public class FileDownloadException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileDownloadException() {
        super();
    }

    public FileDownloadException(String msg) {
        super(msg);
    }

    public FileDownloadException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
