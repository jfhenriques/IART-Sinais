package com.nn.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.nn.gui.error.ExceptionDialog;

public abstract class FrameLauncher {
	
	private final Dimension dim;
	private final String title;
	private final JFrame caller;
	private final Point location;
	
	public FrameLauncher(JFrame caller, Dimension dim, String title, Point location)
	{
		this.caller = caller;
		this.dim = dim;
		this.title = title;
		
		this.location = (location == null) ? new Point(100,100) : location ;

	}
	public FrameLauncher(JFrame caller, Dimension dim, String title)
	{
		this(caller,dim,title,null);
	}
	
	
	public abstract JPanel getContentPane();
	public void doSomethingSuccess(){};
	
	
	public JFrame createFrame()
	{
		JFrame frame = new JFrame();
		
		frame.setTitle(title);
		
		frame.setLocation(location);
		frame.setPreferredSize(dim);
		frame.setMinimumSize(dim);
		
		frame.getContentPane().add(getContentPane(), BorderLayout.CENTER);
		
		return frame;
	}
	
	public void launchFrame()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					JFrame frame = createFrame();
				
					if( caller != null )
					{
						frame.addWindowListener(new WindowAdapter() {
				            public void windowClosing(WindowEvent evt)
				            {
				            	EventQueue.invokeLater(new Runnable() {
									public void run() {
						                caller.setVisible(true);
									}
								});
				            }
				        });
						
						caller.setVisible(false);
					}
					
					frame.setVisible(true);
					
					doSomethingSuccess();
					
				} catch (Exception e) {
					ExceptionDialog.createDialog(caller, e);
					e.printStackTrace();
				}
			}
		});
	}

}
