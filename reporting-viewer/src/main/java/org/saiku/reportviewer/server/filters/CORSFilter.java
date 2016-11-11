package org.saiku.reportviewer.server.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by brunogamacatao on 10/11/16.
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {

  @Override
  public void filter(final ContainerRequestContext requestContext,
                     final ContainerResponseContext cres) throws IOException {
    cres.getHeaders().add("Access-Control-Allow-Origin", "*");
    cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
    cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
    cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    cres.getHeaders().add("Access-Control-Max-Age", "1209600");
  }
}
