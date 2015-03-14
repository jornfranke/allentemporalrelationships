/**
 * This class represents a constraint between two temporal interval relations
 * 
 * Copyright 2010 Jörn Franke Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. 
 * 
 */
package org.allen.temporalintervalrelationships;

/**
 * @author Jörn Franke <jornfranke@gmail.com>
 *
 */
public class Constraint<E> {
	private Node<E> sourceNode;
	private Node<E> destinationNode;
	private short constraints;
	
	public Constraint(Node<E> sourceNode, Node<E> destinationNode, short constraints) {
		this.sourceNode=sourceNode;
		this.destinationNode=destinationNode;
		this.constraints=constraints;
	}
	
	public Node<E> getSourceNode() {
		return this.sourceNode;
	}
	
	public Node<E> getDestinationNode() {
		return this.destinationNode;
	}
	
	public short getConstraints() {
		return this.constraints;
	}
}
