package dissys.keele.ac.uk;

import java.io.File;
import java.net.URI;
//import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.ext.comp.ModelDefinition;
import org.sbml.jsbml.text.parser.ParseException;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLDocument;

import org.virtualparts.model.*;
import org.virtualparts.sbml.*;
import org.virtualparts.*;
import org.virtualparts.sbol.*;


public class MolSim7 {
	
	public SBMLDocument createModelStructure() throws Exception
	{
		String designName="abcomm";
	    
		SBOLDocument sbolDesign=new SBOLDocument();
    	String base="http://virtualparts.org/abdesign/";
    	
    	//String design="promLacI:prom;rbs1:rbs;cdsA:cds;ter1:ter;prom2:prom;rbsLacI:rbs;cdsLacI:cds;ter1:ter;";
		//design+="promAraAraC:prom;rbsB:rbs;cdsB:cds;ter1:ter;promAraC:prom;rbsAraC:rbs;cdsAraC:cds;ter1:ter;";
		//NEW: 
		//design+="promLacI2:prom;rbs2:rbs;cdsA2:cds;ter1:ter;prom3:prom;rbsLacI2:rbs;cdsLacI2:cds;ter1:ter";
        
		
    	sbolDesign.setDefaultURIprefix(base);
    	String ADesign="pLacI:prom;rbsLacI:rbs;cdsA:cds;ter1:ter";
    	String BDesign="pTet:prom;rbsTetR:rbs;cdsB:cds;ter1:ter";
    	String sensorDesign="pConstLacI:prom;rbs1:rbs;cdsLacI:cds;ter1:ter;pConstTetR:prom;rbs2:rbs;cdsTetR:cds;ter1:ter";
    	String ADesignName="A_circuit";
    	String BDesignName="B_circuit";
    	String sensorDesignName="Sensor_circuit";   	
    	
    	SVPWriteHandler.convertToSBOL(sbolDesign,VPRUtil.getSVPDesign(base, ADesign), ADesignName);
    	ComponentDefinition cd=VPRUtil.getComponentDef(sbolDesign, ADesignName, ComponentDefinition.DNA);
    	//TODO COMMENT OUT
    	VPRUtil.setAnnotation(cd,VPRTerms.parameter.copyNumber, 10);
    	

    	SVPWriteHandler.convertToSBOL(sbolDesign,VPRUtil.getSVPDesign(base, BDesign), BDesignName);
    	cd=VPRUtil.getComponentDef(sbolDesign, BDesignName, ComponentDefinition.DNA);
    	//TODO COMMENT OUT
    	VPRUtil.setAnnotation(cd,VPRTerms.parameter.copyNumber, 10);
    	
    	SVPWriteHandler.convertToSBOL(sbolDesign,VPRUtil.getSVPDesign(base, sensorDesign), sensorDesignName);
    	cd=VPRUtil.getComponentDef(sbolDesign, sensorDesignName, ComponentDefinition.DNA);
    	//TODO COMMENT OUT
    	VPRUtil.setAnnotation(cd,VPRTerms.parameter.copyNumber, 10);
    	
    	ModuleDefinition moduleDef=sbolDesign.createModuleDefinition(designName + "_module");
    	
        	
    	VPRUtil.createTranslationInteraction(sbolDesign, moduleDef, "cdsLacI", "LacI");
    	VPRUtil.createTranslationInteraction(sbolDesign, moduleDef, "cdsTetR", "TetR");
    	VPRUtil.createTranslationInteraction(sbolDesign, moduleDef, "cdsA", "A");
    	VPRUtil.createTranslationInteraction(sbolDesign, moduleDef, "cdsB", "B");
    	
    	VPRUtil.createDimerisationInteraction(sbolDesign, moduleDef, "LacI", "LacI_Dimer");
    	VPRUtil.createDimerisationInteraction(sbolDesign, moduleDef, "LacI_Dimer", "LacI_Tetramer");
    	VPRUtil.createComplexFormationWithModifier(sbolDesign, moduleDef, "LacI_Tetramer", "IPTG", "LacI_IPTG");
    	
    	
    	Interaction pLacIRepression=VPRUtil.createPromoterRepression(sbolDesign, moduleDef, "LacI_Tetramer", "pLacI");
    	
    	VPRUtil.createDimerisationInteraction(sbolDesign, moduleDef, "TetR", "TetR_Dimer");
    	Interaction atCbinding=VPRUtil.createComplexFormationWithModifier(sbolDesign, moduleDef, "TetR_Dimer", "ATC", "TetR_ATC");
    	VPRUtil.setAnnotation(atCbinding, VPRTerms.parameter.kforward, 0.25);//gmgmgm
    	
    	
    	Interaction ABformation=VPRUtil.createProteinComplexFormation(sbolDesign, moduleDef,"A","B","AB");
    	ABformation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.1");
    	ABformation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
    	
    	
    	Interaction pTetRepression =VPRUtil.createPromoterRepression(sbolDesign, moduleDef, "TetR_Dimer", "pTet");
    	VPRUtil.setAnnotation (pTetRepression, VPRTerms.parameter.kforward, 0.025);
    	
    	ComponentDefinition pLacI=sbolDesign.getComponentDefinition(URI.create(sbolDesign.getDefaultURIprefix() + "pLacI"));    
    	VPRUtil.setAnnotation(pLacI, VPRTerms.parameter.transcriptionRate, 0.03);
    	
    	ComponentDefinition pTet=sbolDesign.getComponentDefinition(URI.create(sbolDesign.getDefaultURIprefix() + "pTet"));    
    	VPRUtil.setAnnotation(pTet, VPRTerms.parameter.transcriptionRate, 0.05);//gmgmgm
    	
    	ComponentDefinition rbsTetR=sbolDesign.getComponentDefinition(URI.create(sbolDesign.getDefaultURIprefix() + "rbsTetR"));    
    	VPRUtil.setAnnotation(rbsTetR, VPRTerms.parameter.translationRate, 0.0195);
    	
    	ComponentDefinition rbsLacI=sbolDesign.getComponentDefinition(URI.create(sbolDesign.getDefaultURIprefix() + "rbsLacI"));    
    	VPRUtil.setAnnotation(rbsLacI, VPRTerms.parameter.translationRate, 0.015);
    	
    	    	
    	SBOLHandler.write(sbolDesign, new File(designName + ".sbol"));
    	SBMLDocument sbmlDoc=this.getModel(sbolDesign);
    	
    	return sbmlDoc;
    	
	}
	
	
	
