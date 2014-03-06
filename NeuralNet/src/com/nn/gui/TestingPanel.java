package com.nn.gui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.nn.NNProject;
import com.nn.gui.ImageSelectionPanel.PreviewPanel;
import com.nn.gui.error.ExceptionDialog;

import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;

import javax.swing.JLabel;
import java.awt.Font;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class TestingPanel extends JPanel {

	
	private class ListTransferHandler extends TransferHandler {


		private static final long serialVersionUID = -6688849777071466696L;
		private final DataFlavor flavour = DataFlavor.javaFileListFlavor;

		//public ListTransferHandler() { }

	    @Override
	    public boolean canImport(TransferSupport info) {
	        // we only import FileList
	        if (!info.isDataFlavorSupported(flavour)) {
	            return false;
	        }
	        return true;
	    }


		@SuppressWarnings("unchecked")
		@Override
	    public boolean importData(TransferSupport info)
	    {
	        if (!info.isDrop())
	            return false;

	        if (!info.isDataFlavorSupported(flavour))
	            return false;

	        Transferable t = info.getTransferable();
	        List<File> fileList = null;
	        try {
	        	fileList = (List<File>)t.getTransferData(flavour);
	        } catch (Exception e) {
	        	return false;
	        }
	        
	        if(fileList != null && fileList.size() > 0)
	        	
	        TestingPanel.this.testNetImage(fileList.get(0));
	        

	        return true;
	    }

//	    private void displayDropLocation(String string) {
//	        System.out.println(string);
//	    }
	}

	private static final long serialVersionUID = 237049772775909831L;
	
	private final NNProject project;
	private final JFileChooser fChooser;
	private JTable table;
	
	private final PreviewPanel prob1;
	private final PreviewPanel prob2;
	private final PreviewPanel subm;
	
	private final JLabel lblSecondProb;
	private final JLabel lblFirstProb;
	private final DefaultTableModel model ;
	

	/**
	 * Create the panel.
	 */
	public TestingPanel(NNProject projectIn) {
		
		this.project = projectIn;
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("16dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("120dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		
		fChooser = Utils.getIMAGEFileChooser();
		
		JLabel lblSubmit = new JLabel("Submitted Image");
		lblSubmit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(lblSubmit, "2, 2");
		
		JLabel lblMostProbableImage = new JLabel("Most Probable Image");
		lblMostProbableImage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(lblMostProbableImage, "4, 2");
		
		JLabel lblSecondMostProbable = new JLabel("Second Most Probable Image");
		lblSecondMostProbable.setFont(new Font("Tahoma", Font.PLAIN, 12));
		add(lblSecondMostProbable, "6, 2");
		
		subm = new PreviewPanel();
		subm.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		add(subm, "2, 4, fill, fill");
		
		subm.setTransferHandler(new ListTransferHandler());
		
		prob1 = new PreviewPanel();
		prob1.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		add(prob1, "4, 4, fill, fill");
		
		
		prob2 = new PreviewPanel();
		prob2.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		add(prob2, "6, 4, fill, fill");
		
		lblFirstProb = new JLabel("");
		add(lblFirstProb, "4, 6");
		
		lblSecondProb = new JLabel("");
		add(lblSecondProb, "6, 6");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "2, 8, 5, 1, fill, fill");
		
		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		model = new DefaultTableModel(new String[]{"Index", "Output"}, 0);
		table.setModel(model);
		
		scrollPane.setViewportView(table);
		
		JButton btnLoadImage = new JButton("Load Image");
		btnLoadImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				
				fChooser.setMultiSelectionEnabled(false);
				if( fChooser.showOpenDialog(TestingPanel.this) == JFileChooser.APPROVE_OPTION )
					testNetImage( fChooser.getSelectedFile() );	
			}
		});
		btnLoadImage.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(btnLoadImage, "2, 10, 5, 1");

	}
	
	private void testNetImage(File f)
	{
		try {
			
			if( model.getRowCount() > 0 )
				for(int i = (model.getRowCount()-1); i >= 0; i--)
					model.removeRow(i);

			BufferedImage img = ImageIO.read(f);
			
			double[] in = project.extractor.extract(img);
			project.network.propagate(in);
			
			double[] out = project.network.getPropagationOutput();
			
			int i1 = -1,i2 = -1;
			double val1 = -1.0, val2 = -1.0;
			
			for(int i = 0; i < out.length; i++)
			{
				if(out[i] > val1)
				{
					val2 = val1;
					i2 = i1;
					
					val1 = out[i];
					i1 = i;
				}
				
				model.addRow(new Object[]{(i+1), out[i]});
			}
			
			subm.setImage(img);
			
			if( i1 >= 0)
			{
				prob1.setImage(project.imageList.list.get(i1).getImg());
				lblFirstProb.setText("P: " + NNSetCreatorPanel.percentFormat.format(100.0 * val1) + " %");
			}
			else
			{
				prob1.setImage(null);
				lblFirstProb.setText("Not found");
			}
			
			if( i2 >= 0)
			{
				prob2.setImage(project.imageList.list.get(i2).getImg());
				lblSecondProb.setText("P: " + NNSetCreatorPanel.percentFormat.format(100.0 * val2) + " %");
			}
			else
			{
				prob2.setImage(null);
				lblSecondProb.setText("Not found");
			}
			
			
		} catch( Exception e1) {
			ExceptionDialog.createDialog(TestingPanel.this, e1);
		}
	}

}
