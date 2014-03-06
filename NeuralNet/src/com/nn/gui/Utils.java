package com.nn.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public final class Utils {
	
	private Utils() {}
	
	public static class NNFileFilter extends FileFilter {

		public static String NN_EXTENSION = "NN";
		
		public boolean accept(File f)
		{
			if ( f.isDirectory() )
				return true;
		
	        String ext = Utils.getFileExtension(f);

	        if ( ext != null )
	            return ext.equals( NN_EXTENSION );
	        
	        return false;
	    }

	    public String getDescription()
	    {
	    	return ".nn (Neural Network file)";
	    }
	}
	
	public static class TXTFileFilter extends FileFilter {

		public static String TXT_EXTENSION = "TXT";
		
		public boolean accept(File f)
		{
			if ( f.isDirectory() )
				return true;
		
	        String ext = Utils.getFileExtension(f);

	        if ( ext != null )
	            return ext.equals( TXT_EXTENSION );
	        
	        return false;
	    }

	    public String getDescription()
	    {
	    	return ".txt (TXT Report File)";
	    }
	}
	
	public static class ImageFileFilter extends FileFilter {

		public boolean accept(File f)
		{
			if ( f.isDirectory() )
				return true;
		
	        String ext = Utils.getFileExtension(f);

	        if ( ext != null )
	            return ( ext.equals("JPEG") || ext.equals("JPG") || ext.equals("PNG") );
	        
	        return false;
	    }

	    public String getDescription()
	    {
	    	return "Images";
	    }
	}
	
	
	private static JFileChooser nnFileChooser = null;
	private static JFileChooser txtFileChooser = null;
	private static JFileChooser pngFileChooser = null;
	//private static JFileChooser nnFileChooser = null;
	
	
	public static String extractNameOnly(String name)
	{
		if(name == null)
			return null;
		
		int li = name.lastIndexOf('.');
		
		if(li <= 1)
			return name;
		
		return name.substring(0, li);
	}
	
	public static String getFileExtension(File f)
	{
        String ext = null;
        String name = f.getName();
        int i = name.lastIndexOf('.');

        if ( i > 0 &&  i < name.length()-1 )
            ext = name.substring(i+1).toUpperCase();
        
        return ext;
	}
	
	
	
	
	public static JFileChooser getNNFileChooser()
	{
		if(nnFileChooser == null)
		{
			nnFileChooser = new JFileChooser();
			nnFileChooser.setFileFilter(new NNFileFilter());
			nnFileChooser.setAcceptAllFileFilterUsed(false);
			nnFileChooser.setMultiSelectionEnabled(false);
		}
		return nnFileChooser;
	}
	
	public static JFileChooser getTXTFileChooser()
	{
		if(txtFileChooser == null)
		{
			txtFileChooser = new JFileChooser();
			txtFileChooser.setFileFilter(new TXTFileFilter());
			txtFileChooser.setAcceptAllFileFilterUsed(false);
			txtFileChooser.setMultiSelectionEnabled(false);
		}
		return txtFileChooser;
	}
	
	public static JFileChooser getIMAGEFileChooser()
	{
		if(pngFileChooser == null)
		{
			pngFileChooser = new JFileChooser();		
			pngFileChooser.setFileFilter(new ImageFileFilter());
			pngFileChooser.setAcceptAllFileFilterUsed(false);
			pngFileChooser.setMultiSelectionEnabled(true);
		}
		return pngFileChooser;
	}
	

	
	
	public static int showYesNo(JPanel parentPanel, String title, String message)
	{
		return JOptionPane.showConfirmDialog(parentPanel.getTopLevelAncestor(), message, title, JOptionPane.YES_NO_OPTION);
	}
	
	public static File getFIleNameWithExtension(File f, String ext)
	{
		String fileExt = Utils.getFileExtension(f);
		
		return (fileExt != null && fileExt.equals(ext.toUpperCase())) ?
						f :
						new File(f.getAbsolutePath() + "." + ext.toLowerCase()) ;
	}

}
