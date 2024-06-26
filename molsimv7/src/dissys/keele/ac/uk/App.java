package dissys.keele.ac.uk;
import org.COPASI.*;
import java.io.*;

public class App 
{
    public static void main( String[] args )
    {
        try {
            String FOLDER_PATH = args[0];

            MolSim7 bioSign=new MolSim7(); 
            String IPTG_vals = "/IPTG_vals.csv";
            String ARA_vals = "/ARA_vals.csv";
            String bit_seq = "/bit_sequence.csv";
            String COPASI_PARAMS = "/COPASI_PARAMS.csv";
            
            File file = new File(FOLDER_PATH + COPASI_PARAMS);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int signalDur = Integer.parseInt(bufferedReader.readLine());
            int timeshift = Integer.parseInt(bufferedReader.readLine());    
            int total_duration = Integer.parseInt(bufferedReader.readLine());
            int sampling_rate = Integer.parseInt(bufferedReader.readLine());
            
            file = new File(FOLDER_PATH + bit_seq);
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
    
            
            int signalSeq[] = new int[total_duration/signalDur];
            int it = 0;
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                signalSeq[it] = Integer.parseInt(line);
                it++;
            }
            
            int bShift = timeshift;
            
            file = new File(FOLDER_PATH + IPTG_vals);
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            
            int aValues[] = new int[total_duration/sampling_rate];
            it = 0;
            line = bufferedReader.readLine();
            String[] avals = line.split(",");
            for(int q = 0; q<avals.length; q++) {
                aValues[q] = Integer.parseInt(avals[q]);
            }
            
            file = new File(FOLDER_PATH + ARA_vals);
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            
            int bValues[] = new int[total_duration/sampling_rate];
            it = 0;
            line = bufferedReader.readLine();
            String[] bvals = line.split(",");
            for(int q = 0; q<bvals.length; q++) {
                bValues[q] = Integer.parseInt(bvals[q]);
            }
            bufferedReader.close();     
            bioSign.createModel(FOLDER_PATH + "/MolSim7_strong.xml", 0, bShift, total_duration, aValues, bValues);
            //System.out.println("\n\n============================================\n\n");
            
            CTimeSeries timeSeries=TimeSeriesSimulator.timeCourseSimulation(FOLDER_PATH + "/MolSim7_strong.xml", 1000, total_duration);
    		
    		String[][] csvMatrix=TimeSeriesSimulator.createReport(timeSeries);
    		TimeSeriesSimulator.writeCsv(new File(FOLDER_PATH + "/timeCourseResult.csv"), csvMatrix);

//  GM: Commented, 20200212      		
//            CTimeSeries timeSeries = TimeSeriesSimulator.timeCourseSimulation(FOLDER_PATH + "/MolSim6_strong.xml" , 1000, total_duration);
//            int iMax = (int)timeSeries.getNumVariables();
//            int lastIndex = (int)timeSeries.getRecordedSteps() - 1;
//            final String[][] csvMatrix = new String[lastIndex + 1][6];
//            int t = 0;
//            for (int i = 0;i < iMax ;++i)
//            {
//                if(timeSeries.getTitle(i).equals("biosign_dna_model__A") || 
//                	timeSeries.getTitle(i).equals("biosign_dna_model__A2") || 
//                    timeSeries.getTitle(i).equals("biosign_dna_model__B") ||
//                    timeSeries.getTitle(i).equals("biosign_dna_model__IPTG")  || 
//                    timeSeries.getTitle(i).equals("biosign_dna_model__IPTG2")  || 
//                    timeSeries.getTitle(i).equals("biosign_dna_model__Ara")
//                        ) {
//                	System.out.println(timeSeries.getTitle(i));
//                	
//                        for (int j = 0; j < lastIndex+1 ; j++) {
//                            csvMatrix[j][t] = (new Double(timeSeries.getData(j, i))).toString();
//                        }
//                        t++;
//                }
//            }
//            writeCsv(FOLDER_PATH, csvMatrix);    
               
            
        } 
        
        catch(Exception e){
            System.out.println(e);
        }
        
    }
    
