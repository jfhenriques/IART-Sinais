package com.nn;

import java.util.Arrays;
import java.util.Random;

public class Neuron implements java.io.Serializable {
	
	private static final long serialVersionUID = 4617407578115907400L;
	private static Random rand = new Random();
	
	
	private double[] conn_weight;
	private transient double activation;
	private transient double sum = 0.0;
	private transient double deltaError = 0.0;

	
	
	public static void setRandom(Random rand)
	{
		Neuron.rand = rand;
	}


	
	public Neuron(int childs)
	{
		this(childs, 1.0);
	}
	public Neuron(int childs, double activation)
	{
		this.conn_weight = new double[childs];
		this.activation = activation;
		
		randomizeConnections(null);
	}
	
	
	public Neuron(double[] conn_weights)
	{
		this(conn_weights, 1.0);
	}
	public Neuron(double[] conn_weights, double activation)
	{
		this.conn_weight = Arrays.copyOf(conn_weights, conn_weights.length);
		this.activation = activation;
	}
	
	
	
	public void randomizeConnections(Random rand)
	{
		if( rand == null )
			rand = Neuron.rand;
		
		for( int i = 0; i < this.conn_weight.length; i++ )
			this.conn_weight[i] = (2.0 * (1.0 - rand.nextDouble()) - 1.0);
	}
	
	
	
	public double getSum()
	{
		return this.sum;
	}
	
	public double getActivationValue()
	{
		return this.activation;
	}
	
	public double getConnectionWeight(int child)
	{
		return this.conn_weight[child];
	}
	
	public double getActivationTimesWeight(int child)
	{
		return this.activation * this.conn_weight[child];
	}
	
	
	
	
	public void setActivationValue(double activation)
	{
		this.activation = activation;
	}
	
	public void setConnectionWeight(int child, double value)
	{
		this.conn_weight[child] = value;
	}
	
	public void addDelta2ConnectionWeight(int child, double value)
	{
		this.conn_weight[child] += value;
	}
	
	
	
	public void setSum(double sum)
	{
		this.sum = sum;
	}
	public void calculateActivationValue()
	{
		this.activation = NeuralNetwork.sigmoidFunction(this.sum);
	}
	
	
	
	
	public void calculateOutputLayerDeltaError(double expectedValue)
	{
		this.deltaError = (NeuralNetwork.sigmoidFunctionDeriv(this.activation) * (expectedValue - this.activation));
	}
	public void calculateHiddenLayerDeltaError(double sum)
	{
		this.deltaError = (NeuralNetwork.sigmoidFunctionDeriv(this.activation) * sum);
	}
	
	
	
	public int getTotalChilds()
	{
		return this.conn_weight.length;
	}
	
	public double getDeltaError()
	{
		return this.deltaError;
	}
}