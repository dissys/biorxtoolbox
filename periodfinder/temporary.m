%% Parameters needed for most of the functions are defined here
set(0,'DefaultFigureVisible','on');
alpha = 0.15:0.05:0.6;

delay = 0:100:1000;
M = 3500000;
signalDur = 1500;
maxPeriod = 2000;
bitSeqStr = "0010111100101";
oneBitSeq = "0010000000000";

outputFolder="../output_d_200";
TEST_DATA_FOLDER = fullfile (outputFolder,"testData/");%"../output/testData/";
PLOT_FOLDER = fullfile (outputFolder,"plotData/"); % "../output/plotData/";
IMAGE_FOLDER =fullfile (outputFolder,"images/"); %"../output/images/";
HEATMAP_FOLDER = fullfile (outputFolder,"/heatmap/"); %"../output/heatmap/";
TMP_FOLDER = fullfile (outputFolder, "tmp/");%"../output/tmp/";
showFigures=0;
heatmapFile = fullfile (HEATMAP_FOLDER,"heatmapfile.mat");%'heatmapfile.mat';


M_periodFinder = 3000000;
signalDur_periodFinder=3000;
tshift_periodFinder=100; 
bitSeq_periodFinder="0000000"; 
alpha_periodFinder=0;
A_periodFinder= M_periodFinder * (1-alpha_periodFinder);
B_periodFinder= M_periodFinder * alpha_periodFinder;


%A2975000_B525000_K_TS1443_Seq0010111100101_TShift600
%A2975000_B525000_K_TS1443_Seq0010111100101_TShift600.mat

%for a= alpha
%     for d= delay
%        disp('   Log: generateNice - i:' + ", alpha:" + sprintf("%0.2f",a) + ", delay:" + sprintf("%.0f",d))  
%     end
%end
%set(gcf,'position',[500,500,1000,300])

%Example: For detailed input images - Intercellular (External) to intracellular (internal) signalling
%generateOne(0.15,600,M,1500,bitSeqStr, TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER)
%plotter(M*(1-0.15), M*0.15,1500,bitSeqStr,600,PLOT_FOLDER, IMAGE_FOLDER);

%Figure 8A
generateOne(0,600,M,1443,bitSeqStr, TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER)
plotter(M,0,1443,bitSeqStr,600,PLOT_FOLDER, IMAGE_FOLDER);


%To generate images that show how symbol durations were calculated. The
%data is then used to create the heatmap.
%matFileName_zeros = strcat(getName(0, 0, signalDur_periodFinder, tshift_periodFinder, bitSeq_periodFinder),".mat")
%matFileName_zeros
%allPeriods(alpha,delay,M,signalDur,PLOT_FOLDER,matFileName_zeros,bitSeq_periodFinder, oneBitSeq,heatmapFile,1,TMP_FOLDER)


% Figure 5, the ouput without the ISI minimised. A=M, B=0
%plotter(M,0,signalDur,bitSeqStr,900,PLOT_FOLDER, IMAGE_FOLDER);


%figure
%x=[0,1,2,3,4,5,6,7,8,9,10,11,12];
%x(1)
%x(6)
%y=[5,6,7,8,9,10,11,12,10,9,6,7,7]
%plot(x,y);

