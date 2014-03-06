package com.nn.gui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.ScrollPaneConstants;

import com.nn.gui.error.ExceptionDialog;
import com.nn.image.ImageStream;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;


public class ImageSelectionPanel extends JPanel {
	
	
	private static class StringDataModel extends AbstractListModel<String> {

		private static final long serialVersionUID = -5582625602892406822L;
		private ArrayList<String> list = new ArrayList<String>();
		
		@Override
		public String getElementAt(int index) {
			return list.get(index);
		}

		@Override
		public int getSize() {
			return list.size();
		}
		
		public void removeIndex(int index)
		{
			list.remove(index);
			fireIntervalRemoved(this, list.size(), list.size()+1);
		}
		
		public void addEntry(String name)
		{
			list.add(name);
			fireIntervalAdded(this, list.size()-1, list.size());
		}
		
	}
	
	public static class PreviewPanel extends JPanel {

		private static final long serialVersionUID = -7206717080511269399L;
		private Image img = null;
		
		private int start_x = 0,
				    start_y = 0,
				    end_x   = 0,
				    end_y   = 0;
		
		public PreviewPanel()
		{
			this.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent arg0) {
					doMath();
					repaint();
				}
			});
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			if( img != null )
				g.drawImage(img,   start_x,start_y,end_x,end_y,    0,0,img.getWidth(null),img.getHeight(null),   null);
			
		}
		
		public void setImage(Image img)
		{
			this.img = img;
			doMath();
			repaint();
		}
		
		private void doMath()
		{
			if( img != null )
			{

				int avail_x = this.getWidth() - 20,
					avail_y = this.getHeight() - 25,
					img_x = img.getWidth(null),
					img_y = img.getHeight(null);
		 
				if( img_x > avail_x || img_y > avail_y )
				{
					double imgRatio = (double)img_y/(double)img_x;
					
					if( img_x > avail_x )
					{
						img_x = avail_x;
						img_y = (int) (imgRatio * img_x);
					}
					
					if( img_y > avail_y )
					{
						img_y = avail_y;
						img_x = (int) (img_y/imgRatio);
					}
					
				}

				start_x = 10 + ((avail_x - img_x) / 2);
				start_y = 15 + ((avail_y - img_y) / 2);
				
				end_x = start_x + img_x;
				end_y = start_y + img_y;
			}
		}
	}
	

	private static class TotalImagesLabel extends JLabel {

		private static final long serialVersionUID = -6509604612103615064L;
		
		private int total;
		
		public TotalImagesLabel()
		{
			this(0);
		}
		public TotalImagesLabel(int start)
		{
			super();
			
			this.total = start;
			this.updateText();
		}
		
		public void setTotalImages(int q)
		{
			this.total = q;
			
			this.updateText();
		}
		
		public void addQuantity(int q)
		{
			this.total += q;
			
			this.updateText();
		}
		
		@SuppressWarnings("unused")
		public int getTotal()
		{
			return this.total;
		}
		
		private void updateText()
		{
			if(this.total < 0) this.total = 0;
			
			this.setText("Total images: " + this.total);
		}
		
	}
	  


	
	
	private static final long serialVersionUID = -6882708746446937085L;
	
	
	private boolean isLocked = false;
	
	private PreviewPanel imagePreview = null;
	private JFileChooser fChooser = null;
	
	
	private TotalImagesLabel totalImagesLabel = null;
	private JButton btRemoveImage = null;
	private JButton btAddImage = null;
	
	private JList<String> imageJList = null;
	private Map<String, BufferedImage> imageList = null;
	private StringDataModel listDataModel = null;
	
	private Image noPreview = null;
	
	public Map<String, BufferedImage> getImageList()
	{
		return imageList;
	}
	
	
	/**
	 * Create the panel.
	 */
	public ImageSelectionPanel()
	{
	
		this.setBorder(new EmptyBorder(-10, -10, -10, -10));
		
		try {
			this.noPreview = (new ImageIcon(getClass().getResource("/resources/nopreview.png"))).getImage();
		} catch(Exception e) {
			this.noPreview = null;
			e.printStackTrace();
		}

		
		imageList = new LinkedHashMap<String, BufferedImage>();
		listDataModel = new StringDataModel();
		
		fChooser = Utils.getIMAGEFileChooser();		
		
		btAddImage = new JButton("Add Image");
		btAddImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg) 
			{
				if(isLocked()) return;
				
				try {
					
					fChooser.setMultiSelectionEnabled(true);
					
					if( fChooser.showOpenDialog(ImageSelectionPanel.this) == JFileChooser.APPROVE_OPTION )
					{
						
						for(File f : fChooser.getSelectedFiles())
						{
							String fname = f.getAbsolutePath() ;
							if( !imageList.containsKey(fname) )
							{
								BufferedImage img = ImageIO.read(f);
								
								imageList.put(fname, img);
								listDataModel.addEntry(fname);
								
								totalImagesLabel.addQuantity(1);
							}
						}
						
						imageJList.setSelectedIndex(listDataModel.getSize()-1);
						fireSelectionPreview();
						
					}
					
				} catch (Exception e) {
					ExceptionDialog.createDialog(ImageSelectionPanel.this, e);
				}
			}
		});

		btRemoveImage = new JButton("Remove Selected");
		btRemoveImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg) 
			{
				if(isLocked()) return;
				
				int[] sel = imageJList.getSelectedIndices();
				String value;
				
				int sel_index = -1;
				
				for(int i = (sel.length-1); i >= 0 ; i--)
				{
					sel_index = sel[i];
					value = listDataModel.getElementAt(sel_index);
					listDataModel.removeIndex(sel_index);
				
					imageList.remove(value);
				}
				
				totalImagesLabel.setTotalImages(listDataModel.getSize());
				
				if( sel_index >= 0 && listDataModel.getSize() > 0 )
				{
					imageJList.setSelectedIndex( (sel_index<=(listDataModel.getSize()-1))?sel_index:(sel_index-1) );
					fireSelectionPreview();
				}
				else
					clearSelection();
			}
		});
		btRemoveImage.setEnabled(false);
		
		
		imagePreview = new PreviewPanel();
		imagePreview.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		imageJList = new JList<String>();
		imageJList.setModel(listDataModel);
		imageJList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				fireSelectionPreview();
			}
		});
		
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(imageJList);
		
		totalImagesLabel = new TotalImagesLabel();
		
		
		
		
		GroupLayout groupLayout = new GroupLayout(this);
		
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
						.addComponent(imagePreview, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btAddImage, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btRemoveImage, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
						.addComponent(totalImagesLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(imagePreview, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(totalImagesLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btAddImage)
						.addComponent(btRemoveImage))
					.addContainerGap())
		);
		
		setLayout(groupLayout);

	}
	
	
	public void clearSelection()
	{
		if(imageJList != null)
			imageJList.clearSelection();
		
		if(btRemoveImage != null)
			btRemoveImage.setEnabled(false);
		
		if( imagePreview != null )
		{
			imagePreview.setImage(this.noPreview);
			imagePreview.repaint();
		}
	}
	
	private void fireSelectionPreview()
	{
		String value = imageJList.getSelectedValue();
		if( value == null )
			clearSelection();
		
		else
		{
			if( !isLocked() )
				btRemoveImage.setEnabled(true);
			
			imagePreview.setImage(imageList.get(value));
			imagePreview.repaint();
		}
	}


	public boolean isLocked()
	{
		return isLocked;
	}
	public void setLocked(boolean isLocked)
	{
		this.isLocked = isLocked;
		
		if( isLocked )
			btRemoveImage.setEnabled( false );
		
		btAddImage.setEnabled( !isLocked );
	}
	
	public void injectImageList(List<ImageStream> list)
	{
		for(ImageStream entry : list)
		{
			listDataModel.addEntry(entry.name);
			imageList.put(entry.name, entry.getImg());
			
			totalImagesLabel.setTotalImages(list.size());
		}
	}
}
