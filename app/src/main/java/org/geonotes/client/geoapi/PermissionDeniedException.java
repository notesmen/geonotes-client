package org.geonotes.client.geoapi;

public class PermissionDeniedException extends Exception {
    PermissionDeniedException(String errorMessage) {
        super(errorMessage);
    }
}
