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

function periodFinder = allPeriods(alpha, delay, Mol,signalDuration, plotFolder,matFileName_zeros,bit_sequence_zeros, oneBitSeq,heatmapFile)

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

dataA_zeros = s_zeros.signalData(:,7);
dataB_zeros = s_zeros.signalData(:,8);

time_zeros = s_zeros.t; % there is also a time variable in s. click on s in workspace to see

period = length(time_zeros)/length(bit_sequence_zeros);

%%%
% * calculating the ratio of IPTG/aTc in the base state of the cell.
rate2 = dataA_zeros(length(dataA_zeros))/dataB_zeros(length(dataB_zeros));
rate2
%TODO: Replaced the following line. length(dataA_zeros)=1001, period=0.
%Hence the difference is zero. The array(0) will throw an error since the
%arrays are 1 based in Matlab. As a solution. I used the first nonzero row.
%rate1 = dataA_zeros(length(dataA_zeros)-period)/dataB_zeros(length(dataB_zeros)-period);
rate1 = dataA_zeros(length(dataA_zeros)-period +2)/dataB_zeros(length(dataB_zeros)-period +2);
rate1

if(rate1 - rate2 < 0.001)%TODO: rate1-rate2 is currently negative. Is it ok to proceed here. What does 0.001 mean? Rate2: 4.8667, Rate1:2.6582
    zero_rate = rate2;
else
    message="rate is not steady, give a longer time for stabilization";
    error(message)
end

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
        disp('Log - onebit_A/B_ratio: ' + sprintf("%.3f,",rate) + "\talpha:" + sprintf("%.3f,",a) + "\tdelay:" + sprintf("%.3f,",d))
        
        %%%
        % * finding the intervals where rate is close to base rate
        [pks,locs] = findpeaks(rate); %TODO: Not used in the rest of the text        
        [M,I] = max(rate);
        [M1,I1] = max(dataB);
        deltaT = time(2)-time(1);
               
        newdata = [];
        j=1;
        for i = 1:length(rate)
            if(rate(i) <= zero_rate*1.05 && rate(i)>=zero_rate*0.95)
                 if i > I && i> I1
                    newdata(j) = i;
                    j = j + 1;                    
                end
            end
        end
        
        %length(time)/strlength(oneBitSeq): Timepoints allocated to each bit
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
        rsl=(min(newdata)-(length(time)/strlength(oneBitSeq))*(2))*deltaT;

        if(~isempty(rsl) && rsl < 15000)
            result(col,row)=rsl;
        end
    end

end
result
save(heatmapFile,'result', 'alpha','delay');
periodFinder = true;





