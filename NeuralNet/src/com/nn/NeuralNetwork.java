package com.nn;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class NeuralNetwork implements java.io.Serializable {
	
	
	public static class TrainingDataSet implements java.io.Serializable
	{

		private static final long serialVersionUID = 2336494630862678675L;
		public final int inputNeurons;
		public final int outputNeurons;
		
		transient private TrainingListenener singleListener = null;
		transient private long lastInform = 0; 
		transient private long informRate = 0;
		
		
		public ArrayList< double[] > input = new ArrayList< double[] >();
		public ArrayList< double[] > output = new ArrayList< double[] >();
	
		
		public TrainingDataSet(int inputNeurons, int outputNeurons)
		{
			this.inputNeurons = inputNeurons;
			this.outputNeurons = outputNeurons;
		}
	
		
		public void registerTest(double[] input, double[] output)
		{
			if( input.length != this.inputNeurons || output.length != this.outputNeurons )
				throw new IllegalArgumentException("O número de elementos em ambos os arrays tem de coincidir com a configuração da rede.");

			this.input.add(input);
			this.output.add(output);
		}


		
		

		public void setSingleListener(TrainingListenener singleListener, long informRate)
		{
			this.singleListener = singleListener;
			this.informRate = informRate;
			
			this.lastInform = 0;
		}
		
		
		public boolean isInformable()
		{
			if( singleListener != null && (System.currentTimeMillis() - informRate) >= lastInform )
			{
				lastInform = System.currentTimeMillis();

				return true;
			}
			return false;
		}
		
		
		
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
		{
			in.defaultReadObject();
			
			singleListener = null;
			lastInform = 0;
			informRate = 0;

		}
	}
	
	
	
	
	
	private static final long serialVersionUID = -7158377882518446384L;
	private static double LEARN_FACTOR = 0.3;
	
	
	private final int[] networkLayout;
	private final ArrayList< Neuron[] > layers;
	
	private double learnFactor = LEARN_FACTOR;
	
	
	transient private double[] outputCopy = new double[0];
	transient private Object outputCopyMutex = new Object();
	
	transient private boolean interruptTraining = false;

	
	
	public NeuralNetwork(int... layerTotalElements)
	{
		if( layerTotalElements.length < 2 )
			throw new IllegalArgumentException("A rede necessita de ter no mínimo duas camadas.");
		
		
		this.networkLayout = Arrays.copyOf( layerTotalElements, layerTotalElements.length );
		this.layers = new ArrayList <Neuron[] >( layerTotalElements.length );
		
		int layer_plus_total;
		
		for( int level = 0; level < networkLayout.length; level++ )
		{
			Neuron[] layer = new Neuron[ networkLayout[ level ] ];
		
			layer_plus_total = (level < networkLayout.length - 1) ? 
										networkLayout[ level+1 ] : 
										0;
		
			for( int n = 0; n < networkLayout[level]; n++ )
				layer[n] = new Neuron( layer_plus_total );
			
			layers.add( layer );
		}
	}
	
	

	public void propagate(double[] input)
	{
		if ( input.length != networkLayout[0] )
			throw new IllegalArgumentException("Número de parâmetros da camada de entrada não coincide com a configuração da rede.");
		
		Neuron[] layer = layers.get(0),
				 layerMinusOne = layer;
		Neuron neuron;
		
		double sum;
		
		// Insere valores na primeira camada
		for( int i = 0; i < input.length; i++ )
			layer[i].setActivationValue( input[i] );

		// Propaga até a última camada
		for( int level = 1; level < networkLayout.length; level++ )
		{
			layer = layers.get(level);
		
			for( int n = 0; n < layer.length; n++ )
			{
				neuron = layer[n];
				sum = 0.0;
			
				for ( int ln = 0; ln < layerMinusOne.length; ln++ )
					sum += layerMinusOne[ln].getActivationTimesWeight(n);

				neuron.setSum(sum);
				neuron.calculateActivationValue();
			}
		
			layerMinusOne = layer;
		}
		
		
		synchronized (this.outputCopyMutex)
		{
			int lastLayerLen = networkLayout[ networkLayout.length-1 ];
			
			if( outputCopy.length != lastLayerLen )
				outputCopy = new double[lastLayerLen];
				
			Neuron[] lastLayer = layers.get( networkLayout.length-1 );
			
			for( int i = 0; i < lastLayerLen; i++ )
				outputCopy[i] = lastLayer[i].getActivationValue();

		}
	}
	
	

	public void backpropagate(double[] output)
	{
		if( output.length != networkLayout[ networkLayout.length-1 ] )
			throw new IllegalArgumentException("Número de parâmetros da camada de saída não coincide com a configuração da rede.");

		
		Neuron[] layer = layers.get( this.networkLayout.length-1 ),
				 nextLayer = layer;
		Neuron neuron;
		
		double sum,delta;
		
		
		// Coloca os valores esperados na camada de saída
		for( int n = 0; n < networkLayout[ networkLayout.length-1 ]; n++)
		{
			neuron = layer[n];
			neuron.calculateOutputLayerDeltaError( output[n] );
		}
		
		
		// Calcula o delta dos erros 
		for( int level = networkLayout.length-2; level >= 1; level-- )
		{
			layer = layers.get(level);
		
			for( int n = 0; n < networkLayout[level]; n++ )
			{
				neuron = layer[n];
				sum = 0.0;
			
				for( int ln = 0; ln < nextLayer.length; ln++ )
					sum += neuron.getConnectionWeight(ln) * nextLayer[ln].getDeltaError();
				
				neuron.calculateHiddenLayerDeltaError( sum );
			}
			
			nextLayer = layer;
		}
		
		
		// Actualiza os pedos das ligações
		layer = layers.get(0);
		for( int level = 0; level < networkLayout.length-1; level++ )
		{
			nextLayer = layers.get( level+1 );
		
			for( int n = 0; n < this.networkLayout[level]; n++ )
			{
				neuron = layer[n];
			
				for( int ln = 0; ln < nextLayer.length; ln++ )
				{
					delta = this.learnFactor * neuron.getActivationValue() * nextLayer[ln].getDeltaError();
					
					neuron.addDelta2ConnectionWeight( ln, delta );
				}
			}
			
			layer = nextLayer;
		}
	}
	
	

	public void train( TrainingDataSet set, int maxEpochs, int[] bound )
	{
		interruptTraining = false;
		double[] trainOutput = new double[set.input.size()];
		
		boolean isCurrentCycleInformable = false,
				isTestInformable = ( bound != null && bound.length == set.input.size() && set.singleListener != null );
		
		
		for( int epoch = 1; epoch <= maxEpochs; epoch++ )
		{
			
			if( isTestInformable && (set.isInformable() || epoch == maxEpochs) )
				isCurrentCycleInformable = true;
			
			for( int i = 0; i < set.input.size(); i++ )
			{
				propagate(set.input.get(i));
				backpropagate(set.output.get(i));
				
				if( isTestInformable && isCurrentCycleInformable)
					trainOutput[i] = layers.get( this.networkLayout.length-1 )[bound[i]].getActivationValue();
				
				if( interruptTraining ) break;
			}
			
			if( isTestInformable && isCurrentCycleInformable )
				set.singleListener.informOutputedData(epoch, maxEpochs, trainOutput);

			isCurrentCycleInformable = false;
			
			if( interruptTraining ) break;
		}
		
		set.setSingleListener(null, 0);
	}
	
	
	public int getMaxValueIndex(double[] arr)
	{
		int mIndex = -1;
		double mVal = -100.0;
		
		for(int i = 0; i < arr.length; i++)
		{
			if( arr[i] > mVal )
			{
				mVal = arr[i];
				mIndex = i;
			}
		}
		return mIndex;
	}
	
	public void smartTrain( TrainingDataSet set, int[] bound )
	{
		interruptTraining = false;
		double[] trainOutput = new double[set.input.size()];
		double[] expected;
		double error;
		int maxIndex;
		
		final double ALLOWED_ERROR = 0.5;
		
		boolean continueTraining = true,
				isCurrentCycleInformable = false,
				isTestInformable = ( bound != null && bound.length == set.input.size() && set.singleListener != null );
		
		while( continueTraining )
		{
			
			continueTraining = false;
			
			if( isTestInformable && set.isInformable() )
				isCurrentCycleInformable = true;
				
			for( int i = 0; i < set.input.size(); i++ )
			{
				propagate(set.input.get(i));
				
				expected = set.output.get(i);
				
				maxIndex = getMaxValueIndex(outputCopy);
				
				if( maxIndex != bound[i] )
				{
					continueTraining = true;
					break;
				}
				
				for(int j = 0; j < outputCopy.length; j++)
				{
					error = expected[j]-outputCopy[j];
					if( error <= -ALLOWED_ERROR || error >= ALLOWED_ERROR )
					{
						continueTraining = true;
						break;	
					}
				}
				
				if( interruptTraining ) break;
			}
				
			
			if( continueTraining && !interruptTraining )
			{
				for( int i = 0; i < set.input.size(); i++ )
				{
					propagate(set.input.get(i));
					backpropagate(set.output.get(i));
					
					if( isTestInformable && isCurrentCycleInformable)
						trainOutput[i] = layers.get( this.networkLayout.length-1 )[bound[i]].getActivationValue();
					
					if( interruptTraining ) break;
				}
			}
			
			if( !continueTraining || (isTestInformable && isCurrentCycleInformable) )
				set.singleListener.informOutputedData(0, 1, trainOutput);

			isCurrentCycleInformable = false;
			
			if( interruptTraining ) break;
		}
		
		set.setSingleListener(null, 0);
	}

	
	public double[] getPropagationOutput()
	{
		synchronized (this.outputCopyMutex)
		{
			return Arrays.copyOf(outputCopy, outputCopy.length);
		}
	}

	public double getLearnFactor()
	{
		return this.learnFactor;
	}
	
	public void setLearnFactor(double learnFactor)
	{
		if( learnFactor >= 1.0 )
			this.learnFactor = 1.0;
		else if( learnFactor <= 0.01 )
			this.learnFactor = 0.01;
		else
			this.learnFactor = learnFactor;
	}
	
	public int[] getNetworkLayout()
	{
		return Arrays.copyOf( this.networkLayout, this.networkLayout.length );
	}
	
	public static double sigmoidFunction(double val)
	{
		return 1.0 / (1.0 + Math.exp( -val ));
	}
	
	public static double sigmoidFunctionDeriv(double val)
	{
		return val * (1.0 - val);
	}

	
	public static void saveState(String filename, NeuralNetwork network)
	{
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		
		try
		{
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(network);
			out.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public static NeuralNetwork loadState(String filename)
	{
		NeuralNetwork network = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		
		try
		{
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			network = (NeuralNetwork)in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		
		return network;
	}
	
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		outputCopyMutex = new Object();
		outputCopy = new double[0];
		interruptTraining = false;
	}
	
	public void interruptTraining()
	{
		interruptTraining = true;
	}


}