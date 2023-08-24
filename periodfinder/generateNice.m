%% GENERATE NICE
% This function generates all the signals that has shorter symbol duration
% than desired. The symbol duration used here is calculated in the periodFinder.

%%% Input:
% * The parameters of the signal that needs to be simulated

%%% Output:
% * The density of the aTc and IPTG molecules wrt time

%%% Parameters:
% * alpha:          integer or array - M*(1-alpha) = IPTG, M*alpha = aTc
% * delay:          integer or array - # of seconds between emissions of IPTG and aTc
% * M:              integer          - total # of molecules
% * Max Period      integer          - maximum symbol duration desired
function genNice = generateNice(alpha,delay,M,maxperiod,heatMapFile,bitSeqStr,TEST_DATA_FOLDER,PLOT_FOLDER)
genNice = false;
counter = 0;

S = load(heatMapFile);
delayMat = S.result;
i1 = 0;
j1 = 0;
for a= alpha
    j1 = j1 + 1;
    i1 = 0;
    for d= delay
        i1 = i1 + 1;
        %%%            
            % * symbol duration must be an integer for the simulation. So
            % the values coming from periodfinder is rounded here.
        intDelay = round(delayMat(j1,i1));
        %%%            
            % * if symbol duration is -1 this means the symbol duration is
            % too long for practical purposes. To change this go
            % periodFinder.
            
        %%%            
            % * the signals with longer symbol duration than maxPeriod
            % parameter are not gonna be simulated.
        disp('Log: generateNice - i:' + sprintf("%.0f",i1)+ ", j:" + sprintf("%.0f",j1) + ", signal duration:" + sprintf("%.0f",intDelay) + ", alpha:" + sprintf("%.0f",a)  + ", delay:" + sprintf("%.0f",d) + ", A:" + sprintf("%.0f",M*(1-a)) + ", B:" + sprintf("%.0f",M*a))            

        if(intDelay < maxperiod && intDelay > 1)
            counter = counter +1;        
            disp('   Log: generateNice - i:' + sprintf("%.0f",i1)+ ", j:" + sprintf("%.0f",j1) + ", signal duration:" + sprintf("%.0f",intDelay))            
            generateOne(a,d,M,intDelay,bitSeqStr, TEST_DATA_FOLDER,PLOT_FOLDER)                                  
        end
    end
end

genNice = true;



% A2100000_B1400000_K_TS925_Seq0010111100101_TShift200  --> Duration: 3842
% A2450000_B1050000_K_TS1857_Seq0010111100101_TShift0  --> A2450000_B1050000_K_TS1872_Seq0010111100101_TShift0
% A2450000_B1050000_K_TS885_Seq0010111100101_TShift200 --> Duration: 2769
% A2625000_B875000_K_TS426_Seq0010111100101_TShift100 --> A2625000_B875000_K_TS429_Seq0010111100101_TShift100
% A2625000_B875000_K_TS588_Seq0010111100101_TShift200 --> A2625000_B875000_K_TS605_Seq0010111100101_TShift200
% A2800000_B700000_K_TS885_Seq0010111100101_TShift300 --> A2800000_B700000_K_TS897_Seq0010111100101_TShift300
% A2800000_B700000_K_TS925_Seq0010111100101_TShift400 --> A2800000_B700000_K_TS936_Seq0010111100101_TShift400
% A2800000_B700000_K_TS979_Seq0010111100101_TShift500 --> A2800000_B700000_K_TS995_Seq0010111100101_TShift500
% A2975000_B525000_K_TS1303_Seq0010111100101_TShift700 --> A2975000_B525000_K_TS1307_Seq0010111100101_TShift700
% A2975000_B525000_K_TS1317_Seq0010111100101_TShift800 --> A2975000_B525000_K_TS1326_Seq0010111100101_TShift800
% A2975000_B525000_K_TS1371_Seq0010111100101_TShift900 --> A2975000_B525000_K_TS1365_Seq0010111100101_TShift900
% A2975000_B525000_K_TS1425_Seq0010111100101_TShift600 --> A2975000_B525000_K_TS1443_Seq0010111100101_TShift600
% A2975000_B525000_K_TS1500_Seq0010111100101_TShift0
% A2975000_B525000_K_TS1500_Seq0010111100101_TShift100
% A2975000_B525000_K_TS1857_Seq0010111100101_TShift300 --> Duration: 7820
% A3500000_B0_K_TS1500_Seq0010111100101_TShift0
% A3500000_B0_K_TS1500_Seq0010111100101_TShift1371
% A3500000_B0_K_TS1500_Seq0010111100101_TShift1425
% A3500000_B0_K_TS1500_Seq0010111100101_TShift900







