/**
 * This class implements allen's path consistency algorithm for reasoning on temporal interval relationships. Find more information in his paper:
 * Allen, J. F. Maintaining Knowledge about Temporal Intervals Communications of the ACM, 1983, 26, 832-843

 * As Allen points out: the path consistency algorithm is not complete. However, this is not so important in practice.
 * A paper analyzing the completeness of the path consistency algorithm can be found here: 
 * Nebel, B. & Bürckert, H.-J. Reasoning about Temporal Relations: A Maximal Tractable Subclass of Allen's Interval Algebra Journal of the ACM, 1995, 42, 43-66
 * A complete version for all temporal relationships (which is significantly slower than the path consistency algorithm) will be part of a future version.
 * 
 * Please note that the paper contains some minor mistakes which have been corrected in this source code (see also comments). 
 *
 * Copyright 2010 Jörn Franke Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. 
 *
 */
package org.allen.temporalintervalrelationships;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author Jörn Franke <jornfranke@gmail.com>
 *
 */
public class ConstraintNetwork<E> {
	
	
	// store the constraints separately 
	ArrayList<Constraint<E>> modeledConstraints;
	
	// store the nodes separately
	ArrayList<Node<E>> modeledNodes;
	
	// store the constraint network as arraylist (most performant)
	private ArrayList<ArrayList<Short>> constraintnetwork;
	
	// store results from path consistency check after adding constraints
	private boolean previouslyInconsistent=false;	


	// representation of the constraints in binary format
	public final static short bin_before = 1; // 0000000000000001
	public final static short bin_after = 2;  // 0000000000000010
	public final static short bin_during = 4; // 0000000000000100
	public final static short bin_contains = 8; // 0000000000001000
	public final static short bin_overlaps = 16; // 0000000000010000
	public final static short bin_overlappedby = 32; // 0000000000100000
	public final static short bin_meets = 64; // 0000000001000000
	public final static short bin_metby = 128; // 0000000010000000
	public final static short bin_starts = 256;// 0000000100000000
	public final static short bin_startedby = 512; // 0000001000000000
	public final static short bin_finishes = 1024;  // 0000010000000000
	public final static short bin_finishedby = 2048;// 0000100000000000
	public final static short bin_equals = 4096;    // 0001000000000000
	public final static short bin_all = (short) (bin_before | bin_after | bin_during | bin_contains | bin_overlaps | bin_overlappedby | bin_meets | bin_metby | bin_starts | bin_startedby | bin_finishes | bin_finishedby | bin_equals);		  // 0001111111111111

	// last bit is used as a sign

	// representation of the constraints in string format 
	public final static String str_before = "before";
	public final static String str_after = "after";
	public final static String str_during = "during";
	public final static String str_contains = "contains";
	public final static String str_overlaps = "overlaps";
	public final static String str_overlappedby = "overlapped by";
	public final static String str_meets = "meets";
	public final static String str_metby = "met by";
	public final static String str_starts = "starts";
	public final static String str_startedby = "started by";
	public final static String str_finishes = "finishes";
	public final static String str_finishedby = "finished by";
	public final static String str_equals = "equals";
	
