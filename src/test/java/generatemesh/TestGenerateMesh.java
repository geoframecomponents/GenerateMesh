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

import org.junit.Test;

import readtriangularization.Readcsv;

public class TestGenerateMesh {

	@Test
	public void Test() throws Exception {

		//String fileName = "resources/input/square_with_subdomain_100.msh";
		String fileName = "resources/input/CartesianGrid1.csv";

		String splitter = ",";

		Readcsv reader = new Readcsv();
		reader.fileName = fileName;
		reader.splitter = splitter;
		reader.checkData = true;
		reader.process();
		
		GenerateMesh generateMesh = new GenerateMesh();
		generateMesh.verticesCoordinates = reader.verticesCoordinates;
		generateMesh.elementsVertices = reader.elementsVertices;
		generateMesh.borderEdgesVertices = reader.borderEdgesVertices;
		generateMesh.borderEdgesLabel = reader.borderEdgesLabel;
		generateMesh.checkData = true;
		generateMesh.meshType = "cartesian";
		generateMesh.geometryType = "EuclideanCartesian";
		generateMesh.process();
		
		
	}
}
