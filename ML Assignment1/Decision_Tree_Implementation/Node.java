
import java.util.ArrayList;

/**
 * Class to represent a node in the decision tree
 *
 * @author Bhargavi Ravula
 *
 */
public class Node implements Cloneable {
	/**
	 * Children of this node
	 */
	public Node[] children;

	/**
	 * Value of Class Parameter at this node, if this is a leaf node
	 */
	public int value;

	/**
	 * The Parameter which defined the rule to split data at this node
	 */
	public Parameter ruleParameter;

	/**
	 * Index to node in the tree (used when pruning)
	 */
	public int index;

	/**
	 * Entropy/Impurity at this node
	 */
	public double entropy;


	/**
	 * Data set to be considered at this subtree
	 */
	public ArrayList<ArrayList<Parameter>> data;


	public Node() {
		children = null;
		entropy = 0.0;
		data = new ArrayList<>();
		value = 0;
		ruleParameter = new Parameter("", 0);
		index = -1;
	}

	public Object clone() throws CloneNotSupportedException {
		Node cloned = new Node();
		cloned.index = this.index;
		cloned.value = this.value;
		cloned.ruleParameter = new Parameter(this.ruleParameter.name,
				this.ruleParameter.value);
		cloned.entropy = this.entropy;

		// Copy the data
		for (ArrayList<Parameter> Parameters : this.data) {
			ArrayList<Parameter> clonedParameters = new ArrayList<>();
			for (Parameter Parameter : Parameters)
				clonedParameters.add(new Parameter(Parameter.name,
						Parameter.value));

			cloned.data.add(clonedParameters);
		}

		// Recursively clone children
		if (this.children != null) {
			cloned.children = new Node[2];
			for (int i = 0; i < this.children.length; i++)
				cloned.children[i] = (Node) this.children[i].clone();
		}

		// Return the cloned node
		return cloned;
	}
}