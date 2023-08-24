%% Plotter Func
% This function plots the signal with given parameters. And saves as png.

%%% Input:
% * The parameters of the signal.

%%% Output:
% * Plots one signal with given parameters if such signal data exists

%%% Parameters:
% * countAStr: String - # of A' emitted
% * countBStr: String - # of B' emitted
% * tSignalStr:String - symbol duration
% * bitSeqStr: String - bit sequence
% * shiftStr:  String - # of seconds between emissions of IPTG and aTc

function plotter = plotter(A,B,signalDuration,bitSeqStr,tshift,plotFolder, imageFolder)
name= getName(A,B, signalDuration, tshift, bitSeqStr)
matFileName = strcat(name,".mat")
if ~exist(imageFolder, 'dir')
    mkdir(imageFolder)
end  

jpgFile = fullfile(imageFolder,strcat(name, ".png"));
fullMatFileName = fullfile(plotFolder,  matFileName);
if ~exist(fullMatFileName, 'file')
	matFileName 
    error("does not exists!")
else   
    s = load(fullMatFileName);
    figure1 = figure('Name', matFileName);
    set(gcf, 'Position',  [100, 100, 1200, 400])
    xlabel("time(s)");
    grid on;
    ylabel("concentration(nmol)");
   
    hold on
    plot(s.t,s.signalData(:,8),'LineWidth',2,'DisplayName','A_{in}');
    plot(s.t,s.signalData(:,7),'LineWidth',2,'DisplayName','B_{in}');
    legend
    saveas(figure1,jpgFile);
end

