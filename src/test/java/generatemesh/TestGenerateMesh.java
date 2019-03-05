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

import meshtopology.TopologyTriangularMesh;
import readtriangularization.Readmsh;

public class TestGenerateMesh {

	@Test
	public void Test() throws Exception {

		//String fileName = "resources/input/square_with_subdomain_100.msh";
		String fileName = "resources/input/square50.msh";

		String splitter = " ";

		Readmsh reader = new Readmsh();
		reader.fileName = fileName;
		reader.splitter = splitter;
		reader.checkData = false;
		reader.process();
		
		//GenerateMeshTriangles generateMesh = new GenerateMeshTriangles();
		GenerateTriangularMesh generateMesh = new GenerateTriangularMesh();
		generateMesh.verticesCoordinates = reader.verticesCoordinates;
		generateMesh.elementsVertices = reader.elementsVertices;
		generateMesh.borderEdgesVertices = reader.borderEdgesVertices;
		generateMesh.checkData = true;
		generateMesh.process();
		
		
	}
}
