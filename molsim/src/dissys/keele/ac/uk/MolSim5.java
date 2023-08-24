package dissys.keele.ac.uk;

import java.net.URI;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Trigger;
import org.sbml.jsbml.ext.comp.ModelDefinition;
import org.sbml.jsbml.text.parser.ParseException;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;

import uk.ac.ncl.ico2s.model.ModelHandler;
import uk.ac.ncl.ico2s.model.ModelType;
import uk.ac.ncl.ico2s.model.ModellingAbstraction;
import uk.ac.ncl.ico2s.model.VPRTerms;
import uk.ac.ncl.ico2s.sbml.SBMLHandler;
import org.virtualparts.sbol.*;


public class MolSim5 {
	public SBMLDocument createModelStructure() throws Exception{
		//A: exsA
		//B:exsC
		//C: invF
		//Z:rfp
		//D:sicA
		//CD complex activates Z
		
		//String design="prom1:prom;rbs1:rbs;cdsA:cds;ter1:ter;promLacI:prom;rbs2:rbs;cdsLacI:cds;rbsB:rbs;cdsB:cds;ter1:ter;";
		String design="promLacI:prom;rbs1:rbs;cdsA:cds;ter1:ter;prom2:prom;rbsLacI:rbs;cdsLacI:cds;ter1:ter;";
		design+="promAraAraC:prom;rbsB:rbs;cdsB:cds;ter1:ter;promAraC:prom;rbsAraC:rbs;cdsAraC:cds;ter1:ter;";
		//NEW: 
		design+="promLacI2:prom;rbs2:rbs;cdsA2:cds;ter1:ter;prom3:prom;rbsLacI2:rbs;cdsLacI2:cds;ter1:ter";
        
		

		SBOLDocument sbolDocument=SVPWriteHandler.convertToSBOL(design,"biosign_dna");	
    	ComponentDefinition AB=sbolDocument.createComponentDefinition("AB", ComponentDefinition.COMPLEX);

    	ComponentDefinition IPTG=sbolDocument.createComponentDefinition("IPTG", ComponentDefinition.SMALL_MOLECULE);
    	ComponentDefinition LacI=sbolDocument.createComponentDefinition("LacI", ComponentDefinition.PROTEIN);  
    	ComponentDefinition LacI_IPTG=sbolDocument.createComponentDefinition("LacI_IPTG", ComponentDefinition.COMPLEX);
    	ComponentDefinition cdsLacI=sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "cdsLacI"));  
    	ComponentDefinition promLacI=sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "promLacI"));   
    	ComponentDefinition cdsA=sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "cdsA"));  
		ComponentDefinition A=sbolDocument.createComponentDefinition("A", ComponentDefinition.PROTEIN);  
    	
    	ComponentDefinition Ara=sbolDocument.createComponentDefinition("Ara", ComponentDefinition.SMALL_MOLECULE);        	
    	ComponentDefinition B=sbolDocument.createComponentDefinition("B", ComponentDefinition.PROTEIN);
    	ComponentDefinition AraC_Ara=sbolDocument.createComponentDefinition("AraC_Ara", ComponentDefinition.COMPLEX);    	
    	ComponentDefinition AraC=sbolDocument.createComponentDefinition("AraC", ComponentDefinition.PROTEIN);   
    	ComponentDefinition cdsAraC=sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "cdsAraC"));
    	ComponentDefinition cdsB=sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "cdsB"));    	    	
    	ComponentDefinition promAraAraC=sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "promAraAraC"));  
    	
    	//NEW: code section starts here.
    	ComponentDefinition A2B=sbolDocument.createComponentDefinition("A2B", ComponentDefinition.COMPLEX);    	
    	ComponentDefinition A2 = sbolDocument.createComponentDefinition("A2", ComponentDefinition.PROTEIN);  
    	ComponentDefinition IPTG2 = sbolDocument.createComponentDefinition("IPTG2", ComponentDefinition.SMALL_MOLECULE); 	
    	ComponentDefinition cdsA2 = sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "cdsA2"));
    	ComponentDefinition cdsLacI2=sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "cdsLacI2"));   
    	ComponentDefinition promLacI2=sbolDocument.getComponentDefinition(URI.create(sbolDocument.getDefaultURIprefix() + "promLacI2"));  
    	ComponentDefinition LacI2_IPTG2=sbolDocument.createComponentDefinition("LacI2_IPTG2", ComponentDefinition.COMPLEX); 
    	ComponentDefinition LacI2=sbolDocument.createComponentDefinition("LacI2", ComponentDefinition.PROTEIN);
    	
    	//NEW: code section ends here
    	
    	
    	
    	sbolDocument.addNamespace(URI.create(VPRTerms.Ns.getNamespaceURI()), VPRTerms.Ns.getPrefix());
    	promAraAraC.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.transcriptionRate,VPRTerms.Ns.getPrefix()), "0.0001");

    	
    	ModuleDefinition moduleDef=sbolDocument.createModuleDefinition("biosign_module");
    	
    	
    	SBOLInteraction.createTranslationInteraction(moduleDef, cdsA, A);
    	SBOLInteraction.createTranslationInteraction(moduleDef, cdsB, B);    	
    	SBOLInteraction.createTranslationInteraction(moduleDef, cdsLacI, LacI);
    	SBOLInteraction.createTranslationInteraction(moduleDef, cdsAraC, AraC); 
    	// NEW: Translation interaction.
    	SBOLInteraction.createTranslationInteraction(moduleDef, cdsA2, A2);
    	SBOLInteraction.createTranslationInteraction(moduleDef, cdsLacI2, LacI2); 
    	
    	Interaction IPTG_LacI_Formation=SBOLInteraction.createComplexFormationWithModifier(moduleDef, LacI, IPTG, LacI_IPTG);  
    	IPTG_LacI_Formation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.1");
    	IPTG_LacI_Formation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
    	
    	//NEW: IPTG2 LacI2 formation.
    	Interaction IPTG2_LacI2_Formation=SBOLInteraction.createComplexFormationWithModifier(moduleDef, LacI2, IPTG2, LacI2_IPTG2);  
    	IPTG2_LacI2_Formation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.1");
    	IPTG2_LacI2_Formation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
    	
    	//Strong complex formation
    	SBOLInteraction.createComplexFormationWithModifier(moduleDef, AraC, Ara, AraC_Ara);      	
    	Interaction ABformation=SBOLInteraction.createComplexFormation(moduleDef, A, B, AB);
    	ABformation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.1");
    	ABformation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
    	
    	//NEW: AB formation with AB2
    	Interaction AB2formation=SBOLInteraction.createComplexFormation(moduleDef, A2, B, A2B);
    	AB2formation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.1");
    	AB2formation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
    
    	//Weaker complex formation
    	
    	//Strong promoter represssion
    	Interaction promLacIRepression=SBOLInteraction.createPromoterRepression(moduleDef, promLacI, LacI);    	
    	promLacIRepression.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.1");
    	promLacIRepression.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
    	
    	//Strong promoter represssion
    	Interaction promLacI2Repression=SBOLInteraction.createPromoterRepression(moduleDef, promLacI2, LacI2);    	
    	promLacI2Repression.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.1");
    	promLacI2Repression.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
    	
    	/*Interaction promAraAraCInduction=SBOLInteraction.createPromoterInduction(moduleDef, promAraAraC, AraC_Ara);
    	promAraAraCInduction.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.1");
    	promAraAraCInduction.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
    	

    	SBOLInteraction.createPromoterInduction(moduleDef, promC, A);    	
    	SBOLInteraction.createPromoterInduction(moduleDef, promD, C);
    	SBOLInteraction.createPromoterInduction(moduleDef, promZ, CD);*/
    	
    	//Strong promoter activation
    	createStrongPromoterInduction(moduleDef, promAraAraC, AraC_Ara);
    	
    	SBMLDocument sbmlDoc=this.getModel(sbolDocument);
    	
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
    	ModelDefinition modelDef= getMainModelDefinition(sbmlDoc);
    	// NEW: Added dummy IPTG 2 set event to test.
    	handler.createEvent(modelDef,("IPTG2_setEvent_"), "IPTG2", 500, 500);    	
		handler.createEvent(modelDef,("IPTG2_unsetEvent_"), "IPTG2", 1500, 0); 
    	
		for(int i = 0; i < aValues.length; i++) {
			handler.createEvent(modelDef,("IPTG_setEvent_" + i), "IPTG", timeAStart+(duration*i)/aValues.length, aValues[i]);    	
			handler.createEvent(modelDef,("IPTG_unsetEvent_" + i), "IPTG", timeAStart+(duration*(i+1))/aValues.length,0); 
		}
		for(int i = 0; i < bValues.length; i++) {
			handler.createEvent(modelDef,("Arabinose_setevent_" + i), "Ara", timeBStart +(duration*i)/bValues.length, bValues[i]);    	
			handler.createEvent(modelDef,("Arabinose_unsetevent_" + i), "Ara", timeBStart+(duration*(i+1))/bValues.length,0); 
		}
    	
    	//handler.createEvent(modelDef,"LacI_initializeEvent", "LacI", 1,1100);
    	modelDef.getSpecies("LacI").setInitialConcentration(500);
    	modelDef.getSpecies("LacI2").setInitialConcentration(500);
    	
    	handler.trackTwoSpecies_Sum(modelDef, "A", "AB", "Atotal_param");
    	//NEW: AB2 AB track
    	handler.trackTwoSpecies_Sum(modelDef, "A2", "AB", "A2total_param");
    	handler.trackTwoSpecies_Sum(modelDef, "B", "AB", "Btotal_param");
    	
	}
	private void createStrongPromoterInduction(ModuleDefinition moduleDef, ComponentDefinition promoter, ComponentDefinition TF) throws SBOLValidationException
	{
		Interaction promActivation=SBOLInteraction.createPromoterInduction(moduleDef, promoter, TF);
    	promActivation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.01");
    	promActivation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
	}
	
//	private void createWeakPromoterInduction(ModuleDefinition moduleDef, ComponentDefinition promoter, ComponentDefinition TF) throws SBOLValidationException
//	{
//		Interaction promActivation=SBOLInteraction.createPromoterInduction(moduleDef, promoter, TF);
//    	promActivation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kforward,VPRTerms.Ns.getPrefix()), "0.001");
//    	promActivation.createAnnotation(VPRTerms.toQName(VPRTerms.parameter.kback,VPRTerms.Ns.getPrefix()), "0.5");
//	}
//	
	
	private SBMLDocument getModel(SBOLDocument sbolDocument) throws Exception
	{
    	ModelHandler handler=new ModelHandler();    
    	return handler.getSBMLModel(ModelType.SBML_L3, sbolDocument,ModellingAbstraction.Simple);
	}
}
