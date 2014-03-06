package com.nn.image;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public final class ImageFeatures implements java.io.Serializable {
	
	
	private static final long serialVersionUID = 5031660889904290635L;

	private static final double powConst = 1.0/2.2;
	
	private final int widthDivisor;
	private final int heightDivisor;
	
	

	public ImageFeatures()
	{
		// Só divisores pares
		this.widthDivisor  = 8;
		this.heightDivisor = 8;
	}
	
	
	public int getTotalFeatures()
	{
		return 3 + this.widthDivisor + this.heightDivisor ;
	}
	


	public static double getGrayScale(double r, double g, double b)
	{
		return (0.2126*r) + (0.7152*g) + (0.0722*b);
	}
	public static double getGrayScale2(double r, double g, double b)
	{
		return Math.pow(0.2126*Math.pow(r, 2.2) + 0.7152*Math.pow(g, 2.2) + 0.0722*Math.pow(b, 2.2), powConst);
	}
	
	private int getColorIndexStart()
	{
		return this.widthDivisor + this.heightDivisor;
	}
	
	
	public double[] extract(BufferedImage img)
	{
		int[] pix = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		
		return extract(pix, img.getWidth(), img.getHeight());
	}
//	public double[] extract(int[] pix, int width, int height)
//	{
//
//		final int COLORS_MASK = 0x07 ;
//		
//		int totalPixels = width * height ;
//		
//		if( totalPixels != pix.length )
//			throw new IllegalArgumentException("Dimensions don't match array lenght");
//		
//		
//		double[] features = new double[getTotalFeatures()];
//		Arrays.fill(features, 0.0);
//		
//		double red,green,blue,gray;
//		
//		
//		final int subWidth  = (int) Math.ceil( (double)width  / (double)widthDivisor  ),
//			      subHeight = (int) Math.ceil( (double)height / (double)heightDivisor );
//		
//		
//		int iWidth,iHeight, w,h;
//
//		
//		for( int p = 0; p < totalPixels; p++ )
//		{
//			
//			red   = ( ( pix[ p ] >> 16 ) & 0xFF ) | COLORS_MASK ;
//			green = ( ( pix[ p ] >> 8  ) & 0xFF ) | COLORS_MASK ;
//			blue  = ( ( pix[ p ]       ) & 0xFF ) | COLORS_MASK ;
//			
//			gray = getGrayScale(red, green, blue) / (double)0xFF;
//			
//			
//			
//			w = p % width;
//			h = p / height;
//			
//			iWidth  = w / subWidth;
//			iHeight = h / subHeight;
//			
//			if( iWidth  >= widthDivisor  ) iWidth  = (widthDivisor -1) ;
//			if( iHeight >= heightDivisor ) iHeight = (heightDivisor-1);
//			
//			features[ iWidth                  ] += gray;
//			features[ iHeight + widthDivisor  ] += gray;
//			
//			features[ getColorIndexStart()     ] += red   / (double)0xFF;
//			features[ getColorIndexStart() + 1 ] += green / (double)0xFF;
//			features[ getColorIndexStart() + 2 ] += blue  / (double)0xFF;
//			
//		}
//		
//		int lastSubWidth  = width  - (subWidth *(widthDivisor -1)),
//			lastSubHeight = height - (subHeight*(heightDivisor-1));
//		
//		double horStripe0 = 0.5 * (double)(subHeight     * width ),
//			   horStripeL = 0.5 * (double)(lastSubHeight * width ),
//			   
//		       vetStripe0 = 0.5 * (double)(subWidth      * height),
//		       verStripeL = 0.5 * (double)(lastSubWidth  * height);
//		
//		double totalStripe;
//		for(int i = 0; i < widthDivisor; i++)
//		{
//			totalStripe = ( ((i+1) < widthDivisor) ? horStripe0 : horStripeL ) ;
//			
//			features[i] = -1.0 + (features[i] / totalStripe) ;
//		}
//		
//		for(int i = 0; i < heightDivisor; i++)
//		{
//			totalStripe = ( ((i+1) < heightDivisor) ? vetStripe0 : verStripeL ) ;
//			
//			features[widthDivisor + i] = -1.0 + (features[widthDivisor + i] / totalStripe);
//		}
//		
//		final double halfTotalPixels = 0.5 * (double)totalPixels;
//		
//		for(int i = 0; i < 3 ; i ++)
//			features[ getColorIndexStart() + i ] = -1.0 + ( features[ getColorIndexStart() + i ] / halfTotalPixels );
//			
////		for(int i = 0; i < features.length; i++)
////			System.out.println("" + (i+1) + ": " + features[i]);
//		
//		
//		
////		System.out.println("Size[" + width + ", " + height + "]");
////		
////		System.out.println("Div[" + widthDivisor + ", " + widthDivisor + "]");
////		
////		System.out.println("Sub_w: " + subWidth + ", " + lastSubWidth);
////		System.out.println("Sub_h: " + subHeight + ", " + lastSubHeight);
////		
////		
////		System.out.println("HorS0: " + horStripe0 + ", " + horStripeL);
////		System.out.println("VerS0: " + vetStripe0 + ", " + verStripeL);
//
//		
//		
//
//		return features;
//	}
	
	
	private static double alphaMultiplier(double alpha, double color)
	{
		double minLevel = (double)0xFF * (((double)0xFF - alpha) / (double)0xFF );
//		System.out.println(minLevel);
		return (color <= minLevel) ? minLevel : color ;
	}
	
	private static void fixLayoutList(int[] layout, int left, int divisor)
	{
		int index = -1 + (divisor / 2);
		int signal = -1;
	
		for(int i = 0; i < left; i++)
		{
			index += i * signal ;
			
			if(index < 0 || index >= divisor )
				return;
			
			layout[ index ] += 1;
			
			signal *= -1;
		}
	}
	
	private static int getLayoutIndex(int[] layout, int pos)
	{
		int sum = 0;
		
		for(int i = 0; i < layout.length; i++)
		{
			sum += layout[i];
			
			if( pos <= sum )
				return i;
		}
		
		return 0;
	}
	public static long max = (long)Double.MAX_VALUE;;
	public double[] extract(int[] pix, int width, int height)
	{

		final int COLORS_MASK = 0x07 ;
		
		int totalPixels = width * height ;
		
		if( totalPixels != pix.length )
			throw new IllegalArgumentException("Dimensions don't match array lenght");
		
		
		double[] features = new double[getTotalFeatures()];
		Arrays.fill(features, 0.0);
		
		double red,green,blue,gray,alpha;
		
		
		
		int sub_width  = width  / widthDivisor,
		    sub_height = height / heightDivisor ;
		
		int[] w_dist_layout = new int[widthDivisor ],
			  h_dist_layout = new int[heightDivisor];
		
		Arrays.fill(w_dist_layout, sub_width);
		Arrays.fill(h_dist_layout, sub_height);
		
		int w_left = width -  (sub_width *widthDivisor ),
			h_left = height - (sub_height*heightDivisor);
		
		fixLayoutList( w_dist_layout, w_left, widthDivisor );
		fixLayoutList( h_dist_layout, h_left, heightDivisor);
		
		
		int i_width,i_height, w,h;

		
		for( int p = 0; p < totalPixels; p++ )
		{
		
			alpha = ( ( pix[ p ] >> 24 ) & 0xFF ) | COLORS_MASK ;
			
			red   = alphaMultiplier( alpha, ( ( pix[ p ] >> 16 ) & 0xFF ) | COLORS_MASK ) ;
			green = alphaMultiplier( alpha, ( ( pix[ p ] >> 8  ) & 0xFF ) | COLORS_MASK ) ;
			blue  = alphaMultiplier( alpha, ( ( pix[ p ]       ) & 0xFF ) | COLORS_MASK ) ;
			
			gray = getGrayScale(red, green, blue) / (double)0xFF;
			
			w = p % width;
			h = p / height;
			
			i_width  = 				  getLayoutIndex(w_dist_layout, w);
			i_height = widthDivisor + getLayoutIndex(h_dist_layout, h);
			
			features[ i_width  ] += gray;
			features[ i_height ] += gray;
			
			features[ getColorIndexStart()     ] += red   / (double)0xFF;
			features[ getColorIndexStart() + 1 ] += green / (double)0xFF;
			features[ getColorIndexStart() + 2 ] += blue  / (double)0xFF;
			
		}

		double totalStripe;
		for(int i = 0; i < widthDivisor; i++)
		{
			totalStripe = w_dist_layout[i] * height;
			
			features[i] = -1.0 + (features[i] / totalStripe) ;
		}
		
		for(int i = 0; i < heightDivisor; i++)
		{
			totalStripe = h_dist_layout[i] * width;
			
			features[widthDivisor + i] = -1.0 + (features[widthDivisor + i] / totalStripe);
		}
		
		final double halfTotalPixels = 0.5 * (double)totalPixels;
		
		for(int i = 0; i < 3 ; i ++)
			features[ getColorIndexStart() + i ] = -1.0 + ( features[ getColorIndexStart() + i ] / halfTotalPixels );
		

		return features;
	}

}
