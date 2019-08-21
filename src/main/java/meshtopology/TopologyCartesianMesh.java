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

package meshtopology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import meshgeometry.*;
import oms3.annotations.*;

/**
 * 
 * @author Niccolo` Tubini
 *
 */
public class TopologyCartesianMesh extends Topology {

//	//@In
//	public Map<Integer, Double[]> verticesCoordinates;
//
//	//@In
//	public Map<Integer, Integer[]> elementsVertices;
//	
//	//@In
//	public Map<Integer, Integer[]> borderEdgesVertices;
//
//	//@In
//	public Map<Integer, Integer> borderEdgesLabel;
//	
//	//@In
//	public boolean checkData;
//
//	//@Out
//	public Map<Integer, Integer> ll;
//
//	//@Out
//	public Map<Integer, Integer> rr;
//
//	//@Out
//	public Map<Integer, Integer> edgeBoundaryBCType;
//	
//	public Map<Integer, Integer> edgeBoundaryBCValue;
//
//	//@Out
//	public Map<Integer, Integer[]> gamma_j;
//
//	//@Out
//	public Map<Integer, ArrayList<Integer>> s_i;
//
//	//@Out
//	public Map<Integer, ArrayList<Integer>> p;
//
//
//	static int[] temp_edgeExtreme = new int[2];
//
//
//
//	public void set(Map<Integer, Double[]> verticesCoordinates, Map<Integer, Integer[]> elementsVertices,
//			Map<Integer, Integer[]> borderEdgesVertices, Map<Integer, Integer> borderEdgesLabel,  boolean checkData) {
//
//		this.verticesCoordinates = verticesCoordinates;
//		this.elementsVertices = elementsVertices;
//		this.borderEdgesVertices = borderEdgesVertices;
//		this.borderEdgesLabel = borderEdgesLabel;
//		this.checkData = checkData;
//
//	}



