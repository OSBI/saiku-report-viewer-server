package org.saiku.reportviewer.server.util;

import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultHtmlContentGenerator;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This is the implementation of the Saiku DefaultHtmlContentGenerator, responsible for handling images resources and
 * rendering them as inline base64 images.
 */
public class SaikuHtmlContentGenerator extends DefaultHtmlContentGenerator {
  public SaikuHtmlContentGenerator(ResourceManager resourceManager) {
    super(resourceManager);
  }

  /**
   * In order of being processed, images had to be coverted first to BufferedImages objects.
   */
  private static BufferedImage toBufferedImage(Image img) {
    if (img instanceof BufferedImage) {
      return (BufferedImage)img;
    }

    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();

    return bimage;
  }

  /**
   * This method obtains the images resources, convets them to BufferedImages and uses its bytes to render the base64
   * information.
   */
  @Override
  public String writeImage(ImageContainer image, String encoderType, float quality, boolean alpha) throws ContentIOException, IOException {
    // override writeImage
    if (image instanceof DefaultImageReference) {
      DefaultImageReference dir = (DefaultImageReference) image;

      if (dir.getImage() != null && encoderType != null) {
        BufferedImage img = toBufferedImage(dir.getImage());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img,
            encoderType.substring(encoderType.indexOf("/") + 1),
            baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();

        String imageData = java.util.Base64.getEncoder().encodeToString(imageInByte);

        return "data:" + encoderType + ";base64," + imageData;
      }
      else {
        return dir.getSourceURLString();
      }
    }

    return super.writeImage(image, encoderType, quality, alpha);
  }
}
