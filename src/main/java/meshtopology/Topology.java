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

	protected Map<Integer, ArrayList<Double>> verticesCoordinates;

	protected Map<Integer, ArrayList<Integer>> elementsVertices;

	protected Map<Integer, Integer[]> borderEdgesVertices;

	protected boolean checkData = false;
	
	protected Map<Integer, Integer> l;
	
	protected Map<Integer, Integer> r;
	
	protected Map<Integer, Integer[]> gamma_j;
	
	protected Map<Integer, ArrayList<Integer>> s_i;
	
	
	public abstract void defineTopology();
	
	
	
	public void set(Map<Integer, ArrayList<Double>> verticesCoordinates, Map<Integer, ArrayList<Integer>> elementsVertices,
			Map<Integer, Integer[]> borderEdgesVertices, boolean checkData) {
		
		this.verticesCoordinates = verticesCoordinates;
		this.elementsVertices = elementsVertices;
		this.borderEdgesVertices = borderEdgesVertices;
		
	}
	
	
	public Map<Integer, Integer> getL(){
		return l;
	}
	
	
	
	public Map<Integer, Integer> getR(){
		return r;
	}

	
	
	public Map<Integer, Integer[]> getGammaj(){
		return gamma_j;
	}

	
	
	public Map<Integer, ArrayList<Integer>> getSi(){
		return s_i;
	}
	
	
}
