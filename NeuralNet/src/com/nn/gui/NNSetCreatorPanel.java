package com.nn.gui;


import java.awt.Dimension;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.nn.NNProject;
import com.nn.NeuralNetwork;
import com.nn.NeuralNetwork.TrainingDataSet;
import com.nn.gui.error.ExceptionDialog;
import com.nn.image.ImageFeatures;
import com.nn.image.ImageRecTrainingDataSetFactory;
import com.nn.image.ImageRecTrainingDataSetFactory.ImageTrainingBound;


public class NNSetCreatorPanel extends JPanel {

	
	public static String NEW_LINE = System.getProperty("line.separator");
	
	private static final long serialVersionUID = -4420622354181613037L;
	public static final String title = "NN Creator: Image Recognition";
	public static final Dimension preferedDimension = new Dimension(800, 600);
	
	private ImageSelectionPanel imageSelectionPanel;
	private ProjectConfigPanel projectConfigPanel;
	private NetworkTrainingPanel networkTrainingPanel;
	
	private NNProject project = null;

	public static final NumberFormat percentFormat  = new DecimalFormat("00.00000000");
	
	private static NNSetCreatorPanel instance = null;
	
	public static NNSetCreatorPanel getInstance()
	{
		return instance;
	}
	
	public NNProject getNNProject()
	{
		return this.project;
	}
	
	public NNProject createNetwork()
	{
		NeuralNetwork network = null;
		Map<String, BufferedImage> imageList = null;
		TrainingDataSet dataSet = null;
		ImageTrainingBound trainBind = null;
		File saveFile = null;
		ImageFeatures featureExtractor = null;
		
		try {
			
			imageList = imageSelectionPanel.getImageList();
			if(imageList.size() == 0)
			{
				JOptionPane.showMessageDialog(this.getRootPane(), "Image list cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			
			int[] middle = projectConfigPanel.getMiddleLayers();
			if(middle == null)
			{
				JOptionPane.showMessageDialog(this.getRootPane(), "Check if middle layer has only numbers separated by commas.", "Error", JOptionPane.ERROR_MESSAGE);
				return null;				
			}
			
			saveFile = projectConfigPanel.getSaveFile();
			if(saveFile == null)
			{
				JOptionPane.showMessageDialog(this.getRootPane(), "Please choose a file location to save the network.", "Error", JOptionPane.ERROR_MESSAGE);
				return null;				
			}			
			
			
			featureExtractor = new ImageFeatures();
			
			int[] finalLayout = new int[2 + middle.length];
			
			trainBind = ImageRecTrainingDataSetFactory.createOrderedTrainingList(imageList);

			finalLayout[0                   ] = featureExtractor.getTotalFeatures();
			finalLayout[finalLayout.length-1] = 1+trainBind.lastCategority;
			
			if(middle.length > 0)
				System.arraycopy(middle, 0, finalLayout, 1, middle.length);
			
			network = new NeuralNetwork(finalLayout);
			
			
			
			dataSet = ImageRecTrainingDataSetFactory.build(trainBind, featureExtractor);
			
		} catch (Exception e) {
			ExceptionDialog.createDialog(this, e);
			
			return null;
		}
		
		
		NNProject project = new NNProject(network, trainBind, dataSet, featureExtractor);
		NNProject.saveState(saveFile, project);
		

		
		imageSelectionPanel.setLocked(true);
		projectConfigPanel.setLocked(true);
		networkTrainingPanel.setLocked(false);
		
		networkTrainingPanel.setTableDataModel(new TrainingTableModel(imageList.size()));
		
		this.project = project;
		
		return project;
	}
	
	public void writeReport(File f)
	{
		writeReport(project, f);
	}
	public static void writeReport(NNProject project, File f)
	{
		if(project == null) return;
		
		NeuralNetwork net = project.network;
		TrainingDataSet train = project.dataSet;
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			
			bw.write("Network [" + project.getSaveFile().getAbsolutePath() + "]" + NEW_LINE + NEW_LINE);
			
			String numberF = "";
			int nr = train.input.size();
			while(nr > 0)
			{
				numberF += "0";
				nr /= 10;
			}
			
			NumberFormat nfE  = new DecimalFormat("0.00000000"),
						 nfn = new DecimalFormat(numberF);
			
			
			for(int i = 0; i < train.input.size(); i++)
			{
				net.propagate(train.input.get(i));
				
				double[] output = net.getPropagationOutput(),
						 expectedOutput = train.output.get(i);
				
				bw.write(NEW_LINE + "Image [" + project.imageList.list.get(i).name + "]" + NEW_LINE);
				
				int maxIndex = 0;
				double  maxVal = 0;
				
				for(int j = 0; j < output.length; j++)
				{
					if(output[j] > maxVal)
					{
						maxVal = output[j];
						maxIndex = j;
					}
				}
				
				for(int j = 0; j < output.length; j++)
				{
					double error = expectedOutput[j]-output[j];
					String errorString = (error >= 0.0 ? "+":"") + nfE.format(error);
					bw.write("[" + nfn.format(j+1) + "] " + (j == maxIndex ? "* " : "  ") +
							percentFormat.format( output[j]*100.0 ) + " % (" + 
							errorString + ")" + NEW_LINE);
				}
			}
			
		} catch (Exception e) {
			ExceptionDialog.createDialog(getInstance(), e);

			return;
		} finally {
			try {
				if( bw != null )
					bw.close();
			} catch (IOException e) { }
			try {
				if( fw != null )
					fw.close();
			} catch (IOException e) { }
		}

	}
	
	
	private void initialize()
	{
		NNSetCreatorPanel.instance = this;
		
		imageSelectionPanel = new ImageSelectionPanel();

		projectConfigPanel = new ProjectConfigPanel();
		projectConfigPanel.setBorder(new TitledBorder(null, "Network Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		
		networkTrainingPanel = new NetworkTrainingPanel();
		networkTrainingPanel.setBorder(new TitledBorder(null, "Network Training", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(imageSelectionPanel, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(networkTrainingPanel, GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
						.addComponent(projectConfigPanel, GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addComponent(projectConfigPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(networkTrainingPanel, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
						.addComponent(imageSelectionPanel, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))
					.addContainerGap())
		);
		setLayout(groupLayout);
		
	
		this.addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
			    if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == HierarchyEvent.SHOWING_CHANGED){
                    SwingUtilities.getWindowAncestor(NNSetCreatorPanel.this).addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                        	if(NNSetCreatorPanel.this != null && NNSetCreatorPanel.this.project != null )
                        		NNSetCreatorPanel.this.project.network.interruptTraining();
                        }
                    });
                }
			}
		});
	}
	
	
	public boolean writeToFile(File f)
	{
		if(project == null)
			return false;
		
		File save = (f == null) ? projectConfigPanel.getSaveFile() : f;
		if( save == null )
			return false;
		
		NNProject.saveState(save, project);
		return true;
	}
	
	
	/**
	 * Create the panel.
	 */
	public NNSetCreatorPanel()
	{

		
		initialize();
		networkTrainingPanel.setLocked(true);
	}
	
