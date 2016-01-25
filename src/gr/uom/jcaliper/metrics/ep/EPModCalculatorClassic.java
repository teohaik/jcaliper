package gr.uom.jcaliper.metrics.ep;

import gr.uom.jcaliper.preferences.Preferences;
import gr.uom.jcaliper.system.CratEntity;
import gr.uom.jcaliper.system.EntitySet;
import gr.uom.jcaliper.system.HashedClass;

/**
 * Data structures designed to speed up Entity Placement calculation
 */
public class EPModCalculatorClassic extends EPCalculator {

	protected EPModCalculatorClassic(HashedClass prototype, EPMetric metric) {
		super(prototype, metric);
	}

	// EntitySets: overridden method for Welcome

	@Override
	protected void initializeWelcome() {
		EntitySet forbidden = new EntitySet();
		for (int entityId : this) {
			CratEntity entity = entities.getEntity(entityId);
			welcome.addAll(entity.getEntitySet());
			forbidden.addAll(entity.getForbiddenClassmates());
		}
		welcome.removeAll(this);
		welcome.addAll(externalRelatives);
		welcome.removeAll(forbidden);
	}

	// Overridden calculation methods

	@Override
	protected double calculateClassEvaluation(EntitySet theClass, EntitySet intRelatives,
			EntitySet extRelatives) {
		if (PRINT_DEBUG_INFO)
			System.out.format("\tEvaluating Class: %s\n", theClass);
		double innerDistancesTotal = innerDistancesTotal(theClass, intRelatives);
		double result = innerDistancesTotal / nEntities;
		// The EP_MOD modification
		// (for EP is: nExternal = nEntities - unboxed.size();)
		int nExternal = extRelatives.size();
		if (nExternal > 0)
			result *= nExternal / outerDistancesTotal(theClass, extRelatives, nExternal);
		if (PRINT_DEBUG_INFO)
			System.out.format("\t\tEvaluation = %8.6f\t(EP=%8.6f)\n", result,
					(theClass.size() > 0) ? (result * nEntities) / theClass.size() : 0);
		return result;
	}

	// Calculation methods for the classic model

	private static final long serialVersionUID = 1L;

	// Don't modify next line. Change the static value in class Preferences
	private static final boolean PRINT_DEBUG_INFO = Preferences.PRINT_DEBUG_INFO;

}