	// A corrected version of the transivity matrix described by Allen
	private final static short[][] transitivematrixshort = {
		// first row before
		{bin_before,bin_all,bin_before | bin_overlaps | bin_meets | bin_during | bin_starts, bin_before, bin_before, bin_before | bin_overlaps | bin_meets | bin_during | bin_starts, bin_before, bin_before| bin_overlaps | bin_meets | bin_during | bin_starts,  bin_before, bin_before, bin_before | bin_overlaps | bin_meets | bin_during | bin_starts, bin_before, bin_before},
	//	{"<","< > d di o oi m mi s si f fi e","< o m d s","<","<","< o m d s","<","< o m d s","<","<","< o m d s","<","<"}
		
		// second row after
		{bin_all,bin_after,bin_after | bin_overlappedby | bin_metby | bin_during | bin_finishes, bin_after, bin_after | bin_overlappedby | bin_metby | bin_during | bin_finishes,bin_after,bin_after | bin_overlappedby | bin_metby | bin_during | bin_finishes,bin_after,bin_after | bin_overlappedby | bin_metby | bin_during | bin_finishes,bin_after,bin_after,bin_after,bin_after },
	//	{"< > d di o oi m mi s si f fi e",">","> oi mi d f", ">", "> oi mi d f", ">", "> oi mi d f", ">", "> oi mi d f", ">", ">", ">", ">"}
		// third row during
		{bin_before, bin_after,bin_during,bin_all,bin_before | bin_overlaps | bin_meets | bin_during | bin_starts, bin_after | bin_overlappedby | bin_metby | bin_during | bin_finishes, bin_before, bin_after, bin_during, bin_after | bin_overlappedby | bin_metby | bin_during | bin_finishes, bin_during, bin_before | bin_overlaps | bin_meets | bin_during | bin_starts, bin_during},
//		{"<",">","d", "< > d di o oi m mi s si f fi e", "< o m d s", "> oi mi d f", "<", ">", "d", "> oi mi d f", "d", "< o m d s","d"},
		// fourth row contains
		// note: there seems to be a confusion in allens original table
		// bin_contains -> bin_during is in the original: o, oi, dur, con, e
		// it should be: o oi dur con e s si f fi
		{bin_before | bin_overlaps | bin_meets | bin_contains | bin_finishedby, bin_after | bin_overlappedby | bin_contains | bin_metby | bin_startedby, bin_overlaps | bin_overlappedby | bin_during | bin_contains | bin_equals | bin_starts| bin_startedby | bin_finishes | bin_finishedby, bin_contains, bin_overlaps | bin_contains | bin_finishedby,bin_overlappedby | bin_contains | bin_startedby,bin_overlaps | bin_contains | bin_finishedby,bin_overlappedby | bin_contains | bin_startedby,bin_contains | bin_finishedby | bin_overlaps, bin_contains, bin_contains | bin_startedby | bin_overlappedby, bin_contains,bin_contains},
//		{"< o m di fi","> oi mi di si","o oi dur con e s si f fi","di","o di fi","oi di si","o di fi","oi di si","di fi o","di","di si oi","di","di"}
		// fifth row overlaps 
		// note: there seems to be a confusion in allens original table
		// bin_overlaps -> bin_overlappedby is in the original: o oi dur con e
		// it should be: o oi dur con e s si f fi
		{bin_before,bin_after | bin_overlappedby | bin_contains | bin_metby | bin_startedby,bin_overlaps | bin_during | bin_starts, bin_before | bin_overlaps | bin_meets | bin_contains | bin_finishedby, bin_before | bin_overlaps | bin_meets, bin_overlaps | bin_overlappedby | bin_during | bin_contains | bin_equals | bin_starts| bin_startedby | bin_finishes | bin_finishedby, bin_before, bin_overlappedby | bin_contains | bin_startedby, bin_overlaps, bin_contains | bin_finishedby | bin_overlaps, bin_during | bin_starts | bin_overlaps, bin_before | bin_overlaps | bin_meets, bin_overlaps},
//		{"<","> oi di mi si","o d s","< o m di fi","< o m", "o oi dur con e s si f fi","<","oi di si","o","di fi o","d s o","< o m","o"},
		// six row overlapped by
		// note: there seems to be a mistake in allens original table
		// bin_overlappedby -> bin_overlaps is in the original: o oi dur con e
		// it should be: o oi dur con e s si f fi
		{bin_before | bin_overlaps | bin_meets | bin_contains | bin_finishedby, bin_after, bin_overlappedby | bin_during | bin_finishes, bin_after | bin_overlappedby | bin_metby | bin_contains | bin_startedby, bin_overlaps | bin_overlappedby | bin_during | bin_contains | bin_equals | bin_starts | bin_startedby | bin_finishes| bin_finishedby, bin_after | bin_overlappedby | bin_metby, bin_overlaps | bin_contains | bin_finishedby, bin_after, bin_overlappedby | bin_during | bin_finishes, bin_overlappedby | bin_after | bin_metby, bin_overlappedby, bin_overlappedby | bin_contains | bin_startedby, bin_overlappedby},
//		{"< o m di fi",">","oi d f","> oi mi di si","o oi d di e","> oi mi","o di fi",">","oi d f","oi > mi","oi","oi di si","oi"},
		// seventh row meets    
		{bin_before, bin_after | bin_overlappedby | bin_metby | bin_contains | bin_startedby, bin_overlaps | bin_during | bin_starts, bin_before, bin_before, bin_overlaps | bin_during | bin_starts, bin_before, bin_finishes | bin_finishedby | bin_equals, bin_meets, bin_meets, bin_during | bin_starts | bin_overlaps, bin_before, bin_meets},
//		{"<","> oi mi di si","o d s","<","<","o d s","<","f fi e","m","m","d s o","<","m"},
		// eights row metby 
		{bin_before | bin_overlaps | bin_meets | bin_contains | bin_finishedby, bin_after, bin_overlappedby | bin_during | bin_finishes, bin_after,bin_overlappedby | bin_during | bin_finishes, bin_after, bin_starts | bin_startedby | bin_equals, bin_after, bin_during | bin_finishes | bin_overlappedby, bin_after, bin_metby, bin_metby, bin_metby},
//		{"< o m di fi",">","oi d f",">","oi d f",">","s si e",">","d f oiX",">","mi","mi","mi"},
		// ninth row starts 
		{bin_before, bin_after, bin_during, bin_before | bin_overlaps | bin_meets | bin_contains | bin_finishedby,bin_before | bin_overlaps | bin_meets, bin_overlappedby | bin_during | bin_finishes, bin_before, bin_metby, bin_starts, bin_starts | bin_startedby | bin_equals, bin_during, bin_before | bin_meets | bin_overlaps, bin_starts},
//		{"<",">","d","< o m di fi","< o m","oi d f","<","mi","s","s si e","d","< m o","s"},
		// tenth row startedby 
		{bin_before | bin_overlaps | bin_meets | bin_contains | bin_finishedby, bin_after, bin_overlappedby | bin_during | bin_finishes, bin_contains, bin_overlaps | bin_contains | bin_finishedby, bin_overlappedby, bin_overlaps | bin_contains | bin_finishedby, bin_metby, bin_starts | bin_startedby | bin_equals, bin_startedby, bin_overlappedby, bin_contains, bin_startedby},
//		{"< o m di fi",">","oi d f","di","o di fi","oi","o di fi","mi","s si eX","si","oi","di","si"},
		// eleventh row finishes   
		{bin_before, bin_after, bin_during, bin_after | bin_overlappedby | bin_metby | bin_contains | bin_startedby,bin_overlaps | bin_during | bin_starts, bin_after | bin_overlappedby | bin_metby , bin_meets, bin_after, bin_during, bin_after | bin_overlappedby | bin_metby, bin_finishes, bin_finishes | bin_finishedby | bin_equals, bin_finishes},
//		{"<",">","d","> oi mi di si","o d s","> oi mi di","m",">","d","> oi mX","f","f fi e","f"},
		// twelfth row finishedby 
		{bin_before, bin_after | bin_overlappedby | bin_metby | bin_contains | bin_startedby, bin_overlaps | bin_during | bin_starts, bin_contains, bin_overlaps, bin_overlappedby | bin_contains | bin_startedby, bin_meets, bin_startedby | bin_overlappedby | bin_contains, bin_overlaps, bin_contains, bin_finishes | bin_finishedby | bin_equals, bin_finishedby, bin_finishedby},
//		{"<","> oi mi di si","o d s","di","o","oi di si","m","si oi di","o","di","f fi eX","fi","fi"},
		// thirteenth row equals 
		{bin_before,bin_after,bin_during,bin_contains,bin_overlaps,bin_overlappedby,bin_meets,bin_metby,bin_starts,bin_startedby,bin_finishes,bin_finishedby,bin_equals},
//		{"<",">","d","di","o","oi","m","mi","s","si","f","fi","e"}
			
	};
	
