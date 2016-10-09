package org.saiku.reportviewer.server.exporter;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.*;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.saiku.reportviewer.server.util.SaikuHtmlPrinter;

import javax.ws.rs.core.MediaType;
import java.io.OutputStream;

/**
 * Implementation of the ReportExporter interface to the HTML file format.
 */
public class HtmlExporter implements ReportExporter {
  private static final String NAME_HINT = "index";
  private static final String SUFFIX = "html";

  @Override
  public String getExtension() {
    return "html";
  }

  @Override
  public void process(OutputStream outputStream, MasterReport report) {
    AbstractReportProcessor processor = null;
    StreamRepository targetRepository = new StreamRepository(outputStream);
    ContentLocation targetRoot = targetRepository.getRoot();
    HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor(report.getConfiguration());
    HtmlPrinter printer = new SaikuHtmlPrinter(report.getResourceManager());

    printer.setContentWriter(targetRoot, new DefaultNameGenerator(targetRoot, NAME_HINT, SUFFIX));
    printer.setDataWriter(null, null);
    printer.setUrlRewriter(new FileSystemURLRewriter());
    outputProcessor.setPrinter(printer);

    try {
      processor = new StreamReportProcessor(report, outputProcessor);
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
    return MediaType.TEXT_HTML_TYPE;
  }
}
