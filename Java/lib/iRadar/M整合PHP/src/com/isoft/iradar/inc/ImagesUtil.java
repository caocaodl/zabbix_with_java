package com.isoft.iradar.inc;

import static com.isoft.iradar.Cgd.imagecreate;
import static com.isoft.iradar.Cgd.imagefilledrectangle;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.types.CArray.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.core.g;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class ImagesUtil {

	private ImagesUtil() {
	}
	
	public static byte[] get_default_image(){
		BufferedImage image = imagecreate(50, 50);
		Graphics2D gd = image.createGraphics();
		Color color = new Color(250,50,50);
		imagefilledrectangle(gd,0,0,50,50,color);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.flush();
		try {
			ImageIO.write(image, "PNG", baos);
			baos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			IOUtils.closeQuietly(baos);
		}		
	}
	
	/**
	 * Get image data from db, cache is used
	 * @param  imageid
	 * @return array image data from db
	 */
	public static Map get_image_by_imageid(SQLExecutor executor, String imageid) {
		CArray<Map> images = g.images.$();
		if (images == null) {
			images = map();
			g.images.$(images);
		}

		if (!isset(images,imageid)) {
			Map params = new HashMap();
			params.put("imageid", imageid);
			Map row = DBfetch(DBselect(executor,"SELECT i.* FROM images i WHERE i.imageid=#{imageid}",params));
			Nest.value(row,"image").$(rda_unescape_image((byte[])Nest.value(row,"image").$()));
			Nest.value(images,imageid).$(row);
		}
		return images.get(imageid);
	}
	
	public static byte[] rda_unescape_image(byte[] image) {
		return (image!=null) ? image : new byte[0];
	}
	
	/**
	 * Resizes the given image resource to the specified size keeping the original
	 * proportions of the image.
	 *
	 * @param resource source
	 * @param int thumbWidth
	 * @param int thumbHeight
	 *
	 * @return resource
	 */
	public static BufferedImage imageThumb(BufferedImage source, int thumbWidth, int thumbHeight) {
		int srcWidth = source.getWidth();
		int srcHeight = source.getHeight();
		if (srcWidth > thumbWidth || srcHeight > thumbHeight) {
			if (thumbWidth == 0) {
				thumbWidth = thumbHeight * srcWidth / srcHeight;
			} else if (thumbHeight == 0) {
				thumbHeight = thumbWidth * srcHeight / srcWidth;
			} else {
				float a = thumbWidth / thumbHeight;
				float b = srcWidth / srcHeight;

				if (a > b) {
					thumbWidth = (int)(b * thumbHeight);
				} else {
					thumbHeight = (int)(thumbWidth / b);
				}
			}
			
			int type = source.getType();
			BufferedImage cropped = new BufferedImage(thumbWidth, thumbHeight, type);
			Graphics2D g = cropped.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        g.setComposite(AlphaComposite.Src);	        
	        g.drawImage(source, 0, 0, thumbWidth, thumbHeight, 0, 0, srcWidth, srcHeight, null);
	        g.dispose();
	        
	        return cropped;
		}
		return source;
	}
	
	/**
	 * Creates an image from a string preserving PNG transparency.
	 *
	 * @param imageBytes
	 *
	 * @return resource
	 */
	public static BufferedImage imageFromBytes(byte[] imageBytes) {
		try {
			return ImageIO.read(new ByteArrayInputStream(imageBytes));
		} catch (Exception e) {
			return null;
		}
	}
}
