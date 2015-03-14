/**
 *  This class represents a Node (Temporal Interval)
 *
 * Copyright 2010 Jörn Franke Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. 
 *
 */
package org.allen.temporalintervalrelationships;

/**
 * @author Jörn Franke <jornfranke@gmail.com>
 *
 */
public class Node<E> {
	E identifier;
	int allenId;
	
	public Node(E identifier) {
		this.identifier=identifier;
	}
	
	public E getIdentifier() {
		return this.identifier;
	}
	
	public int getAllenId() {
		return this.allenId;
	}
	
	public void setAllenId(int allenId) {
		this.allenId=allenId;
	}

}
