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

import java.util.Map;

public interface Geometry {

	public Map<Integer, Double[]> computeCentroid(Map<Integer, Integer[]> elementsVertices, Map<Integer, Double[]> verticesCoordinates);
		
	public Map<Integer, Double> computeEdgeLength(Map<Integer, Integer[]> elementsVertices, Map<Integer, Double[]> verticesCoordinates);
	
	public Map<Integer, Double[]> computeEdgeCentroid(Map<Integer, Integer[]> elementsVertices, Map<Integer, Double[]> verticesCoordinates);

	public Map<Integer, Double> computeArea(Map<Integer, Integer[]> elementsVertices, Map<Integer, Double[]> verticesCoordinates);
	
	public Map<Integer, Double[]> computeNormalVector(Map<Integer, Double[]> verticesCoordinates, Map<Integer, Integer[]> gamma_j,
			Map<Integer, Integer> l, Map<Integer, Integer> r);
	
	public Map<Integer, Double> computeCentroidsNormalDistance(Map<Integer, Double[]> elementsCentroidsCoordinates, Map<Integer, Integer> l, Map<Integer, Integer> r,
			Map<Integer, Double[]> edgeNormalVector, Map<Integer, Double[]> verticesCoordinates, Map<Integer, Integer[]> gamma_j, Map<Integer, Double> edgeLegth);
	

}