	public void defineTopology() {

		long startTime = System.nanoTime();

		/*
		 * TOPOLOGY FOR TRIANGULAR MESH (arrays and matrices)
		 * Aim: define the edges and the left and right element of each edge
		 * 
		 * 	The grid is counterclockwise oriented
		 *  tmp_l array containing the left triangle of an edge
		 *  tmp_r array containing the right triangle of an edge
		 *  tmp_S matrix for each triangle the set of its edges
		 *  tmp_Gamma_j matrix containing for each edge its extremes. These are not oriented.
		 */
		int[] tmp_l = new int[4*elementsVertices.keySet().size()];
		int[] tmp_r = new int[4*elementsVertices.keySet().size()];
		int[] loc_ind_S = new int[4*elementsVertices.keySet().size()];
		int[][] tmp_S = new int[4*elementsVertices.keySet().size()][4];
		int[][] tmp_Gamma_j = new int[4*elementsVertices.keySet().size()][2];

		int N_ins = 0;
		for(Integer element : elementsVertices.keySet()) { // line 239
			//System.out.println("\n\n element: " + element);
			loc_ind_S[element] = 0;
			int j1 = elementInVector(sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1]),tmp_Gamma_j,N_ins, 4*elementsVertices.keySet().size());
			int j2 = elementInVector(sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2]),tmp_Gamma_j,N_ins, 4*elementsVertices.keySet().size());
			int j3 = elementInVector(sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[3]),tmp_Gamma_j,N_ins, 4*elementsVertices.keySet().size());
			int j4 = elementInVector(sort2(elementsVertices.get(element)[3],elementsVertices.get(element)[0]),tmp_Gamma_j,N_ins, 4*elementsVertices.keySet().size()); 
			
			if(j1==0) {
				N_ins = N_ins+1;
				loc_ind_S[element] = loc_ind_S[element];
				tmp_Gamma_j[N_ins][0] = sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1])[0];
				tmp_Gamma_j[N_ins][1] = sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1])[1];
				tmp_S[element][loc_ind_S[element]] = N_ins;
				tmp_l[N_ins] = element;		
			} else {
				loc_ind_S[element] = loc_ind_S[element];
				tmp_S[element][loc_ind_S[element]] = j1;
				tmp_r[j1] = element;

			}

			if(j2==0) {
				N_ins = N_ins + 1;
				loc_ind_S[element] = loc_ind_S[element] +1;
				tmp_Gamma_j[N_ins][0] = sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2])[0];
				tmp_Gamma_j[N_ins][1] = sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2])[1];
				tmp_S[element][loc_ind_S[element]] = N_ins;
				tmp_l[N_ins] = element;		
			} else {
				loc_ind_S[element] = loc_ind_S[element] +1;
				tmp_S[element][loc_ind_S[element]] = j2;
				tmp_r[j2] = element;

			}

			if(j3==0) {
				N_ins = N_ins + 1;
				loc_ind_S[element] = loc_ind_S[element] +1;
				tmp_Gamma_j[N_ins][0] = sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[3])[0];
				tmp_Gamma_j[N_ins][1] = sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[3])[1];
				tmp_S[element][loc_ind_S[element]] = N_ins;
				tmp_l[N_ins] = element;		
			} else {
				loc_ind_S[element] = loc_ind_S[element] +1;
				tmp_S[element][loc_ind_S[element]] = j3;
				tmp_r[j3] = element;

			}
			
			if(j4==0) {
				N_ins = N_ins + 1;
				loc_ind_S[element] = loc_ind_S[element] +1;
				tmp_Gamma_j[N_ins][0] = sort2(elementsVertices.get(element)[3],elementsVertices.get(element)[0])[0];
				tmp_Gamma_j[N_ins][1] = sort2(elementsVertices.get(element)[3],elementsVertices.get(element)[0])[1];
				tmp_S[element][loc_ind_S[element]] = N_ins;
				tmp_l[N_ins] = element;		
			} else {
				loc_ind_S[element] = loc_ind_S[element] +1;
				tmp_S[element][loc_ind_S[element]] = j4;
				tmp_r[j4] = element;

			}

		}
		//System.out.println("\n\nNumber of elements: " + elementsVertices.keySet().size());
		//System.out.println("Number of edges: " + N_ins);
		//		if(checkData == true) {
		//			System.out.println("\n   Edges:");
		//			for(int i=1; i<=N_ins; i++) {
		//				System.out.println( "      edge " + i + " : " + tmp_Gamma_j[i][0] + "-" + tmp_Gamma_j[i][1] ); 
		//			}
		//			System.out.println("\n   Elements' edges:");
		//			for(Integer element : elementsVertices.keySet()) {
		//				System.out.println( "      element " + element + " : " + tmp_S[element][0] + "," + tmp_S[element][1] + "," + tmp_S[element][2] ); 
		//			}
		//			System.out.println("\n   Left and right element of each edge:");
		//			for(int i=1; i<=N_ins; i++) {
		//				System.out.println( "      edge " + i + " : left " + tmp_l[i] + " , right " + tmp_r[i]); 
		//			}
		//
		//		}



		/*
		 * tmp_Gamma_j has to be modified in order to define the correct orientation of each edge
		 */
		for(Integer element : elementsVertices.keySet()) {
			//System.out.println(elementsVertices.get(element)[0] + " " + elementsVertices.get(element)[1] + " " + elementsVertices.get(element)[2]);
			for(int i=0; i<4; i++) {
				int edge = tmp_S[element][i];
				if(tmp_l[edge] == element) {
					//System.out.println(tmp_Gamma_j[edge][0] + " " + tmp_Gamma_j[edge][1]);
					if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[1] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[1]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[0];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[1];
					} else if (tmp_Gamma_j[edge][0] == elementsVertices.get(element)[1] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[1] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[2]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[1];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[2];
					} else if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[2] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[3] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[3]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[2];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[3];
					} else if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[3] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[0] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[3] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[0]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[3];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[0];
					}
				}

				/*
				 * FIXME: necessary?
				 */
				if(tmp_r[edge] == 0) {
					if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[1] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[1]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[0];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[1];
					} else if (tmp_Gamma_j[edge][0] == elementsVertices.get(element)[1] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[2]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[1];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[2];
					} else if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[2] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[3] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[3]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[2];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[3];
					} else if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[3] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[0] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[3] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[0]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[3];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[0];
					}
				}
			}
		}

		//System.out.println("\nMesh topology created. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms.\n\n\n\n\n" );




		/*
		 * Move topology in the definitive array/matrix
		 * FIXME: clean commented lines
		 */
		startTime = System.nanoTime();
		int N_j = N_ins;
		startTime = System.nanoTime();
		int[] l = new int[N_j+1];
		int[] r = new int[N_j+1];
		//int[][] S_i = new int[N_j+1][3];
		int[][] Gamma_j = new int[N_j+1][2];
		for(int i=1; i<=N_j; i++) {
			l[i] = tmp_l[i];
			r[i] = tmp_r[i];
			//S_i[i][0] = tmp_S[i][0];
			//S_i[i][1] = tmp_S[i][1];
			//S_i[i][2] = tmp_S[i][2];
			Gamma_j[i][0] = tmp_Gamma_j[i][0];
			Gamma_j[i][1] = tmp_Gamma_j[i][1];
		}
		int[][] S_i = new int[elementsVertices.keySet().size()][4];
		for(int i=1; i<elementsVertices.keySet().size(); i++) {
			S_i[i][0] = tmp_S[i][0];
			S_i[i][1] = tmp_S[i][1];
			S_i[i][2] = tmp_S[i][2];
			S_i[i][3] = tmp_S[i][3];
		}
		//System.out.println("\nMoved tmp in definitive array/matrix. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );


		/*
		 * FIXME: just to check the time required to traverse the 
		 * topology stored in primitive variables (array/matrix)
		 * Loop over definitive array/matrix
		 */
		if(checkData == true) {
			System.out.println("Loop over array...");
			startTime = System.nanoTime();
			System.out.println("\n\tEdges extremes:");
			for(int i=1; i<=N_ins; i++) {
				//System.out.println( "\t\tedge " + i + " : " + Gamma_j[i][0] + "-" + Gamma_j[i][1] ); 
			}
			System.out.println("\n\tEdges of each element:");
			for(Integer element : elementsVertices.keySet()) {
				//System.out.println( "\t\telement " + element + " : " + S_i[element][0] + "," + S_i[element][1] + "," + S_i[element][2] ); 
				for(int i=0; i++<3;) {

				}
			}
			System.out.println("\n\tLeft and right element of each edge:");
			for(int i=1; i<=N_ins; i++) {
				//System.out.println( "\t\tedge " + i + " : left " + l[i] + " , right " + r[i]); 
			}
			System.out.println("...elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );
			//System.out.println("...elapsed time was " + (System.nanoTime()-startTime) + " ns" );
		} 


		/*
		 * Move topology in maps
		 */
		startTime = System.nanoTime();
		ll = new HashMap<Integer, Integer>();
		rr = new HashMap<Integer, Integer>();
		gamma_j = new HashMap<Integer, Integer[]>();
		s_i = new HashMap<Integer, ArrayList<Integer>>();
		p = new HashMap<Integer, ArrayList<Integer>>(); 
		int[] tmp_p = new int[4];
		for(int i=1; i<=N_j; i++) {
			ll.put(i,  tmp_l[i]);
			rr.put(i, tmp_r[i]);
			//s_i.put( i, new ArrayList<Integer>(Arrays.asList(tmp_S[i][0],tmp_S[i][1],tmp_S[i][2])) );
			gamma_j.put(i, new Integer[] {tmp_Gamma_j[i][0],tmp_Gamma_j[i][1]});
		}
		for(int i=1; i<=elementsVertices.keySet().size(); i++) {
			s_i.put( i, new ArrayList<Integer>(Arrays.asList(tmp_S[i][0],tmp_S[i][1],tmp_S[i][2],tmp_S[i][3])) );
			for(int j=0; j<tmp_S[0].length; j++) {
				if(rr.get(tmp_S[i][j]) != i) {
					tmp_p[j] = rr.get(tmp_S[i][j]);
				} else {
					tmp_p[j] = ll.get(tmp_S[i][j]);
				}
			}
			p.put( i, new ArrayList<Integer>(Arrays.asList(tmp_p[0],tmp_p[1],tmp_p[2],tmp_p[3]) ) );
		}
		//System.out.println("\n\nMoved tmp in definitive maps. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );
		//System.out.println("\n\nMoved tmp in definitive maps. Elapsed time was " + (System.nanoTime()-startTime) + " ns" );
		int[] a = new int[2];
		int[] b = new int[2];
		
		edgeBoundaryBCType = new HashMap<Integer, Integer>();
		edgeBoundaryBCValue = new HashMap<Integer, Integer>();
		for(Integer edge : borderEdgesLabel.keySet()) {
			a = sort2(borderEdgesVertices.get(edge)[0], borderEdgesVertices.get(edge)[1]).clone();
			for(Integer edge1 : gamma_j.keySet()) {
				b = sort2(gamma_j.get(edge1)[0],gamma_j.get(edge1)[1]).clone();
				if( isEqual( a,b  ) ) {
					/*
					 * Tens of borderEdgesLabel are used to define the type of the boundary condition (edgeBoundaryBCType),
					 * the borderEdgesLabel are used to identify the value of the boundary condition (edgeBoundaryValue).
					 */
					edgeBoundaryBCType.put(edge1, borderEdgesLabel.get(edge)/10);
					edgeBoundaryBCValue.put(edge1, borderEdgesLabel.get(edge));
					break;
				}
			}
		}	
		
		if(checkData == true) {
		
		}


		/*
		 * Loop over maps to print the topology
		 */
		if(checkData == true) {
			System.out.println("\nTopology summary:");
			startTime = System.nanoTime();
			System.out.println("\n\tEdges extremes:");
			for(Integer edge : gamma_j.keySet()) {
				System.out.println( "\t\tedge " + edge + " : " + gamma_j.get(edge)[0] + "-" + gamma_j.get(edge)[1] ); 
			}
			System.out.println("\n\tEdges of each element:");
			for(Integer element : elementsVertices.keySet()) {
				System.out.print( "\t\telement " + element + " : ");
				for(Integer edge : s_i.get(element)) {
					System.out.print( edge + " "); 
				}
				System.out.println("\n");
			}
			System.out.println("\n\tLeft and right element of each edge:");
			for(Integer edge : gamma_j.keySet()) {
				System.out.println( "\t\tedge " + edge + " : left " + ll.get(edge) + " , right " + rr.get(edge)); 
			}
			System.out.println("\n\tBoundary labels:");
			for(Integer edge : edgeBoundaryBCType.keySet()) {
				System.out.println( "\t\tedge " + edge + " BC type " + edgeBoundaryBCType.get(edge) + " BC value ID " + edgeBoundaryBCValue.get(edge) ); 
			}
			System.out.println("\n\tElement neighbours:");
			for(Integer element : p.keySet()) {
				System.out.print( "\t\telement " + element + " : " ); 
				for(Integer edge : p.get(element)) {
					System.out.print( edge + " "); 
				}
				System.out.println("\n");
			}
			System.out.println("...elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );
			//System.out.println("...elapsed time was " + (System.nanoTime()-startTime) + " ns" );

		}

	}//close defineTopology


//	public Map<Integer, Integer> getL(){
//		return ll;
//	}
//
//
//
//	public Map<Integer, Integer> getR(){
//		return rr;
//	}
//
//
//
//	public Map<Integer, Integer[]> getGammaj(){
//		return gamma_j;
//	}
//
//
//
//	public Map<Integer, ArrayList<Integer>> getSi(){
//		return s_i;
//	}
//
//
//	public Map<Integer, Integer> getEdgeBoundaryBCType(){
//		return edgeBoundaryBCType;
//	}
//	
//	public Map<Integer, Integer> getEdgeBoundaryBCValue(){
//		return edgeBoundaryBCValue;
//	}
//
//	//////////////////
//	//////////////////
//	//////////////////
//	/*
//	 * metodi per: Define the edges and the left and right element of the edge
//	 */
//
//
//
//	private int[] sort2( int vertex0, int vertex1 ) {
//		if(vertex0>vertex1) {
//			temp_edgeExtreme[0] = vertex1;
//			temp_edgeExtreme[1] = vertex0;
//		} else {
//			temp_edgeExtreme[0] = vertex0;
//			temp_edgeExtreme[1] = vertex1;
//		}
//		return temp_edgeExtreme;
//	}
//
//
//
//	private boolean isEqual(int[] edgeExtreme0, int[] edgeExtreme1) {
//		boolean isEqual = true;
//		for(int i=0; i<2; i++) {
//			if(edgeExtreme0[i] != edgeExtreme1[i]) {
//				isEqual = false;
//				return isEqual;
//			}
//		}
//		return isEqual;
//	}
//
//
//
//	private int elementInVector( int[] edgeExtreme, int[][] temp_Gamma_j, int N_ins, int N_edges) {
//		int tmp_elementInVector;
//		if(N_ins==0) {
//			tmp_elementInVector = 0;
//			//return 0;
//		} else {
//			tmp_elementInVector = 0;
//			for(int i=0; i<=N_ins ; i++) {
//				if(isEqual(edgeExtreme, temp_Gamma_j[i]))
//					tmp_elementInVector = i;
//				//return i;
//			}
//		} 
//
//		return tmp_elementInVector;
//	}
//
//	//////////////////
//	//////////////////
//	//////////////////

}