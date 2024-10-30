package dissys.keele.ac.uk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.COPASI.CCommonName;
import org.COPASI.CCopasiMessage;
import org.COPASI.CCopasiParameter;
import org.COPASI.CCopasiReportSeparator;
import org.COPASI.CCopasiTask;
import org.COPASI.CDataHandler;
import org.COPASI.CDataModel;
import org.COPASI.CDataObject;
import org.COPASI.CDataString;
import org.COPASI.CMetab;
import org.COPASI.CModel;
import org.COPASI.CModelEntity;
import org.COPASI.CRegisteredCommonName;
import org.COPASI.CReportDefinition;
import org.COPASI.CReportDefinitionVector;
import org.COPASI.CRootContainer;
import org.COPASI.CTaskEnum;
import org.COPASI.CTimeSeries;
import org.COPASI.CTrajectoryMethod;
import org.COPASI.CTrajectoryProblem;
import org.COPASI.CTrajectoryTask;
import org.COPASI.FloatStdVector;
import org.COPASI.ReportItemVector;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class TimeSeriesSimulator {

	public static CTimeSeries timeCourseSimulation(String filename, int stepNumber, int duration) {
		assert CRootContainer.getRoot() != null;
		CDataModel dataModel = CRootContainer.addDatamodel();
		assert CRootContainer.getDatamodelList().size() == 1;
		try {
			dataModel.importSBML(filename);
		} catch (java.lang.Exception ex) {
			System.err.println("Error while importing the model from file named \"" + filename + "\".");
			ex.printStackTrace();
			System.exit(1);
		}

		CModel model = dataModel.getModel();
		assert model != null;
		// create a report with the correct filename and all the species against
		// time.
		CReportDefinitionVector reports = dataModel.getReportDefinitionList();
		// create a new report definition object
		CReportDefinition report = reports.createReportDefinition("Report", "Output for timecourse");
		// set the task type for the report definition to timecourse
		report.setTaskType(CTaskEnum.Task_timeCourse);
		// TODO: Decide whether we want table on report.
		report.setIsTable(false);
		// the entries in the output should be seperated by a ", "
		report.setSeparator(new CCopasiReportSeparator(", "));

		// we need a handle to the header and the body
		// the header will display the ids of the metabolites and "time" for
		// the first column
		// the body will contain the actual timecourse data
		ReportItemVector header = report.getHeaderAddr();
		ReportItemVector body = report.getBodyAddr();

		body.add(new CRegisteredCommonName(model.getObject(new CCommonName("Reference=Time")).getCN().getString()));
		body.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
		header.add(new CRegisteredCommonName(new CDataString("time").getCN().getString()));
		header.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));

		int i, iMax = (int) model.getMetabolites().size();
		for (i = 0; i < iMax; ++i) {
			CMetab metab = model.getMetabolite(i);
			assert metab != null;
			// we don't want output for FIXED metabolites right now
			if (metab.getStatus() != CModelEntity.Status_FIXED) {
				// we want the concentration oin the output
				// alternatively, we could use "Reference=Amount" to get the
				// particle number
				body.add(new CRegisteredCommonName(
						metab.getObject(new CCommonName("Reference=Concentration")).getCN().getString()));
				// add the corresponding id to the header
				header.add(new CRegisteredCommonName(new CDataString(metab.getSBMLId()).getCN().getString()));
				// after each entry, we need a seperator
				if (i != iMax - 1) {
					body.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
					header.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
				}
			} else {
				System.out.println("Fixed:" + metab.getSBMLId());
			}
		}

		// get the trajectory task object
		CTrajectoryTask trajectoryTask = (CTrajectoryTask) dataModel.getTask("Time-Course");
		assert trajectoryTask != null;

		// run a deterministic time course
		trajectoryTask.setMethodType(CTaskEnum.Method_deterministic);

		// pass a pointer of the model to the problem
		trajectoryTask.getProblem().setModel(dataModel.getModel());

		// actiavate the task so that it will be run when the model is saved
		// and passed to CopasiSE
		trajectoryTask.setScheduled(true);

		// set the report for the task
		trajectoryTask.getReport().setReportDefinition(report);
		// set the output filename
		trajectoryTask.getReport().setTarget("report.txt");
		// don't append output if the file exists, but overwrite the file
		trajectoryTask.getReport().setAppend(false);

		// get the problem for the task to set some parameters
		CTrajectoryProblem problem = (CTrajectoryProblem) trajectoryTask.getProblem();

		// Set number of steps.
		problem.setStepNumber(stepNumber);
		// start at time 0
		dataModel.getModel().setInitialTime(0.0);
		// simulate a duration of time units
		problem.setDuration(duration);
		// tell the problem to actually generate time series data
		problem.setTimeSeriesRequested(true);

		// set some parameters for the LSODA method through the method
		CTrajectoryMethod method = (CTrajectoryMethod) trajectoryTask.getMethod();

		CCopasiParameter parameter = method.getParameter("Absolute Tolerance");
		assert parameter != null;
		assert parameter.getType() == CCopasiParameter.Type_DOUBLE;
		parameter.setDblValue(1.0e-12);

		boolean result = true;
		try {
			// now we run the actual trajectory
			// GMGM: x86 code
			// result=trajectoryTask.processWithOutputFlags(true,
			// (int)CCopasiTask.ONLY_TIME_SERIES);

			result = trajectoryTask.processWithOutputFlags(true, CCopasiTask.getONLY_TIME_SERIES());

		} catch (java.lang.Exception ex) {
			System.err.println("Error. Running the time course simulation failed.");
			String lastError = trajectoryTask.getProcessError();
			// check if there are additional error messages
			if (lastError.length() > 0) {
				// print the messages in chronological order
				System.err.println(lastError);
			}
			System.exit(1);
		}
		if (result == false) {
			System.err.println("An error occured while running the time course simulation.");
			// check if there are additional error messages
			if (CCopasiMessage.size() > 0) {
				// print the messages in chronological order
				System.err.println(CCopasiMessage.getAllMessageText(true));
			}
			System.exit(1);
		}

		// look at the timeseries
		return trajectoryTask.getTimeSeries();

	}

	public static CTimeSeries timeCourseSimulation2(String filename, int stepNumber, int duration) {
		assert CRootContainer.getRoot() != null;
		CDataModel dataModel = CRootContainer.addDatamodel();
		assert CRootContainer.getDatamodelList().size() == 1;
		try {
			dataModel.importSBML(filename);
		} catch (java.lang.Exception ex) {
			System.err.println("Error while importing the model from file named \"" + filename + "\".");
			ex.printStackTrace();
			System.exit(1);
		}

		CModel model = dataModel.getModel();
		assert model != null;
		System.out.println("# compartments:" + model.getNumCompartments());
		System.out.println("# metabolites : " + model.getNumMetabs());
		System.out.println("# parameters  : " + model.getNumModelValues());
		System.out.println("# reactions   : " + model.getNumReactions());

		// get the trajectory task object
		CTrajectoryTask trajectoryTask = (CTrajectoryTask) dataModel.getTask("Time-Course");
		assert trajectoryTask != null;

		// run a deterministic time course
		trajectoryTask.setMethodType(CTaskEnum.Method_deterministic);

		// activate the task so that it will be run when the model is saved
		// and passed to CopasiSE
		trajectoryTask.setScheduled(true);

		// get the problem for the task to set some parameters
		CTrajectoryProblem problem = (CTrajectoryProblem) trajectoryTask.getProblem();

		// pass a pointer of the model to the problem
		problem.setModel(model);

		// Set number of steps.
		problem.setStepNumber(stepNumber);

		// start at time 0
		model.setInitialTime(0.0);

		// simulate a duration of time units
		problem.setDuration(duration);
		problem.setOutputStartTime(0);

		// tell the problem to actually generate time series data
		problem.setTimeSeriesRequested(true);

		// set some parameters for the LSODA method through the method
		CTrajectoryMethod method = (CTrajectoryMethod) trajectoryTask.getMethod();

		CCopasiParameter parameter = method.getParameter("Absolute Tolerance");
		assert parameter != null;
		assert parameter.getType() == CCopasiParameter.Type_DOUBLE;
		parameter.setDblValue(1.0e-12);

		// create data handler that collects all named elements
		String[] modifiers = { "IPTG", "ATC", "SigmaA" };
		CDataHandler dh = new CDataHandler();
		for (int i = 0; i < modifiers.length; i++) {
			CDataObject obj = dataModel.findObjectByDisplayName(modifiers[i]);

			// if we got null returned, the element wasn't found
			if (obj == null) {
				System.err.println(
						"the display name: " + modifiers[i] + " could not be resolved in the model, skipping.");
				continue;
			}

			// if it is a model entity, we want to narrow it to its value
			// reference
			if (obj instanceof CModelEntity)
				obj = ((CModelEntity) obj).getValueReference();

			// add as during handler to collect during the execution of the task
			dh.addDuringName(new CRegisteredCommonName(obj.getCN().getString()));

			// also collect the same data after the task is run
			dh.addAfterName(new CRegisteredCommonName(obj.getCN().getString()));
		}

		// initialize passing along the output handler
		if (!trajectoryTask.initializeRawWithOutputHandler(CCopasiTask.getOUTPUT_UI(), dh)) {
			System.err.println("Couldn't initialize the steady state task");
			System.err.println(CCopasiMessage.getAllMessageText());
			System.exit(1);
		}
		// run
		if (!trajectoryTask.processRaw(true)) {
			System.err.println("Couldn't run the steady state task");
			System.err.println(CCopasiMessage.getAllMessageText());
			System.exit(1);
		}
		trajectoryTask.restore();

		// get number of rows recorded
		int numRows = dh.getNumRowsDuring();

		// print each row collected during the run
		for (int i = 0; i < numRows; i++) {
			FloatStdVector data = dh.getNthRow(i);
			for (int j = 0; j < data.size(); j++) {
				System.out.print(data.get(j));
				if (j + 1 < data.size())
					System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println();

		// print final row collected after the run
		FloatStdVector data = dh.getAfterData();
		for (int j = 0; j < data.size(); j++) {
			System.out.print(data.get(j));
			if (j + 1 < data.size())
				System.out.print("\t");
		}

		boolean result = true;
		try {
			// now we run the actual trajectory
			// GMGM: x86 code
			// result=trajectoryTask.processWithOutputFlags(true,
			// (int)CCopasiTask.ONLY_TIME_SERIES);
			result = trajectoryTask.processWithOutputFlags(true, CCopasiTask.getONLY_TIME_SERIES());
		} catch (java.lang.Exception ex) {
			System.err.println("Error. Running the time course simulation failed.");
			String lastError = trajectoryTask.getProcessError();
			// check if there are additional error messages
			if (lastError.length() > 0) {
				// print the messages in chronological order
				System.err.println(lastError);
			}
			System.exit(1);
		}

		if (result == false) {
			System.err.println("An error occured while running the time course simulation.");
			// check if there are additional error messages
			if (CCopasiMessage.size() > 0) {
				// print the messages in chronological order
				System.err.println(CCopasiMessage.getAllMessageText(true));
			}
			System.exit(1);
		}

		// look at the timeseries
		return trajectoryTask.getTimeSeries();

	}

	public static List<Object> timeCourseSimulation3(String filename, int stepNumber, int duration) {
		List<Object> output = new ArrayList<Object>();

		assert CRootContainer.getRoot() != null;
		CDataModel dataModel = CRootContainer.addDatamodel();
		assert CRootContainer.getDatamodelList().size() == 1;
		try {
			dataModel.importSBML(filename);
		} catch (java.lang.Exception ex) {
			System.err.println("Error while importing the model from file named \"" + filename + "\".");
			ex.printStackTrace();
			System.exit(1);
		}

		CModel model = dataModel.getModel();
		assert model != null;
		System.out.println("# compartments:" + model.getNumCompartments());
		System.out.println("# metabolites : " + model.getNumMetabs());
		System.out.println("# parameters  : " + model.getNumModelValues());
		System.out.println("# reactions   : " + model.getNumReactions());

		// get the trajectory task object
		CTrajectoryTask trajectoryTask = (CTrajectoryTask) dataModel.getTask("Time-Course");
		assert trajectoryTask != null;

		// run a deterministic time course
		trajectoryTask.setMethodType(CTaskEnum.Method_deterministic);

		// activate the task so that it will be run when the model is saved
		// and passed to CopasiSE
		trajectoryTask.setScheduled(true);

		// get the problem for the task to set some parameters
		CTrajectoryProblem problem = (CTrajectoryProblem) trajectoryTask.getProblem();

		// pass a pointer of the model to the problem
		problem.setModel(model);

		// Set number of steps.
		problem.setStepNumber(stepNumber);

		// start at time 0
		model.setInitialTime(0.0);

		// simulate a duration of time units
		problem.setDuration(duration);
		problem.setOutputStartTime(0);

		// tell the problem to actually generate time series data
		problem.setTimeSeriesRequested(true);

		// set some parameters for the LSODA method through the method
		CTrajectoryMethod method = (CTrajectoryMethod) trajectoryTask.getMethod();

		CCopasiParameter parameter = method.getParameter("Absolute Tolerance");
		assert parameter != null;
		parameter.setDblValue(1.0e-12);

		// create data handler that collects all named elements
		CDataHandler dh = new CDataHandler();
		for (int i = 0; i < model.getMetabolites().size(); i++) {

			CMetab species = model.getMetabolite(i);
			String speciesId = species.getSBMLId();
			CDataObject obj = dataModel.findObjectByDisplayName(speciesId);

			// if we got null returned, the element wasn't found
			if (obj == null) {
				System.err.println("the display name: " + speciesId + " could not be resolved in the model, skipping.");
				continue;
			}

			// if it is a model entity, we want to narrow it to its value
			// reference
			if (obj instanceof CModelEntity)
				obj = ((CModelEntity) obj).getValueReference();

			// add as during handler to collect during the execution of the task
			dh.addDuringName(new CRegisteredCommonName(obj.getCN().getString()));
			
			// also collect the same data after the task is run
			dh.addAfterName(new CRegisteredCommonName(obj.getCN().getString()));
			
			//dh.addName(new CRegisteredCommonName(obj.getCN().getString()), i);
			System.out.println("Metabolite:" + obj.getCN().getString());
			
		}

		// initialize passing along the output handler
		if (!trajectoryTask.initializeRawWithOutputHandler(CCopasiTask.getONLY_TIME_SERIES(), dh)) {
			System.err.println("Couldn't initialize the steady state task");
			System.err.println(CCopasiMessage.getAllMessageText());
			System.exit(1);
		}
		// run
		if (!trajectoryTask.processRaw(true)) {
			System.err.println("Couldn't run the steady state task");
			System.err.println(CCopasiMessage.getAllMessageText());
			System.exit(1);
		}
		trajectoryTask.restore();
		int numColumns = (int) model.getMetabolites().size();
		String[][] labels = new String[1][numColumns];

		for (int i = 0; i < numColumns; i++) {
			labels[0][i] = model.getMetabolite(i).getSBMLId();
			/*
			 * System.out.print(model.getMetabolite(i).getSBMLId()); if (i + 1 <
			 * model.getMetabolites().size()) System.out.print("\t");
			 */
		}
		
		System.out.println();
		
		ReportItemVector r= dh.getNames(2);
		if (r!=null)
		{
			for (int j = 0; j < r.size(); j++) {
				System.out.println("Name2:"  + r.get(j).getString());
			}
				
		}
		
		r= dh.getNames(4);
		if (r!=null)
		{
			for (int j = 0; j < r.size(); j++) {
				System.out.println("Name4:"  + r.get(j).getString());
			}
				
		}
		/*
		if (r!=null)
		{
			for (int j = 0; j < r.size(); j++) {
				System.out.println("Name:"  + r.get(j).getString());
			}
				
		}
		System.out.println();
		
		*/	
		
		//SWIGTYPE_p_std__vectorT_std__vectorT_double_t_t aa= dh.getDuringData();
		
		
		// get number of rows recorded
		int numRows = dh.getNumRowsDuring();
		String[][] values = new String[numRows + 1][numColumns];
		// print each row collected during the run
		for (int i = 0; i < numRows; i++) {
			FloatStdVector data = dh.getNthRow(i);
			
			for (int j = 0; j < data.size(); j++) {
				values[i][j] = String.valueOf(data.get(j));
				/*
				 * System.out.print(data.get(j)); if (j + 1 < data.size())
				 * System.out.print("\t");
				 */
			}
			// System.out.println();
		}
		// System.out.println();

		// print final row collected after the run
		FloatStdVector data = dh.getAfterData();
		for (int j = 0; j < data.size(); j++) {
			values[numRows][j] = String.valueOf(data.get(j));
			/*
			 * System.out.print(data.get(j)); if (j + 1 < data.size())
			 * System.out.print("\t");
			 */
		}
		output.add(labels);
		output.add(values);
		return output;

		/*
		 * boolean result = true; try { // now we run the actual trajectory // GMGM: x86
		 * code // result=trajectoryTask.processWithOutputFlags(true, //
		 * (int)CCopasiTask.ONLY_TIME_SERIES); result =
		 * trajectoryTask.processWithOutputFlags(true,
		 * CCopasiTask.getONLY_TIME_SERIES()); } catch (java.lang.Exception ex) {
		 * System.err.println("Error. Running the time course simulation failed.");
		 * String lastError = trajectoryTask.getProcessError(); // check if there are
		 * additional error messages if (lastError.length() > 0) { // print the messages
		 * in chronological order System.err.println(lastError); } System.exit(1); }
		 * 
		 * if (result == false) { System.err.
		 * println("An error occured while running the time course simulation."); //
		 * check if there are additional error messages if (CCopasiMessage.size() > 0) {
		 * // print the messages in chronological order
		 * System.err.println(CCopasiMessage.getAllMessageText(true)); } System.exit(1);
		 * }
		 * 
		 * // look at the timeseries return trajectoryTask.getTimeSeries();
		 */
	}

	public static void writeCsv(File file, String[][] csvMatrix) {

		ICsvListWriter csvWriter = null;
		try {
			csvWriter = new CsvListWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);

			for (int i = 0; i < csvMatrix.length; i++) {
				csvWriter.write(csvMatrix[i]);
			}

		} 
		catch (IOException e) {
			e.printStackTrace(); // TODO handle exception properly
		} 
		finally {
			try {
				csvWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void addColumn(CTimeSeries timeSeries, int dataColumn, int numberOfSteps, int column,
			String[][] csvMatrix) {
		System.out.println(timeSeries.getTitle(dataColumn));

		for (int row = 0; row < numberOfSteps; row++) {
			csvMatrix[row][column] = (new Double(timeSeries.getData(row, dataColumn))).toString();
		}
	}

	public static String[][] createReport(CTimeSeries timeSeries) {
		int iMax = (int) timeSeries.getNumVariables();
		int numberOfSteps = (int) timeSeries.getRecordedSteps();

		String[][] csvMatrix = new String[numberOfSteps][iMax];

		int column = 0;
		for (int i = 0; i < iMax; ++i) {
			addColumn(timeSeries, i, numberOfSteps, column, csvMatrix);
			column++;
		}
		return csvMatrix;

	}

	public static String[][] createReport(CTimeSeries timeSeries, String[] variableList) {
		int iMax = (int) timeSeries.getNumVariables();
		int numberOfSteps = (int) timeSeries.getRecordedSteps();
		String[][] csvMatrix = null;
		if (variableList != null && variableList.length > 0) {
			int numberOfVariables = variableList.length;
			csvMatrix = new String[numberOfSteps][numberOfVariables];
			// Report the columns in the order specified.
			int column = 0;
			for (int index = 0; index < numberOfVariables; index++) {
				for (int i = 0; i < iMax; ++i) {
					if (timeSeries.getTitle(i).equals(variableList[index])) {
						addColumn(timeSeries, i, numberOfSteps, column, csvMatrix);
						column++;
						break;
					}
				}
			}
		}

		return csvMatrix;
	}

	public static void main(String[] args) {
		CTimeSeries timeSeries = TimeSeriesSimulator.timeCourseSimulation("abcommv7.xml", 1000, 80000);

		String[][] csvMatrix = TimeSeriesSimulator.createReport(timeSeries);
		TimeSeriesSimulator.writeCsv(new File("abcomm_all_v7.csv"), csvMatrix);
		String[] columns = "A,B,IPTG,ATC".split(",");
		String[][] csvMatrix2 = TimeSeriesSimulator.createReport(timeSeries, columns);
		TimeSeriesSimulator.writeCsv(new File("abcomm_A_B_IPTG_ATC_v7.csv"), csvMatrix2);
		System.out.println("done!");
	}
}
