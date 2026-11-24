package com.agnello.order;

/** The Catalog Service could not give the wine: it cannot be reached, it fails, or it has no such active wine. */
public class CatalogUnavailableException extends RuntimeException {

    public CatalogUnavailableException(Throwable cause) {
        super("Catalog Service unavailable: " + cause, cause);
    }
}
