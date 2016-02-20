/**
 * Class to represent a parameter
 *
 * @author Bhargavi Ravula
 *
 */
public class Parameter {

	/**
	 * Name of this attribute
	 */
	public String name;

	/**
	 * Value of this attribute in the corresponding data instance
	 */
	public int value;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name to set
	 * @param value
	 *            Value to set
	 */
	public Parameter(String name, int value) {
		
		this.name = name;
		this.value = value;
	}
}