package org.saiku.reportviewer.server.exporter;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;

import javax.ws.rs.core.MediaType;
import java.io.OutputStream;

/**
 * Implementation of the ReportExporter interface to the PDF file format.
 */
public class PdfExporter implements ReportExporter {
  @Override
  public String getExtension() {
    return "pdf";
  }

  @Override
  public void process(OutputStream outputStream, MasterReport report) {
    AbstractReportProcessor processor = null;
    PdfOutputProcessor outputProcessor = new PdfOutputProcessor(report.getConfiguration(), outputStream, report.getResourceManager());

    try {
      processor = new PageableReportProcessor(report, outputProcessor);
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

  @Override
  public MediaType getMediaType() {
    return new MediaType("application", "pdf");
  }
}
