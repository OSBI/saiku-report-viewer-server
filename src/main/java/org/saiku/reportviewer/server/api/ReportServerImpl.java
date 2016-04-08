package org.saiku.reportviewer.server.api;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class ReportServerImpl implements ReportServer {
  @Override
  public String exportReportToHTML() throws Exception {
    return "<html><body><h1>Hello World</h1></body></html>";
  }

  public static void main(String[] args) throws Exception {
    ClassicEngineBoot.getInstance().start();

    URL url = ReportServerImpl.class.getResource("/basic_sample.prpt");
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly(url, MasterReport.class).getResource();

    HtmlReportUtil.createDirectoryHTML(report, "report.html");
    System.out.println ("Successfully created 'report.html' in the current directory.");
  }
}
