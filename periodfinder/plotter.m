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
jpgFileAnnotated = fullfile(imageFolder,strcat(name, "_annotated.png"));
fullMatFileName = fullfile(plotFolder,  matFileName);
if ~exist(fullMatFileName, 'file')
	matFileName 
    fullMatFileName
    error("does not exists!")
else   
    s = load(fullMatFileName);
    figure1 = figure('Name', matFileName);
    set(gcf, 'Position',  [100, 100, 1200, 400])
    xlabel("time (s)");
    grid on;
    ylabel("concentration(nmol)");
   
    hold on
    p1=plot(s.t,s.signalData(:,8),'LineWidth',2,'DisplayName','A_{in}');
    aColor=p1.Color;
    p2=plot(s.t,s.signalData(:,7),'LineWidth',2,'DisplayName','B_{in}');
    bColor=p2.Color;
    
    legend
    saveas(figure1,jpgFile);

    %Create the annotated figure with bits and the warm-up period
    legend('AutoUpdate','off');        
    gray = [.3 .3 .3];   
    lightyellow="#FFE5B4";
    yl=ylim;
    %bitDataYPosition=yl(2) + 5; % yposition: figure height plus 5
    bitDataYPosition=yl(2)+yl(2)*0.02 + 0.02;
    for i=1:strlength(bitSeqStr)
        xline(signalDuration * i,'--'); % Draw a vertical line for each signal (bit) duration
        text(signalDuration * i - (signalDuration/2) ,bitDataYPosition,bitSeqStr{1}(i),'Color',gray,'FontSize',14);        
    end
    %Colour the warm up period
    r=rectangle('Position',[0,0,signalDuration*2,yl(2)]);%'FaceColor',[0.7,0.7,0.7])
    r.FaceColor=lightyellow;
    r.EdgeColor='none';
    %Remove the previous plots and redraw them so that they appear on top
    delete(p1);
    delete(p2);
    legend('AutoUpdate','on');            
    plot(s.t,s.signalData(:,8),'LineWidth',2,'DisplayName','A_{in}','Color',aColor);
    plot(s.t,s.signalData(:,7),'LineWidth',2,'DisplayName','B_{in}','Color',bColor);
    legend;
        
    saveas(figure1,jpgFileAnnotated);    
end