	/*
	 * Constructor creates an empty constraint network
	 * 
	 * 
	 */
	
	public ConstraintNetwork() {
		this.modeledNodes=new ArrayList<Node<E>>();
		this.modeledConstraints=new ArrayList<Constraint<E>>();
		this.constraintnetwork=new ArrayList<ArrayList<Short>>();
	}
	
	/*
	 * Adds a node to the list of modeled nodes constraint network
	 * 
	 * @param nodeAdd Node to be added to the list of modeled nodes and the constraint network
	 * 
	 * @return true if node has been successfully added, false, if not
	 * 
	 */
	
	public boolean addNode(Node<E> nodeAdd)  {
		// add to the list of modeled nodes
		if (!this.modeledNodes.contains(nodeAdd)) {
			this.modeledNodes.add(nodeAdd);
		} else {
			return false;
		}
		this.addNodeToConstraintNetwork(nodeAdd);
		return true;
	}
	
	
	/*
	 * Adds a node to the constraint network. By default all interval relationships are possible from this node to the others and the other
	 * way around. This has to be verified/corrected by the path consistency algorithm (@see #pathConsistency)
	 * 
	 * @param nodeAdd Node to be added to the constraint network
	 * 
	 */
	
	private void addNodeToConstraintNetwork(Node<E> nodeAdd) {
		// add to constraint network
		// create default relationship to all the other nodes
		Iterator<ArrayList<Short>> constraintnetworkIterator = this.constraintnetwork.iterator();
		while (constraintnetworkIterator.hasNext()==true) {
			ArrayList<Short> currentALShort = constraintnetworkIterator.next();
			currentALShort.add(bin_all); // default there can be any relationship between the old nodes and the newly added node
		}
		// add node to constraint network
		this.constraintnetwork.add(new ArrayList<Short>());
		nodeAdd.setAllenId(this.constraintnetwork.size()-1);
		// copy from previous (if exists)
		if (this.constraintnetwork.size()>1) {
			ArrayList<Short> previousALShort = this.constraintnetwork.get(0); // copy reference
			for (int i=0;i<previousALShort.size();i++)
				// add for all previous a shadow constraint
				this.constraintnetwork.get(this.constraintnetwork.size()-1).add(bin_all); // we do not know the references yet
			
		} else  this.constraintnetwork.get(this.constraintnetwork.size()-1).add(bin_equals); // to oneself it is always equals
		// add the previous...
		this.constraintnetwork.get(this.constraintnetwork.size()-1).set(this.constraintnetwork.size()-1, bin_equals); // to oneself is always equals

	}
	
	
	
