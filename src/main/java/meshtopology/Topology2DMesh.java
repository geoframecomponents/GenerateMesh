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
 * FIXME: questa classe e` per generalizzare al caso di mesh 
 * con elementi tra loro diveri. Oltre a questa classe e` necessario
 * modificare il Readmsh (vedi ReadmshNew) e tutto il pacchetto geometry.
 * @author Niccolo` Tubini
 *
 */
public class Topology2DMesh {

	@In
	public Map<Integer, Double[]> verticesCoordinates;

	@In
	public Map<Integer, Integer[]> elementsVertices;

	@In
	public Map<Integer, Integer[]> borderEdgesVertices;

	@In
	public boolean checkData = false;

	static int[] temp_edgeExtreme = new int[2];


	@Execute
	public void process() throws IOException {

		long startTime = System.nanoTime();


		/*
		 * TOPOLOGY: Define the edges and the left and right element of the edge
		 */

		if(checkData == true) {
			System.out.println("\n   Sort extreme vertices of each edge in the element:");
			for(Integer element : elementsVertices.keySet()) {
				System.out.println( "      " + element + " : "+ sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1])[0]
						+ "-" + sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1])[1]);
				System.out.println( "      " + element + " : "+ sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2])[0]
						+ "-" + sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2])[1]);
				System.out.println( "      " + element + " : "+ sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[0])[0]
						+ "-" + sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[0])[1]);			
			}
		}


		// to check only a portion of the domain
		//		Map<Integer, Integer[]> test_elementsVertices = new HashMap<Integer, Integer[]>();
		//		test_elementsVertices.put(1, new Integer[] {1,2,5});
		//		test_elementsVertices.put(2, new Integer[] {1,5,4});
		//		test_elementsVertices.put(5, new Integer[] {4,5,8});

		/*
		 * TOPOLOGY OLD VERSION (arrays and matrices)
		 */
		int[] tmp_l = new int[3*elementsVertices.keySet().size()];
		int[] tmp_r = new int[3*elementsVertices.keySet().size()];
		int[] loc_ind_S = new int[3*elementsVertices.keySet().size()];
		int[][] tmp_S = new int[3*elementsVertices.keySet().size()][3];
		int[][] tmp_Gamma_j = new int[3*elementsVertices.keySet().size()][2];

		int N_ins = 0;
		for(Integer element : elementsVertices.keySet()) { // line 239
			//System.out.println("\n\n element: " + element);
			loc_ind_S[element] = 0;
			int j1 = elementInVector(sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1]),tmp_Gamma_j,N_ins, 3*elementsVertices.keySet().size());
			int j2 = elementInVector(sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2]),tmp_Gamma_j,N_ins, 3*elementsVertices.keySet().size());
			int j3 = elementInVector(sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[0]),tmp_Gamma_j,N_ins, 3*elementsVertices.keySet().size());

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
				tmp_Gamma_j[N_ins][0] = sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[0])[0];
				tmp_Gamma_j[N_ins][1] = sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[0])[1];
				tmp_S[element][loc_ind_S[element]] = N_ins;
				tmp_l[N_ins] = element;		
			} else {
				loc_ind_S[element] = loc_ind_S[element] +1;
				tmp_S[element][loc_ind_S[element]] = j3;
				tmp_r[j3] = element;

			}

		}

		System.out.println("N_ins: " + N_ins);
		if(checkData == true) {
			System.out.println("\n   Edges:");
			for(int i=1; i<=N_ins; i++) {
				System.out.println( "      edge " + i + " : " + tmp_Gamma_j[i][0] + "-" + tmp_Gamma_j[i][1] ); 
			}
			System.out.println("\n   Elements' edges:");
			for(Integer element : elementsVertices.keySet()) {
				System.out.println( "      element " + element + " : " + tmp_S[element][0] + "," + tmp_S[element][1] + "," + tmp_S[element][2] ); 
			}
			System.out.println("\n   Left and right element of each edge:");
			for(int i=1; i<=N_ins; i++) {
				System.out.println( "      edge " + i + " : left " + tmp_l[i] + " , right " + tmp_r[i]); 
			}

		}

		/*
		 * rioriento gli estremi dei lati
		 */
		for(Integer element : elementsVertices.keySet()) {
			//System.out.println(elementsVertices.get(element)[0] + " " + elementsVertices.get(element)[1] + " " + elementsVertices.get(element)[2]);
			for(int i=0; i<3; i++) {
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
					} else if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[2]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[2];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[0];
					}
				}

				if(tmp_r[edge] == 0) {
					if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[1] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[1]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[0];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[1];
					} else if (tmp_Gamma_j[edge][0] == elementsVertices.get(element)[1] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[2]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[1];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[2];
					} else if(tmp_Gamma_j[edge][0] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][1] == elementsVertices.get(element)[2] ||
							tmp_Gamma_j[edge][1] == elementsVertices.get(element)[0] & tmp_Gamma_j[edge][0] == elementsVertices.get(element)[2]) {
						tmp_Gamma_j[edge][0] = elementsVertices.get(element)[2];
						tmp_Gamma_j[edge][1] = elementsVertices.get(element)[0];
					}
				}
			}
		}

		System.out.println("Mesh created. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms.\n\n\n\n\n" );




		/*
		 * Move topology in the definitive array/matrix
		 */
		startTime = System.nanoTime();
		int N_j = N_ins;
		startTime = System.nanoTime();
		int[] l = new int[N_j+1];
		int[] r = new int[N_j+1];
		int[][] S_i = new int[N_j+1][3];
		int[][] Gamma_j = new int[N_j+1][2];
		for(int i=1; i<=N_j; i++) {
			l[i] = tmp_l[i];
			r[i] = tmp_r[i];
			S_i[i][0] = tmp_S[i][0];
			S_i[i][1] = tmp_S[i][1];
			S_i[i][2] = tmp_S[i][2];
			Gamma_j[i][0] = tmp_Gamma_j[i][0];
			Gamma_j[i][1] = tmp_Gamma_j[i][1];
		}
		System.out.println("Moved tmp in definitive array/matrix. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );
		/*
		 * Loop over definitive array/matrix
		 */
		System.out.println("Loop over array...");
		startTime = System.nanoTime();
		if(checkData == false) {
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

		} 
		System.out.println("Iterate over arrays. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );
		//System.out.println("Iterate over arrays. Elapsed time was " + (System.nanoTime()-startTime) + " ns" );


		/*
		 * Move topology in maps
		 */
		startTime = System.nanoTime();
		Map<Integer, Integer> ll = new HashMap<Integer, Integer>();
		Map<Integer, Integer> rr = new HashMap<Integer, Integer>();
		Map<Integer, Integer[]> gamma_j = new HashMap<Integer, Integer[]>();
		Map<Integer, ArrayList<Integer>> s_i = new HashMap<Integer, ArrayList<Integer>>();
		//List<Integer> temp_S = new ArrayList<Integer>();
		for(int i=1; i<=N_j; i++) {
			ll.put(i,  tmp_l[i]);
			rr.put(i, tmp_r[i]);
			s_i.put( i, new ArrayList<Integer>(Arrays.asList(tmp_S[i][0],tmp_S[i][1],tmp_S[i][2])) );
			//S_i[i][0] = tmp_S[i][0];
			//S_i[i][1] = tmp_S[i][1];
			//S_i[i][2] = tmp_S[i][2];
			gamma_j.put(i, new Integer[] {tmp_Gamma_j[i][0],tmp_Gamma_j[i][1]});
		}
		System.out.println("Moved tmp in definitive maps. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );
		//System.out.println("Moved tmp in definitive maps. Elapsed time was " + (System.nanoTime()-startTime) + " ns" );



		/*
		 * Loop over maps
		 */
		System.out.println("Loop over maps with FOREACH...");
		startTime = System.nanoTime();
		if(checkData == false) {
			System.out.println("\n\tEdges extremes:");
			for(Integer edge : gamma_j.keySet()) {
				//System.out.println( "\t\tedge " + edge + " : " + gamma_j.get(edge)[0] + "-" + gamma_j.get(edge)[1] ); 
			}
			System.out.println("\n\tEdges of each element:");
			for(Integer element : elementsVertices.keySet()) {
				//System.out.print( "\t\telement " + element + " : ");
				for(Integer edge : s_i.get(element)) {
					//System.out.print( edge + " "); 
				}
				//System.out.println("\n");
			}
			System.out.println("\n\tLeft and right element of each edge:");
			for(Integer edge : gamma_j.keySet()) {
				//System.out.println( "\t\tedge " + edge + " : left " + ll.get(edge) + " , right " + rr.get(edge)); 
			}
			System.out.println("\n\tComponents (x,y) of the normal vector of each edge:");
			for(Integer edge : gamma_j.keySet()) {
				//System.out.println( "\t\tedge " + edge + " : " + nn_v.get(edge)[0] + " , " + nn_v.get(edge)[1]); 
			}
			System.out.println("Iterate over maps. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );
			//System.out.println("Iterate over maps. Elapsed time was " + (System.nanoTime()-startTime) + " ns" );

		}

		System.out.println("Loop over maps with ITERATOR...");
		startTime = System.nanoTime();
		if(checkData == false) {
			System.out.println("\n\tEdges extremes:");
			Iterator<Integer> outerIterator;
			Iterator<Integer> innerIterator;
			outerIterator = gamma_j.keySet().iterator();
			while(outerIterator.hasNext()) {
				int edge = outerIterator.next();
				//System.out.println( "\t\tedge " + edge + " : " + gamma_j.get(edge)[0] + "-" + gamma_j.get(edge)[1] ); 
			}
			System.out.println("\n\tEdges of each element:");
			outerIterator = elementsVertices.keySet().iterator();
			while(outerIterator.hasNext()) {
				int element = outerIterator.next();
				//System.out.print( "\t\telement " + element + " : " );
				innerIterator = s_i.get(element).iterator();
				while(innerIterator.hasNext()) {
					innerIterator.next();
					//System.out.print( innerIterator.next() + " " ); 
				}
				//System.out.println("\n");	
			}
			System.out.println("\n\tLeft and right element of each edge:");
			outerIterator = gamma_j.keySet().iterator();
			while(outerIterator.hasNext()) {
				int edge = outerIterator.next();
				//System.out.println( "\t\tedge " + edge + " : left " + ll.get(edge) + " , right " + rr.get(edge)); 
			}
			System.out.println("\n\tComponents (x,y) of the normal vector of each edge:");
			outerIterator = gamma_j.keySet().iterator();
			while(outerIterator.hasNext()) {
				int edge = outerIterator.next();
				//System.out.println( "\t\tedge " + edge + " : left " + nn_v.get(edge)[0] + " , " + nn_v.get(edge)[1]); 
			}
			System.out.println("Iterate over maps with iterator. Elapsed time was " + (System.nanoTime()-startTime)/1000000 + " ms" );
			//System.out.println("Iterate over maps with iterator. Elapsed time was " + (System.nanoTime()-startTime) + " ns" );
		}


		//int N_j = N_ins;

		///////////////////////////////////////////
		////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////

//		Map<Integer, Integer> l = new HashMap<Integer, Integer>();
//		Map<Integer, Integer> r = new HashMap<Integer, Integer>();
//		Map<Integer, Integer[]> gamma_j = new HashMap<Integer, Integer[]>();
//		Map<Integer, ArrayList<Integer>> s_i = new HashMap<Integer, ArrayList<Integer>>();
//		List<Integer> temp_S = new ArrayList<Integer>();
//
//		int N_ins = 0; // ID del lato che viene inserito nella topologia
//		System.out.println(elementsVertices.keySet().size());
//		for(Integer element : elementsVertices.keySet()) { 
//			if(element==10000 || element==20000 || element==30000 || element==40000 || element==50000 || element==60000 ) {
//				System.out.println(element);
//				long endTime = System.nanoTime();
//				System.out.println("Mesh created. Elapsed time was " + (endTime-startTime)/1000000000 + " seconds" );
//			}
//
//			temp_S.clear();
//			int j1 = elementInVector(sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1]),gamma_j,N_ins);
//			int j2 = elementInVector(sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2]),gamma_j,N_ins);
//			int j3 = elementInVector(sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[0]),gamma_j,N_ins);
//
//			if(j1==0) {
//				N_ins = N_ins+1;
//				gamma_j.put(N_ins, new Integer[] {sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1])[0],
//						sort2(elementsVertices.get(element)[0],elementsVertices.get(element)[1])[1]} );
//				temp_S.add(N_ins);
//				l.put(N_ins, element);
//				r.put(N_ins, 0);
//			} else {
//				temp_S.add(j1); 
//				/*
//				 * FIXME: is it necessary?
//				 * l.put(j1, 0);
//				 */
//				r.put(j1,  element);
//			}
//
//			if(j2==0) {
//				N_ins = N_ins + 1;
//				gamma_j.put(N_ins, new Integer[] {sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2])[0],
//						sort2(elementsVertices.get(element)[1],elementsVertices.get(element)[2])[1]} );
//				temp_S.add(N_ins);
//				l.put(N_ins, element);
//				r.put(N_ins, 0);
//			} else {
//				temp_S.add(j2);
//				/**
//				 * FIXME: is it necessary?
//				 *l.put(j2, 0);
//				 */
//				r.put(j2,  element);
//			}
//
//			if(j3==0) {
//				N_ins = N_ins + 1;
//				gamma_j.put(N_ins, new Integer[] {sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[0])[0],
//						sort2(elementsVertices.get(element)[2],elementsVertices.get(element)[0])[1]} );
//				temp_S.add(N_ins);
//				l.put(N_ins, element);
//				r.put(N_ins, 0);
//			} else {
//				temp_S.add(j3);
//				/*
//				 * FIXME: is it necessary?
//				 * l.put(j3, 0); 
//				 */
//				r.put(j3,  element);
//			}
//
//			s_i.put(element, new ArrayList<Integer>(temp_S));
//
//		}
//
//		System.out.println("N_ins: " + N_ins);
//		if(checkData == true) {
//			System.out.println("\n   Edges:");
//			for(Integer edge : gamma_j.keySet()) {
//				//System.out.println( "      edge " + edge + " : " + gamma_j.get(edge)[0] + "-" + gamma_j.get(edge)[1] ); 
//			}
//			System.out.println("\n   Elements' edges:");
//			for(Integer element : elementsVertices.keySet()) {
//				System.out.println( "      element " + element );
//				for(Integer edge : s_i.get(element)) {
//					//System.out.println( "         " + edge ); 
//				}
//				System.out.println("\n   Left and right element of each edge:");
//				for(Integer edge : gamma_j.keySet()) {
//					//System.out.println( "      edge " + edge + " : left " + l.get(edge) + " , right " + r.get(edge)); 
//				}
//
//			}
//
//		}
		////////////////////////////////////////////
		///////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//long endTime = System.nanoTime();
		//System.out.println("Mesh created. Elapsed time was " + (endTime-startTime)/1000000000 + " seconds" );




	}//close @Execute





	//////////////////
	//////////////////
	//////////////////
	/*
	 * metodi per: Define the edges and the left and right element of the edge
	 */



	public int[] sort2( int vertex0, int vertex1 ) {
		//static int[] temp_edgeExtreme = new int[2];
		if(vertex0>vertex1) {
			temp_edgeExtreme[0] = vertex1;
			temp_edgeExtreme[1] = vertex0;
		} else {
			temp_edgeExtreme[0] = vertex0;
			temp_edgeExtreme[1] = vertex1;
		}
		return temp_edgeExtreme;
	}

	// changed int[] --> Integer[]
	public boolean isEqual(int[] edgeExtreme0, int[] edgeExtreme1) {
		boolean isEqual = true;
		for(int i=0; i<2; i++) {
			if(edgeExtreme0[i] != edgeExtreme1[i]) {
				isEqual = false;
				return isEqual;
			}
		}
		return isEqual;
	}

	/*
	 * OLD VERSION
	 *
	 */
	public int elementInVector( int[] edgeExtreme, int[][] temp_Gamma_j, int N_ins, int N_edges) {
		int tmp_elementInVector;
		if(N_ins==0) {
			tmp_elementInVector = 0;
			//return 0;
		} else {
			tmp_elementInVector = 0;
			for(int i=0; i<=N_ins ; i++) {
				if(isEqual(edgeExtreme, temp_Gamma_j[i]))
					tmp_elementInVector = i;
				//return i;
			}
		} 

		return tmp_elementInVector;
	}

	//new version
//		private int elementInVector(Integer[] sort2, Map<Integer, Integer[]> gamma_j, int N_ins) {
//			int tmp_elementInVector;
//			if(N_ins==0) {
//				tmp_elementInVector = 0;
//				//return 0;
//			} else {
//				tmp_elementInVector = 0;
//				for(Integer edge : gamma_j.keySet()) {
//					if(isEqual(sort2, gamma_j.get(edge)))
//						tmp_elementInVector = edge;
//					//return i;
//				}
//			} 
//	
//			return tmp_elementInVector;
//		}

	//////////////////
	//////////////////
	//////////////////

}