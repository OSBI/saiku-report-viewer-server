package org.saiku.reportviewer.server.api;

import java.io.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;
import org.pentaho.reporting.engine.classic.core.*;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.saiku.reportviewer.server.exporter.HtmlExporter;
import org.saiku.reportviewer.server.exporter.PdfExporter;
import org.saiku.reportviewer.server.exporter.ReportExporter;
import org.saiku.reportviewer.server.exporter.XlsExporter;

import javax.activation.DataHandler;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class ReportServerImpl implements ReportServer {
  private static final String QUERY_NAME = "ReportQuery";

  private static File reportsRoot;
  private ResourceManager mgr;
  private static List<ReportExporter> exporters;

  /**
   * Initialization method called automatically by blueprint bean instantiation.
   */
  public void init() {
    // Initialize exporters list
    if (exporters == null) {
      exporters = new ArrayList<>();
      exporters.add(new HtmlExporter());
      exporters.add(new PdfExporter());
      exporters.add(new XlsExporter());
    }

    ClassicEngineBoot.getInstance().start();
    mgr = new ResourceManager();
    mgr.registerDefaults();

    try {
      render(null, null, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public Response render(String reportId, String outputFormat, UriInfo info) throws Exception {
    File outputFile = File.createTempFile("report", outputFormat);
    OutputStream outputStream = new FileOutputStream(outputFile);

    MasterReport report = getReportDefinition(reportId);

    // Add any parameters to the report
    for (String key : info.getQueryParameters().keySet()) {
      report.getParameterValues().put(key, info.getQueryParameters().get(key));
    }

    // Process the report on the desired output format
    getExporter(outputFormat).process(outputStream, report);

    Response.ResponseBuilder response = Response.ok(outputFile);
    response.header("Content-Disposition", "attachment; filename=" + outputFile.getName());

    return response.build();
  }

  @Override
  public Response uploadPRPTFile(DataHandler data) throws Exception {
    File root = getReportsRoot();

    InputStream input = data.getInputStream();
    OutputStream output = new FileOutputStream(new File(root, data.getName()));


    byte[] bytes = new byte[1024];
    int read;

    while((read = input.read(bytes)) > 0) {
      output.write(bytes, 0, read);
    }

    input.close();
    output.flush();
    output.close();

    String result = "File saved successfully";

    return Response.status(200).entity(result).build();
  }

  @Override
  public List<String> listUploadedFiles() throws Exception {
    List<String> result = new ArrayList<>();

    File root = getReportsRoot();
    for (File f : root.listFiles()) {
      result.add(f.getName());
    }

    return result;
  }

  private File getReportsRoot() {
    if (reportsRoot == null) {
      reportsRoot = Files.createTempDir();
    }

    return reportsRoot;
  }

  private ReportExporter getExporter(String extension) {
    for (ReportExporter exporter : exporters) {
      if (exporter.getExtension().equals(extension)) return exporter;
    }
    throw new RuntimeException("Not found any available exporter for " + extension + " format");
  }

  @Override
  public String helloWorld() {
    return "{\"status\": \"ok\", \"message\": \"Hello World\"}";
  }

  protected MasterReport getReportDefinition(String reportName) throws Exception {
    File f = new File(getReportsRoot(), reportName);
    URL reportDefinitionURL = f.toURI().toURL();

    // Parse the report file
    Resource directly = mgr.createDirectly(reportDefinitionURL, MasterReport.class);
    MasterReport report = (MasterReport)directly.getResource();

    report.setQuery(QUERY_NAME);
    report.setVisible(true);

    return report;
  }
}