	/*
	 * This method adds a constraint to the list of modeled constraints and the constraint network
	 * 
	 *  @param constraintAdd Constraint to be added to the list of modeled constraints and the constraint network
	 * 
	 *  @return true if constraint has been added, false, if not
	 * 
	 */
	
	public boolean addConstraint(Constraint<E> constraintAdd) {
		// add to the list of modeled constraints
		if (!this.modeledConstraints.contains(constraintAdd)) {
			// check if it contains the same nodes
			Iterator<Constraint<E>> modeledConstraintsIterator = this.modeledConstraints.iterator();
			while (modeledConstraintsIterator.hasNext()) {
				Constraint<E> currentConstraint = modeledConstraintsIterator.next();
				if (currentConstraint.getSourceNode().equals(constraintAdd.getSourceNode()) & (currentConstraint.getDestinationNode().equals(constraintAdd.getDestinationNode()))) {
					return false;
				}
				if (currentConstraint.getSourceNode().equals(constraintAdd.getDestinationNode()) & (currentConstraint.getDestinationNode().equals(constraintAdd.getSourceNode()))) {
					return false;
				}
			}
			// if the nodes defined in the constraint are not part of the network then do not add the constraint
			if (!this.modeledNodes.contains(constraintAdd.getSourceNode()) || (!this.modeledNodes.contains(constraintAdd.getDestinationNode()))) {
				return false;
			}
			this.modeledConstraints.add(constraintAdd);
		} else {
			return false;
		}
		this.addConstraintToConstraintNetwork(constraintAdd);
		// execute path consistency to have a correct constraint network
		if (this.pathConsistency()==false) this.previouslyInconsistent=true;
		return true;
	}
	
	/*
	 * Adds a constraint to the constraint network
	 * 
	 * @param constraintAdd constraint to be added to the constraint network
	 * 
	 */
	
