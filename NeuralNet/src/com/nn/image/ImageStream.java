package com.nn.image;

import java.awt.image.BufferedImage;

public class ImageStream implements java.io.Serializable {

	private static final long serialVersionUID = -5726152898244630781L;
	public final int width;
	public final int height;
	public final int[] pixels;
	public final int type;
	public final String name;

	transient private BufferedImage img = null; 
	
	public ImageStream(BufferedImage img, String name)
	{
		this.img = img;
		
		this.name = name;
		
		this.width  = img.getWidth();
		this.height = img.getHeight();
		this.type   = img.getType();
		
		//this.pixels = img.getRGB(0, 0, width, height, new int[width*height], 0, width);
		this.pixels = img.getRGB(0, 0, width, height, null, 0, width);
	}
	
	public BufferedImage getImg()
	{
		if( img == null )
		{
			img = new BufferedImage(width, height, type);
			img.setRGB(0, 0, width, height, pixels, 0, width);
		}
		return img;
	}
	
}
