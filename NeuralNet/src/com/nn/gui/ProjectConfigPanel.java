package com.nn.gui;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Color;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;



public class ProjectConfigPanel extends JPanel {
	
	
	private static final long serialVersionUID = -1404968067029384524L;
	private JTextField middleLayersText;
	private JTextField textField;
	private JCheckBox middleLayersCheckBox;
	
	final private JFileChooser fileSaver;
	
	private JButton btnSelectFile;
	private JButton btnCreateNework;
	
	private boolean isLocked = false; 
	
	private File saveFile = null;
	
//	private JSpinner widthDivSpinner;
//	private JSpinner heightDivSpinner;
	
	
	
//	public int getWidthDivisor()
//	{
//		return (int) widthDivSpinner.getValue();
//	}
//	public int getHeightDivisor()
//	{
//		return (int) heightDivSpinner.getValue();
//	}
	
	public File getSaveFile()
	{
		return this.saveFile;
	}
	
	
	
	public int[] getMiddleLayers()
	{

		if(middleLayersCheckBox.isSelected())
		{
			try {
				ArrayList<Integer> lista = new ArrayList<Integer>();
				String s2;
				for(String s: middleLayersText.getText().split(","))
				{
					s2 = s.trim();
					if(s2.length() == 0)
						continue;
					
					lista.add(Integer.parseInt(s2));
				}
				
				if(lista.size() > 0)
				{
					int[] mLayers = new int[lista.size()];
					
					for(int i = 0; i <lista.size(); i++)
						mLayers[i] = lista.get(i);
					
					return mLayers;
				}
			} catch(Exception e) {
				return null;
			}
		}
		
		return new int[0];
	}

	
	public ProjectConfigPanel()
	{
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(25dlu;default)"),
				ColumnSpec.decode("max(4dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(65dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("6dlu"),
				FormFactory.DEFAULT_ROWSPEC,}));
		
		
//		JLabel lblWidth = new JLabel("Width Div");
//		lblWidth.setHorizontalAlignment(SwingConstants.LEFT);
//		add(lblWidth, "2, 2, 3, 1, left, default");
//		
//		widthDivSpinner = new JSpinner();
//		widthDivSpinner.setModel(new SpinnerNumberModel(new Integer(8), new Integer(2), null, new Integer(2)));
//		add(widthDivSpinner, "5, 2, 5, 1");
//		
//		JLabel lblNewLabel = new JLabel("Height Div");
//		add(lblNewLabel, "2, 4, 3, 1, left, default");
//		
//		heightDivSpinner = new JSpinner();
//		heightDivSpinner.setModel(new SpinnerNumberModel(new Integer(8), new Integer(2), null, new Integer(2)));
//		add(heightDivSpinner, "5, 4, 5, 1");
		
		JLabel lblNewLabel_1 = new JLabel("Hidden Layers");
		add(lblNewLabel_1, "2, 2, 4, 1");
		
		JLabel lblseparadasPorVrgula = new JLabel("(comma separated)");
		lblseparadasPorVrgula.setForeground(Color.LIGHT_GRAY);
		add(lblseparadasPorVrgula, "7, 2, 3, 1");
		
		middleLayersCheckBox = new JCheckBox("");
		middleLayersCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(isLocked()) return;
				
				middleLayersText.setEnabled(!middleLayersText.isEnabled());
			}
		});
		add(middleLayersCheckBox, "2, 4");
		
		middleLayersText = new JTextField();
		middleLayersText.setEnabled(false);
		add(middleLayersText, "4, 4, 6, 1, fill, default");
		middleLayersText.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Save File");
		add(lblNewLabel_2, "2, 6, 3, 1");
		
		textField = new JTextField();
		textField.setForeground(Color.LIGHT_GRAY);
		textField.setEditable(false);
		add(textField, "5, 6, 3, 1, fill, default");
		textField.setColumns(10);
		
		fileSaver = Utils.getNNFileChooser();
		
		
		btnSelectFile = new JButton("Select File");
		btnSelectFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(isLocked()) return;


				if( fileSaver.showSaveDialog(ProjectConfigPanel.this) == JFileChooser.APPROVE_OPTION )
				{
					File f = Utils.getFIleNameWithExtension(fileSaver.getSelectedFile(), Utils.NNFileFilter.NN_EXTENSION );
					
					if( f.exists() )
					{
						int n = Utils.showYesNo(ProjectConfigPanel.this, null, "The file already exstis. Allow it to be overwriten?");
						if( n != JOptionPane.OK_OPTION ) return;
					}
					
					saveFile = f;
					textField.setText(f.getAbsolutePath() );
				}
			}
		});
		add(btnSelectFile, "9, 6");
		
		btnCreateNework = new JButton("Create Network");
		btnCreateNework.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(isLocked()) return;
				
				NNSetCreatorPanel.getInstance().createNetwork();
			}
		});

		add(btnCreateNework, "2, 8, 8, 1");
		
	}



	public boolean isLocked()
	{
		return isLocked;
	}
	public void setLocked(boolean isLocked)
	{
		this.isLocked = isLocked;
		
		middleLayersCheckBox.setEnabled( !isLocked );
		middleLayersText.setEnabled( !isLocked && middleLayersCheckBox.isSelected() );
		btnSelectFile.setEnabled( !isLocked );
		btnCreateNework.setEnabled( !isLocked );
//		heightDivSpinner.setEnabled( !isLocked );
//		widthDivSpinner.setEnabled( !isLocked );
	}
	
	public void setSaveFile(File f)
	{
		saveFile = f;
		fileSaver.setSelectedFile( f );
		
		textField.setText( (f != null) ? f.getAbsolutePath() : "" );
	}
	
	public void setHiddenLayers(String text)
	{
		middleLayersText.setText(text);
	}
}
