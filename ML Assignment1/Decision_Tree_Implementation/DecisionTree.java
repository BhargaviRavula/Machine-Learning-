

import java.util.ArrayList;
import java.util.Random;

/**
 * Class to implement a custom decision tree
 *
 * @author Bhargavi Ravula
 *
 */
public class DecisionTree {
	/** The tree's root node. */
	public Node root;

	/**
	 * Variable to number the nodes
	 */	
	private int index;

	/**
	 * Computes the accuracy of a decision tree
	 *
	 * @param data
	 *            The test data
	 * @param root
	 *            Root node of the decision tree
	 * @return Accuracy percentage as a decimal
	 */
	public double getAccuracy(ArrayList<ArrayList<Parameter>> data, Node root) {
		double accuracy = 0.0;

		// For each data set, check if result is correct or not
		for (ArrayList<Parameter> arrayList : data)
			if (treeMatchesClass(root, arrayList))
				accuracy++;

		// Return the computed accuracy
		return (accuracy * 100 / data.size());
	}

	/**
	 * Traverses along a decision tree till a leaf node is reached to check if
	 * the value of class Parameter in data set matches with decision tree
	 *
	 * 
	 * @param root
	 *            Root node of the decision tree
	 * @param data
	 *            Data instance
	 * 
	 * @return True if class value matches leaf node value, false otherwise
	 */
	private boolean treeMatchesClass(Node root, ArrayList<Parameter> data) {

		if (root.children == null)
			return data.get(data.size() - 1).value == root.value;
		else {
			// Compute the value of Parameter in the data set
			int value = -1;
			for (Parameter Parameter : data)
				if (Parameter.name.equals(root.ruleParameter.name)) {
					value = Parameter.value;
					break;
				}

			// Call recursively the child node
			return treeMatchesClass(root.children[value], data);
		}
	}

	/**
	 * Finds and remove subtree rooted at index
	 *
	 * @param root
	 *            Root of the subtree being processed
	 * @param index
	 *            Index to be removed
	 */
	private void findAndRemove(Node root, int index) {
		// If we found the root with the index
		if (root.index == index) {
			// Decide which is the majority class, and make it a leaf node
			if (root.children[0].data.size() > root.children[1].data.size()) {
				int[] valuecount = new int[2];
				for (ArrayList<Parameter> Parameter : root.children[0].data)
					valuecount[Parameter.get(Parameter.size() - 1).value]++;

				root.children[0].value = valuecount[0] > valuecount[1] ? 0 : 1;
				root.children[0].children = null;
				root.children[0].index = -1;
			} else {
				int[] valueCount = new int[2];
				for (ArrayList<Parameter> Parameter : root.children[1].data)
					valueCount[Parameter.get(Parameter.size() - 1).value]++;

				root.children[1].value = valueCount[0] > valueCount[1] ? 0 : 1;
				root.children[1].children = null;
				root.children[1].index = -1;
			}
		} else if (root.children != null) {

			// recursively explore the subtrees

			findAndRemove(root.children[0], index);
			findAndRemove(root.children[1], index);
		}
	}

	@Override
	public String toString() {
		return print(this.root, 0);
	}

	/**
	 * Prunes a tree
	 *
	 * @param L
	 *            Bound L on post pruning algorithm
	 * @param K
	 *            Bound K on post pruning algorithm
	 * @param data
	 *            The validation data
	 * @throws CloneNotSupportedException
	 */
	public void postPruning(int L, int K, ArrayList<ArrayList<Parameter>> data)
			throws CloneNotSupportedException {
		// Assign the tree as best
		Node best = (Node) root.clone();
		for (int i = 0; i < L; i++) {
			// Copy root to D
			Node D = (Node) root.clone();

			// Generate a random number between 1 and K
			Random random = new Random();
			int M = random.nextInt(K);
			for (int j = 0; j < M; j++) {
				// Generate a random number between 1 and index
				int P = random.nextInt(index);
				// Find subtree rooted at P and remove it
				findAndRemove(D, P);
			}

			// Set D to best if its accuracy is better
			if (getAccuracy(data, D) > getAccuracy(data, best))
				best = (Node) D.clone();
		}

		// Set root as the best tree formed
		root = (Node) best.clone();
	}

