package com.nn.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Font;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import com.nn.NNProject;
import com.nn.gui.error.ExceptionDialog;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class SelectorPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3714495144532138443L;
	private JPanel contentPane;
	private JFileChooser fChooser;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					SelectorPanel frame = new SelectorPanel();
					frame.setTitle("NN: Image Recognition");
					frame.setVisible(true);
				} catch (Exception e) {
					ExceptionDialog.createDialog((JFrame)null, e);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SelectorPanel() {
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 476);
		
		fChooser = Utils.getNNFileChooser();
		
		final Image img = (new ImageIcon(getClass().getResource("/resources/networklogo.jpg"))).getImage();
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g)
			{
				super.paint(g);
				
				g.drawImage(img, 1,1, this);
			};
		};
		panel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		panel.setBounds(10, 11, 262, 422);
		contentPane.add(panel);

		
		
		JButton btnNewButton = new JButton("Create Network");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				
				FrameLauncher fl = new FrameLauncher(
						SelectorPanel.this,
						NNSetCreatorPanel.preferedDimension,
						NNSetCreatorPanel.title) {

					private NNSetCreatorPanel creatorPanel = null;
					public JPanel getContentPane() {
						creatorPanel = new NNSetCreatorPanel();
						return creatorPanel;
					}
					
					public void doSomethingSuccess() {
						if(creatorPanel != null)
							creatorPanel.clearImagePreviewSelection();
					}
				};
				
				fl.launchFrame();
			}
		});
		
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnNewButton.setBounds(295, 59, 269, 44);
		contentPane.add(btnNewButton);
		

		JButton btnUseNetwork = new JButton("Load and Train Network");
		btnUseNetwork.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if( fChooser.showOpenDialog(SelectorPanel.this) == JFileChooser.APPROVE_OPTION )
				{
					final File f = fChooser.getSelectedFile();
					if( f != null && f.exists())
					{
						try {
							
							final NNProject project = NNProject.loadState(f);
							
							FrameLauncher fl = new FrameLauncher(
									SelectorPanel.this,
									NNSetCreatorPanel.preferedDimension,
									NNSetCreatorPanel.title) {
	
								private NNSetCreatorPanel creatorPanel = null;
								public JPanel getContentPane() {
									creatorPanel = new NNSetCreatorPanel(project, f);
									return creatorPanel;
								}
								
								public void doSomethingSuccess() {
									if(creatorPanel != null)
										creatorPanel.clearImagePreviewSelection();
								}
							};
							
							fl.launchFrame();
							
						} catch(Exception e) {
							ExceptionDialog.createDialog(SelectorPanel.this, e);
						}
					}
				}
			}
		});
		btnUseNetwork.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnUseNetwork.setBounds(295, 141, 269, 44);
		contentPane.add(btnUseNetwork);
		
		JButton btnTrainNetwork = new JButton("Load and Use Network");
		btnTrainNetwork.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if( fChooser.showOpenDialog(SelectorPanel.this) == JFileChooser.APPROVE_OPTION )
				{
					final File f = fChooser.getSelectedFile();
					if( f != null && f.exists())
					{
						try {
							
							final NNProject project = NNProject.loadState(f);
							
							FrameLauncher fl = new FrameLauncher(
									SelectorPanel.this,
									new Dimension(700,500),
									"NN Project: Image Recognition") {
			
								private TestingPanel testingPanel = null;
								public JPanel getContentPane() {
									testingPanel = new TestingPanel(project);
									return testingPanel;
								}
								
							};
							
							fl.launchFrame();
						
						} catch(Exception e) {
							ExceptionDialog.createDialog(SelectorPanel.this, e);
						}
					}
				}
			}
		});
		btnTrainNetwork.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnTrainNetwork.setBounds(295, 222, 269, 44);
		contentPane.add(btnTrainNetwork);
		
	}
}
