package org.saiku.reportviewer.server.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public interface ReportServer {
  @GET
  @Path("/html")
  @Produces("application/xml")
  String exportReportToHTML() throws Exception;
}