	public void createModel(String filePath, int timeAStart, int timeBStart, int duration, int[] aValues, int[] bValues) throws Exception
	{

		SBMLDocument sbmlDoc=createModelStructure();
		createEvents(sbmlDoc, timeAStart, timeBStart, duration, aValues, bValues);
    	SBMLWriter.write(sbmlDoc, filePath ,' ', (short) 2);   
    	
    	System.out.println("...done!");
	}
	
	private String getModelName()
	{
		return "biosign_dna_model";
	}
	
	private ModelDefinition getMainModelDefinition(SBMLDocument sbmlDoc)
	{
		SBMLHandler handler=new SBMLHandler(3,1 );
		return handler.getDocumentPlugin(sbmlDoc).getListOfModelDefinitions().get(getModelName());
	}
	
	
	public void setInitialConcentration(String moleculeId, double concentration, SBMLDocument sbmlDoc)
	{
		ModelDefinition modelDef= getMainModelDefinition(sbmlDoc);
		modelDef.getSpecies(moleculeId).setInitialConcentration(concentration);
	}
	
	
	public void setParameterValue(String id, double value, SBMLDocument sbmlDoc)
	{
		ModelDefinition modelDef= getMainModelDefinition(sbmlDoc);
		modelDef.getParameter(id).setValue(value);
	}
	
	
	
	public void createEvents(SBMLDocument sbmlDoc, int timeAStart, int timeBStart, int duration, int[] aValues, int[] bValues) throws Exception
	{
		SBMLHandler handler=new SBMLHandler(3,1 );
		Model modelDef=  sbmlDoc.getModel();
    	
		for(int i = 0; i < aValues.length; i++) {
			handler.createEvent(modelDef,("IPTG_setEvent_" + i), "IPTG", timeAStart+(duration*i)/aValues.length, aValues[i]);    	
			handler.createEvent(modelDef,("IPTG_unsetEvent_" + i), "IPTG", timeAStart+(duration*(i+1))/aValues.length,0); 
		}
		for(int i = 0; i < bValues.length; i++) {
			handler.createEvent(modelDef,("ATC_setevent_" + i), "ATC", timeBStart +(duration*i)/bValues.length, bValues[i]);    	
			handler.createEvent(modelDef,("ATC_unsetevent_" + i), "ATC", timeBStart+(duration*(i+1))/bValues.length,0); 
		}
    	
    	trackTwoSpecies_Sum(modelDef, "A", "AB", "Atotal_param");
    	trackTwoSpecies_Sum(modelDef, "B", "AB", "Btotal_param");
    	
	}
	 