	private void addConstraintToConstraintNetwork(Constraint<E> constraintAdd) {
		// add in constraint network
		
		int i = constraintAdd.getSourceNode().getAllenId();
		int j = constraintAdd.getDestinationNode().getAllenId();

		// set constraint
		this.constraintnetwork.get(i).set(j, constraintAdd.getConstraints());
		// set inverse of constraint
		this.constraintnetwork.get(j).set(i, this.inverseConstraintsShort(constraintAdd.getConstraints()));

	}

	
	/*
	 * Implements allen's path consistency algorithm. Please note that the algorithm may not be able to detect all inconsistent networks
	 * (see references above). Please note that another output of this algorithm is an updated constraint network (@see #getConstraintNetwork), with
	 * all possible constraints between nodes given the defined constraints
	 * 
	 * @return true, network is consistent and false, network is inconsistent
	 * 
	 */
	
	public boolean pathConsistency() {
		if (previouslyInconsistent==true) return false; // network has not changed and is still inconsistent
		if (this.modeledConstraints.size()==0) return true; // no constraint => nothing todo		
		ArrayList<Pair<Integer,Integer>> batchStack = new ArrayList<Pair<Integer,Integer>>();
		// cache to check if an entry is already on the stack => faster then looking up the whole stack each time
		ArrayList<ArrayList<Boolean>> stackEntries = new ArrayList<ArrayList<Boolean>>();
		for (int i=0;i<this.constraintnetwork.size();i++) {
			ArrayList<Boolean> currentStackEntryAL = new ArrayList<Boolean>();
			stackEntries.add(currentStackEntryAL);
			for (int j=0;j<this.constraintnetwork.size();j++){
				currentStackEntryAL.add(false); // initially there is nothing on the stack
			}
		}
		// end Caching
		// find at least one constraint to process
		Constraint<E> startConstraint =  this.modeledConstraints.get(modeledConstraints.size() - 1);
		// Add constraint to batchStack
		batchStack.add(new Pair<Integer,Integer>(startConstraint.getSourceNode().getAllenId(),startConstraint.getDestinationNode().getAllenId()));
		// Add stack entry
		stackEntries.get(startConstraint.getSourceNode().getAllenId()).set(startConstraint.getDestinationNode().getAllenId(),true);
		// Put inverse also on the stack
		stackEntries.get(startConstraint.getDestinationNode().getAllenId()).set(startConstraint.getSourceNode().getAllenId(), true);
		//
		int iterations=0;
		while (batchStack.size()>0) {
			iterations++;
			Pair<Integer,Integer> currentEdge = batchStack.get(0);
			batchStack.remove(0);
			// Remove stack entry
			stackEntries.get(currentEdge.getP1()).set(currentEdge.getP2(),false);
			
			
			int i = currentEdge.getP1().intValue();
			int j = currentEdge.getP2().intValue();
			// Browse through all nodes
			for (int k=0;k<this.constraintnetwork.size();k++) {
				// Preliminaries get the constraints 
				short ckj = this.constraintnetwork.get(k).get(j);
				short cki = this.constraintnetwork.get(k).get(i);
				short cik = this.constraintnetwork.get(i).get(k);
				short cjk = this.constraintnetwork.get(j).get(k);
				short cij = this.constraintnetwork.get(i).get(j);
				/////////////////////////////////////////First Part
				// get the constraints for k -> j
				// lookup in the transivity matrix (k,i) and (i,j)
				short ckiij = collectConstraintsShort(cki, cij);
				// the following line intersects the set of contraints in ckj and ckiij
				ckj = (short) (ckj & ckiij);
				// if no valid constraint is possible this means the network is inconsistent 
    			if (ckj==0) {
					return false;
				}
				// Please note a change of allens original algorithm here:
					// there seems to be a mistake in Allen's paper with respect to the algorithm
					// Original: cki subset of getConstraints(eki)
					// Proposed Modification: ckj subset of getConstraints(ekj)
					// Rationale: If the constraints have changed then we need to revisit this dependency again
					// note: bit operation detrect if it is a real subset or not
					//if (subsetConstraintsShort(ckj,this.staticmodelshort.get(k).get(j)==true) {
				short ckjtemp = this.constraintnetwork.get(k).get(j);
				if ((ckj!=ckjtemp && ((short)ckjtemp&ckj)==ckj)) {
				
						// if it already contains them then we do not need to add them again (they will be processed anyway)
						if ((stackEntries.get(k).get(j)==false)) {  //only add if not already there
							// Add stack entry
							stackEntries.get(k).set(j,true);
							Pair<Integer,Integer> updatePair =new Pair<Integer,Integer>(k,j); 
							batchStack.add(updatePair);
						}
						if ((stackEntries.get(j).get(k)==false)) {  //only add if not already there
						// Put inverse also on the stack
						stackEntries.get(j).set(k, true);
						Pair<Integer,Integer> updatePairInverse =new Pair<Integer,Integer>(j,k); 
						batchStack.add(updatePairInverse);							
						}
						// update constraint network
						this.constraintnetwork.get(k).set(j, ckj);			
						// we also update directly the inverse constraint between the nodes: ejk
						short iCon = this.inverseConstraintsShort(ckj);
						this.constraintnetwork.get(j).set(k, iCon);						
					}
				////////////////////////////////////////Second part
				// get the constraints for i -> k
				short cijjk = collectConstraintsShort(cij,cjk);
				// the following line is equivalent to an intersection of the set of constraints defined in cik and cijjk
				cik = (short) (cik & cijjk);
				// if no valid constraint is possible this means the network is inconsistent
				if (cik==0) {

					return false;
				}
					// there seems to be a mistake in Allen's paper with respect to the algorithm
					// Original: cik subset of getConstraints(eki)
					// Proposed Modification: cik subset of getConstraints(eik)
					// Rationale: If the constraints have changed then we need to revisit them again
					
					short ciktemp = this.constraintnetwork.get(i).get(k);
					if ((cik!=ciktemp && ((short)ciktemp&cik)==cik)) {
				
						if ((stackEntries.get(i).get(k)==false)) {  //only add if not already there
							// Add stack entry
							stackEntries.get(i).set(k,true);
							Pair<Integer,Integer> updatePair =new Pair<Integer,Integer>(i,k); 
							batchStack.add(updatePair);	
						}
						if ((stackEntries.get(k).get(i)==false)) {  //only add if not already there
							// Put inverse also on the stack
							stackEntries.get(k).set(i, true);
							Pair<Integer,Integer> updatePairInverse =new Pair<Integer,Integer>(k,i); 
							batchStack.add(updatePairInverse);	
						}
						// update constraint network
						this.constraintnetwork.get(i).set(k, cik);
						// And also the inverse
						Short iCon = this.inverseConstraintsShort(cik);
						this.constraintnetwork.get(k).set(i, iCon);
					

				}				
			
			}
		}
		return true;

	}
	
