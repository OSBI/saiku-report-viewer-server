package org.saiku.reportviewer.server.util;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.net.URL;

/**
 * A class containing utility methods to handle report definition objects.
 */
public class ReportUtil {
  private static final String QUERY_NAME = "ReportQuery";

  public static MasterReport getReportDefinition(ResourceManager mgr, File reportFile) throws Exception {
    return getReportDefinition(mgr, reportFile.toURI().toURL());
  }

  public static MasterReport getReportDefinition(ResourceManager mgr, URL reportDefinitionURL) throws Exception {
    // Parse the report file
    Resource directly = mgr.createDirectly(reportDefinitionURL, MasterReport.class);
    MasterReport report = (MasterReport)directly.getResource();

    report.setQuery(QUERY_NAME);
    report.setVisible(true);

    return report;
  }


  public static void fillParameters(MasterReport report, MultivaluedMap<String, String> params) {
    for (String key : params.keySet()) {
      report.getParameterValues().put(key, params.get(key));
    }
  }


  public static MasterReport getAndFillReport(ResourceManager mgr, File reportFile, MultivaluedMap<String, String> params) throws Exception {
    MasterReport report = getReportDefinition(mgr, reportFile);
    fillParameters(report, params);
    return report;
  }

  public static MasterReport getAndFillReport(ResourceManager mgr, URL reportURL, MultivaluedMap<String, String> params) throws Exception {
    MasterReport report = getReportDefinition(mgr, reportURL);
    fillParameters(report, params);
    return report;
  }
}
