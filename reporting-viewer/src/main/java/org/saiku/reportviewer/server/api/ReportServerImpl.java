package org.saiku.reportviewer.server.api;

import java.io.*;

import java.net.URL;
import java.util.Map;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.factory.image.ImageResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.factory.property.PropertiesResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.loader.URLResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.loader.file.FileResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.loader.raw.RawResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.loader.resource.ClassloaderResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.loader.zip.ZipResourceLoader;

import javax.activation.DataHandler;
import javax.ws.rs.core.Response;

public class ReportServerImpl implements ReportServer {
  private static File reportsRoot;
  ResourceManager mgr;
  public void init(){
    ClassicEngineBoot.getInstance().start();
    mgr = new ResourceManager();
    //mgr.registerDefaults();
  }
  @Override
  public String render(/*String reportId, String outputFormat, Map<String, String> params*/) throws Exception {
    byte[] reportBytes = IOUtils.toByteArray(ReportServerImpl.class.getResourceAsStream("/basic_sample.prpt"));



   // final URL url = ReportServerImpl.class.getResource("/basic_sample.prpt");


   /* mgr.registerLoader(new URLResourceLoader());
    mgr.registerLoader(new FileResourceLoader());
    mgr.registerLoader(new RawResourceLoader());
    mgr.registerLoader(new ClassloaderResourceLoader());
    mgr.registerLoader(new ZipResourceLoader());

    //mgr.registerDefaults();
    mgr.registerFactory(new PropertiesResourceFactory());
    mgr.registerFactory(new DrawableResourceFactory());
    mgr.registerFactory(new ImageResourceFactory());*/




    MasterReport report = (MasterReport) mgr.createDirectly(reportBytes, MasterReport.class).getResource();

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bos, true);

    HtmlReportUtil.createStreamHTML(report, ps);

    ps.flush();
    bos.flush();

    return bos.toString();
    //return "<html><body><h1>It Works</h1></body></html>";
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
  public String listUploadedFiles() throws Exception {
    StringBuilder result = new StringBuilder("[");

    File root = getReportsRoot();
    for (File f : root.listFiles()) {
      if (result.length() > 1) {
        result.append(",");
      }
      result.append(f.getName());
    }

    result.append("]");

    return result.toString();
  }

  private File getReportsRoot() {
    if (reportsRoot == null) {
      reportsRoot = Files.createTempDir();
    }

    return reportsRoot;
  }

  public static void main(String[] args) throws Exception {
    ReportServerImpl r = new ReportServerImpl();
    System.out.println(r.render());
  }
}