	/*
	 * This is one of the most costly functions in Allen's path consistency algorithm. For each constraint (between A and B) given the first parameter it looks 
	 * up for each constraint (between B and C) given in the second parameter in the transivity table the relationship between A and C.
	 * 
	 * @param c1 constraints between A and B
	 * @param c2 constraints between B and C
	 * 
	 * @return constraints between A and C
	 * 
	 * 
	 */
	
	public Short collectConstraintsShort(Short c1, Short c2) {
		short result=0;
		// for each entry (max 13)
		for (int i=0;i<14;i++) {
			// determine for c1 the position
			short c1select = (short)(1<<i); //new Double(Math.pow(2, i)).shortValue();
			// Check if c1 has a constraint
			if ((short) (c1 & c1select)==c1select) { // there is a constraint at this position
			for (int j=0;j<14;j++) {
				// determine for c2 the position
				short c2select = (short)(1<<j);//new Double(Math.pow(2, j)).shortValue();
				// check if c2 has a constraint
				if ((short) (c2 & c2select)==c2select) { // c2 has a constraint at this position
					// look up in transitivitymatrix
					short constraints = transitivematrixshort[i][j];
					// the following line means a union of the constraint set in result and constraints
					result = (short) (result |constraints);
					if (((short)result&bin_all)==bin_all) {
						return result; // all constraints are already in there we do not need further unions
					}
				}
			}
			}
		}
		return result;
	}
	
	/*
	 * This method inverts the constraints given in the parameter (e.g. the constraint "overlaps" becomes the constraint "overlapped by" and vice versa)
	 * 
	 *  @param c constraints (represented as  a short) to invert
	 * 
	 * @return inverted constraints
	 * 
	 */
	