//  GM: Commented, 20200212   
//    public static CTimeSeries timeCourseSimulation(String filename, int stepNumber, int duration) {
//        assert CRootContainer.getRoot() != null;
//        CDataModel dataModel = CRootContainer.addDatamodel();
//        assert CRootContainer.getDatamodelList().size() == 1;
//        try
//        {
//            dataModel.importSBML(filename);
//        }
//        catch (java.lang.Exception ex)
//        {
//            System.err.println( "Error while importing the model from file named \"" + filename + "\"." );
//            System.exit(1);
//        }
//        
//        CModel model = dataModel.getModel();
//        assert model != null;
//        // create a report with the correct filename and all the species against
//        // time.
//        CReportDefinitionVector reports = dataModel.getReportDefinitionList();
//        // create a new report definition object
//        CReportDefinition report = reports.createReportDefinition("Report", "Output for timecourse");
//        // set the task type for the report definition to timecourse
//        report.setTaskType(CTaskEnum.Task_timeCourse);
//        // TODO: Decide whether we want table on report.
//        report.setIsTable(false);
//        // the entries in the output should be seperated by a ", "
//        report.setSeparator(new CCopasiReportSeparator(", "));
//
//        // we need a handle to the header and the body
//        // the header will display the ids of the metabolites and "time" for
//        // the first column
//        // the body will contain the actual timecourse data
//        ReportItemVector header = report.getHeaderAddr();
//        ReportItemVector body = report.getBodyAddr();
//        
//        body.add(new CRegisteredCommonName(model.getObject(new CCommonName("Reference=Time")).getCN().getString()));
//        body.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
//        header.add(new CRegisteredCommonName(new CDataString("time").getCN().getString()));
//        header.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
//
//        int i, iMax =(int) model.getMetabolites().size();
//        for (i = 0;i < iMax;++i)
//        {
//            CMetab metab = model.getMetabolite(i);
//            assert metab != null;
//            // we don't want output for FIXED metabolites right now
//            if (metab.getStatus() != CModelEntity.Status_FIXED)
//            {
//                // we want the concentration oin the output
//                // alternatively, we could use "Reference=Amount" to get the
//                // particle number
//                body.add(new CRegisteredCommonName(metab.getObject(new CCommonName("Reference=Concentration")).getCN().getString()));
//                // add the corresponding id to the header
//                header.add(new CRegisteredCommonName(new CDataString(metab.getSBMLId()).getCN().getString()));
//                // after each entry, we need a seperator
//                if(i!=iMax-1)
//                {
//                  body.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
//                  header.add(new CRegisteredCommonName(report.getSeparator().getCN().getString()));
//                }
//
//            }
//        }
//
//
//        // get the trajectory task object
//        CTrajectoryTask trajectoryTask = (CTrajectoryTask)dataModel.getTask("Time-Course");
//        assert trajectoryTask != null;
//
//        // run a deterministic time course
//        trajectoryTask.setMethodType(CTaskEnum.Method_deterministic);
//
//        // pass a pointer of the model to the problem
//        trajectoryTask.getProblem().setModel(dataModel.getModel());
//
//        // actiavate the task so that it will be run when the model is saved
//        // and passed to CopasiSE
//        trajectoryTask.setScheduled(true);
//
//        // set the report for the task
//        trajectoryTask.getReport().setReportDefinition(report);
//        // set the output filename
//        trajectoryTask.getReport().setTarget("report.txt");
//        // don't append output if the file exists, but overwrite the file
//        trajectoryTask.getReport().setAppend(false);
//
//        // get the problem for the task to set some parameters
//        CTrajectoryProblem problem = (CTrajectoryProblem)trajectoryTask.getProblem();
//
//        // Set number of steps.
//        problem.setStepNumber(stepNumber);
//        // start at time 0
//        dataModel.getModel().setInitialTime(0.0);
//        // simulate a duration of time units
//        problem.setDuration(duration);
//        // tell the problem to actually generate time series data
//        problem.setTimeSeriesRequested(true);
//
//        // set some parameters for the LSODA method through the method
//        CTrajectoryMethod method = (CTrajectoryMethod)trajectoryTask.getMethod();
//
//        CCopasiParameter parameter = method.getParameter("Absolute Tolerance");
//        assert parameter != null;
//        assert parameter.getType() == CCopasiParameter.DOUBLE;
//        parameter.setDblValue(1.0e-12);
//
//        boolean result=true;
//        try
//        {
//            // now we run the actual trajectory
//            result=trajectoryTask.processWithOutputFlags(true, (int)CCopasiTask.ONLY_TIME_SERIES);
//        }
//        catch (java.lang.Exception ex)
//        {
//            System.err.println( "Error. Running the time course simulation failed." );
//                        String lastError = trajectoryTask.getProcessError();
//          // check if there are additional error messages
//          if (lastError.length() > 0)
//          {
//              // print the messages in chronological order
//              System.err.println(lastError);
//          }
//            System.exit(1);
//        }
//        if(result==false)
//        {
//            System.err.println( "An error occured while running the time course simulation." );
//            // check if there are additional error messages
//            if (CCopasiMessage.size() > 0)
//            {
//                // print the messages in chronological order
//                System.err.println(CCopasiMessage.getAllMessageText(true));
//            }
//            System.exit(1);
//        }
//
//        // look at the timeseries
//        return trajectoryTask.getTimeSeries();
// 
//        
//    }
//    private static void writeCsv(String path, String[][] csvMatrix) {
//
//        ICsvListWriter csvWriter = null;
//        try {
//            csvWriter = new CsvListWriter(new FileWriter(path + "/timeCourseResult.csv"), 
//                CsvPreference.STANDARD_PREFERENCE);
//
//            for (int i = 0; i < csvMatrix.length; i++) {
//                csvWriter.write(csvMatrix[i]);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace(); // TODO handle exception properly
//        } finally {
//            try {
//                csvWriter.close();
//            } catch (IOException e) {
//            }
//        }
//
//    }
}
