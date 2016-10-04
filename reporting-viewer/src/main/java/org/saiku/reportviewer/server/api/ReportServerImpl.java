package org.saiku.reportviewer.server.api;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.*;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.saiku.reportviewer.server.exporter.HtmlExporter;
import org.saiku.reportviewer.server.exporter.PdfExporter;
import org.saiku.reportviewer.server.exporter.ReportExporter;
import org.saiku.reportviewer.server.exporter.XlsExporter;
import org.saiku.reportviewer.server.util.FileUtil;
import org.saiku.reportviewer.server.util.ReportUtil;

import javax.activation.DataHandler;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class ReportServerImpl implements ReportServer {
  private static final String REPORTS_FOLDER = "./reports/upload/";
  private static final String CONTENT_DISPOSITION = "Content-Disposition";
  private static final String ATTACHMENT_FILENAME = "attachment; filename=";
  private static final String FILE_SAVED_SUCCESSFULLY_MESSAGE = "File saved successfully";

  private ResourceManager mgr;
  private static List<ReportExporter> exporters;

  /**
   * Initialization method called automatically by blueprint bean instantiation.
   */
  public void init() {
    // Initialize exporters list (each output format has an associated exporter implementation)
    if (exporters == null) {
      exporters = new ArrayList<>();
      exporters.add(new HtmlExporter());
      exporters.add(new PdfExporter());
      exporters.add(new XlsExporter());
    }

    // Initialize Pentaho's reporting engine
    ClassicEngineBoot.getInstance().start();
    mgr = new ResourceManager();
    mgr.registerDefaults();
  }

  @Override
  public Response render(String reportId, String outputFormat, UriInfo info) throws Exception {
    File outputFile = FileUtil.createTempFile(outputFormat);
    OutputStream outputStream = new FileOutputStream(outputFile);

    MasterReport report = null;

    if (reportId.equals("test")) {
      report = ReportUtil.getAndFillReport(mgr, ReportServerImpl.class.getResource("/basic_sample.prpt"), info.getQueryParameters());
    } else if (reportId.equals("test_data")) {
      report = ReportUtil.getAndFillReport(mgr, ReportServerImpl.class.getResource("/test.prpt"), info.getQueryParameters());
    } else if (reportId.equals("test_image")) {
      report = ReportUtil.getAndFillReport(mgr, ReportServerImpl.class.getResource("/test_image.prpt"), info.getQueryParameters());
    } else {
      report = ReportUtil.getAndFillReport(mgr, new File(getReportsRoot(), reportId), info.getQueryParameters());
    }

    // Process the report on the desired output format
    getExporter(outputFormat).process(outputStream, report);

    Response.ResponseBuilder response = Response.ok(outputFile);
    response.header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + outputFile.getName());

    return response.build();
  }

  @Override
  public Response uploadPRPTFile(DataHandler data) throws Exception {
    FileUtil.copy(data.getInputStream(), new FileOutputStream(new File(getReportsRoot(), data.getName())));
    return Response.status(200).entity(FILE_SAVED_SUCCESSFULLY_MESSAGE).build();
  }

  @Override
  public List<String> listUploadedFiles() throws Exception {
    return FileUtil.listFileNames(getReportsRoot());
  }

  /**
   * This method returns the reports root (where the reports definitions are stored). It also checks
   * if this folder exists, otherwise it creates it.
   * @return An instance of the java.io.File class, pointing to the reports root.
   */
  private File getReportsRoot() {
    File reportsRoot = new File(REPORTS_FOLDER);

    if (!reportsRoot.exists()) {
      reportsRoot.mkdirs();
    }

    return reportsRoot;
  }

  /**
   * Helper method used to retrieve a ReportExporter instance for an specific output format.
   * @param extension The desired output format (extension) to look for a ReportExporter.
   * @return A ReportExporter instance.
   */
  private ReportExporter getExporter(String extension) {
    for (ReportExporter exporter : exporters) {
      if (exporter.getExtension().equals(extension)) return exporter;
    }
    throw new RuntimeException("Not available exporter found for " + extension + " format");
  }

  @Override
  public String helloWorld() {
    return "{\"status\": \"ok\", \"message\": \"Hello World\"}";
  }
}
