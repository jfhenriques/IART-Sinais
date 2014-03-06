package com.nn.gui;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.nn.NNProject;
import com.nn.TrainingListenener;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class NetworkTrainingPanel extends JPanel implements TrainingListenener {

	
	private static final long serialVersionUID = -7772250773730937222L;
	private JTable table;
	
	private JButton btnSaveNetwork;
	private JButton btnTrain;
	
	
	private boolean isLocked = false;
	private JLabel lblNewLabel;
	
	final private JSpinner spinnerEpoch;
	final private JSpinner spinnerRate;
	
	final private JFileChooser fSaver;
	final private JFileChooser nnfSaver;
	
	private JProgressBar progressBar;

	
	private TrainingTableModel tModel;
	
	private JButton btnSaveReport;
	private JPanel panel_1;
	private JButton btnStopTraining;
	private JButton btnSmartTrain;

	/**
	 * Create the panel.
	 */
	public NetworkTrainingPanel()
	{
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("50dlu:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(56dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		
		fSaver = Utils.getTXTFileChooser();
		nnfSaver = Utils.getNNFileChooser();
		
		JLabel lblEpochs = new JLabel("Epochs");
		add(lblEpochs, "2, 2, left, default");
		
		spinnerEpoch = new JSpinner();
		spinnerEpoch.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		add(spinnerEpoch, "4, 2, 3, 1");
		
		lblNewLabel = new JLabel("Training Rate");
		add(lblNewLabel, "2, 4");
		
		spinnerRate = new JSpinner();
		spinnerRate.setModel(new SpinnerNumberModel(0.30, 0.05, 1.00, 0.05));
		add(spinnerRate, "4, 4, 3, 1");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "2, 6, 5, 1, fill, fill");
		
		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		scrollPane.setViewportView(table);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		add(progressBar, "2, 8, 5, 1");
		
		panel_1 = new JPanel();
		add(panel_1, "2, 10, 5, 1, fill, fill");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				RowSpec.decode("default:grow"),}));
		
		btnStopTraining = new JButton("S");
		btnStopTraining.setEnabled(false);
		panel_1.add(btnStopTraining, "1, 1");
		
		btnSmartTrain = new JButton("Smart Train");
		panel_1.add(btnSmartTrain, "3, 1");
		
		btnTrain = new JButton("Train");
		panel_1.add(btnTrain, "5, 1");
		
		btnSaveNetwork = new JButton("Save Network");
		panel_1.add(btnSaveNetwork, "7, 1");
		
		btnSaveReport = new JButton("Save Report");
		panel_1.add(btnSaveReport, "9, 1");
		btnSaveReport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(isLocked) return;
				
				if( fSaver.showSaveDialog(NetworkTrainingPanel.this) == JFileChooser.APPROVE_OPTION )
				{
					File f = Utils.getFIleNameWithExtension(fSaver.getSelectedFile(), Utils.TXTFileFilter.TXT_EXTENSION );

					if( f.exists() )
					{
						int n = Utils.showYesNo(NetworkTrainingPanel.this, null, "The file already exstis. Allow it to be overwriten?");
						if( n != JOptionPane.OK_OPTION ) return;
					}
					NNSetCreatorPanel.getInstance().writeReport(f);
				}
			}
		});
		btnSaveNetwork.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(isLocked) return;
				
				if( nnfSaver.showSaveDialog(NetworkTrainingPanel.this) == JFileChooser.APPROVE_OPTION )
				{
					File f = Utils.getFIleNameWithExtension(nnfSaver.getSelectedFile(), Utils.NNFileFilter.NN_EXTENSION );

					if( f.exists() )
					{
						int n = Utils.showYesNo(NetworkTrainingPanel.this, null, "The file already exstis. Allow it to be overwriten?");
						if( n != JOptionPane.OK_OPTION ) return;
					}
					NNSetCreatorPanel.getInstance().writeToFile(f);
				}
			}
		});
		btnSmartTrain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(isLocked) return;
				
				setIsTraning(true);
					
				final NetworkTrainingPanel panel = NetworkTrainingPanel.this;
				
				
				(new Thread() {
					@Override
					public void run()
					{
						NNProject proj = NNSetCreatorPanel.getInstance().getNNProject();

						proj.dataSet.setSingleListener(panel, 1000);
						

						proj.network.setLearnFactor((double)spinnerRate.getValue());
						//proj.network.train(proj.dataSet, (int)spinnerEpoch.getValue(), proj.imageList.outputBind);
						proj.network.smartTrain(proj.dataSet, proj.imageList.outputBind);
				
						setIsTraning(false);
					}
				}).start();

			}
		});
		
		btnTrain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(isLocked) return;
				
				setIsTraning(true);
					
				final NetworkTrainingPanel panel = NetworkTrainingPanel.this;
				
				
				(new Thread() {
					@Override
					public void run()
					{
						NNProject proj = NNSetCreatorPanel.getInstance().getNNProject();

						proj.dataSet.setSingleListener(panel, 1000);
						

						proj.network.setLearnFactor((double)spinnerRate.getValue());
						proj.network.train(proj.dataSet, (int)spinnerEpoch.getValue(), proj.imageList.outputBind);
				
						setIsTraning(false);
					}
				}).start();

			}
		});
		
		btnStopTraining.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				
				NNSetCreatorPanel.getInstance().getNNProject().network.interruptTraining();
				setIsTraning(false);
			}
		});
	}

	
	public boolean isLocked()
	{
		return isLocked;
	}

	public void setLocked(boolean isLocked)
	{
		this.isLocked = isLocked;
		
		btnSaveNetwork.setEnabled( !isLocked );
		btnTrain.setEnabled( !isLocked );
		spinnerEpoch.setEnabled( !isLocked );
		spinnerRate.setEnabled( !isLocked );
		btnSaveReport.setEnabled( !isLocked );
		btnSmartTrain.setEnabled( !isLocked );
		btnStopTraining.setEnabled( !isLocked );
	}

	public void setTableDataModel(TrainingTableModel tModel)
	{
		this.tModel = tModel;
		table.setModel(tModel);
	}
	
	public void insertTableRow(double[] row)
	{
		tModel.addRow(row);
	}


	@Override
	public void informOutputedData(final int trainNumber, final int totalTrains, final double[] output)
	{
		if(tModel != null)
		{			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					tModel.addRow(output);
					
					progressBar.setMaximum(totalTrains);
					progressBar.setValue(trainNumber);
					
					table.scrollRectToVisible(table.getCellRect(tModel.getRowCount()-1, 0, true));
				}
			});
		}
	}


	private void setIsTraning(boolean training)
	{
		setLocked( training );
		btnStopTraining.setEnabled( training );
		
	}
}
