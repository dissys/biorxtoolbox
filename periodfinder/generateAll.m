%% GENERATE ALL
% This function is mainly used to generate all 1-bit signals for evaluation

%%% Input:
% * The parameters and bit sequence of the signal that needs to be simulated

%%% Output:
% * The density of the aTc and IPTG molecules wrt time

%%% Parameters:
% * alpha:          integer or array - M*(1-alpha) = IPTG, M*alpha = aTc
% * delay:          integer or array - # of seconds between emissions of IPTG and aTc
% * M:              integer          - total # of molecules
% * offset:         integer          - to start the algorithm in the middle (giving 0 value starts it from the beginning)
% * signal duration:integer          - initial signal duration (not optimized yet). should be long enough to observe one full signal.

function genAll = generateAll(alpha,delay,M,offset,signalDuration,bitSeqStr,TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER)
genAll = false;
counter = 0;


%%%
% * this loop simulates the given bit sequence for each alpha - delay couple
for a = alpha
    for d = delay        
        counter = counter +1;    
        counter
        if (counter > offset)  
            generateOne(a,d,M,signalDuration,bitSeqStr, TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER)             
        end
        
    end
end
genAll = true;
