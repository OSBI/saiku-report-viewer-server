package org.saiku.reportviewer.server.api;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.*;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
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

/**
 * This is the implementation of the ReportServer interface. It is the core of the Saiku Report processing (fill and
 * render).
 */
public class ReportServerImpl implements ReportServer {
  /**
   * This is the folder where the uploaded reports will be placed.
   */
  private static final String REPORTS_FOLDER = "./reports/upload/";
  private static final String CONTENT_DISPOSITION = "Content-Disposition";
  private static final String ATTACHMENT_FILENAME = "inline; filename=";
  private static final String FILE_SAVED_SUCCESSFULLY_MESSAGE = "File saved successfully";

  private ResourceManager mgr;
  private static List<ReportExporter> exporters;

  static {
    // Initialize exporters list (each output format has an associated exporter implementation)
    exporters = new ArrayList<>();
    exporters.add(new HtmlExporter());
    exporters.add(new PdfExporter());
    exporters.add(new XlsExporter());
  }

  /**
   * Initialization method called automatically by blueprint bean instantiation.
   */
  public void init() {
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

    /*
     * Those are some sample routes to provided PRPT files in order to ease the process of testing and demoing the
      * Saiku Report Server.
     */
    if (reportId.equals("test")) {
      report = ReportUtil.getAndFillReport(mgr, ReportServerImpl.class.getResource("/basic_sample.prpt"), info.getQueryParameters());
    } else if (reportId.equals("test_data")) {
      report = ReportUtil.getAndFillReport(mgr, ReportServerImpl.class.getResource("/test.prpt"), info.getQueryParameters());
    } else if (reportId.equals("test_params")) {
      report = ReportUtil.getAndFillReport(mgr, ReportServerImpl.class.getResource("/test_params.prpt"), info.getQueryParameters());
    } else if (reportId.equals("test_image")) {
      report = ReportUtil.getAndFillReport(mgr, ReportServerImpl.class.getResource("/test_image.prpt"), info.getQueryParameters());
    } else if (reportId.equals("demo")) {
      report = ReportUtil.getAndFillReport(mgr, ReportServerImpl.class.getResource("/test_demo.prpt"), info.getQueryParameters());
      setDataFactory(report);
    } else {
      report = ReportUtil.getAndFillReport(mgr, new File(getReportsRoot(), reportId), info.getQueryParameters());
    }

    // Set report's data factory
    setDataFactory(report);

    // Process the report on the desired output format
    ReportExporter exporter = getExporter(outputFormat);
    exporter.process(outputStream, report);

    Response.ResponseBuilder response = Response.ok(outputFile, exporter.getMediaType());
    response.header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + outputFile.getName());

    return response.build();
  }

  private void setDataFactory(MasterReport report) {
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

  /**
   * This is the implementation of the helloWorld, a simple 'pingish' method. It is used to determine if the server was
   * successfully deployed and running.
   * @return A single string to show that the server is deployed and running.
   */
  @Override
  public String helloWorld() {
    return "{\"status\": \"ok\", \"message\": \"Hello World\"}";
  }
}
