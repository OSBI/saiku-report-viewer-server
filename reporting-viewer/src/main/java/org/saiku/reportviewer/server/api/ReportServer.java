package org.saiku.reportviewer.server.api;

import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * This is the interface of the Saiku Report Server.
 * It also defines an API (cxf webservice) that could be consumed by other services.
 */
@Path("/")
public interface ReportServer {
  /**
   * This method should be called whenever a user needs to upload a report definition.
   * @param data The multipart form data with a file containing the report definition.
   * @return An http response with the status of the upload process.
   * @throws Exception If an error occurs in the upload process.
   */
  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  Response uploadPRPTFile(DataHandler data) throws Exception;

  /**
   * This method is called when the user wants to list all the previously uploaded report definitions.
   * @return A JSON list of all the report definitions names.
   */
  @GET
  @Path("/list")
  @Produces("application/json")
  List<String> listUploadedFiles() throws Exception;

  /**
   * This method is responsible for rendering report definitions (fetching them, filling with data and
   * exporting in the desired output format - xls, pdf or html).
   * @param reportId The report definition name (should be one contained on the /list method return).
   * @param outputFormat The output format (xls, pdf or html).
   * @param info The report parameters may be specified as HTTP query parameters, this object gathers them.
   * @return It returns to the browser, the rendered report definition file with the specified output format.
   * @throws Exception
   */
  @GET
  @Path("/render/{id}.{format}")
  @Produces("text/html")
  Response render(@PathParam("id") String reportId, @PathParam("format") String outputFormat,
                @Context UriInfo info) throws Exception;

  /**
   * This is a simple ping method to test if everything is working correctly.
   * @return A simple JSON object, containing a success status and a 'Hello World' message.
   */
  @GET
  @Path("/hello_world")
  @Produces("application/json")
  String helloWorld() throws Exception;
}