	public NNSetCreatorPanel(NNProject nnPoject, File f)
	{
		this.project = nnPoject;
		

		
		initialize();
		
		projectConfigPanel.setSaveFile( f );
		
		StringBuilder sb = new StringBuilder("");
		
		int[] layout = nnPoject.network.getNetworkLayout();
		int totalMiddleLayers = layout.length-2;
		for(int i = 1; i <= totalMiddleLayers ; i++)
		{
			sb.append(layout[i]);
			
			if(i < totalMiddleLayers)
				sb.append(", ");
		}		
		
		projectConfigPanel.setHiddenLayers(sb.toString());
		
		imageSelectionPanel.injectImageList(nnPoject.imageList.list);
		
		imageSelectionPanel.setLocked(true);
		projectConfigPanel.setLocked(true);
		
		networkTrainingPanel.setTableDataModel(new TrainingTableModel(nnPoject.imageList.list.size()));
		
		
		
		
		double[] res = new double[nnPoject.dataSet.input.size()];
		
		for(int i = 0; i < nnPoject.dataSet.input.size(); i++)
		{
			nnPoject.network.propagate(nnPoject.dataSet.input.get(i));
			
			double[] out = nnPoject.network.getPropagationOutput();
			
			res[i] = out[nnPoject.imageList.outputBind[i]];
		}
		
		networkTrainingPanel.insertTableRow(res);
		
		
		
		
		
	}
	
	public void clearImagePreviewSelection()
	{
		imageSelectionPanel.clearSelection();
	}
	
}
