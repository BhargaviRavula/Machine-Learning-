
import java.util.ArrayList;

/**
 * Class which implements calculation of heuristics
 *
 * @author Bhargavi Ravula
 *
 */
public class Heuristic {
	/**
	 * Calculates the variance impurity 
	 *
	 * @param data
	 *            Data for which impurity is to be computed
	 * @return Variance impurity 
	 */
	public double calculateVarianceImpurity(ArrayList<ArrayList<Parameter>> data) {

		double[] K = new double[2];
		for (ArrayList<Parameter> Parameter : data)
			K[Parameter.get(Parameter.size() - 1).value]++;
		double impurity = 1;
		for (int i = 0; i < K.length; i++)
			impurity *= K[i];

		return impurity / (Math.pow(data.size(), K.length));
	}

	/**
	 * Computes the information gain for subset of data
	 *
	 * @param currentEntropy
	 *            Entropy/Impurity of root node
	 * @param subsetEntropies
	 *            Entropy/Impurity of each subset whose parent is root node
	 * @param subsetSize
	 *            list of number of examples in each subset
	 * @param totalExamples
	 *            total number of examples for the given set
	 * @return
	 */
	public double infoGain(double currentEntropy,
			ArrayList<Double> subsetEntropies,
			ArrayList<Integer> subsetSize, double totalExamples) {
		// Compute gain as root entropy - (sum of (K/N(entropy of K))
		double gain = currentEntropy;
		for (int j = 0; j < subsetEntropies.size(); j++)
			gain -= (subsetSize.get(j) / totalExamples)
					* subsetEntropies.get(j);

		return gain;
	}

	/**
	 * Computes the entropy of a data set.
	 *
	 * @param data
	 *            the data for which entropy is to be computed
	 * @return the entropy of the data's class distribution
	 */
	public double calculateEntropy(ArrayList<ArrayList<Parameter>> data) {
		// Assuming there are two values 0,1 possible, count how many values are
		// in 0 and 1
		double[] valueCount = new double[2];
		for (ArrayList<Parameter> Parameter : data)
			valueCount[Parameter.get(Parameter.size() - 1).value]++;

		// Entropy = sum of K/N(log(K/N)) = (sum of KlogK) / N - logN
		double entropy = 0;
		for (int j = 0; j < valueCount.length; j++)
			if ((int) valueCount[j] != 0)
				entropy -= valueCount[j]
						* (Math.log(valueCount[j]) / Math.log(2));

		entropy /= data.size();
		return entropy + (Math.log(data.size()) / Math.log(2));
	}
}