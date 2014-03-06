package com.nn.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	

	private static final long serialVersionUID = 854752271449787690L;
	private Image img = null;
	
	public ImagePanel(URL location)
	{
		this.setImage(location);
	}
	
	public void setImage(URL location)
	{
		img = (new ImageIcon(location)).getImage();		
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		if(img != null)
			g.drawImage(img, 0,0, this);
	}

}
 