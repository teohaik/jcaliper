package gr.uom.jcaliper.executor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import gr.uom.jcaliper.explorer.CraCase;
import gr.uom.jcaliper.explorer.CratState;
import gr.uom.jcaliper.refactoring.ExtractClass;
import gr.uom.jcaliper.refactoring.LocalOptimum;
import gr.uom.jcaliper.refactoring.RefactoredClass;
import gr.uom.jcaliper.system.SystemClass;

/**
 * @author Panagiotis Kouros
 */
public class ExecutionSummary {

	private CraCase craCase;
	private String heuristicName = "";
	private String heuristicShortName = "";
	private boolean isRunning;
	private long statesEvaluated = -1;
	private long totalMoves = -1;
	private double runningTime = -1;
	private LocalOptimum refactored;
	private double evaluation = -1;
	private double improvement = -1;
	private int numOfClasses = -1;
	private int refactoringSteps = -1;

	/**
	 * @param craCase
	 */
	public ExecutionSummary(CraCase craCase) {
		super();
		this.craCase = craCase;
	}

	// Presentation methods

	public String getFullPresentation() {
		StringBuilder sb = new StringBuilder();
		CratState initial = craCase != null ? craCase.getInitial() : null;
		sb.append(String.format(" Heuristic : %s\n", heuristicName));
		sb.append(String.format(" Applied on %s\n", craCase != null ? craCase.getFullName() : ""));
		sb.append(String.format(" size: %d entities in %d classes\n",
				craCase != null ? craCase.getSystemTotalEntities() : 0,
						craCase != null ? craCase.getNumOfClasses() : 0));
		sb.append(String.format(" Evaluated %d states and carried out %d moves in %.2f secs\n",
				statesEvaluated, totalMoves, runningTime));
		sb.append(String.format(" Fitness before refactoring: %.6f\n",
				initial != null ? initial.getEvaluation() : 0));
		sb.append(String.format(" Fitness  after refactoring: %.6f (improvement %.1f%%)\n",
				evaluation, improvement));
		sb.append(String.format(" Refactored model has %d classes\n", numOfClasses));
		sb.append(String.format(" Refactoring consists of %d steps:\n", refactoringSteps));
		sb.append(refactored.getRefactoringDescription()).append("\n\n");
		sb.append(getRefactoredClasses());
		return sb.toString();
	}

	public String getRefactoringClusters(){
		StringBuilder sb = new StringBuilder();

		Graph<RefNode, String> graph = createRefactoringGraph();
		
		sb.append("--------------------------------- \n");
		sb.append("Refactoring Graph : \n");
		sb.append("--------------------------------- \n");
		sb.append(graph + "\n\n");
		sb.append("--------------------------------- \n");
		sb.append("Refactoring Clusters : \n");
		sb.append("--------------------------------- \n");
		for(Collection<String> cluster : getRefactoringClusters(graph)){

			sb.append(cluster+"\n");
		}
		
		return sb.toString();
	}
	
	class RefNode implements Serializable{

		private static final long serialVersionUID = 1L;
		long hash;
		String name;
		
		public RefNode(long anId, String aName){
			hash = anId;
			name = aName;
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			RefNode other = (RefNode)obj;
			if(this.hash == other.hash){
				return true;
			}
			if(this.name.equals(other.name)){
				return true;
			}
			return false;
		}
		
		@Override
		public String toString() {
			return hash+" - "+name;
		}
	}

	private List<Collection<String>> getRefactoringClusters(Graph<RefNode, String> graph) {
		WeakComponentClusterer<RefNode,String> clusterer = new WeakComponentClusterer<RefNode,String>();
		Set<Set<RefNode>> sets = clusterer.transform(graph);
		List<Collection<String>> refactoringClusters = new ArrayList<Collection<String>>();

		for(Set<RefNode> component : sets){
			Collection<String> componentEdges = new HashSet<String>();
			for(RefNode node : component){
				Collection<String> incidentEdges = graph.getIncidentEdges(node);
				componentEdges.addAll(incidentEdges);
			}
			refactoringClusters.add(componentEdges);
			/*
			 * Calculation of Initial Entity Placement for classes in each cluster
			 */
			Set<Long> clusterNodeIDs = new HashSet<Long>();
			for(RefNode node : component){
				clusterNodeIDs.add(node.hash);
			}
			double totalEP = craCase.getInitial().getTotalEntityPlacement(clusterNodeIDs);
			System.out.println("Total Initital EP of cluster "+component+" = "+totalEP);
		}
		return refactoringClusters;
	}

