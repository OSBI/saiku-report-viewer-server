package org.saiku.reportviewer.server.exporter;

import org.pentaho.reporting.engine.classic.core.MasterReport;

import java.io.OutputStream;

/**
 * Definition of the ReportExporter interface.
 */
public interface ReportExporter {
  /**
   * Method that process an report definition, exporting it to an specific file format.
   * @param outputStream The outpuStream to which the report definition will be generated.
   * @param report The report definition object.
   */
  void process(OutputStream outputStream, MasterReport report);

  /**
   * This method should return the file extension to which this exporter renders reports.
   * @return An string containing this exporter file extension.
   */
  String getExtension();
}
