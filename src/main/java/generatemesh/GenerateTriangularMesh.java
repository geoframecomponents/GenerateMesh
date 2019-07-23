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

package generatemesh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import meshgeometry.*;
import meshtopology.TopologyTriangularMesh;
import oms3.annotations.*;

/**
 * 
 * @author Niccolo` Tubini
 *
 */
public class GenerateTriangularMesh {

	@In
	public Map<Integer, Double[]> verticesCoordinates;

	@In
	public Map<Integer, Integer[]> elementsVertices;

	@In
	public Map<Integer, Integer[]> borderEdgesVertices;

	@In
	public Map<Integer, Integer> borderEdgesLabel;
	
	@In
	public boolean checkData = false;
	
	@In 
	public String geometryType;

	@Out
	public Map<Integer, Integer> l;
	
	@Out
	public Map<Integer, Integer> r;
	
	@Out
	public Map<Integer, Integer[]> gamma_j;
	
	@Out
	public Map<Integer, ArrayList<Integer>> s_i;
	
	@Out
	public Map<Integer, Double[]> elementsCentroidsCoordinates;
	
	@Out
	public Map<Integer, Double[]> edgesCentroidsCoordinates;
	
	@Out
	public Map<Integer, Double> elementsArea;
	
	@Out
	public Map<Integer, Double> edgesLength;
	
	@Out
	public Map<Integer, Double> delta_j;
	
	@Out
	public Map<Integer, Double[]> edgeNormalVector;
	
	@Out
	public Map<Integer, Integer> edgeBoundaryBCType;
	
	@Out
	public Map<Integer, Integer> edgeBoundaryBCValue;



	@Execute
	public void process() throws IOException {

		long startTime = System.nanoTime();
		
		elementsCentroidsCoordinates = new HashMap<Integer, Double[]>();
		elementsArea = new HashMap<Integer, Double>();
		edgesLength = new HashMap<Integer, Double>();
		edgesCentroidsCoordinates = new HashMap<Integer, Double[]>();
		delta_j = new HashMap<Integer, Double>();
		edgeNormalVector = new HashMap<Integer, Double[]>();
		edgeBoundaryBCType = new HashMap<Integer, Integer>();
		edgeBoundaryBCValue = new HashMap<Integer, Integer>();


		TopologyTriangularMesh topology = new TopologyTriangularMesh();
		GeometrySimpleFactory geometryFactory = new GeometrySimpleFactory();
		Geometry geometry = geometryFactory.createGeometry(geometryType);

		topology.set(verticesCoordinates, elementsVertices, borderEdgesVertices, borderEdgesLabel, checkData);
		topology.defineTopology();
		l = topology.getL();
		r = topology.getR();
		gamma_j = topology.getGammaj();
		//s_i = topology.getSi();
		s_i = new HashMap<Integer, ArrayList<Integer>>(topology.getSi());
		edgeBoundaryBCType = topology.getEdgeBoundaryBCType();
		edgeBoundaryBCValue = topology.getEdgeBoundaryBCValue();
		
		
		/*
		 * GEOMETRY: compute the coordinates of the centroid of each element
		 * FIXME: fare un loop sui vertici degli elementi per avere una cosa per generale.
		 * 		Fare delle classi non consente di avere una mesh con elementi misti.
		 * 		In caso la classe va messa nel pacchetto euclideangeometry
		 */
		elementsCentroidsCoordinates = geometry.computeCentroid(elementsVertices, verticesCoordinates);
		
		if(checkData == true) {
			System.out.println("\n\tElements' centroid:");
			for(Integer element : elementsCentroidsCoordinates.keySet()) {
				System.out.println( "\t\t" + element + " : "+ elementsCentroidsCoordinates.get(element)[0] 
						+ "," +elementsCentroidsCoordinates.get(element)[1] );
			}
		}

		
		
		/*
		 * GEOMETRY: compute the length of each element
		 *  lambda_j
		 */
		edgesLength = geometry.computeEdgeLength(gamma_j, verticesCoordinates);
		if(checkData == true) {
			System.out.println("\n\tEdges' length:");
			for(Integer edge : edgesLength.keySet()) {
				System.out.println( "\t\t" + edge + " : "+ edgesLength.get(edge) ); 
			}
		}
		
		
		/*
		 * GEOMETRY: compute the coordinates of the centroid of each edge
		 *  lambda_j
		 */
		edgesCentroidsCoordinates = geometry.computeEdgeCentroid(gamma_j, verticesCoordinates);
		
		if(checkData == true) {
			System.out.println("\n\tEdges' centroid:");
			for(Integer edge : edgesCentroidsCoordinates.keySet()) {
				System.out.println( "\t\t" + edge + " : "+ edgesCentroidsCoordinates.get(edge)[0] 
						+ "," +edgesCentroidsCoordinates.get(edge)[1] );
			}
		}

		
		/*
		 * GEOMETRY: compute the area of each element
		 * O'Rourke, J., Computational Geometry in C, page 26
		 */
		elementsArea = geometry.computeArea(elementsVertices, verticesCoordinates);

		if(checkData == true) {
			System.out.println("\n\tElements' area:");
			for(Integer element : elementsArea.keySet()) {
				System.out.println( "\t\t" + element + " : "+ elementsArea.get(element) ); 
			}
		}


		/*
		 * GEOMETRY: Compute the normal directions for each internal edge
		 */
		edgeNormalVector = geometry.computeNormalVector(verticesCoordinates, gamma_j, l, r);
		if(checkData == true) {
			System.out.println("\n\tComponents (x,y) of the normal vector of each edge:");
			for(Integer edge : edgeNormalVector.keySet()) {
				System.out.println( "\t\t" + edge + " : " + edgeNormalVector.get(edge)[0] + "," + edgeNormalVector.get(edge)[1] ); 
			}
		}

		
		/*
		 * GEOMETRY: compute the distance normal to the edge between the centers of two adjacent polygons
		 * 	
		 * delta_j
		 */
		delta_j = geometry.computeCentroidsNormalDistance(elementsCentroidsCoordinates, l, r, edgeNormalVector, verticesCoordinates, gamma_j, edgesLength);
		if(checkData == true) {
			System.out.println("\n\tDistance between the centroids of two adjacent polygon:");
			for(Integer edge : delta_j.keySet()) {
				System.out.println( "\t\t" + edge + " : " + delta_j.get(edge) ); 
			}
		}


		System.out.println("Computed topology and geometry");


	}//close @Execute

}