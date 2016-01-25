package gr.uom.jcaliper.refactoring;

import gr.uom.jcaliper.system.CratSystem;
import gr.uom.jcaliper.system.SystemClass;

import java.util.ArrayList;

/**
 * @author Panagiotis Kouros
 */
public class LocalOptimum {

	private ArrayList<RefactoredClass> refClasses;
	private ArrayList<ExtractClass> extractClassRefs;
	private ArrayList<MoveEntity> moveEntityRefs;
	private int refactoringSteps;
	private double evaluation;
	private long hash;
	private CratSystem system;

	public LocalOptimum(ArrayList<RefactoredClass> refClasses,
			ArrayList<ExtractClass> extractClassRefs, ArrayList<MoveEntity> moveEntityRefs,
			double evaluation, CratSystem system) {
		super();
		this.refClasses = refClasses;
		this.extractClassRefs = extractClassRefs;
		this.moveEntityRefs = moveEntityRefs;
		this.evaluation = evaluation;
		this.system = system;
		updateRefactoringSteps();
		updateHash();
	}

	// presentation methods

	public String getRefactoredClasses() {
		StringBuilder sb = new StringBuilder();
		sb.append("REFACTORED CLASS RESPONSIBILITY ASSIGNMENT:\n");
		String currentPackage = "";
		for (RefactoredClass refCl : refClasses)
			if (refCl.size() > 0) {
				SystemClass sysCl = refCl.getOrigin();
				String sysPackage = sysCl.getPackage().getName();
				if (!currentPackage.equals(sysPackage)) {
					currentPackage = sysPackage;
					sb.append("*** package '").append(currentPackage).append("'\n");
				}
				sb.append(String.format("    %s=%s\n", refCl.getName(),
						refCl.showNamesSetUnboxed(system)));
			}
		return sb.toString();
	}

	public String getRefactoringDescription() {
		StringBuilder sb = new StringBuilder();
		if (extractClassRefs.size() > 0)
			sb.append(String.format("\nEXTRACT CLASS REFACTORINGS (%d new classes, %d steps)",
					extractClassRefs.size(), refactoringSteps - moveEntityRefs.size()));
		String currentPackage = "";
		for (ExtractClass ref : extractClassRefs) {
			
			String refPackage = ref.getRefPackage().getName();
			if (!currentPackage.equals(refPackage)) {
				currentPackage = refPackage;
				sb.append("\n*** in package '").append(currentPackage).append("'\n");
			}
			sb.append(ref.describe(this)).append('\n');
		}
		if (moveEntityRefs.size() > 0)
			sb.append(String.format("\nMOVE ENTITY REFACTORINGS (%d steps)", moveEntityRefs.size()));
		currentPackage = "";
		for (MoveEntity ref : moveEntityRefs) {
			String refPackage = ref.getRefPackage().getName();
			if (!currentPackage.equals(refPackage)) {
				currentPackage = refPackage;
				sb.append("\n*** in package '").append(currentPackage).append("'\n");
			}
			sb.append(ref.describe(this)).append('\n');
		}
		if (sb.length() > 0)
			sb.setLength(sb.length() - 1); // remove last newline
		return sb.toString();
	}

	/**
	 * @return the refClasses
	 */
	public ArrayList<RefactoredClass> getRefClasses() {
		return refClasses;
	}

	/**
	 * @return the extractClassRefs
	 */
	public ArrayList<ExtractClass> getExtractClassRefs() {
		return extractClassRefs;
	}

	/**
	 * @return the moveEntityRefs
	 */
	public ArrayList<MoveEntity> getMoveEntityRefs() {
		return moveEntityRefs;
	}

	/**
	 * @return the refactoringSteps
	 */
	public int getRefactoringSteps() {
		return refactoringSteps;
	}

	/**
	 * @return the system
	 */
	public CratSystem getSystem() {
		return system;
	}

	/**
	 * @return the evaluation
	 */
	public double getEvaluation() {
		return evaluation;
	}

	private void updateRefactoringSteps() {
		refactoringSteps = extractClassRefs.size();
		for (ExtractClass cl : extractClassRefs)
			refactoringSteps += cl.getRefMoves();
		refactoringSteps += moveEntityRefs.size();
	}

	private void updateHash() {
		hash = 0;
		for (RefactoredClass refCl : refClasses)
			hash = ((hash << 3) - hash) + refCl.getHash(); // 7 * hash + classHash
	}

}