	private Graph<RefNode, String> createRefactoringGraph() {
		Graph<RefNode, String> graph = new UndirectedSparseGraph<RefNode, String>();
		ArrayList<ExtractClass> extractClassRefs = refactored.getExtractClassRefs();
		int counter = 1;
		for(ExtractClass extractClassRefactoring : extractClassRefs) {
			SystemClass sourceClass = extractClassRefactoring.getOrigin();
			RefactoredClass targetClass = extractClassRefactoring.getTarget();
			RefNode node1 = new RefNode(sourceClass.getHash(), sourceClass.getName());
			RefNode node2 = new RefNode(targetClass.getHash(), targetClass.getName());
			graph.addVertex(node1);
			graph.addVertex(node2);
			graph.addEdge("R"+counter, node1, node2);
			counter++;
		}
		return graph;
	}

	public String getOneLinePresentation() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("|%-10s|%8d|%6d|%6.2f|%9.6f|%5.1f%%|%3d|%3d|", heuristicShortName,
				statesEvaluated, totalMoves, runningTime, evaluation, improvement, numOfClasses,
				refactoringSteps));
		return sb.toString();
	}

	private String getRefactoredClasses() {
		return refactored.getRefactoredClasses();
	}

	// Getters and Setters

	/**
	 * @return the craCase
	 */
	public CraCase getCraCase() {
		return craCase;
	}

	/**
	 * @param craCase
	 *            the craCase to set
	 */
	public void setCraCase(CraCase craCase) {
		this.craCase = craCase;
	}

	/**
	 * @return the heuristicName
	 */
	public String getHeuristicName() {
		return heuristicName;
	}

	/**
	 * @param heuristicName
	 *            the heuristicName to set
	 */
	public void setHeuristicName(String heuristicName) {
		this.heuristicName = heuristicName;
	}

	/**
	 * @return the heuristicShortName
	 */
	public String getHeuristicShortName() {
		return heuristicShortName;
	}

	/**
	 * @param heuristicShortName
	 *            the heuristicShortName to set
	 */
	public void setHeuristicShortName(String heuristicShortName) {
		this.heuristicShortName = heuristicShortName;
	}

	/**
	 * @return the statesEvaluated
	 */
	public long getStatesEvaluated() {
		return statesEvaluated;
	}

	/**
	 * @param statesEvaluated
	 *            the statesEvaluated to set
	 */
	public void setStatesEvaluated(long statesEvaluated) {
		this.statesEvaluated = statesEvaluated;
	}

	/**
	 * @return the totalMoves
	 */
	public long getTotalMoves() {
		return totalMoves;
	}

	/**
	 * @param totalMoves
	 *            the totalMoves to set
	 */
	public void setTotalMoves(long totalMoves) {
		this.totalMoves = totalMoves;
	}

	/**
	 * @return the runningTime
	 */
	public double getRunningTime() {
		return runningTime;
	}

	/**
	 * @param runningTime
	 *            the runningTime to set
	 */
	public void setRunningTime(double runningTime) {
		this.runningTime = runningTime;
	}

	/**
	 * @return the refactored
	 */
	public LocalOptimum getRefactored() {
		return refactored;
	}

	/**
	 * @param refactored
	 *            the refactored to set
	 */
	public void setRefactored(LocalOptimum refactored) {
		this.refactored = refactored;
		evaluation = refactored.getEvaluation();
		numOfClasses = refactored.getRefClasses().size();
		refactoringSteps = refactored.getRefactoringSteps();
		double initialEvaluation = craCase.getInitial().getEvaluation();
		if (initialEvaluation == 0)
			improvement = 0.0;
		else
			improvement = 100.0 * (1.0 - (evaluation / initialEvaluation));
	}

	/**
	 * @return the evaluation
	 */
	public double getEvaluation() {
		return evaluation;
	}

	/**
	 * @return the improvement
	 */
	public double getImprovement() {
		return improvement;
	}

	/**
	 * @return the refactoringSteps
	 */
	public int getRefactoringSteps() {
		return refactoringSteps;
	}

	/**
	 * @return the numOfClasses
	 */
	public int getNumOfClasses() {
		return numOfClasses;
	}

	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @param isRunning
	 *            the isRunning to set
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

}
