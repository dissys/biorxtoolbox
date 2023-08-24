%% MOL-Eye Score Calculator
% * Moleye score is the area of the eye aparture of the given signal.

%%% Input:
% * The parameters and the heatmap file of signals.

%%% Output:
% * The moleye dcore and the parameters of the signals generated by
% generateNice.

%%% Parameters:
% * heatmap:        alpha x delay matrix - heatmapFile of symbol durations that periodFinder
% generates
% * alpha:          integer or array     - M*(1-alpha) = IPTG, M*alpha = aTc
% * delay:          integer or array     - # of seconds between emissions of IPTG and aTc
% * M:              integer              - total # of molecules
% * Max Period      integer          - maximum symbol duration desired


function moleyeScore = molEyeScore(heatmapFile,alpha,delay,M,maxPeriod,bitSeqStr,plotFolder,imageFolder)

S = load(heatmapFile);
delayMat = S.result;

i1 = 0;
j1 = 0;
size = 0;
for a= alpha
    j1 = j1 + 1;
    i1 = 0;
    for d = delay
        i1 = i1 + 1;
        intDelay = round(delayMat(j1,i1));
        if(intDelay < maxPeriod)
            size = size + 1;
        end
    end
end
moleyeScores = zeros(size,4);

i1 = 0;
j1 = 0;
counter = 1;
for a= alpha
    j1 = j1 + 1;
    i1 = 0;
    for d = delay
        i1 = i1 + 1;
        intDelay = round(delayMat(j1,i1));
        if(intDelay < maxPeriod)
            moleyeScores(counter,1) = moleye((1-a)*M,a*M,d,intDelay,bitSeqStr,plotFolder,imageFolder);
            moleyeScores(counter,2) = a;
            moleyeScores(counter,3) = d;
            moleyeScores(counter,4) = intDelay;
            counter = counter + 1;
        end
    end
end

moleyeScore = moleyeScores;
