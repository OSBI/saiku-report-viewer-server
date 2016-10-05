package org.saiku.reportviewer.server.util;

import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultHtmlContentGenerator;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class SaikuHtmlPrinter extends AllItemsHtmlPrinter {
  private SaikuHtmlContentGenerator generator;

  public SaikuHtmlPrinter(ResourceManager resourceManager) {
    super(resourceManager);
    this.generator = new SaikuHtmlContentGenerator(resourceManager);
  }

  @Override
  public DefaultHtmlContentGenerator getContentGenerator() {
    return generator;
  }
}
