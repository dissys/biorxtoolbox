%% GENERATE ONE
% This function is used to generate data for a single scenario. The data files and the model is created and the model simulation results are saved.

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
% * showFigures:    integer          - 1 to show figures, otherwise 0.
function generateInputSignal = genegenerateInputSignal (inputSignalParams, csvFileFullPath, bit_sequence, signal_duration, total_time, sampling_rate, figureParameters)

t = 1:total_time;
sig_rx = genThrDiffSignal(inputSignalParams, t);
sig_rx_pdf = sig_rx - [0 sig_rx(1:end-1)];

figureParameters=displayFigure(figureParameters, sig_rx, 'sig_rx');

figureParameters=displayFigure(figureParameters, sig_rx_pdf, 'sig_rx_pdf');

disp('Log - sig_rx        :' + sprintf("%.0f,",sig_rx))
disp('Log - sig_rx shifted:' + sprintf("%.0f,",[0 sig_rx(1:end-1)]))
disp('Log - sig_rx_pdf    :' + sprintf("%.0f,",sig_rx_pdf))

t_shift = 30;
sig_rx_pdf_sq = [sig_rx_pdf; ...
    [zeros(1,t_shift), sig_rx_pdf(1:end-t_shift)]; ...
    [zeros(1,2*t_shift), sig_rx_pdf(1:end-2*t_shift)]; ...
    [zeros(1,3*t_shift), sig_rx_pdf(1:end-3*t_shift)]; ...
    [zeros(1,4*t_shift), sig_rx_pdf(1:end-4*t_shift)] ...
    ];
disp('Log - sig_rx_pdf_sq    :' + sprintf("%.0f,",sig_rx_pdf_sq))

%An example with 15 values when tshift is zero:
%1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
%0,0,0,1,2,3,4,5,6, 7, 8, 9,10,11,12
%0,0,0,0,0,0,1,2,3, 4, 5, 6, 7, 8, 9
%0,0,0,0,0,0,0,0,0, 1, 2, 3, 4, 5, 6

figureParameters=displayFigure(figureParameters, sig_rx_pdf_sq, 'sig_rx_pdf_sq');

main_signal = sum(sig_rx_pdf_sq);
figureParameters=displayFigure(figureParameters, main_signal, 'main_signal');

sequence_signal = bit_sequence(1) * main_signal;

x = length(bit_sequence);

for i = 2:x
    sequence_signal = [sequence_signal; ...
        [zeros(1,(i-1)*signal_duration), bit_sequence(i) * main_signal(1:end-(i-1)*signal_duration)]];
end

sequence_signal_total = sum(sequence_signal);
figureParameters=displayFigure(figureParameters, sequence_signal_total, 'sequence_signal_total');

moving_average = 1/sampling_rate*ones(sampling_rate,1);
out = filter(moving_average,1,sequence_signal_total);
figureParameters=displayFigure(figureParameters, out, 'out');

x = stem(t(sampling_rate:sampling_rate:end), out(sampling_rate:sampling_rate:end));

x.Visible = 'off';
set(findall(gca, 'Type', 'Line'),'LineWidth',2);
if (figureParameters.showFigures==1)
    x.Visible='on';
end

csvwrite(csvFileFullPath,floor(x.YData));
figureParameters=displayFigure(figureParameters, floor(x.YData), 'input');
%if (showFigures.showFigures==0)
%    close all;
%end
generateInputSignal = figureParameters;



