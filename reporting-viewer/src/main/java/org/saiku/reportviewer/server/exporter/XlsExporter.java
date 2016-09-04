package org.saiku.reportviewer.server.exporter;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.FlowExcelOutputProcessor;

import java.io.OutputStream;

public class XlsExporter implements ReportExporter {
  @Override
  public String getExtension() {
    return "xls";
  }

  @Override
  public void process(OutputStream outputStream, MasterReport report) {
    AbstractReportProcessor processor = null;
    FlowExcelOutputProcessor target = new FlowExcelOutputProcessor(report.getConfiguration(), outputStream, report.getResourceManager());

    try {
      processor = new FlowReportProcessor(report, target);
      // Fill and generate report
      processor.processReport();
    } catch (ReportProcessingException e) {
      throw new RuntimeException(e);
    } finally {
      // Ensure that the processor was correctly closed
      if (processor != null) {
        processor.close();
      }
    }
  }
}
