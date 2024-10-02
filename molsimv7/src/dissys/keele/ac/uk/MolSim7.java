package dissys.keele.ac.uk;

import java.io.File;
import java.net.URI;
import java.util.Set;

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
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.Sequence;
import org.virtualparts.model.*;
import org.virtualparts.sbml.*;
import org.virtualparts.*;
import org.virtualparts.sbol.*;

public class MolSim7 {
	
	private SBOLDocument sbolDesign=null;
	public SBMLDocument createModelStructure() throws Exception
	{
		String designName="abcomm";
	    
		sbolDesign=new SBOLDocument();
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
    	
    	    	
    	setSequences(sbolDesign);    	
    	
    	SBMLDocument sbmlDoc=this.getModel(sbolDesign);
    	
    	return sbmlDoc;
    	
	}
	
	public void setSequences(SBOLDocument sbolDesign) throws SBOLValidationException
	{
    	//Sequences:
    	
    	//BBa_J23101: https://parts.igem.org/Part:BBa_J23101 
    	String pConstSequence="TTTACAGCTAGCTCAGTCCTAGGTATTATGCTAGC";
    	
    	//BBa_B0064: https://parts.igem.org/Part:BBa_B0064
    	String rbsConstSequence="AAAGAGGGGAAA";    		
    	
    	//Terminator: L3S2P21
    	String terSequence="CTCGGTACCAAATTCCAGAAAAGAGGCCTCCCGAAAGGGGGGCCTTTTTTCGTTTTGGTCC";      	
    	
    	//https://www.nature.com/articles/nature11516 (Nielsen at al) - SupplementaryTableS9
    	String pTetSequence = "TACTCCACCGTTGGCTTTTTTCCCTATCAGTGATAGAGATTGACATCCCTATCAGTGATAGAGATAATGAGCAC";
    	
    	//LacI and TetR transcriptional unit - sensor module from Nielsen et al - supplementary table 8.
    	//GCGGCGCGCCATCGAATGGCGCAAAACCTTTCGCGGTATGGCATGATAGCGCCCGGAAGAGAGTCAATTCAGGGTGGTGAATATGAAACCAGTAACGTTATACGATGTCGCAGAGTATGCCGGTGTCTCTTATCAGACCGTTTCCCGCGTGGTGAACCAGGCCAGCCACGTTTCTGCGAAAACGCGGGAAAAAGTGGAAGCGGCGATGGCGGAGCTGAATTACATTCCCAACCGCGTGGCACAACAACTGGCGGGCAAACAGTCGTTGCTGATTGGCGTTGCCACCTCCAGTCTGGCCCTGCACGCGCCGTCGCAAATTGTCGCGGCGATTAAATCTCGCGCCGATCAACTGGGTGCCAGCGTGGTGGTGTCGATGGTAGAACGAAGCGGCGTCGAAGCCTGTAAAGCGGCGGTGCACAATCTTCTCGCGCAACGCGTCAGTGGGCTGATCATTAACTATCCGCTGGATGACCAGGATGCCATTGCTGTGGAAGCTGCCTGCACTAATGTTCCGGCGTTATTTCTTGATGTCTCTGACCAGACACCCATCAACAGTATTATTTTCTCCCATGAGGACGGTACGCGACTGGGCGTGGAGCATCTGGTCGCATTGGGTCACCAGCAAATCGCGCTGTTAGCGGGCCCATTAAGTTCTGTCTCGGCGCGTCTGCGTCTGGCTGGCTGGCATAAATATCTCACTCGCAATCAAATTCAGCCGATAGCGGAACGGGAAGGCGACTGGAGTGCCATGTCCGGTTTTCAACAAACCATGCAAATGCTGAATGAGGGCATCGTTCCCACTGCGATGCTGGTTGCCAACGATCAGATGGCGCTGGGCGCAATGCGCGCCATTACCGAGTCCGGGCTGCGCGTTGGTGCGGATATCTCGGTAGTGGGATACGACGATACCGAAGATAGCTCATGTTATATCCCGCCGTTAACCACCATCAAACAGGATTTTCGCCTGCTGGGGCAAACCAGCGTGGACCGCTTGCTGCAACTCTCTCAGGGCCAGGCGGTGAAGGGCAATCAGCTGTTGCCAGTCTCACTGGTGAAAAGAAAAACCACCCTGGCGCCCAATACGCAAACCGCCTCTCCCCGCGCGTTGGCCGATTCATTAATGCAGCTGGCACGACAGGTTTCCCGACTGGAAAGCGGGCAGTGATAATCCAGGAGGAAAAAAATGTCCAGATTAGATAAAAGTAAAGTGATTAACAGCGCATTAGAGCTGCTTAATGAGGTCGGAATCGAAGGTTTAACAACCCGTAAACTCGCCCAGAAGCTAGGTGTAGAGCAGCCTACATTGTATTGGCATGTAAAAAATAAGCGGGCTTTGCTCGACGCCTTAGCCATTGAGATGTTAGATAGGCACCATACTCACTTTTGCCCTTTAGAAGGGGAAAGCTGGCAAGATTTTTTACGTAATAACGCTAAAAGTTTTAGATGTGCTTTACTAAGTCATCGCGATGGAGCAAAAGTACATTTAGGTACACGGCCTACAGAAAAACAGTATGAAACTCTCGAAAATCAATTAGCCTTTTTATGCCAACAAGGTTTTTCACTAGAGAATGCATTATATGCACTCAGCGCTGTGGGGCATTTTACTTTAGGTTGCGTATTGGAAGATCAAGAGCATCAAGTCGCTAAAGAAGAAAGGGAAACACCTACTACTGATAGTATGCCGCCATTATTACGACAAGCTATCGAATTATTTGATCACCAAGGTGCAGAGCCAGCCTTCTTATTCGGCCTTGAATTGATCATATGCGGATTAGAAAAACAACTTAAATGTGAAAGTGGGTCCTAATAA
    	//pLacI: 
    	String pLacISequence="GCGGCGCGCCATCGAATGGCGCAAAACCTTTCGCGGTATGGCATGATAGCGCCCGG";    	
    	//rbsLacI - The rest of the sequence from the pLacI sequence in Nielsen et al - supplementary table 8: 
    	String rbsLacISequence="AAGAGAGTCAATTCAGGGTGGTGAAT";
    	//* lacI:
    	String lacISequence="ATGAAACCAGTAACGTTATACGATGTCGCAGAGTATGCCGGTGTCTCTTATCAGACCGTTTCCCGCGTGGTGAACCAGGCCAGCCACGTTTCTGCGAAAACGCGGGAAAAAGTGGAAGCGGCGATGGCGGAGCTGAATTACATTCCCAACCGCGTGGCACAACAACTGGCGGGCAAACAGTCGTTGCTGATTGGCGTTGCCACCTCCAGTCTGGCCCTGCACGCGCCGTCGCAAATTGTCGCGGCGATTAAATCTCGCGCCGATCAACTGGGTGCCAGCGTGGTGGTGTCGATGGTAGAACGAAGCGGCGTCGAAGCCTGTAAAGCGGCGGTGCACAATCTTCTCGCGCAACGCGTCAGTGGGCTGATCATTAACTATCCGCTGGATGACCAGGATGCCATTGCTGTGGAAGCTGCCTGCACTAATGTTCCGGCGTTATTTCTTGATGTCTCTGACCAGACACCCATCAACAGTATTATTTTCTCCCATGAGGACGGTACGCGACTGGGCGTGGAGCATCTGGTCGCATTGGGTCACCAGCAAATCGCGCTGTTAGCGGGCCCATTAAGTTCTGTCTCGGCGCGTCTGCGTCTGGCTGGCTGGCATAAATATCTCACTCGCAATCAAATTCAGCCGATAGCGGAACGGGAAGGCGACTGGAGTGCCATGTCCGGTTTTCAACAAACCATGCAAATGCTGAATGAGGGCATCGTTCCCACTGCGATGCTGGTTGCCAACGATCAGATGGCGCTGGGCGCAATGCGCGCCATTACCGAGTCCGGGCTGCGCGTTGGTGCGGATATCTCGGTAGTGGGATACGACGATACCGAAGATAGCTCATGTTATATCCCGCCGTTAACCACCATCAAACAGGATTTTCGCCTGCTGGGGCAAACCAGCGTGGACCGCTTGCTGCAACTCTCTCAGGGCCAGGCGGTGAAGGGCAATCAGCTGTTGCCAGTCTCACTGGTGAAAAGAAAAACCACCCTGGCGCCCAATACGCAAACCGCCTCTCCCCGCGCGTTGGCCGATTCATTAATGCAGCTGGCACGACAGGTTTCCCGACTGGAAAGCGGGCAGTGATAA";
    	//* rbsTetR:
    	String rbsTetRSequence="TCCAGGAGGAAAAAA";
    	//* tetR:
    	String tetRSequence="ATGTCCAGATTAGATAAAAGTAAAGTGATTAACAGCGCATTAGAGCTGCTTAATGAGGTCGGAATCGAAGGTTTAACAACCCGTAAACTCGCCCAGAAGCTAGGTGTAGAGCAGCCTACATTGTATTGGCATGTAAAAAATAAGCGGGCTTTGCTCGACGCCTTAGCCATTGAGATGTTAGATAGGCACCATACTCACTTTTGCCCTTTAGAAGGGGAAAGCTGGCAAGATTTTTTACGTAATAACGCTAAAAGTTTTAGATGTGCTTTACTAAGTCATCGCGATGGAGCAAAAGTACATTTAGGTACACGGCCTACAGAAAAACAGTATGAAACTCTCGAAAATCAATTAGCCTTTTTATGCCAACAAGGTTTTTCACTAGAGAATGCATTATATGCACTCAGCGCTGTGGGGCATTTTACTTTAGGTTGCGTATTGGAAGATCAAGAGCATCAAGTCGCTAAAGAAGAAAGGGAAACACCTACTACTGATAGTATGCCGCCATTATTACGACAAGCTATCGAATTATTTGATCACCAAGGTGCAGAGCCAGCCTTCTTATTCGGCCTTGAATTGATCATATGCGGATTAGAAAAACAACTTAAATGTGAAAGTGGGTCCTAATAA";
    	
    	//https://www.nature.com/articles/nature11516#SupplementaryTableS4 && https://www.uniprot.org/uniprotkb/P95429/entry
    	//Verified the NA sequence's translation information against the  AA sequence in P95429.    	
    	String exsDSequence = "ATGGAGCAGGAAGACGATAAGCAGTACTCCCGAGAAGCGGTGTTCGCTGGCAGGCGGGTATCCGTGGTGGGCTCGGACGCCCGCTCGCGGGGTCGGGTGCCGGGTTACGCATCGAGCAGTTTGTATCGTGAGTCCGGAATCATCAGTGCGCGGCAACTGGCGTTGCTGCAGCGGATGCTGCCGCGCCTGCGGCTGGAGCAACTGTTCCGCTGCGAGTGGTTGCAGCAGCGCCTGGCGCGCGGCCTGGCGCTGGGGCGCGAAGAGGTGCGGCAGATTCTCCTCTGCGCGGCGCAGGACGACGACGGCTGGTGCTCCGAACTGGGCGACCGGGTCAACCTCGCCGTGCCGCAGTCGATGATCGACTGGGTCCTGCTGCCGGTCTATGGCTGGTGGGAAAGCCTGCTCGACCAGGCGATCCCCGGCTGGCGCCTGTCGCTGGTGGAGCTGGAGACCCAGTCCCGGCAACTGCGAGTCAAGTCCGAATTCTGGTCCCGCGTGGCCGAGCTGGAGCCGGAGCAGGCCCGCGAGGAACTGGCCAGGGTCGCCAAGTGCCAGGCGCGCACCCAGGAACAGGTGGCCGAACTGGCCGGCAAGCTGGAGACGGCTTCGGCACTGGCGAAGAGCGCCTGGCCGAACTGGCAGCGGGGCATGGCGACGCTGCTCGCCAGCGGCGGGCTGGCCGGCTTCGAGCCGATCCCCGAGGTCCTCGAATGCCTCTGGCAACCTCTCTGCCGGCTGGACGACGACGTCGGCGCGGCGGACGCCGTCCAGGCCTGGCTGCACGAACGCAACCTGTGCCAGGCACAGGATCACTTCTACTGGCAGAGCTGA";
    	    	
    	//https://www.nature.com/articles/nature11516#SupplementaryTableS4 && https://www.uniprot.org/uniprotkb/P26993/entry
    	String exsASequence = "ATGCAAGGAGCCAAATCTCTTGGCCGAAAGCAGATAACGTCTTGTCATTGGAACATTCCAACTTTCGAATACAGGGTAAACAAGGAAGAGGGCGTATATGTTCTGCTCGAGGGCGAACTGACCGTCCAGGACATCGATTCCACTTTTTGCCTGGCGCCTGGCGAGTTGCTTTTCGTCCGCCGCGGAAGCTATGTCGTAAGTACCAAGGGAAAGGACAGCCGAATACTCTGGATTCCATTATCTGCCCAGTTTCTACAAGGCTTCGTCCAGCGCTTCGGCGCGCTGTTGAGTGAAGTCGAGCGTTGCGACGAGCCCGTGCCGGGCATCATCGCGTTCGCTGCCACGCCTCTGCTGGCCGGTTGCGTCAAGGGGTTGAAGGAATTGCTTGTGCATGAGCATCCGCCGATGCTCGCCTGCCTGAAGATCGAGGAGTTGCTGATGCTCTTCGCGTTCAGTCCGCAGGGGCCGCTGCTGATGTCGGTCCTGCGGCAACTGAGCAACCGGCATGTCGAGCGTCTGCAGCTATTCATGGAGAAGCACTACCTCAACGAGTGGAAGCTGTCCGACTTCTCCCGCGAGTTCGGCATGGGGCTGACCACCTTCAAGGAGCTGTTCGGCAGTGTCTATGGGGTTTCGCCGCGCGCCTGGATCAGCGAGCGGAGAATCCTCTATGCCCATCAGTTGCTGCTCAACAGCGACATGAGCATCGTCGACATCGCCATGGAGGCGGGCTTTTCCAGTCAGTCCTATTTCACCCAGAGCTATCGCCGCCGTTTCGGCTGCACGCCGAGCCGCTCGCGGCAGGGGAAGGACGAATGCCGGGCTAAAAATAACTGA"; 
    	    	
    	//Circuit A
    	Sequence pLacSeqEntity=createDNASequence(sbolDesign, "pLacI", pLacISequence);
    	pLacSeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS8_LacITetRSensorModule"));
    	pLacSeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS9_PLacI"));
    	    	    	
    	Sequence rbsLacISeqEntity=createDNASequence(sbolDesign, "rbsLacI", rbsLacISequence);
    	rbsLacISeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS8_LacITetRSensorModule"));
    	
    	Sequence cdsASeqEntity=createDNASequence(sbolDesign, "cdsA", exsDSequence);
    	cdsASeqEntity.addWasDerivedFrom(URI.create("https://www.nature.com/articles/nature11516#SupplementaryTableS4_exsD"));
    	cdsASeqEntity.addWasDerivedFrom(URI.create("https://www.uniprot.org/uniprotkb/P95429/entry"));
    	    	
    	Sequence terSeqEntity=createDNASequence(sbolDesign, "ter1", terSequence);
    	terSeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS9_L3S2P21"));
    	terSeqEntity.addWasDerivedFrom(URI.create("https://parts.igem.org/Part:BBa_K2675031"));
    	    	    	    	    	
    	String circuitASequence=pLacISequence + rbsLacISequence + exsDSequence + terSequence;
    	createDNASequence(sbolDesign, "A_circuit", circuitASequence);
    	    	    	
    	
    	//Circuit B
    	Sequence pTetSeqEntity=createDNASequence(sbolDesign, "pTet", pTetSequence);
    	pTetSeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS9_PTet"));
    	
    	Sequence rbsTetRSeqEntity=createDNASequence(sbolDesign, "rbsTetR", rbsTetRSequence);
    	rbsTetRSeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS8_LacITetRSensorModule"));
    	
    	Sequence cdsBSeqEntity=createDNASequence(sbolDesign, "cdsB", exsASequence);
    	cdsBSeqEntity.addWasDerivedFrom(URI.create("https://www.nature.com/articles/nature11516#SupplementaryTableS4_exsA"));
    	cdsBSeqEntity.addWasDerivedFrom(URI.create("https://www.uniprot.org/uniprotkb/P26993/entry"));
    	    	
    	String circuitBSequence=pTetSequence + rbsTetRSequence + exsASequence + terSequence;
    	createDNASequence(sbolDesign, "B_circuit", circuitBSequence);

    	
    	//Sensor specific    	
    	Sequence pConstLacISeqEntity=createDNASequence(sbolDesign, "pConstLacI", pConstSequence);
    	pConstLacISeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS9_BBa_J23101"));
    	pConstLacISeqEntity.addWasDerivedFrom(URI.create("https://parts.igem.org/Part:BBa_J23101"));
    	    	    	
    	Sequence rbs1SeqEntity=createDNASequence(sbolDesign, "rbs1", rbsConstSequence);
    	rbs1SeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryFigureS34"));
    	rbs1SeqEntity.addWasDerivedFrom(URI.create("https://parts.igem.org/Part:BBa_B0064"));        
    	
    	Sequence cdsLacISeqEntity=createDNASequence(sbolDesign, "cdsLacI", lacISequence);
    	cdsLacISeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS8_LacITetRSensorModule"));
    	cdsLacISeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS9_lacI"));
    	            	
    	Sequence pConstTetRSeqEntity=createDNASequence(sbolDesign, "pConstTetR", pConstSequence);    	
    	pConstTetRSeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS9_BBa_J23101"));
    	pConstTetRSeqEntity.addWasDerivedFrom(URI.create("https://parts.igem.org/Part:BBa_J23101"));
    	
    	Sequence rbs2SeqEntity =createDNASequence(sbolDesign, "rbs2", rbsConstSequence);
    	rbs2SeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryFigureS34"));
    	rbs2SeqEntity.addWasDerivedFrom(URI.create("https://parts.igem.org/Part:BBa_B0064"));
            	
    	Sequence cdsTetRSeqEntity=createDNASequence(sbolDesign, "cdsTetR", tetRSequence);
    	cdsTetRSeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS8_LacITetRSensorModule"));
    	cdsTetRSeqEntity.addWasDerivedFrom(URI.create("https://doi.org/10.1126/science.aac7341#SupplementaryTableS9_tetR"));    	
    	    	
    	String circuitSensorSequence=pConstSequence + rbsConstSequence + lacISequence + terSequence + pConstSequence + rbsConstSequence + tetRSequence + terSequence;  ;
    	createDNASequence(sbolDesign, "Sensor_circuit", circuitSensorSequence);
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
	
	private ComponentDefinition getComponent(SBOLDocument doc, String name)
	{
		ComponentDefinition found=null;
		Set<ComponentDefinition> components=doc.getComponentDefinitions();
		if (doc!=null && doc.getComponentDefinitions()!=null)
		{
			for (ComponentDefinition cd: components)
			{
				if (cd.getDisplayId().equals(name))
				{
					found=cd;
					break;
				}
			}
		}
		return found;		
	}
	
	private Sequence createDNASequence(SBOLDocument doc, String componentName, String sequence) throws SBOLValidationException
	{
		ComponentDefinition component=getComponent(doc, componentName);
		return createDNASequence(doc, component, componentName + "_seq", sequence);			
	}
	
	private Sequence createDNASequence(SBOLDocument doc, ComponentDefinition component, String name, String na) throws SBOLValidationException
	{
		Sequence sequence=doc.createSequence(name, na, Sequence.IUPAC_DNA);
		component.addSequence(sequence.getIdentity());		
		return sequence;						
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
		
		SBOLDocument sbolDesign=bioSign.getGeneticDesign();
		SBOLWriter.write(sbolDesign, new File("abcomm.sbol"));
    	
    	System.out.println("...done!");
	}
	private SBOLDocument getGeneticDesign() {
		return sbolDesign;
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
