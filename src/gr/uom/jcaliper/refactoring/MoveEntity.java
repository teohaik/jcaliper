package gr.uom.jcaliper.refactoring;

import gr.uom.jcaliper.system.CratEntity;
import gr.uom.jcaliper.system.CratPackage;
import gr.uom.jcaliper.system.SystemClass;

/**
 * @author Panagiotis Kouros
 */
public class MoveEntity {

	private SystemClass origin;
	private RefactoredClass target;
	private CratEntity moved;
	protected CratPackage refPackage;
	protected int refMoves;
	protected double refGain;

	/**
	 * @param origin
	 * @param target
	 * @param moved
	 */
	public MoveEntity(CratEntity entity, SystemClass origin, RefactoredClass target) {
		super();
		this.origin = origin;
		this.target = target;
		moved = entity;
		refPackage = origin.getPackage();
		refMoves = 1;
	}

	public String describe(LocalOptimum state) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Move %s '%s' from '%s' to '%s'", moved.isAttribute() ? "field"
				: "method", moved.getName(), origin.getName(), target.getName()));
		return sb.toString();
	}

	/**
	 * @return the refPackage
	 */
	public CratPackage getRefPackage() {
		return refPackage;
	}

}
