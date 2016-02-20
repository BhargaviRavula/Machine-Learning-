

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Class to implement the program of decision trees. Contains the main method
 *
 * @author Bhargavi Ravula
 *
 */
public class DT_Impurity_BP {
	/**
	 * Parses a CSV file to read a data set
	 *
	 * @param fileLocation
	 *            Location of the file to be parsed
	 * @return List of Parameter name, value pairs
	 */

	private static ArrayList<ArrayList<Parameter>> parseCSV(String fileLocation) {
		BufferedReader br = null;
		String line = "";
		ArrayList<ArrayList<Parameter>> Parameters = new ArrayList<>();

		try {
			// Read the first line as header and parse the Parameter
			br = new BufferedReader(new FileReader(fileLocation));
			String[] header = br.readLine().split(",");

			// Read each line
			while ((line = br.readLine()) != null) {

				// Use comma as separator
				String[] values = line.split(",");

				// Parse each value, assign it to the specified Parameter and
				// add the Parameter to the list
				ArrayList<Parameter> row = new ArrayList<>();
				for (int i = 0; i < values.length; i++)
					row.add(new Parameter(header[i], Integer
							.parseInt(values[i])));

				Parameters.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Finally, try to close reader
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return Parameters;
		}
	}

	/**
	 * Execution starts here
	 *
	 * @param args
	 *            Command line arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Parse arguments
	
		ArrayList<ArrayList<Parameter>> trainingSet = parseCSV(args[0]);
		ArrayList<ArrayList<Parameter>> validationSet = parseCSV(args[1]);
		ArrayList<ArrayList<Parameter>> testSet = parseCSV(args[2]);
		boolean print = args[3].equalsIgnoreCase("yes");
		
		// Build tree using impurity gain
		DecisionTree tree = new DecisionTree();
		tree.buildTree(trainingSet, new Node(), "impurity");
	
		System.out
				.println("Accuracy of  tree constructed using impurity gain before pruning: "
						+ tree.getAccuracy(testSet, tree.root) + "%\n");


		if (print) {
			System.out.println("Decision tree before pruning: ");
			System.out.println(tree);
		} 
		
	}
}