/**
 * Author: Colin Koo
 * Professor: Raheja
 * Program: Perform interpolation using Newton's and Langrange's methods.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
		//		System.out.println("Enter filename ex(ex, test.txt): ");
		try {
			//			filename = kb.nextLine();
			File file = new File("input.txt");
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
			//			lagrangeExpanded(independent, y, size);

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
		/*for (int z = 0; z < eqCoeffs.length; ++z){
			System.out.println(eqCoeffs[z]);
		}*/
		return eqCoeffs;
	}
	/**
	 * Prints out a string representation of newton's form.
	 * @param coeffs - Leading coefficients to be used
	 * @param independent - given x values
	 * @param size - actual size of the array
	 */
	public static void equationPrint(double[] coeffs, double[] independent, int size) {
		System.out.println("Newton Equation");
		StringBuilder sb = new StringBuilder();
		double num = 0, num2 = 0;
		for (int i = 0; i < coeffs.length; i++) {
			System.out.print(Math.rint((coeffs[i] * 1000))/1000);

			if (i == 0) {	//print first value
				if (Math.signum(coeffs[i+1]) == 1)
					System.out.print(" + ");
				else
					System.out.print(" - ");
			}
			if (i > 0) {
				sb.append("(x");

				num = (independent[i-1]);
				num2 = (Math.abs(independent[i - 1]));

				if (independent[i - 1] == 0)
					sb.append(")"); 
				else if (independent[i - 1] > 0) 
					sb.append("-" + num + ")");
				else
					sb.append("+" + num2 + ")");
				System.out.print(sb);
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
		System.out.println("Lagrange");
		StringBuilder[] eq = new StringBuilder[size];
		double subtotal;
		ArrayList<Double> polynomials = new ArrayList<Double>();
		ArrayList<Double> segmentPoly = new ArrayList<Double>();
		ArrayList<Double> condensed = new ArrayList<Double>();

		for (int i = 0; i < 2; ++i){	//for ea sub equation SIZE
			subtotal = 1;
			eq[i] = new StringBuilder();
			for (int j = 0; j < size; ++j){
				if (j != i){
					if (independent[j] != 0){
						eq[i].append("(x-" +  independent[j] + ")");
					}
					else{
						eq[i].append("(x)");
					}
					polynomials.add(-independent[j]);
					subtotal *= (independent[i]-independent[j]);
				}
			}
			subtotal = dependent[i]/subtotal;
			subtotal = Math.rint((subtotal * 1000))/1000;
			polynomials.add(0, subtotal);
			eq[i].insert(0, subtotal);
			if (i < size-1 ){
				eq[i].append(" + ");
			}
			System.out.println(eq[i]);//print
			segmentPoly = simplify(polynomials);
			/*			for (Double temp: segmentPoly)
				System.out.println("segmentPoly: " + temp);*/
			for (int l = 0; l < segmentPoly.size(); ++l){
				if (condensed.size() < segmentPoly.size()){
					System.out.println("size++");
					condensed.add(0.0);
				}
				condensed.set(l, condensed.get(l) + segmentPoly.get(l));
				/*				System.out.println("segment: " + segmentPoly.get(l));
				System.out.println("condensed: " + condensed.get(l));*/
			}
			polynomials.clear();
		}
		for (Double temp: condensed)
			System.out.println("condensed: " + temp);
		System.out.println("\n");
	}
	public static ArrayList<Double> simplify(ArrayList<Double> polynomials){
		/*		for (int l = 0; l < polynomials.size(); ++l){
			System.out.println("param: " + polynomials.get(l));
		}*/
		ArrayList<Double> simplified = new ArrayList<Double>();
		simplified.add(polynomials.get(1));
		simplified.add(1.0);
		//	double temp2 = 0;

		for (int i = 2; i < polynomials.size(); ++i){
			simplified.add(0, 0.0);	//shift
			/*			for (Double temp: simplified){
				System.out.println(i + " curr list: " + temp);
			}*/
			for (int j = 1; j < simplified.size(); ++j){
				//				System.out.println((j-1) + "Multiplying values: " + polynomials.get(i) + " * " +  simplified.get(j) + " + " + simplified.get(j-1));
				//temp2 = (polynomials.get(i) * simplified.get(j)) + simplified.get(j-1);
				simplified.set(j-1, (polynomials.get(i) * simplified.get(j)) + simplified.get(j-1));
			}
			/*			for (Double temp: simplified){
				System.out.println("after mod: " + temp);
			}*/
		}
		for (int k = 0; k < simplified.size(); ++k){
			simplified.set(k, polynomials.get(0)*simplified.get(k));
		}
		return simplified;
	}
	/**
	 * Same formulation as {@link lagrange} but prints out numerical calculations rather than solving.
	 * @param independent - input x
	 * @param dependent - input y
	 * @param size - actual size of array
	 */
	public static void lagrangeExpanded(double[] independent, double[] dependent, int size){
		String[] eq = new String[size];

		for (int i = 0; i < size; ++i){	//for ea sub equation
			eq[i] = ""; 
			eq[i] += dependent[i]; 	//store coeff
			for (int j = 0; j < size; ++j){
				if (j != i){
					if (independent[j] != 0){
						eq[i] += "(x-" +  independent[j] + ")";
					}
					else{
						eq[i] += "(x)";
					}
				}
			}
			eq[i] += "/";
			for (int k = 0; k < size; ++k){
				if (k != i){
					if (independent[k] != 0){
						eq[i] += "(" + independent[i] +  "-" +  independent[k] + ")";
					}
					else{
						eq[i] += "(" + independent[i] + ")";
					}
				}
			}
			if (i < size-1){
				eq[i] += " + ";
			}
			System.out.print(eq[i]);
		}
		System.out.println();
	}

	//takes in an arraylist<double>
	public class Polynomial{
		ArrayList<Double> polynomials;
		public Polynomial(ArrayList<Double> polynomials){
			this.polynomials = polynomials;
		}
		//where [0] is coeff, [1-(size-1)] are polynomials all of first power and x-1
		public void evaluate(){
			ArrayList<Double> simplified = new ArrayList<Double>();
			simplified.add(1.0);
			simplified.add(polynomials.get(1));
			for (int i = 2; i < polynomials.size(); ++i){
				simplified.add(0, 0.0);	//shift
				for (int j = 0; j < simplified.size(); ++j){
					simplified.set(j, simplified.get(1) * polynomials.get(i)); 
				}
			}
			for (int k = 0; k < simplified.size(); ++k){
				System.out.println(simplified.get(k));
			}
		}
	}
}