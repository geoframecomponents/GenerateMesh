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
 * @author Niccolo` Tubini
 *
 */
public class Readmsh {

	@In
	public String fileName = null; 

	@In
	public String splitter = " ";

	@In
	public boolean printFile = false;

	@In 
	public boolean checkData = false;


	@Out
	public Map<Integer, Double[]> verticesCoordinates = new HashMap<Integer, Double[]>();
	//public Map<Integer,Double> yCoordinates = new HashMap<Integer,Double>();

	@Out
	public Map<Integer, Integer[]> elementsVertices = new HashMap<Integer, Integer[]>();

	@Out
	public Map<Integer, Integer> elementsLabel = new HashMap<Integer, Integer>();

	@Out
	public Map<Integer, Integer[]> borderEdgesVertices = new HashMap<Integer, Integer[]>();

	@Out
	public Map<Integer, Integer> borderEdgesLabel = new HashMap<Integer, Integer>();

	public int nVertices =-999;
	public int nElements = -999;
	public int nBorderEdges = -999;

	int step = 0;
	/**
	 * @param args
	 * @throws IOException 
	 */
	@Execute
	public void process() throws IOException {

		if (step == 0) {
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
				String[] lineContent = line.split(splitter);

				if(iLine==0) {
					//.out.println("Line: " + iLine +"  " + lineContent[0] + " " +lineContent[1] + " " +lineContent[2] + "\n\n");
					nVertices = Integer.valueOf(lineContent[0]);
					nElements = Integer.valueOf(lineContent[1]);
					nBorderEdges = Integer.valueOf(lineContent[2]);
				} else if(iLine>0 && iLine<=nVertices) {
					verticesCoordinates.put( iLine, new Double[] { Double.valueOf(lineContent[0]),Double.valueOf(lineContent[1]) } ); 
					//yCoordinates.put(iLine,Double.valueOf(lineContent[1])); 
				} else if(iLine>nVertices && iLine<=nVertices+nElements) {
					elementsVertices.put(iLine-nVertices, new Integer[] { Integer.valueOf(lineContent[0]),Integer.valueOf(lineContent[1]), 
							Integer.valueOf(lineContent[2]) } ); 
					elementsLabel.put(iLine-nVertices, Integer.valueOf(lineContent[3])); 
				} else {
					if (Integer.valueOf(lineContent[2]) == -1) {

					} else { 
						borderEdgesVertices.put(iLine-(nVertices+nElements), new Integer[] { Integer.valueOf(lineContent[0]),Integer.valueOf(lineContent[1]) } );
						borderEdgesLabel.put(iLine-(nVertices+nElements), Integer.valueOf(lineContent[2])); 
					}
				}

				iLine ++;

			}

			br.close();
			long endTime = System.nanoTime();
			System.out.println("Reading file completed. Elapsed time was " + (endTime-startTime)/1000000000 + " seconds" );

			System.out.println("\n\t nVertices : " +nVertices);
			System.out.println("\t nElements : " +nElements);
			System.out.println("\t nBorderEdges : " +nBorderEdges);

			/*
			 * Check informations are correctly stored
			 */
			if(checkData == true) {

				System.out.println("\n   Vertices set :");
				for(Integer vertex : verticesCoordinates.keySet()) {
					System.out.println("      " + vertex + " : "+ verticesCoordinates.get(vertex)[0] + "," +verticesCoordinates.get(vertex)[1]);
				}

				System.out.println("\n   Elements' vertices :");
				for(Integer element : elementsVertices.keySet()) {
					System.out.println("      " + element + " : "+ elementsVertices.get(element)[0] + "," +elementsVertices.get(element)[1]
							+ "," +elementsVertices.get(element)[2] + " ; " + elementsLabel.get(element));
				}

				System.out.println("\n   Border edges :");
				for(Integer edge : borderEdgesVertices.keySet()) {
					System.out.println("      " + edge + " : "+ borderEdgesVertices.get(edge)[0] + "," +borderEdgesVertices.get(edge)[1]
							+ " ; " + borderEdgesLabel.get(edge));
				}


			}

			System.out.println("\nExit Readmsh.java\n\n\n");

		}

		step++;
	}// close @Execute

}