    public Parameter trackTwoSpecies_Sum (Model modelDef, String species1, String species2, String parameterName) throws ParseException
	{
		Parameter param=modelDef.createParameter(parameterName);
    	param.setConstant(false);
    	
    	AssignmentRule rule=modelDef.createAssignmentRule();
    	rule.setMath(ASTNode.parseFormula(String.format("%s + %s", species1, species2)));	
    	rule.setVariable(param.getId());
    	return param;
	}
    
	
	private static SBMLDocument addEvents(SBMLDocument sbmlModel) throws Exception
	{
		SBMLHandler sbmlHandler=new SBMLHandler(3,1 );
    	Model modelDef=  sbmlModel.getModel();//sbmlHandler.getDocumentPlugin(sbmlModel);//getListOfModelDefinitions().get("toggle_switch_model");
    	
    	int duration=0;
    	int time=0;
    	int i;
    	for (i=0;i<12;i++)
    	{
    		if (i%2==0)
    		{
    			//TODO (ModelDefinition) TYPECAST
    			sbmlHandler.createEvent( modelDef,"IPTG_setevent"+i, "IPTG", time+1,50);   
    			sbmlHandler.createEvent(modelDef,"ATC_unsetevent"+i, "ATC", time,0);  
    			duration=120 * 60;
    		}
    		else
    		{
    			//TODO (ModelDefinition) TYPECAST
    			sbmlHandler.createEvent(modelDef,"IPTG_unsetevent"+i, "IPTG", time,0); 
    			sbmlHandler.createEvent(modelDef,"ATC_setevent"+i, "ATC", time+1,50); 
    			duration=30* 60;
    		}   
    		time=time + duration;
    	}
    	//TODO (ModelDefinition) TYPECAST
    	sbmlHandler.createEvent(modelDef,"IPTG_unsetevent"+i, "IPTG", time,25); 
		sbmlHandler.createEvent(modelDef,"ATC_setevent"+i, "ATC", time,25); 
    	
		Parameter p=sbmlHandler.createVariableParameter(modelDef, "A_B_ratio", null);								
		AssignmentRule rule=modelDef.createAssignmentRule();
		
	    try
	    {
	    	rule.setMath(ASTNode.parseFormula("A/B"));		
	    	rule.setVariable(p);
	    }
	    catch (Exception e)
	    {
	    	throw new VPRException("Could not crete the rule for RFP/GFP",e);
	    }
	    return sbmlModel;
		//String newFilePath=sbmlFilePath + "." + newVersion + ".xml";
    	//SBMLWriter.write(sbmlModel, newFilePath,' ', (short) 2); 
    	
	}
	

	
	private SBMLDocument getModel(SBOLDocument sbolDocument) throws Exception
	{
    	ModelHandler handler=new ModelHandler();    
    	return handler.getSBMLModel(ModelType.SBML_L3, sbolDocument,ModellingAbstraction.Simple);
	}
	
	
	public static void main (String[] args) throws Exception
	{
		MolSim7 bioSign=new MolSim7();
		SBMLDocument sbmlDoc=bioSign.createModelStructure();
		sbmlDoc=addEvents(sbmlDoc);
		SBMLWriter.write(sbmlDoc, "abcommv7.xml" ,' ', (short) 2);   
    	
    	System.out.println("...done!");
	}
	private static int[] getValues(String line, int total_duration, int sampling_rate )
	{
		  int aValues[] = new int[total_duration/sampling_rate];
	      String[] avals = line.split(",");
	       for(int q = 0; q<avals.length; q++) {
	            aValues[q] = Integer.parseInt(avals[q]);
	        }
	       return aValues;
	}
	
	public static void main2 (String[] args) throws Exception
	{
		MolSim7 bioSign=new MolSim7();
		
		int bShift=2000;
		int total_duration = 20000;
        int sampling_rate = 5000;
        
        String line="20,30,40,50";
        int[] aValues =getValues(line, total_duration, sampling_rate);
        int[] bValues = getValues(line, total_duration, sampling_rate);
        
		
		bioSign.createModel("MolSim7_strong_test.xml", 0, bShift, total_duration, aValues, bValues);
          
		  
    	System.out.println("...done!");
	}
}
