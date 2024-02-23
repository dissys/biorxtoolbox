%% Parameters needed for most of the functions are defined here
set(0,'DefaultFigureVisible','on');
alpha = 0.15:0.05:0.6;
delay = 0:100:1000;
M = 3500000;
signalDur = 1500;
maxPeriod = 2000;
bitSeqStr = "0010111100101";
oneBitSeq = "0010000000000";
TEST_DATA_FOLDER = "../output/testData/";
PLOT_FOLDER = "../output/plotData/";
IMAGE_FOLDER = "../output/images/";
HEATMAP_FOLDER = "../output/heatmap/";
TMP_FOLDER = "../output/tmp/";
showFigures=0;
heatmapFile = 'heatmapfile.mat';


%% Generates all the one shot signals with for each alpha and delay parameter pair
generateAll(alpha,delay, M, 0, signalDur,oneBitSeq,TEST_DATA_FOLDER,PLOT_FOLDER)

%% Example only and can be commented out. Generates files for a fixed signal duration for each alpha and delay pair parameter. The output files are not integrated with period finder.
generateAll(alpha,delay ,M, 0, signalDur,bitSeqStr,TEST_DATA_FOLDER,PLOT_FOLDER)

%% Parameters for when all bits are zero.
M_periodFinder = 3000000;%TODO: This is a different from the original parameter. Double check
signalDur_periodFinder=3000; %TODO: This is a different from the original parameter. Double check
tshift_periodFinder=100; %TODO: This is a different from the original parameter. Double check
bitSeq_periodFinder="0000000"; %TODO Should not we use 13 bits?
alpha_periodFinder=0;
A_periodFinder= M_periodFinder * (1-alpha_periodFinder);
B_periodFinder= M_periodFinder * alpha_periodFinder;

%% Figure 3A, A,B=0, Initial state. Creates A0_B0_K_TS3000_Seq0000000_TShift100.mat
matFileName_zeros = generateOne(alpha_periodFinder,tshift_periodFinder,0,signalDur_periodFinder,bitSeq_periodFinder, TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER) %TODO Check these parameters            

%% Figure 3B, A>0,B>0, The third bit is set to 1 only.
%Creates A2975000_B525000_K_TS1500_Seq0010000000000_TShift900.mat for the plotter example
generateOne(0.15,900,M,signalDur,oneBitSeq, TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER)            

%% Figure 5, the ouput without the ISI minimised. A=M, B=0
generateOne(alpha_periodFinder,900,M,signalDur,bitSeqStr, TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER)            

%% Figure 7, the ouput without the ISI minimised. A, B >0
generateOne(0.15,600,M,1425,bitSeqStr, TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER)

%% Calculates the minimum symbol duration and records them into a matrix ("heatmapfile.mat")
matFileName_zeros = strcat(getName(0, 0, signalDur_periodFinder, tshift_periodFinder, bitSeq_periodFinder),".mat")
allPeriods(alpha,delay,M,signalDur,PLOT_FOLDER,matFileName_zeros,bitSeq_periodFinder, oneBitSeq,heatmapFile,showFigures,TMP_FOLDER)

%%%
%* plots all the one bit signals
%{
for a = alpha
    for d = delay
        plotter(int2str(M*(1-a)),int2str(M*a),int2str(signalDur),"001000000",int2str(d));
    end
end
%}

%% Prints/saves the heatmap file generated from the heatmap matrix.
myheatmap(HEATMAP_FOLDER, heatmapFile, "heatmap1.png");

%% Plots spesific signals such as the initial states.

% Figure 3A, A,B=0, Initial state. Creates A3000000_B0_K_TS3000_Seq0000000_TShift100.mat
plotter(0, 0,signalDur_periodFinder,bitSeq_periodFinder,tshift_periodFinder,PLOT_FOLDER, IMAGE_FOLDER);

%%
% Figure 3B, A>0,B>0, The third bit is set to 1 only.
plotter(M*(1-0.15), M*0.15,signalDur,oneBitSeq,900,PLOT_FOLDER, IMAGE_FOLDER);

% Figure 5, the ouput without the ISI minimised. A=M, B=0
plotter(M,0,signalDur,bitSeqStr,900,PLOT_FOLDER, IMAGE_FOLDER);

% Figure 7, the ouput without the ISI minimised. A, B >0
plotter(M*(1-0.15), M*0.15,1425,bitSeqStr,600,PLOT_FOLDER, IMAGE_FOLDER); 

%% Generates all the multi-bit signals with symbol durations less then maxPeriod.
%Simulates the identified solutions that meets the fitness criteria (sd < maxPeriod)
generateNice(alpha,delay,M,maxPeriod,heatmapFile, bitSeqStr,TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER);

%% Plots all the signals simulated by generateNice
nicePlotter(heatmapFile,alpha,delay,M,maxPeriod,bitSeqStr,PLOT_FOLDER, IMAGE_FOLDER);

%% Calculates, sorts and display the signals with highest moleye score.
res = MolEyeScore(heatmapFile,alpha,delay,M,maxPeriod,bitSeqStr,PLOT_FOLDER, IMAGE_FOLDER);

%% Rank the scores
B = sortrows(res,1,'descend'); 
disp(num2str(B(1:5,:)));
disp(num2str(B));

moleyeFolder=fullfile(IMAGE_FOLDER, "moleye");
resultFile= fullfile(moleyeFolder,  "ranked.txt");

num2str(B)

fileID = fopen(resultFile,'w');
fprintf(fileID,'%.1f\t %.2f\t %.f\t %.f\n', transpose(B));
            
for k = 1:size(B,1)
    a=B(k,2);
    d=B(k,3); 
    duration=B(k,4);
    name = getName((1-a)*M,a*M, duration, d, bitSeqStr);
    score= B(k,1); 
    disp('Log - main: ' + name + ", score:" + sprintf("%.000f",score))
    fprintf(fileID, name + ", Score:" + sprintf("%.000f",score) + '\n');
end

fclose(fileID);

close all