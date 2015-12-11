/**
 * Copyright 2010 Jörn Franke Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. 
 */
package org.allen.temporalintervalrelationships;


import static org.junit.Assert.*;

import org.allen.temporalintervalrelationships.Constraint;
import org.allen.temporalintervalrelationships.ConstraintNetwork;
import org.allen.temporalintervalrelationships.Node;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jörn Franke <jornfranke@gmail.com>
 *
 */
public class TestConstraintNetwork {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/*
	 * Test Example of Consistent network
	 * A STARTS B
	 * A CONTAINS C
	 *
	 */
	
	@Test
	public void testPathConsistency1() throws Exception {
		ConstraintNetwork<String> myConstraintNetwork = new ConstraintNetwork<String>();
		Node<String> nodeA = new Node<String>("A");
		myConstraintNetwork.addNode(nodeA);
		Node<String> nodeB = new Node<String>("B");
		myConstraintNetwork.addNode(nodeB);
		Node<String> nodeC = new Node<String>("C");
		myConstraintNetwork.addNode(nodeC);
		Constraint<String> constraintAB = new Constraint<String> (nodeA,nodeB,ConstraintNetwork.bin_starts);
		myConstraintNetwork.addConstraint(constraintAB);
		Constraint<String> constraintAC = new Constraint<String> (nodeA,nodeC,ConstraintNetwork.bin_contains);
		myConstraintNetwork.addConstraint(constraintAC);
		assertEquals(myConstraintNetwork.pathConsistency(),true);	
	}
	
	/*
	 * Test Example inconsistent network
	 * A EQUALS B
	 * B EQUALS C
	 * C EQUALS D
	 * A OVERLAPS D
	 * 
	 */
	
	@Test
	public void testPathConsistency2() throws Exception {
		ConstraintNetwork<String> myConstraintNetwork = new ConstraintNetwork<String>();
		Node<String> nodeA = new Node<String>("A");
		myConstraintNetwork.addNode(nodeA);
		Node<String> nodeB = new Node<String>("B");
		myConstraintNetwork.addNode(nodeB);
		Node<String> nodeC = new Node<String>("C");
		myConstraintNetwork.addNode(nodeC);
		Node<String> nodeD = new Node<String>("D");
		myConstraintNetwork.addNode(nodeD);
		Constraint<String> constraintAB = new Constraint<String> (nodeA,nodeB,ConstraintNetwork.bin_equals);
		myConstraintNetwork.addConstraint(constraintAB);
		Constraint<String> constraintBC = new Constraint<String> (nodeB,nodeC,ConstraintNetwork.bin_equals);
		myConstraintNetwork.addConstraint(constraintBC);
		Constraint<String> constraintCD = new Constraint<String> (nodeC,nodeD,ConstraintNetwork.bin_equals);
		myConstraintNetwork.addConstraint(constraintCD);
		assertEquals(myConstraintNetwork.pathConsistency(),true);	
		Constraint<String> constraintAD = new Constraint<String> (nodeA,nodeD,ConstraintNetwork.bin_overlaps);
		myConstraintNetwork.addConstraint(constraintAD);
		assertEquals(myConstraintNetwork.pathConsistency(),false);		
	}

	/*
	 * Github Issue #1
	 * FIRST CASE
	 * 2 BEFORE 0
	 * 3 BEFORE 4
	 * 4 BEFORE 1
	 * 0 EQUALS 3
	 * 4 MEETS 2
	 * 5 FINISHES 1
	 * SECOND CASE
	 * REMOVE 4 BEFORE 1
	 * 
	 */
	
	@Test
	public void testPathConsistency_GithubIssue1() throws Exception {
		ConstraintNetwork<String> myConstraintNetwork = new ConstraintNetwork<String>();
		Node<String> node0 = new Node<String>("0");
		myConstraintNetwork.addNode(node0);
		Node<String> node1 = new Node<String>("1");
		myConstraintNetwork.addNode(node1);
		Node<String> node2 = new Node<String>("2");
		myConstraintNetwork.addNode(node2);
		Node<String> node3 = new Node<String>("3");
		myConstraintNetwork.addNode(node3);
		Node<String> node4 = new Node<String>("4");
		myConstraintNetwork.addNode(node4);
		Node<String> node5 = new Node<String>("5");
		myConstraintNetwork.addNode(node5);
		Constraint<String> constraint20 = new Constraint<String> (node2,node0,ConstraintNetwork.bin_before);
		myConstraintNetwork.addConstraint(constraint20);
		Constraint<String> constraint34 = new Constraint<String> (node3,node4,ConstraintNetwork.bin_before);
		myConstraintNetwork.addConstraint(constraint34);
		Constraint<String> constraint41 = new Constraint<String> (node4,node1,ConstraintNetwork.bin_before);
		myConstraintNetwork.addConstraint(constraint41);
		Constraint<String> constraint03 = new Constraint<String> (node0,node3,ConstraintNetwork.bin_equals);
		myConstraintNetwork.addConstraint(constraint03);
		Constraint<String> constraint42 = new Constraint<String> (node4,node2,ConstraintNetwork.bin_meets);
		myConstraintNetwork.addConstraint(constraint42);
		Constraint<String> constraint51 = new Constraint<String> (node4,node2,ConstraintNetwork.bin_finishes);
		myConstraintNetwork.addConstraint(constraint51);
		assertEquals(myConstraintNetwork.pathConsistency(),false);	
		assertEquals(myConstraintNetwork.removeConstraint(constraint41),true);	
		assertEquals(myConstraintNetwork.pathConsistency(),false);	
	}

}
