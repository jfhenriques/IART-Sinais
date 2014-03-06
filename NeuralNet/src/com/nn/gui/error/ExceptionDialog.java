package com.nn.gui.error;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8984700117770124409L;


	/**
	 * Create the dialog.
	 */
	public ExceptionDialog(JFrame frame, Throwable t)
	{
		super(frame, true);
		//setType(Type.POPUP);
		
		setBounds(100, 100, 800, 400);
		setLocationRelativeTo(this.getOwner());
		
		
		getContentPane().setLayout(new BorderLayout());
		this.setResizable(false);
		

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier", Font.PLAIN, 11));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(textArea);
		
		
		if (t!= null)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			
//			if ( (t.getCause() != null && t.getCause().getMessage() != null) || t.getMessage() != null )
//				ps.append("Caused by:\n" +
//						( t.getCause() != null ? t.getCause().getMessage() : t.getMessage() )
//						+"\n\n\n\n");
				
			t.printStackTrace(ps);
			textArea.setText(baos.toString());
			textArea.setCaretPosition(0);
			
			this.setTitle("Error [" + t.getClass().getCanonicalName() + "]");
		}
		
		
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Fechar");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				
				cancelButton.addMouseListener(new MouseAdapter() {
					
					public void mouseClicked(MouseEvent event)
					{
						ExceptionDialog.this.setVisible(false);
					}
				});
			}
		}

		

        

	}
	
	
	
	public static ExceptionDialog createDialog(JComponent comp, Throwable t)
	{
		JFrame parent = ( comp == null ) ? null : (JFrame)comp.getTopLevelAncestor() ; 
		
		return createDialog(parent, t);
	}
	public static ExceptionDialog createDialog(JFrame parentFrame, Throwable t)
	{
		if(t == null)
			return null;
		
		ExceptionDialog dial = new ExceptionDialog(parentFrame, t);
		
		dial.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dial.setVisible(true);
		
		return dial;
	}

}
