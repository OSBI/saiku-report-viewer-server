package org.saiku.reportviewer.server.api;

public class ReportServerImpl implements ReportServer {
  @Override
  public String exportReportToHTML() throws Exception {
    return "<html><body><h1>Hello World 4</h1></body></html>";
  }
}
