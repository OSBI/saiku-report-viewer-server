package org.saiku.reportviewer.server.exporter;

import org.pentaho.reporting.engine.classic.core.MasterReport;

import java.io.OutputStream;

public interface ReportExporter {
  void process(OutputStream outputStream, MasterReport report);
  String getExtension();
}
