package com.nn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.nn.NeuralNetwork.TrainingDataSet;
import com.nn.image.ImageFeatures;
import com.nn.image.ImageRecTrainingDataSetFactory.ImageTrainingBound;

public class NNProject implements java.io.Serializable {
	

	private static final long serialVersionUID = 950487846732161625L;
	
	public final NeuralNetwork network;

	public final ImageTrainingBound imageList;
	public final TrainingDataSet dataSet;
	public final ImageFeatures extractor;
	
	transient private File saveFile = null;
	
	public NNProject(NeuralNetwork network, ImageTrainingBound imageList, TrainingDataSet dataSet, ImageFeatures extractor)
	{
		this.saveFile = null;
		
		this.network = network;
		this.dataSet = dataSet;
		this.extractor = extractor;
		
//		ArrayList<ImageStream> list = new  ArrayList<ImageStream>(inImageList.size());
//		for(String str : inImageList.keySet())
//			list.add(new ImageStream(inImageList.get(str), (new File(str)).getName()));
		
		this.imageList = imageList;
	}
	
	public File getSaveFile()
	{
		return this.saveFile;
	}
	
	
	public static void saveState(String filename, NNProject project)
	{
		saveState(new File(filename), project);
	}
	public static void saveState(File file, NNProject project)
	{
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		
		try
		{
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(project);
			project.saveFile = file;
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}

	public static NNProject loadState(String filename)
	{
		return loadState(new File(filename));
	}
	public static NNProject loadState(File file)
	{
		NNProject project = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		
		try
		{
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			project = (NNProject)in.readObject();
			project.saveFile = file;
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		
		return project;
	}

}
