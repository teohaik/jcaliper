package gr.uom.jcaliper.refactoring;

import gr.uom.jcaliper.system.CratPackage;
import gr.uom.jcaliper.system.EntitySet;
import gr.uom.jcaliper.system.SystemClass;

/**
 * @author Panagiotis Kouros
 */
public class ExtractClass {

	private SystemClass origin;
	private RefactoredClass target;
	private EntitySet extracted;
	protected CratPackage refPackage;
	protected int refMoves;
	protected double refGain;

	/**
	 * @param origin
	 * @param target
	 * @param extracted
	 */
	public ExtractClass(SystemClass origin, RefactoredClass target) {
		super();
		this.origin = origin;
		this.target = target;
		extracted = origin.intersection(target);
		refPackage = origin.getPackage();
		refMoves = extracted.size();
	}

	public String describe(LocalOptimum state) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Extract Class '%s' from '%s'\n\tby moving %d entit%s: %s",
				target.getName(), origin.getName(), refMoves, (refMoves > 1) ? "ies" : "y",
				extracted.showNamesUnboxed(state.getSystem())));
		return sb.toString();
	}
	
	public SystemClass getOrigin() {
		return origin;
	}
	
	public RefactoredClass getTarget() {
		return target;
	}

	/**
	 * @return the refPackage
	 */
	public CratPackage getRefPackage() {
		return refPackage;
	}

	/**
	 * @return the refMoves
	 */
	public int getRefMoves() {
		return refMoves;
	}
	
	public EntitySet getEntitiesExtracted() {
		return extracted;
	}
	
	@Override
	public int hashCode() {
		return 17 + this.origin.hashCode() 
			   + this.target.hashCode()
			   + this.refMoves
			   + this.extracted.hashCode()  ;
	}

}
