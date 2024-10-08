%% PERIOD FINDER
% finds the near minimum symbol duration that minimizes the interference between
% two consecutive symbols

%%% Input:
% * A one bit signal data
% * Simulation of the cell with no molecules coming from the environment (Base state of the cel)

%%% Output:
% * The near minimum symbol duration

%%% Parameters:
% * alpha:          integer or array - M*(1-alpha) = IPTG, M*alpha = aTc
% * delay:          integer or array - # of seconds between emissions of IPTG and aTc
% * Mol:            integer          - total # of molecules
% * offset:         integer          - to start the algorithm in the middle (giving 0 value starts it from the beginning)
% * signal duration:integer          - initial signal duration (not optimized yet). should be long enough to observe one full signal.

function periodFinder = allPeriods(alpha, delay, Mol,signalDuration, plotFolder,matFileName_zeros,bit_sequence_zeros, oneBitSeq,heatmapFile,showFigures, TMP_FOLDER)

%%% 
% * Reading the base state cell data
counter = 0;
sizeAlpha = size(alpha,2); %E.g.: alpha= 0.15,0.20,0.25,0.30,0.35,0.40,0.45,0.500,0.55,0.60
sizeDelay = size(delay,2); %E.g.: delay= 0,100,200,300,400,500,600,700,800,900,1000

result = ones(sizeAlpha,sizeDelay);
result = -1*result;
           
fullMatFileName_zeros = fullfile(plotFolder,  matFileName_zeros);
if ~exist(fullMatFileName_zeros, 'file')
    message = sprintf('%s does not exist', fullMatFileName_zeros);
    error(message);
else
    s_zeros = load(fullMatFileName_zeros);
    disp('Log - PeriodFinder: Loading the mat file ' +  matFileName_zeros);
end

%Data was sampled using 1001 values to create 1000 time intervals in
%GenerateOne. Hence the length of time_zeros,dataA_zeros and dataB_zeros
%are 1001.
dataA_zeros = s_zeros.signalData(:,7); %TODO: B_in
dataB_zeros = s_zeros.signalData(:,8); %TODO: A_in

time_zeros = s_zeros.t; % there is also a time variable in s. click on s in workspace to see
%time_zeros

%Period=1001/7bits=143
period = length(time_zeros)/strlength(bit_sequence_zeros);

%%%
% Calculating the ratio of Bin/Ain in the base state of the cell.
% rate2: The Bin/Ain rate when the simulation ends.
rate2 = dataA_zeros(length(dataA_zeros))/dataB_zeros(length(dataB_zeros));

rate1 = dataA_zeros(length(dataA_zeros)-period)/dataB_zeros(length(dataB_zeros)-period);

%Compare the final rate with the previous bit's value. If the difference is
%significant, then the the system has not reached the equilibrium yet.
if(rate1 - rate2 < 0.001) %e.g. both rates are 4.8688
    zero_rate = rate2;
else
    message="rate is not steady, give a longer time for stabilization";
    error(message)
end

disp('Zero rate: ' + sprintf("%.5f",zero_rate));
     
col = 0;
row = 0;

