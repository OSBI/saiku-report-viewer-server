package org.saiku.reportviewer.server.api;

import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/")
public interface ReportServer {
  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  Response uploadPRPTFile(DataHandler data) throws Exception;

  @GET
  @Path("/list")
  @Produces("application/json")
  String listUploadedFiles() throws Exception;

  @GET
  @Path("/render")
  @Produces("text/html")
  String render(/*String reportId, String outputFormat, Map<String, String> params*/) throws Exception;
}
