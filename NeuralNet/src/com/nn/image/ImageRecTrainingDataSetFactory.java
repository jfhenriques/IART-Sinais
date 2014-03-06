package com.nn.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


import com.nn.NeuralNetwork.TrainingDataSet;
import com.nn.gui.Utils;



public final class ImageRecTrainingDataSetFactory {
	
	

	

	public static class ImageTrainingBound implements java.io.Serializable {
		
		private static final long serialVersionUID = -1246682997007363489L;
		
		public ArrayList<ImageStream> list;
		public int[] outputBind;
		public int lastCategority;
		
		public ImageTrainingBound(int totalImages, int lastCategority)
		{
			this.list = new ArrayList<ImageStream>(totalImages);
			this.outputBind = new int[totalImages];
			this.lastCategority = lastCategority;
			
			Arrays.fill(this.outputBind, 0);
		}
	}
	
	
	private ImageRecTrainingDataSetFactory() {}
	

	
	
	
	public static ImageTrainingBound createOrderedTrainingList(Map<String, BufferedImage> inList)
	{
		HashMap<Integer, LinkedList<ImageStream>> categorities = new HashMap<Integer, LinkedList<ImageStream>>();
		
		LinkedList<ImageStream> catList;
		
		ImageTrainingBound output;

		String[] spl;
		String fileName,nameOnly;
		int catNr,maxCat = 0;
		ImageStream imgSteam;
		
		// coloca cada imagem na queue list com o indice da sua categoria
		for(String name : inList.keySet())
		{
			fileName = (new File(name)).getName();
			nameOnly = Utils.extractNameOnly(fileName);
			
			if( nameOnly == null )
				continue;
			
			spl = nameOnly.split("\\_");
			
			try {
				
				if( spl.length > 0 )
				{
					catNr = Integer.parseInt( spl[0] ) -1;
					
					if( catNr < 0 )
						continue;
					
					if( catNr > maxCat )
						maxCat = catNr ;
					
					if( !categorities.containsKey(catNr) || (catList = categorities.get(catNr)) == null )
					{
						catList = new LinkedList<ImageStream>();
						categorities.put(catNr, catList);
					}
					
					imgSteam = new ImageStream(inList.get(name), fileName);
					
					if( spl[spl.length-1].toLowerCase().equals("default") )
						catList.addFirst(imgSteam);
					else
						catList.add(imgSteam);
					
				}
			} catch(Exception e) { }
		}
		
		
		// rearranja os indices das categorias
		int totalSkip = 0;
		for(int i = 0; i <= maxCat; i++)
		{
			if( categorities.containsKey(i) && (catList = categorities.get(i)) != null )
			{
				if(totalSkip > 0)
				{
					categorities.remove(i);
					categorities.put(i-totalSkip, catList);
				}
			}
			else
				totalSkip++;
			
		}
		
		maxCat -= totalSkip;
		output = new ImageTrainingBound( inList.size(), maxCat );
		
		
		// Cria a última lista ordenada
		boolean entriesLeft = true;
		while( entriesLeft )
		{
			entriesLeft = false;
			
			for(int catIndex = 0; catIndex <= maxCat; catIndex++)
			{
				if( categorities.containsKey(catIndex) && (catList = categorities.get(catIndex)) != null )
				{
					
					try {
						
						imgSteam = catList.removeFirst();
						
						if( imgSteam != null )
						{
							output.list.add(imgSteam);
							int index = output.list.size()-1;
							
							output.outputBind[ index ] = catIndex;
						}
						
					} catch(Exception e) { }
					
					if(catList.size() > 0)
						entriesLeft = true;
					else
						categorities.remove( catIndex );
				}
			}
		}
		
		return output;
		
	}
	
	public static TrainingDataSet build(ImageTrainingBound trainBound, ImageFeatures extractor) throws IOException
	{
		
		double[] output,input;
		
		if( trainBound == null ) return null;
		
		int totalOutput = trainBound.lastCategority +1;
		
		TrainingDataSet dataSet = new TrainingDataSet(extractor.getTotalFeatures(), totalOutput);
		
		for( int i = 0; i < trainBound.list.size(); i++ )
		{
		
			input = extractor.extract(trainBound.list.get(i).getImg());
		
			output = new double[totalOutput];
			
			for (int j = 0; j < totalOutput; j++)
				output[j] = (trainBound.outputBind[i] == j) ? 1.0 : 0.0;
		
			
			dataSet.registerTest(input, output);
		}
		
		return dataSet;
	}
	
	
//	public static TrainingDataSet build(Collection<BufferedImage> imageList) throws IOException
//	{
//		
//		double[] output,input;
//		
//		TrainingDataSet dataSet = new TrainingDataSet(ImageFeatures.TOTAL_FEATURES, imageList.size());
//		
//		int index = 0;
//		for( BufferedImage img : imageList )
//		{
//			output = new double[imageList.size()];
//		
//			//input = image2Double(img, entryNeurons);
//			input = ImageFeatures.extract(img);
//		
//			for (int j = 0; j < imageList.size(); j++)
//				output[j] = (index == j) ? 1.0 : 0.0;
//		
//			
//			dataSet.registerTest(input, output);
//			index++;
//		}
//		
//		return dataSet;
//	}
	
//	public static TrainingDataSet buildStringList(List<String> imageList) throws IOException
//	{
//		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
//		for(String file: imageList)
//			list.add(ImageIO.read(new File(file)));
//
//		return build(list);
//	}

	
	
	
//	public static double[] image2Double(BufferedImage img, int size)
//	{
//		double[] pixels = new double[size];
//		int arrSize = img.getWidth()*img.getHeight();
//		
//		final double divisor = ((double)0xFF)/2.0 ;
//		final double powConst = 1.0/2.2;
//		double red,green,blue, gray;
//		
//		int[] pixelsList = img.getRGB(0, 0, img.getWidth(), img.getHeight(), new int[arrSize], 0, img.getWidth());
//		
//		for (int j = 0; j < arrSize; j++)
//		{
//			red   = (double)(( pixelsList[j] >> 16 ) & 0xFF);
//			green = (double)(( pixelsList[j] >> 8  ) & 0xFF);
//			blue  = (double)(( pixelsList[j]       ) & 0xFF);
//		
//			//gray = 0.299*red + 0.587*green + 0.114*blue;
//			//gray = 0.2126*red + 0.7152*green + 0.0722*blue;
//			
//			gray = Math.pow(0.2126*Math.pow(red, 2.2) + 0.7152*Math.pow(green, 2.2) + 0.0722*Math.pow(blue, 2.2), powConst);
//			
//			pixels[j] = (gray / divisor) - 1.0;
//			
//			
//			
////			for(int i = 0,shift=16; i < 3; i++,shift-=8)
////			{
////				pixels[pos + i] = ( pixelsList[j] >> shift ) & 0xFF ;
//////				pixels[pos     ] = ( pixelsList[j] >> 16 ) & 0xFF ;
//////				pixels[pos + 1 ] = ( pixelsList[j] >>  8 ) & 0xFF ;
//////				pixels[pos + 2 ] = ( pixelsList[j]       ) & 0xFF ;
////			}
//			
//			
//		}
//		
//		return pixels;
//	}
	
}