%%% 
% * calculates the near optimal symbol duration for all alpha-delay couples
% given as input
for a = alpha
    col = col + 1;
    row = 0;
    
    for d= delay
        row = row +1;        
        counter = counter +1;       
              
        %%% 
        % * Reading one of the one-bit signal data    
        AMol = Mol*(1-a);
        BMol = Mol*a;
        
        dirName= getName(AMol, BMol, signalDuration, d, oneBitSeq);
        matFileName = strcat(dirName,".mat");
        disp('Log - Calculating rate values for ' + matFileName +  " - alpha:" + sprintf("%.3f,",a) + " delay:" + sprintf("%.0f",d));
                               
        fullMatFileName = fullfile(plotFolder,  matFileName);
        if ~exist(fullMatFileName, 'file')
            message=sprintf('%s does not exist', fullMatFileName);
            error(message);
        else
            s = load(fullMatFileName);
        end
        % Reading from the data array. 
        dataA = s.signalData(:,7); %TODO: B_in
        dataB = s.signalData(:,8); %TODO: A_in
        time = s.t;    
        
        %%%
        % * calculating the current rate of IPTG and aTc
        rate = dataA ./ dataB;
       
        disp('Log - onebit_A/B_ratio: ' + sprintf("%.3f,",rate));
        
        %%%
        % * finding the intervals where rate is close to base rate
        [pks,locs] = findpeaks(rate); %TODO: Not used later!       
        [M,I] = max(rate); % M:max(Bin/Ain), I:t
        [M1,I1] = max(dataB);% M1:max(Ain), I1:t
        % The simulation duration is split into 1000 intervals. 
        % For 13 bits with 1500 symbol duration deltaT: (13*1500)/1000=19.5
        deltaT = time(2)-time(1); 

        %GM: For debugging only:
        %if d==900    
            
        %    max(rate)
        %    M
        %    I
        %    M1
        %    I1
        %    plot(dataB)
        %    plot(dataA)
        %    plot(rate);
        
        %    error ('gmgmx')
        %end
         
        %Find the minimum t that comes after the I1 time point (when
        %Bin/Ain is maximum) and after the I2 time point (when Ai is the
        %maximum). Condition: The rate should be within minus or plus 5% of
        %the zero rate (the rate at the native state). The symbol duration
        %must also be bigger than 100 seconds. This is the differen between
        %the found time point minus the warmup period (two times onebitperiod)
        newdata = [];
        j=1;
        onebitPeriod=length(time)/strlength(oneBitSeq);
        for i = 1:length(rate)
            if(rate(i) <= zero_rate*1.05 && rate(i)>=zero_rate*0.95)
                 if i > I && i> I1 && i-(onebitPeriod*2)>100  
                    newdata(j) = i;
                    j = j + 1;                    
                end
            end
        end
        
        minSufficientTimePoint=min(newdata);
        
        imageFolder=strcat(TMP_FOLDER,dirName);
        mkdir(char(imageFolder));           
        figureParameters.showFigures=showFigures;
        figureParameters.index=1;
        figureParameters.x=100;
        figureParameters.y=100;
        figureParameters.imageFolder=imageFolder;
        figureParameters.prefix='OptimiseDuration';
       
        %if d==0 && a==0.25  %Comment        
            figureParameters=displayFigure(figureParameters, dataA, 'BinScaled');
            figureParameters=displayFigure(figureParameters, dataB, 'AinScaled');            
            figureParameters=saveRateFigure(figureParameters, rate, 'rateScaled',I,I1,minSufficientTimePoint,onebitPeriod, oneBitSeq);
            %f=gcf; %Comment
            %f.Visible='on'; %Comment

            %error ('gmgm'); %Comment
        %end %Comment

        %length(time)/strlength(oneBitSeq): Timepoints allocated to each
        %bit:1000/13
        %length(time)/strlength(oneBitSeq) * 2: Take out the timepoints for
        %the first two bits that start with zero (00100...)
        %min(newdata): Includes the time point for the end of the signal
        %min(newdata)-(length(time)/strlength(oneBitSeq))*(2): Must include
        %the timepoints for the one bit signal
        %GM: rsl: By multiplying the number of time points by deltat time
        %increment we find the actual signal duration. E.g. for 19500
        %seconds of simulation the time is split into 1000 intervals and
        %each time slot is 19.5 seconds (deltaT)
        
        % * Finding the interval with minimum symbol duration and saving it
        % if its lss the 15000 seconds. 
        %rsl=(min(newdata)-(length(time)/strlength(oneBitSeq))*(2))*deltaT;
        rsl=(minSufficientTimePoint-(onebitPeriod*2))*deltaT;
        

        if(~isempty(rsl) && rsl < 15000)
            result(col,row)=rsl;
        end        
    end

end
result

save(heatmapFile,'result', 'alpha','delay');

[fid,msg] = fopen(heatmapFile + ".txt",'w');
assert(fid>=3,msg)
fprintf(fid,'%d\n',result)
fclose(fid);

periodFinder = true;





