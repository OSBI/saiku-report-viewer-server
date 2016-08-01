package org.saiku.reportviewer.server.api;

import java.io.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportingInterface;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.*;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.factory.image.ImageResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.factory.property.PropertiesResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.loader.URLResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.loader.file.FileResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.loader.raw.RawResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.loader.resource.ClassloaderResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.loader.zip.ZipResourceLoader;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class ReportServerImpl implements ReportServer {
  private static final String QUERY_NAME = "ReportQuery";

  private static File reportsRoot;
  private ResourceManager mgr;
  private ReportingInterface myservice;

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

  /**
   * Hook to set the ReportingInterface implementation via blueprint injection.
   * @param myservice ReportingInterface implementation
   */
  public void setMyservice(org.pentaho.reporting.engine.classic.core.ReportingInterface myservice){
    this.myservice = myservice;
  }

  @Override
  public Response render(String reportId, String outputFormat, UriInfo info) throws Exception {
    File outputFile = File.createTempFile("report", outputFormat);
    OutputStream outputStream = new FileOutputStream(outputFile);

    MasterReport report = getReportDefinition(reportId);
    DataFactory dataFactory = getDataFactory();

    if (dataFactory != null) {
      report.setDataFactory(dataFactory);
    }

    // Add any parameters to the report
    for (String key : info.getQueryParameters().keySet()) {
      report.getParameterValues().put(key, info.getQueryParameters().get(key));
    }

    // Prepare to generate the report
    AbstractReportProcessor reportProcessor = null;

    switch (outputFormat) {
      case "xls": // TODO - Implement XLS (Excel) generation
      case "pdf": // TODO - Implement PDF generation
      case "html":
        StreamRepository targetRepository = new StreamRepository(outputStream);
        ContentLocation targetRoot = targetRepository.getRoot();
        HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor(report.getConfiguration());
        HtmlPrinter printer = new AllItemsHtmlPrinter(report.getResourceManager());
        printer.setContentWriter(targetRoot, new DefaultNameGenerator(targetRoot, "index", "html"));
        printer.setDataWriter(null, null);
        printer.setUrlRewriter(new FileSystemURLRewriter());
        outputProcessor.setPrinter(printer);
        reportProcessor = new StreamReportProcessor(report, outputProcessor);
        break;
    }

    reportProcessor.processReport();

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

  protected DataFactory getDataFactory() {
    // TODO - Implement the datafactory via datasource
    return null;
  }

  public static void main(String[] args) throws Exception {
    ReportServerImpl r = new ReportServerImpl();
    System.out.println(r.render(null, null, null));
  }
}