	public Short inverseConstraintsShort(Short c){
		// Probably there is one clever bit operation which can do this in one line...
		short result=0;
		// test before
		if ((short)(c & bin_before)==bin_before) result = (short) (result | bin_after);
		// test after
		if ((short)(c & bin_after)==bin_after) result = (short) (result | bin_before);
		// test during
		if ((short)(c & bin_during)==bin_during) result = (short) (result | bin_contains);
		// test contains
		if ((short)(c & bin_contains)==bin_contains) result = (short) (result | bin_during);
		// test overlaps
		if ((short)(c & bin_overlaps)==bin_overlaps) result = (short) (result | bin_overlappedby);
		// test overlappedby
		if ((short)(c & bin_overlappedby)==bin_overlappedby) result = (short) (result | bin_overlaps);
		// test meets
		if ((short)(c & bin_meets)==bin_meets) result = (short) (result | bin_metby);
		// test metby
		if ((short)(c & bin_metby)==bin_metby) result = (short) (result | bin_meets);
		// test starts
		if ((short)(c & bin_starts)==bin_starts) result = (short) (result | bin_startedby);
		// test startedby
		if ((short)(c & bin_startedby)==bin_startedby) result = (short) (result | bin_starts);
		// test finishes
		if ((short)(c & bin_finishes)==bin_finishes) result = (short) (result | bin_finishedby);
		// test finished by
		if ((short)(c & bin_finishedby)==bin_finishedby) result = (short) (result | bin_finishes);
		// test equals 
		if ((short)(c & bin_equals)==bin_equals) result = (short) (result | bin_equals);
	
		return new Short(result);
	}
	
	/*
	 * This method returns the current constraint network (useful after it has been processed by @see #pathConsistency)
	 * 
	 * @return the current constraint network
	 * 
	 */
	
	public ArrayList<ArrayList<Short>> getConstraintNetwork () {
		return this.constraintnetwork;
	}
	
	/*
	 * This method returns the list of modeled constraints
	 * 
	 * @return list of modeled constraints
	 * 
	 */
	public ArrayList<Constraint<E>> getModeledConstraints() {
		return this.modeledConstraints;
	}
	
	/*
	 * This method returns the lsit of modeled nodes
	 * 
	 *  @return list of modeled nodes
	 * 
	 */
	
	public ArrayList<Node<E>> getModeledNodes() {
		return this.modeledNodes;
	}
	
	/*
	 * Returns a list of names of the constraints given in the set of constraints
	 * 
	 * @param set of constraints c
	 * 
	 * @return list of names of the constraints given in c
	 * 
	 * 
	 */
	
	public ArrayList<String> getConstraintStringFromConstraintShort(short c) {
		ArrayList<String> result = new ArrayList<String>();
		// test before
		if ((short)(c & bin_before)==bin_before) result.add(str_before);
		// test after
		if ((short)(c & bin_after)==bin_after) result.add(str_after);
		// test during
		if ((short)(c & bin_during)==bin_during) result.add(str_during);
		// test contains
		if ((short)(c & bin_contains)==bin_contains) result.add(str_contains);
		// test overlaps
		if ((short)(c & bin_overlaps)==bin_overlaps) result.add(str_overlaps);
		// test overlappedby
		if ((short)(c & bin_overlappedby)==bin_overlappedby) result.add(str_overlappedby);
		// test meets
		if ((short)(c & bin_meets)==bin_meets) result.add(str_meets);
		// test metby
		if ((short)(c & bin_metby)==bin_metby) result.add(str_metby);
		// test starts
		if ((short)(c & bin_starts)==bin_starts) result.add(str_starts);
		// test startedby
		if ((short)(c & bin_startedby)==bin_startedby) result.add(str_startedby);
		// test finishes
		if ((short)(c & bin_finishes)==bin_finishes) result.add(str_finishes);
		// test finished by
		if ((short)(c & bin_finishedby)==bin_finishedby) result.add(str_finishedby);
		// test equals 
		if ((short)(c & bin_equals)==bin_equals) result.add(str_equals);
		return result;
	}
}
