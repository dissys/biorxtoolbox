package dissys.keele.ac.uk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.COPASI.CCommonName;
import org.COPASI.CCopasiMessage;
import org.COPASI.CCopasiParameter;
import org.COPASI.CCopasiReportSeparator;
import org.COPASI.CCopasiTask;
import org.COPASI.CDataModel;
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
import org.COPASI.ReportItemVector;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class TimeSeriesSimulator {

	public static CTimeSeries timeCourseSimulation(String filename, int stepNumber, int duration) {
        assert CRootContainer.getRoot() != null;
        CDataModel dataModel = CRootContainer.addDatamodel();
        assert CRootContainer.getDatamodelList().size() == 1;
        try
        {
            dataModel.importSBML(filename);
        }
        catch (java.lang.Exception ex)
        {
            System.err.println( "Error while importing the model from file named \"" + filename + "\"." );
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

        int i, iMax =(int) model.getMetabolites().size();
        for (i = 0;i < iMax;++i)
        {
            CMetab metab = model.getMetabolite(i);
            assert metab != null;
            // we don't want output for FIXED metabolites right now
            if (metab.getStatus() != CModelEntity.Status_FIXED)
            {
                // we want the concentration oin the output
                // alternatively, we could use "Reference=Amount" to get the
                // particle number
                body.add(new CRegisteredCommonName(metab.getObject(new CCommonName("Reference=Concentration")).getCN().getString()));
                // add the corresponding id to the header
                header.add(new CRegisteredCommonName(new CDataString(metab.getSBMLId()).getCN().getString()));
                // after each entry, we need a seperator
                if(i!=iMax-1)
                {
                  body.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
                  header.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
                }

            }
        }


        // get the trajectory task object
        CTrajectoryTask trajectoryTask = (CTrajectoryTask)dataModel.getTask("Time-Course");
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
        CTrajectoryProblem problem = (CTrajectoryProblem)trajectoryTask.getProblem();

        // Set number of steps.
        problem.setStepNumber(stepNumber);
        // start at time 0
        dataModel.getModel().setInitialTime(0.0);
        // simulate a duration of time units
        problem.setDuration(duration);
        // tell the problem to actually generate time series data
        problem.setTimeSeriesRequested(true);

        // set some parameters for the LSODA method through the method
        CTrajectoryMethod method = (CTrajectoryMethod)trajectoryTask.getMethod();

        CCopasiParameter parameter = method.getParameter("Absolute Tolerance");
        assert parameter != null;
        assert parameter.getType() == CCopasiParameter.Type_DOUBLE;
        parameter.setDblValue(1.0e-12);

        boolean result=true;
        try
        {
            // now we run the actual trajectory
            result=trajectoryTask.processWithOutputFlags(true, (int)CCopasiTask.ONLY_TIME_SERIES);
        }
        catch (java.lang.Exception ex)
        {
            System.err.println( "Error. Running the time course simulation failed." );
                        String lastError = trajectoryTask.getProcessError();
          // check if there are additional error messages
          if (lastError.length() > 0)
          {
              // print the messages in chronological order
              System.err.println(lastError);
          }
            System.exit(1);
        }
        if(result==false)
        {
            System.err.println( "An error occured while running the time course simulation." );
            // check if there are additional error messages
            if (CCopasiMessage.size() > 0)
            {
                // print the messages in chronological order
                System.err.println(CCopasiMessage.getAllMessageText(true));
            }
            System.exit(1);
        }

        // look at the timeseries
        return trajectoryTask.getTimeSeries();

    }
	
	  public static void writeCsv(File file, String[][] csvMatrix) {

	        ICsvListWriter csvWriter = null;
	        try {
	            csvWriter = new CsvListWriter(new FileWriter(file), 
	                CsvPreference.STANDARD_PREFERENCE);

	            for (int i = 0; i < csvMatrix.length; i++) {
	                csvWriter.write(csvMatrix[i]);
	            }

	        } catch (IOException e) {
	            e.printStackTrace(); // TODO handle exception properly
	        } finally {
	            try {
	                csvWriter.close();
	            } catch (IOException e) {
	            }
	        }

	    }
	
	  private static void addColumn(CTimeSeries  timeSeries, int dataColumn, int numberOfSteps, int column, String[][]csvMatrix)
	  {
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

	public static String[][] createReport(CTimeSeries  timeSeries, String[] variableList)
	{
		 int iMax = (int)timeSeries.getNumVariables();
         int numberOfSteps = (int)timeSeries.getRecordedSteps();
         String[][] csvMatrix=null; 
         if (variableList!=null && variableList.length>0)
         {
        	 int numberOfVariables=variableList.length;
        	 csvMatrix = new String[numberOfSteps][numberOfVariables];
        	//Report the columns in the order specified.
        	 int column = 0;
        	 for (int index=0; index<numberOfVariables; index++){
      		   for (int i = 0; i<iMax ;++i)
                 {
      			   if (timeSeries.getTitle(i).equals(variableList[index]))
      			   {
      				   addColumn(timeSeries, i, numberOfSteps, column, csvMatrix);
      				   column++;
      				   break;
      			   }
                 }
      	   }
         }
 
        return csvMatrix;
	}
	
	public static void main(String[] args)
	{
		CTimeSeries timeSeries=TimeSeriesSimulator.timeCourseSimulation("abcommv7.xml", 1000, 80000);
		
		String[][] csvMatrix=TimeSeriesSimulator.createReport(timeSeries);
		TimeSeriesSimulator.writeCsv(new File("abcomm_all_v7.csv"), csvMatrix);
		String[] columns="A,B,IPTG,ATC".split(",");
		String[][] csvMatrix2=TimeSeriesSimulator.createReport(timeSeries,columns);
		TimeSeriesSimulator.writeCsv(new File("abcomm_A_B_IPTG_ATC_v7.csv"), csvMatrix2);
		System.out.println("done!");
	}
}
