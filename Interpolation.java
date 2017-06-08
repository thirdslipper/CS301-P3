/**
 * Author: Colin Koo
 * Professor: Raheja
 * Program: Perform interpolation using Newton's and Langrange's methods.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Interpolation {
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		StringTokenizer st;
		String word, filename; 
		int size = 0, index;
		boolean start = true;

		double[] independent = new double[50];
		double temp = 0.0;
		double[] dependent = new double[50];
		System.out.println("Enter filename ex(ex, test.txt): ");
		try {
			filename = kb.nextLine();
			File file = new File(filename);
			Scanner inputFile = new Scanner(file);   // opens file of user input

			while (inputFile.hasNext()) {
				word = inputFile.nextLine();
				st = new StringTokenizer(word); 
				if (start) {	// line  0, x's
					index = 0;
					while (st.hasMoreTokens()) {	//stores independent variables into an array, max 50 independents
						if (size < 50) {
							temp = Double.parseDouble(st.nextToken());
							independent[index] = temp;   // stores individual parts into array for x
							index++;
							size++; // keeps track of actual size, not max array
						}
					}
					start = false;
				}
				else {	// other line, y's.
					index = 0;
					while (st.hasMoreTokens()) { // tokenizes string into parts
						if (index < size) {
							temp = Double.parseDouble(st.nextToken());
							dependent[index] = temp;   // stores individual parts into array for fx
							index++;
						}
					}
				}
			}

			double[] coef = new double[size];  // creates array for storing coefficients of solution
			int fxsize = size;
			int s = size - 1;
			while (s != 0) { // counts independent vars of times for # of iterations
				fxsize += s;
				s--;
			}
			double[] y = dependent.clone();
			solveCoef(independent, dependent, size, fxsize);
			coef = tablePrint(independent, dependent, size, fxsize);
			equationPrint(coef, independent, size);
			lagrange(independent, y, size);

			kb.close();
			inputFile.close();
		}
		catch (FileNotFoundException e) {
			System.err.println(e.getMessage());    	
		}
	}

	/**
	 * Appends the fx array with the coefficients of the proceeding factors of the polynomial equation. 
	 * @param x	independent variable array
	 * @param fx dependent variable array
	 * @param size actual size of the given arrays
	 * @param fxsize actual size of the given arrays
	 */
	public static void solveCoef(double[] x, double[] fx, int size, int fxsize) {
		int num = size - 1; // for keeping track of each 
		int lowerPos = 0;  // for subtracting x at target position by in x a lower position of table
		int upperPos = size - 1; // for determining x in higher position in the table
		int counter2Bound = size - 1; // for changing the counter2 when moving alongside the table
		for (int i = size; i < fxsize; i++) {
			int a = i - num;
			int b = i - num - 1;
			int upper = lowerPos;
			int lower = size - 1;
			lowerPos++;
			upperPos--;
			lower -= upperPos;
			fx[i] = (fx[a] - fx[b]) / (x[lower] - x[upper]);

			if (lowerPos == num) {
				num--;
				lowerPos = 0;
				counter2Bound--;
				upperPos = counter2Bound;
			}
		}
		/*		for (int c = 0; c < fx.length; ++c){
			System.out.println(fx[c]);
		}*/
	}
	/**
	 * Gets the coefficients of the leading polynomials
	 * @param x	independent variable array
	 * @param fx dependent variable array, including coefficients of the proceeding terms to be used
	 * @param size actual size of the given arrays
	 * @param fxsize actual size of the given arrays
	 * @return
	 */
	public static double[] tablePrint(double[] x, double[] fx, int size, int fxSize) {
		System.out.printf("%5s %7s", "x", "fx");  // Table formatting
		System.out.println("");

		int index = 1, count = 1; 
		double[] eqCoeffs = new double[size];  // for holding the coefficient results

		int k, m, temp;
		double newX, newFx, result;
		for (int i = 0; i < size; i++) {
			m = k = size;
			temp = size;

			if (i < k) {
				newX = x[i];
				newFx = fx[i];
				if (i == 0){  // for storing the 1st value of the coefficients 
					eqCoeffs[0] = newFx; 
				}
				System.out.printf("%7.3f %7.3f", + newX, newFx); // prints 'x' and 'fx' values while available
			}
			while (m + i < fxSize) {
				result = fx[i + m];
				if (i == 0) { // stores start of each row in coefficients
					eqCoeffs[index++] = result;
				} 
				System.out.printf("%7.3f", result); // prints remainder of a table's row
				m += --temp;
			}
			fxSize -= count++;
			System.out.println("");
		}
		System.out.println("");
		/*		for (int z = 0; z < eqCoeffs.length; ++z){
			System.out.println(eqCoeffs[z]);
		}*/
		return eqCoeffs;
	}
	/**
	 * Prints out a string representation of newton's form.
	 * @param coeffs - Leading coefficients to be used
	 * @param indepedent - given x values
	 * @param size - actual size of the array
	 */
	public static void equationPrint(double[] coeffs, double[] indepedent, int size) {
		String temp = "";
		double num = 0, num2 = 0;
		double hold;
		String[] eq = new String[size];
		for (int i = 0; i < coeffs.length; i++) {
			System.out.print(Math.rint((coeffs[i] * 1000))/1000);

			if (i == 0) {	//print first value
				if (Math.signum(coeffs[i+1]) == 1)
					System.out.print(" + ");
				else
					System.out.print(" - ");
			}
			if (i > 0) {
				temp += "(x";

				num = (indepedent[i-1]);
				num2 = (Math.abs(indepedent[i - 1]));

				if (indepedent[i - 1] == 0)
					temp += ")"; 
				else if (indepedent[i - 1] > 0) 
					temp += "-" + num + ")";
				else
					temp += "+" + num2 + ")";
				System.out.print(temp);
				if ((i+1) < size) {
					if (coeffs[i+1] > 0) 
						System.out.print(" + ");
					else
						System.out.print(" ");
				}
			}
		}
		System.out.println("\n");
	}
	/**
	 * Prints out the simplified version of langrange's form
	 * @param independent - given x values
	 * @param dependent - given y values
	 * @param size - actual size of the array
	 */
	public static void lagrange(double[] independent, double[] dependent, int size){
		String[] eq = new String[size];
		double subtotal;

		for (int i = 0; i < size; ++i){	//for ea sub equation
			subtotal = 1;
			eq[i] = "";
			for (int j = 0; j < size; ++j){
				if (j != i){
					if (independent[j] != 0){
						eq[i] += "(x-" +  independent[j] + ")";
					}
					else{
						eq[i] += "(x)";
					}
					subtotal *= (independent[i]-independent[j]);
				}
			}
			subtotal = dependent[i]/subtotal;
			subtotal = Math.rint((subtotal * 100))/100;
			eq[i] = subtotal + eq[i];
			if (i < size-1){
				eq[i] += " + ";
			}
			System.out.print(eq[i]);
		}
		System.out.println();
	}
}