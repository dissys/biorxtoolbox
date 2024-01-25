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
heatmapFile = 'heatmapfile.mat';


%A2975000_B525000_K_TS1443_Seq0010111100101_TShift600
%A2975000_B525000_K_TS1443_Seq0010111100101_TShift600.mat

for a= alpha
     for d= delay
        disp('   Log: generateNice - i:' + ", alpha:" + sprintf("%0.2f",a) + ", delay:" + sprintf("%.0f",d))  
     end
          
end