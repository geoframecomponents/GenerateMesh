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

package meshgeometry;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Niccolo` Tubini
 *
 */
public class EuclideanCartesianGeometry implements Geometry {

	private int count;
	private double tmpArea;
	private double tmpXC;
	private double tmpYC;
	private double tmpLength;
	private Double[] tmpNormalVector = new Double[2];
	private Map<Integer, Double[]> elementsCentroidsCoordinates;
	private Map<Integer, Double[]> edgesCentroidsCoordinates;
	private Map<Integer, Double> elementsArea;
	private Map<Integer, Double> edgeLength;
	private Map<Integer, Double[]> edgeNormalVector;
	private Map<Integer, Double> delta_j;


	/*
	 * compute the coordinates of the centroid of each element
	 * FIXME: fare un loop sui vertici degli elementi per avere una cosa per generale.
	 * 		Fare delle classi non consente di avere una mesh con elementi misti. -> Da testare
	 */
	@Override
	public Map<Integer, Double[]> computeCentroid(Map<Integer, Integer[]> elementsVertices,
			Map<Integer, Double[]> verticesCoordinates) {
		elementsCentroidsCoordinates = new HashMap<Integer, Double[]>();
		
		for(Integer element : elementsVertices.keySet()) {
			count = 0;
			tmpXC = 0.0;
			tmpYC = 0.0;
			for(Integer vertex : elementsVertices.get(element)) {
				
				tmpXC += verticesCoordinates.get(vertex)[0];
				tmpYC += verticesCoordinates.get(vertex)[1];
				count ++;
						
			}		
			
			tmpXC = tmpXC/count;
			tmpYC = tmpYC/count;
			elementsCentroidsCoordinates.put( element, new Double[] {tmpXC,tmpYC} );
		}
		
		return elementsCentroidsCoordinates;

	}



	@Override
	public Map<Integer, Double> computeEdgeLength(Map<Integer, Integer[]> edgesVertices,
			Map<Integer, Double[]> verticesCoordinates) {
		
		edgeLength = new HashMap<Integer, Double>();
		for(Integer edge : edgesVertices.keySet()) {
			tmpLength = edgeLength( verticesCoordinates.get(edgesVertices.get(edge)[0]), verticesCoordinates.get(edgesVertices.get(edge)[1]) );
//			tmpLength = Math.sqrt( Math.pow( verticesCoordinates.get(edgesVertices.get(edge)[0])[0] - verticesCoordinates.get(edgesVertices.get(edge)[1])[0],2 ) +
//					 Math.pow( verticesCoordinates.get(edgesVertices.get(edge)[0])[1] - verticesCoordinates.get(edgesVertices.get(edge)[1])[1],2 ) );
			edgeLength.put(edge, tmpLength);
		}
		
		return edgeLength;

	}
	
	
	
	@Override
	public Map<Integer, Double[]> computeEdgeCentroid(Map<Integer, Integer[]> edgesVertices,
			Map<Integer, Double[]> verticesCoordinates) {
		
		edgesCentroidsCoordinates = new HashMap<Integer, Double[]>();
		for(Integer edge : edgesVertices.keySet()) {
			tmpXC = ( verticesCoordinates.get(edgesVertices.get(edge)[0])[0] + verticesCoordinates.get(edgesVertices.get(edge)[1])[0] )/2.0;
			tmpYC = ( verticesCoordinates.get(edgesVertices.get(edge)[0])[1] + verticesCoordinates.get(edgesVertices.get(edge)[1])[1] )/2.0;

			edgesCentroidsCoordinates.put(edge, new Double[] {tmpXC,tmpYC});
		}
		
		return edgesCentroidsCoordinates;

	}
	
	
	/*
	 * compute the area of each element
	 * O'Rourke, J., Computational Geometry in C, page 26
	 */
	@Override
	public Map<Integer, Double> computeArea(Map<Integer, Integer[]> elementsVertices, Map<Integer, Double[]> verticesCoordinates) {

		elementsArea = new HashMap<Integer, Double>();
		for(Integer element : elementsVertices.keySet()) {
			tmpArea = 0.0;
			for(int iVertex=1; iVertex<elementsVertices.get(element).length-1; iVertex++) {
				tmpArea += triangleArea( verticesCoordinates.get(elementsVertices.get(element)[0]), verticesCoordinates.get(elementsVertices.get(element)[iVertex]),
						verticesCoordinates.get(elementsVertices.get(element)[iVertex+1]) );
			}
			elementsArea.put(element, tmpArea);
		}
		return elementsArea;
	}

	

	@Override
	public Map<Integer, Double[]> computeNormalVector(Map<Integer, Double[]> verticesCoordinates, Map<Integer, Integer[]> gamma_j,
														Map<Integer, Integer> l, Map<Integer, Integer> r) {
		
		edgeNormalVector = new HashMap<Integer, Double[]>();
		for(Integer edge : gamma_j.keySet()) {
			//if(l.get(edge) != 0 & r.get(edge) != 0) {
				tmpLength = edgeLength( verticesCoordinates.get(gamma_j.get(edge)[0]), verticesCoordinates.get(gamma_j.get(edge)[1]) );
				tmpNormalVector[0] = - ( verticesCoordinates.get(gamma_j.get(edge)[0])[1]-verticesCoordinates.get(gamma_j.get(edge)[1])[1] )/tmpLength;
				tmpNormalVector[1] = ( verticesCoordinates.get(gamma_j.get(edge)[0])[0]-verticesCoordinates.get(gamma_j.get(edge)[1])[0] )/tmpLength;
				edgeNormalVector.put( edge, tmpNormalVector.clone() );
			//} else {
				
			//}
		}
		
		return edgeNormalVector;
	}
	
	
	
	@Override
	public Map<Integer, Double> computeCentroidsNormalDistance(Map<Integer, Double[]> elementsCentroidsCoordinates, Map<Integer, Integer> l, Map<Integer, Integer> r,
			Map<Integer, Double[]> edgeNormalVector, Map<Integer, Double[]> verticesCoordinates, Map<Integer, Integer[]> gamma_j, Map<Integer, Double> edgeLegth) {
		delta_j = new HashMap<Integer, Double>();
		for(Integer edge : gamma_j.keySet()) {
			if(l.get(edge) != 0 & r.get(edge) != 0) {
				tmpLength = Math.abs( (elementsCentroidsCoordinates.get(l.get(edge))[0] - elementsCentroidsCoordinates.get(r.get(edge))[0] )*edgeNormalVector.get(edge)[0] +
						(elementsCentroidsCoordinates.get(l.get(edge))[1] - elementsCentroidsCoordinates.get(r.get(edge))[1] )*edgeNormalVector.get(edge)[1] );
			} else {
				tmpArea = triangleArea( verticesCoordinates.get(gamma_j.get(edge)[0]),  verticesCoordinates.get(gamma_j.get(edge)[1]),
										elementsCentroidsCoordinates.get(l.get(edge)) );
				tmpLength = 2*tmpArea/edgeLength.get(edge);
			}
			delta_j.put( edge, tmpLength );
		}
		return delta_j;
	}
	
	

	private Double edgeLength(Double[] vertex0, Double[] vertex1) {
		return Math.sqrt( Math.pow( vertex0[0] - vertex1[0],2 ) + Math.pow( vertex0[1] - vertex1[1],2 ) );
	}
	
	
	
	/*
	 * compute the area of a triangle
	 * O'Rourke, J., Computational Geometry in C, page 20
	 */
	private Double triangleArea(Double[] vertex0, Double[] vertex1, Double[] vertex2) {		
		return 0.5*(vertex0[0]* vertex1[1] - vertex0[1]*vertex1[0] +
				vertex0[1]* vertex2[0] - vertex0[0]*vertex2[1] +
				vertex1[0]* vertex2[1] - vertex1[1]*vertex2[0]);
	}



}