	/**
	 * Displays string representation a decision tree
	 *
	 * @param root
	 *            Root node of the subtree
	 * @param level
	 *            Level of the node
	 * @return String representation of the tree
	 */
	private String print(Node root, int level) {

		StringBuilder stringBuilder = new StringBuilder();

		// Do for all subtrees
		for (int j = 0; j < root.children.length; j++) {
			// Append tabs
			for (int i = 0; i < level; i++)
				stringBuilder.append("| ");

			// Print subtree if present
			stringBuilder.append(root.ruleParameter.name + " = " + j + " :");
			if (root.children[j].children != null)
				stringBuilder.append("\n" + print(root.children[j], level + 1));
			else
				stringBuilder.append(" " + root.children[j].value + "\n");
		}

		return stringBuilder.toString();
	}

	/**
	 * Method building decision tree using entropy/impurity as a heuristic
	 *
	 * @param data
	 *            the training data
	 * @param root
	 *            Starting node of the tree to be constructed
	 * @param heuristic
	 *            entropy/impurity
	 */

	@SuppressWarnings("unchecked")
	public void buildTree(ArrayList<ArrayList<Parameter>> data, Node root,
			String heuristic) {
		// Initialize
		Heuristic h = new Heuristic();
		double maxGain = 0.0;
		Parameter newRule = null;
		ArrayList<ArrayList<Parameter>> left = new ArrayList<>();
		ArrayList<ArrayList<Parameter>> right = new ArrayList<>();
		int index = -1;

		// Compute root`s heuristic
		if (heuristic.equalsIgnoreCase("entropy"))
			root.entropy = h.calculateEntropy(data);
		else
			root.entropy = h.calculateVarianceImpurity(data);

		// Split data in class of 0 as left tree and 1 as right tree
		for (int i = 0; i < data.get(0).size() - 1; i++) {
			ArrayList<ArrayList<Parameter>> leftSubset = new ArrayList<>();
			ArrayList<ArrayList<Parameter>> rightSubset = new ArrayList<>();

			// Calculate subtree split on this Parameter
			for (int j = 0; j < data.size(); j++) {
				ArrayList<Parameter> list = new ArrayList<>();
				list.addAll(data.get(j));

				if (data.get(j).get(i).value == 0)
					leftSubset.add(list);
				else
					rightSubset.add(list);
			}

			// Calculate sub entropies
			ArrayList<Double> subEntropies = new ArrayList<>();
			if (heuristic.equalsIgnoreCase("entropy")) {
				subEntropies.add(h.calculateEntropy(leftSubset));
				subEntropies.add(h.calculateEntropy(rightSubset));
			} else {
				subEntropies.add(h.calculateVarianceImpurity(rightSubset));
				subEntropies.add(h.calculateVarianceImpurity(leftSubset));

			}

			// Calculate size
			ArrayList<Integer> sizesOfSubsets = new ArrayList<>();
			sizesOfSubsets.add(leftSubset.size());
			sizesOfSubsets.add(rightSubset.size());

			// Compute Parameter with maximum information gain
			double gain = h.infoGain(root.entropy, subEntropies,
					sizesOfSubsets, data.size());
			if ((int) (gain * 100000000) > (int) (maxGain * 100000000)) {
				maxGain = gain;
				newRule = data.get(0).get(i);
				index = i;
				left = (ArrayList<ArrayList<Parameter>>) leftSubset.clone();
				right = (ArrayList<ArrayList<Parameter>>) rightSubset.clone();
			}
		}

		// If index was set, means we have an Parameter on which data can be
		// split
		if (index > -1) {
			// Remove the Parameter used from further consideration
			for (ArrayList<Parameter> Parameters : left)
				Parameters.remove(index);
			for (ArrayList<Parameter> Parameters : right)
				Parameters.remove(index);

			// Set the nodes for the recursive call to the subtree
			Node leftChild = new Node();
			Node rightChild = new Node();
			leftChild.data = left;
			rightChild.data = right;

			root.children = new Node[2];
			root.children[0] = leftChild;
			root.children[1] = rightChild;
			root.ruleParameter = newRule;
			root.index = ++this.index;

			// Recursively call subtrees
			buildTree(right, rightChild, heuristic);
			buildTree(left, leftChild, heuristic);

		} else {
			// Else, no more splitting is possible for this subtree
			root.value = data.get(0).get(data.get(0).size() - 1).value;
			return;
		}

		this.root = root;
	}
}