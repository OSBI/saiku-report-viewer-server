package org.saiku.reportviewer.server.api;

import java.io.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportingInterface;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
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
import javax.ws.rs.core.Response;

public class ReportServerImpl implements ReportServer {
  private static File reportsRoot;
  private ResourceManager mgr;
  private ReportingInterface myservice;

  public void init() {
    //ClassicEngineBoot.getInstance().start();
    //mgr = new ResourceManager();
    //mgr.registerDefaults();
    try {
      render(null, null, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setMyservice(org.pentaho.reporting.engine.classic.core.ReportingInterface myservice){
  this.myservice = myservice;
  }

  @Override
  public String render(String reportId, String outputFormat, HttpServletRequest request) throws Exception {


    return myservice.createReport("/tmp/");
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

  public static void main(String[] args) throws Exception {
    ReportServerImpl r = new ReportServerImpl();
    System.out.println(r.render(null, null, null));
  }
}
