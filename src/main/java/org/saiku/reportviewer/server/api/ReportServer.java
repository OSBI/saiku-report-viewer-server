package org.saiku.reportviewer.server.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public interface ReportServer {
  @GET
  @Path("/html")
  @Produces("text/html")
  String exportReportToHTML() throws Exception;
}
