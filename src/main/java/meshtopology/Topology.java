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

import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * @author Niccolo` Tubini
 *
 */
public abstract class Topology {

	protected Map<Integer, Double[]> verticesCoordinates;

	protected Map<Integer, Integer[]> elementsVertices;

	protected Map<Integer, Integer[]> borderEdgesVertices;
	
	protected Map<Integer, Integer> borderEdgesLabel;

	protected boolean checkData = false;
	
	protected Map<Integer, Integer> ll;
	
	protected Map<Integer, Integer> rr;
	
	protected Map<Integer, Integer> edgeBoundaryBCType;
	
	protected Map<Integer, Integer> edgeBoundaryBCValue;
	
	protected Map<Integer, Integer[]> gamma_j;
	
	protected Map<Integer, ArrayList<Integer>> s_i;
	
	protected Map<Integer, ArrayList<Integer>> p;

	protected static int[] temp_edgeExtreme = new int[2];
	
	public abstract void defineTopology();
	
	
	
	public void set(Map<Integer, Double[]> verticesCoordinates, Map<Integer, Integer[]> elementsVertices,
			Map<Integer, Integer[]> borderEdgesVertices, Map<Integer, Integer> borderEdgesLabel,  boolean checkData) {

		this.verticesCoordinates = verticesCoordinates;
		this.elementsVertices = elementsVertices;
		this.borderEdgesVertices = borderEdgesVertices;
		this.borderEdgesLabel = borderEdgesLabel;
		this.checkData = checkData;

	}
	
	
	public Map<Integer, Integer> getL(){
		return ll;
	}
	
	
	
	public Map<Integer, Integer> getR(){
		return rr;
	}

	
	
	public Map<Integer, Integer[]> getGammaj(){
		return gamma_j;
	}

	
	
	public Map<Integer, ArrayList<Integer>> getSi(){
		return s_i;
	}
	
	
	
	public Map<Integer, Integer> getEdgeBoundaryBCType(){
		return edgeBoundaryBCType;
	}
	
	
	
	
	public Map<Integer, Integer> getEdgeBoundaryBCValue(){
		return edgeBoundaryBCValue;
	}
	
	
	
	protected int[] sort2( int vertex0, int vertex1 ) {
		if(vertex0>vertex1) {
			temp_edgeExtreme[0] = vertex1;
			temp_edgeExtreme[1] = vertex0;
		} else {
			temp_edgeExtreme[0] = vertex0;
			temp_edgeExtreme[1] = vertex1;
		}
		return temp_edgeExtreme;
	}



	protected boolean isEqual(int[] edgeExtreme0, int[] edgeExtreme1) {
		boolean isEqual = true;
		for(int i=0; i<2; i++) {
			if(edgeExtreme0[i] != edgeExtreme1[i]) {
				isEqual = false;
				return isEqual;
			}
		}
		return isEqual;
	}



	protected int elementInVector( int[] edgeExtreme, int[][] temp_Gamma_j, int N_ins, int N_edges) {
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
	
	
	
}
