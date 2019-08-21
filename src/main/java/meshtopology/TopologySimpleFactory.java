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

import meshgeometry.EuclideanCartesianGeometry;
import meshgeometry.Geometry;

/**
 * 
 * @author Niccolo` Tubini
 *
 */
public class TopologySimpleFactory {

	/**
	 * Create a new Topology object.
	 * @param type name of the mesh
	 * @return topology
	 */
	public Topology createTopology (String type) {

		Topology topology = null;
		if(type.equalsIgnoreCase("triangular") ){
			topology = new TopologyTriangularMesh();
		} else if (type.equalsIgnoreCase("cartesian") ){
			topology = new TopologyCartesianMesh();
		} 
		
		return topology;
	}
}
