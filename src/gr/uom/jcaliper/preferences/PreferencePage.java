﻿package gr.uom.jcaliper.preferences;

import gr.uom.jcaliper.plugin.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Panagiotis Kouros
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor useForSysHCSteepestEditor;
	private BooleanFieldEditor useForSysHCFirstChoiceEditor;
	private BooleanFieldEditor useForSysHCTabuSearchEditor;
	private BooleanFieldEditor useForSysHCTabuSearchDynEditor;
	private BooleanFieldEditor useForSysHCSimAnnealingEditor;
	private BooleanFieldEditor doPreoptimizeEditor;
	private BooleanFieldEditor logResultsEditor;
	private DirectoryFieldEditor logPathEditor;
	private FileFieldEditor logResultsFileEditor;
	private BooleanFieldEditor limitTimeEditor;
	private IntegerFieldEditor maxRunningTimeEditor;

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Class Responsibility Assignment Tool Preferences");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {

		useForSysHCSteepestEditor = new BooleanFieldEditor(
				Preferences.P_USE4SYS_HILL_CLIMBING_STEEPEST,
				"Use &Hill Climbing - Steepest Asc./Descent", getFieldEditorParent());
		addField(useForSysHCSteepestEditor);

		useForSysHCFirstChoiceEditor = new BooleanFieldEditor(
				Preferences.P_USE4SYS_HILL_CLIMBING_FIRST_CHOICE,
				"Use Hill Climbing - &First Choice", getFieldEditorParent());
		addField(useForSysHCFirstChoiceEditor);

		useForSysHCTabuSearchEditor = new BooleanFieldEditor(Preferences.P_USE4SYS_TABU_SEARCH,
				"Use &Tabu Search w Static Tenure", getFieldEditorParent());
		addField(useForSysHCTabuSearchEditor);

		useForSysHCTabuSearchDynEditor = new BooleanFieldEditor(
				Preferences.P_USE4SYS_TABU_SEARCH_DYNAMIC, "Use Tabu Search w &Dynamic Tenure",
				getFieldEditorParent());
		addField(useForSysHCTabuSearchDynEditor);

		useForSysHCSimAnnealingEditor = new BooleanFieldEditor(
				Preferences.P_USE4SYS_SIMULATED_ANNEALING, "Use &Simulated Annealing",
				getFieldEditorParent());
		addField(useForSysHCSimAnnealingEditor);

		doPreoptimizeEditor = new BooleanFieldEditor(Preferences.P_DO_PREOPTIMIZE,
				"&Pre-optimize at class/package level", getFieldEditorParent());
		addField(doPreoptimizeEditor);

		logResultsEditor = new BooleanFieldEditor(Preferences.P_LOG_RESULTS,
				"&Log searching moves and results", getFieldEditorParent());
		addField(logResultsEditor);

		logPathEditor = new DirectoryFieldEditor(Preferences.P_LOG_PATH, "Moves log path:",
				getFieldEditorParent());
		logPathEditor.setEnabled(logResultsEditor.getBooleanValue(), getFieldEditorParent());
		logPathEditor.setEmptyStringAllowed(false);
		addField(logPathEditor);

		logResultsFileEditor = new FileFieldEditor(Preferences.P_LOG_RESULTS_FILE,
				"Results log file:", getFieldEditorParent());
		logResultsFileEditor.setEnabled(logResultsEditor.getBooleanValue(), getFieldEditorParent());
		logResultsFileEditor.setEmptyStringAllowed(false);
		addField(logResultsFileEditor);

		limitTimeEditor = new BooleanFieldEditor(Preferences.P_SEARCH_LIMIT_TIME,
				"&Limit running time", getFieldEditorParent());
		addField(limitTimeEditor);

		maxRunningTimeEditor = new IntegerFieldEditor(Preferences.P_SEARCH_MAX_RUNNING_TIME,
				"Ma&x time per algorithm (sec)", getFieldEditorParent());
		maxRunningTimeEditor.setEnabled(limitTimeEditor.getBooleanValue(), getFieldEditorParent());
		maxRunningTimeEditor.setEmptyStringAllowed(false);
		addField(maxRunningTimeEditor);

		updateDependentFields();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getProperty().equals(FieldEditor.VALUE)
				&& ((event.getSource() == logResultsEditor) || (event.getSource() == limitTimeEditor)))
			updateDependentFields();
	}

	private void updateDependentFields() {
		logPathEditor.setEnabled(logResultsEditor.getBooleanValue(), getFieldEditorParent());
		logResultsFileEditor.setEnabled(logResultsEditor.getBooleanValue(), getFieldEditorParent());
		maxRunningTimeEditor.setEnabled(limitTimeEditor.getBooleanValue(), getFieldEditorParent());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

}