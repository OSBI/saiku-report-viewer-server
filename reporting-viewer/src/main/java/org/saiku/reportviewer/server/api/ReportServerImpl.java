package org.saiku.reportviewer.server.api;

import java.io.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;
import org.pentaho.reporting.engine.classic.core.*;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.*;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.FlowExcelOutputProcessor;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.activation.DataHandler;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class ReportServerImpl implements ReportServer {
  private static final String QUERY_NAME = "ReportQuery";

  private static File reportsRoot;
  private ResourceManager mgr;

  /**
   * Initialization method called automatically by blueprint bean instantiation.
   */
  public void init() {
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

    // Prepare to generate the report
    AbstractReportProcessor reportProcessor = null;

    try {
      // Create the report generator for each report type
      switch (outputFormat) {
        case "xls":
          reportProcessor = createXlsProcessor(outputStream, report);
        case "pdf":
          reportProcessor = createPdfProcessor(outputStream, report);
        case "html":
          reportProcessor = createHtmlProcessor(outputStream, report);
          break;
      }

      // Fill and generate report
      reportProcessor.processReport();
    } finally {
      // Ensure that the processor was correctly closed
      reportProcessor.close();
    }

    Response.ResponseBuilder response = Response.ok(outputFile);
    response.header("Content-Disposition", "attachment; filename=" + outputFile.getName());

    return response.build();
  }

  private AbstractReportProcessor createXlsProcessor(OutputStream outputStream, MasterReport report) throws ReportProcessingException {
    FlowExcelOutputProcessor target = new FlowExcelOutputProcessor(report.getConfiguration(), outputStream, report.getResourceManager());
    return new FlowReportProcessor(report, target);
  }

  private AbstractReportProcessor createPdfProcessor(OutputStream outputStream, MasterReport report) throws ReportProcessingException {
    PdfOutputProcessor outputProcessor = new PdfOutputProcessor(report.getConfiguration(), outputStream, report.getResourceManager());
    return new PageableReportProcessor(report, outputProcessor);
  }

  private AbstractReportProcessor createHtmlProcessor(OutputStream outputStream, MasterReport report) throws ReportProcessingException {
    StreamRepository targetRepository = new StreamRepository(outputStream);
    ContentLocation targetRoot = targetRepository.getRoot();
    HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor(report.getConfiguration());
    HtmlPrinter printer = new AllItemsHtmlPrinter(report.getResourceManager());
    printer.setContentWriter(targetRoot, new DefaultNameGenerator(targetRoot, "index", "html"));
    printer.setDataWriter(null, null);
    printer.setUrlRewriter(new FileSystemURLRewriter());
    outputProcessor.setPrinter(printer);
    return new StreamReportProcessor(report, outputProcessor);
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

  public static void main(String[] args) throws Exception {
    ReportServerImpl r = new ReportServerImpl();
    System.out.println(r.render(null, null, null));
  }
}
