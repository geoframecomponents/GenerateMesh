/*
 * GNU GPL v3 License
 *
 * Copyright 2019 Niccolo` Tubini
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package readtriangularization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import oms3.annotations.*;

/**
 * This class read a .msh file containing the triangularization of the computational domain.
 * The file .msh is the output of FreeFem++: https://freefem.org/
 * 
 * The description of the .msh file can be found at: 
 * 	https://doc.freefem.org/documentation/MeshGeneration/#data-structures-and-readwrite-statements-for-a-mesh
 * 
 * To read a text file:
 *  https://www.journaldev.com/867/java-read-text-file
 *  
 *  FIXME: questa classe e` per generalizzare al caso di una mesh con elementi misti
 * 
 * @author Niccolo` Tubini
 *
 */
public class ReadmshNew {

	@In
	public String fileName = null; 

	@In
	public String splitter = " ";

	@In
	public boolean printFile = false;
	
	@In 
	public boolean checkData = false;

	public int nVertices =-999;
	public int nElements = -999;
	public int nBorderEdges = -999;

	public Map<Integer, ArrayList<Double>> verticesCoordinates = new HashMap<Integer, ArrayList<Double>>();
	//public Map<Integer,Double> yCoordinates = new HashMap<Integer,Double>();
	public Map<Integer, ArrayList<Integer>> elementsVertices = new HashMap<Integer, ArrayList<Integer>>();
	public Map<Integer, Integer> elementsLabel = new HashMap<Integer, Integer>();
	public Map<Integer, Integer[]> borderEdgesVertices = new HashMap<Integer, Integer[]>();
	public Map<Integer, Integer> borderEdgesLabel = new HashMap<Integer, Integer>();

	private ArrayList<Double> tmp_Double = new ArrayList<Double>();
	private ArrayList<Integer> tmp_Integer = new ArrayList<Integer>();

	/**
	 * @param args
	 * @throws IOException 
	 */
	@Execute
	public void process() throws IOException {

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);

		String line;
		System.out.println("Opened the file: " + fileName + "\n\n");
		long startTime = System.nanoTime();
		if(printFile == true) {
			while((line = br.readLine()) != null){
				//process the line
				System.out.println(line);
			}
		}
		int iLine = 0;
		while((line = br.readLine()) != null){
			//process the line
			ArrayList<String> lineContent = new ArrayList<String>( Arrays.asList(line.split(splitter)) );

			if(iLine==0) {
				//.out.println("Line: " + iLine +"  " + lineContent[0] + " " +lineContent[1] + " " +lineContent[2] + "\n\n");
				nVertices = Integer.valueOf(lineContent.get(0));
				nElements = Integer.valueOf(lineContent.get(1));
				nBorderEdges = Integer.valueOf(lineContent.get(2));
			} else if(iLine>0 && iLine<=nVertices) {
				for(String element : lineContent) {
					tmp_Double.add(Double.valueOf(element));
				}
				tmp_Double.remove(tmp_Double.size()-1);
				verticesCoordinates.put( iLine, new ArrayList<Double>(tmp_Double) );
				tmp_Double.clear();
			} else if(iLine>nVertices && iLine<=nVertices+nElements) {
				for(String element : lineContent) {
					tmp_Integer.add(Integer.valueOf(element));
				}
				elementsLabel.put(iLine-nVertices, tmp_Integer.get(tmp_Integer.size()-1)); 
				tmp_Integer.remove(tmp_Integer.size()-1);
				elementsVertices.put( iLine-nVertices, new ArrayList<Integer>(tmp_Integer) );
				tmp_Integer.clear();			
			} else {
				for(String element : lineContent) {
					tmp_Integer.add(Integer.valueOf(element));
				}
				borderEdgesLabel.put(iLine-(nVertices+nElements), Integer.valueOf(tmp_Integer.get(tmp_Integer.size()-1))); 
				tmp_Integer.remove(tmp_Integer.size()-1);
				borderEdgesVertices.put( iLine-(nVertices+nElements), new Integer[] {tmp_Integer.get(0), tmp_Integer.get(1)} );
				tmp_Integer.clear();			
			}

			iLine ++;

		}

		br.close();
		long endTime = System.nanoTime();
		System.out.println("Reading file completed. Elapsed time was " + (endTime-startTime)/1000000000 + " seconds" );

		/*
		 * Check informations are correctly stored
		 */
		if(checkData == true) {
			System.out.println("\n\tnVertices = " +nVertices);
			System.out.println("\tnElements = " +nElements);
			System.out.println("\tnBorderEdges = " +nBorderEdges);

			System.out.println("\n\tVertices set :");
			for(Integer vertex : verticesCoordinates.keySet()) {
				System.out.print("\t" + vertex + " : ");
				for(Double coordinate : verticesCoordinates.get(vertex)) {
					System.out.print(coordinate + ", ");
				}
				System.out.println();
			}

			System.out.println("\n\tElements' vertices :");
			for(Integer element : elementsVertices.keySet()) {
				System.out.print("\t" + element + " : ");
				for(Integer edge : elementsVertices.get(element)) {
					System.out.print(edge + ", ");
				}
				System.out.print(elementsLabel.get(element) + "\n");
			}

			System.out.println("\n   Border edges :");
			for(Integer edge : borderEdgesVertices.keySet()) {
				System.out.println("      " + edge + " : "+ borderEdgesVertices.get(edge)[0] + "," +borderEdgesVertices.get(edge)[1]
						+ " ; " + borderEdgesLabel.get(edge));
			}
			
			System.out.println("Exit Readmsh.java\n\n\n");
		}
		

	}// close @Execute

}
