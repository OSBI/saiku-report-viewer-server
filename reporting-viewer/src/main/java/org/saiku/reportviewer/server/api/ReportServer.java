package org.saiku.reportviewer.server.api;

import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/")
public interface ReportServer {
  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  Response uploadPRPTFile(DataHandler data) throws Exception;

  @GET
  @Path("/list")
  @Produces("application/json")
  List<String> listUploadedFiles() throws Exception;

  @GET
  @Path("/render/{id}.{format}")
  @Produces("text/html")
  Response render(@PathParam("id") String reportId, @PathParam("format") String outputFormat,
                @Context UriInfo info) throws Exception;

  @GET
  @Path("/hello_world")
  @Produces("application/json")
  String helloWorld() throws Exception;
}
