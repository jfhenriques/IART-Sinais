package com.nn.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class TrainingTableModel extends AbstractTableModel {


	private static final long serialVersionUID = 4541523353108267508L;
	
	private ArrayList<double[]> data = new ArrayList<double[]>();
	private final int columns;
 
	
	public TrainingTableModel(int outputs)
	{
		this.columns = outputs;
	}
	
	
	@Override
	public String getColumnName(int column)
	{				
		return "Imagem " + (column+1);
	}
	
	@Override
	public int getColumnCount()
	{
		return columns;
	}

	@Override
	public int getRowCount()
	{
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		try {
			return data.get(row)[col];
		} catch(Exception e) {
			return -1.0;
		}
	}
	

	
	public void addRow(double[] row)
	{
		if( row.length == columns )
		{
			data.add(row.clone());
			fireTableRowsInserted(data.size()-2, data.size()-1);
		}
	}
	
	
	public void clearRows()
	{
		data.clear();
		fireTableDataChanged();
	}

}
