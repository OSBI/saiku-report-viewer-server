package org.saiku.reportviewer.server.util;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

public class MockUriInfo implements UriInfo {
    public URI getAbsolutePath() {
        return null;
    }

    public UriBuilder getAbsolutePathBuilder() {
        return null;
    }

    public URI getBaseUri() {
        return null;
    }

    public UriBuilder getBaseUriBuilder() {
        return null;
    }

    public List<Object> getMatchedResources() {
        return null;
    }

    public List<String> getMatchedURIs() {
        return null;
    }

    public List<String> getMatchedURIs(boolean decode) {
        return null;
    }

    public String getPath() {
        return null;
    }

    public String getPath(boolean decode) {
        return null;
    }

    public MultivaluedMap<String,String> getPathParameters() {
        return null;
    }

    public MultivaluedMap<String,String> getPathParameters(boolean decode) {
        return null;
    }

    public List<PathSegment> getPathSegments() {
        return null;
    }

    public List<PathSegment> getPathSegments(boolean decode) {
        return null;
    }

    public MultivaluedMap<String,String> getQueryParameters() {
        return new javax.ws.rs.core.MultivaluedHashMap<>();
    }

    public MultivaluedMap<String,String> getQueryParameters(boolean decode) {
        return new javax.ws.rs.core.MultivaluedHashMap<>();
    }

    public URI getRequestUri() {
        return null;
    }

    public UriBuilder getRequestUriBuilder() {
        return null;
    }

    public URI relativize(URI uri) {
        return null;
    }

    public URI resolve(URI uri) {
        return null;
    }
}
