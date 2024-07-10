function molEye = moleye(A,B,tShift,signalDuration,bitSeqStr,plotFolder,imageFolder)

moleyeFolder=fullfile(imageFolder, "moleye");

if ~exist(moleyeFolder, 'dir')
    mkdir(moleyeFolder);
end           

bit_sequence = transpose(num2str(bitSeqStr) - '0');

dirName= getName(A, B, signalDuration, tShift, bitSeqStr);
matFileName = strcat(dirName,".mat");
                  
% reads the file from plotData and plots the whole signal. 
%I am not sure if this is the true density data we should use in moleye
%but it looks like it so i used this data.
%GMCommented: folder = '/home/neval/Desktop/492/preqReceiver/plotData';  % You specify this!
fullMatFileName = fullfile(plotFolder,  matFileName);
if ~exist(fullMatFileName, 'file')
  message=sprintf('%s does not exist', fullMatFileName)
  error(message);
else
  s = load(fullMatFileName);
end
%here i am reading from the data array. there are 4 rows of data in the
%struct. i plotted them all to see what are those values. if you also want
%to see what is the data i am reading from the file please activate the
%commented code below. I thought data1 is our density data because it
%looks like it. 
%data1 is plotted in figure 1.
dataB = s.signalData(:,7); 
dataA = s.signalData(:,8);
time = s.t; %there is also a time variable in s. click on s in workspace to see


%% here i am cutting the data1 into pieces and putting them in a matrix to 
%%visualize moleye. Eg: for 7 bits, there will be 7 pieces.
count1 = 0; % # (number of) ones in the bit sequence
count0 = 0; % # zeros in the bit sequence

numberOfWarmUpBits=2;
for i = 1:length(bit_sequence)
    if (bit_sequence(i) == 1) 
        count1 = count1 + 1;
    else
        count0 = count0 + 1;
    end             
end

period = length(dataA)/length(bit_sequence); % i redefined period because the data in data1 is sampled

oneArray = zeros(period,count1);
zeroArray = zeros(period,count0);
blue=[0, 0.4470, 0.7410];
%red= [0.8500, 0.3250, 0.0980]; %red
%red=[0.4940 0.1840 0.5560] %purple
%red=[0.9290 0.6940 0.1250] %orange
%red=[0.4660 0.6740 0.1880] %green
red=[0.6350 0.0780 0.1840];%darkred
green=[0.4660 0.6740 0.1880]; %green
orange=[0.9290 0.6940 0.1250]; %orange
purple=[0.4940 0.1840 0.5560];
darkgreen=[0 0.5 0];

j = 1;
k = 1;
figMolEyeCont=figure('Name', matFileName + "_moleye_I_continuous");
set(gcf, 'Position',  [100, 100, 1200, 400]);
xlabel("Time (s)", 'FontSize',14);
grid on;
ylabel("Particle numbers", 'FontSize',14);
set(gca, 'FontSize', 14)
   
hold on
for i = 1:length(bit_sequence)   
   %if(i == 1)
   %    plot(((i-1)*period)+1:i*period, dataA(((i-1)*period)+1:i*period) , "b",'LineWidth',2); 
   %elseif (bit_sequence(i) == 1)        
   %    plot(((i-1)*period):i*period, dataA(((i-1)*period):i*period) , "b",'LineWidth',2);       
   %else        
   %    plot(((i-1)*period):i*period, dataA(((i-1)*period):i*period) , "r",'LineWidth',2);       
   %end   
   
   start=(i-1)*period;   
   endPos=i*period;

   %Matlab is one based. If i=1 start from t=1    
   if (i==1)
       start=start+1;
   end
      
   if (bit_sequence(i) == 1)        
       plot(start:endPos, dataA(start:endPos) , 'Color',blue,'LineWidth',2);       
   else        
       plot(start:endPos, dataA(start:endPos) , 'Color', red,'LineWidth',2);       
   end   
end

%%Show the 0 and 1 bits at the top
gray = [.3 .3 .3];   
yl=ylim;
bitDataYPosition=yl(2)+yl(2)*0.02 + 0.02; % yposition: figure height plus   

for i=1:strlength(bitSeqStr)
    xline(period * i,'--'); % Draw a vertical line for each signal (bit) duration
    text(period * i - (period/2) ,bitDataYPosition,bitSeqStr{1}(i),'Color',gray,'FontSize',14);        
end
    

saveas(figMolEyeCont,fullfile(moleyeFolder, figMolEyeCont.Name + ".png"));
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Overlay figure:
for i = 1:length(bit_sequence)
    current = dataA(((i-1)*period)+1:i*period);
    if (bit_sequence(i) == 1) 
        oneArray(:,j) = current;        
        j = j + 1;
    else
        zeroArray(:,k) = current;       
        k = k + 1;
    end             
end

%Ignore the warmup period bits that are all zero - the first two bits. Load
%the columns from 3 to the end:
zeroArray=zeroArray(:,[1+numberOfWarmUpBits:end]);


%this is the part that plots ones and zeros on the same plot 
%blue lines are ones and the red lines are zeros in figure 2.

figMolEyes=figure('Name', matFileName + "_moleye_II_overlayed");
set(gcf, 'Position',  [100, 100, 400, 400])
xlabel("Time (s)", 'FontSize',14);
grid on;
ylabel("Particle numbers", 'FontSize',14);
set(gca, 'FontSize', 14)


hold on
plot(1:period, oneArray , 'Color', blue, 'LineWidth',1);
plot(1:period, zeroArray , 'Color', red, 'LineWidth',1);

saveas(figMolEyes,fullfile(moleyeFolder, figMolEyes.Name + ".png"));

%% Identify the min and max plots and draw them
%here i am taking the mininum of ones and maximum of zeros and putting them
%in a seperate array, then taking the difference for each data point and
%summing this difference up. basicly calculating the area between the
%lowest one and highest 0.
%the bigger this score gets the better the signal quality is.
%also i plotted the minimum of ones and maximum of zeros in figure 3.
minOne = min(oneArray,[],2);% Returns a column vector containing the minimum value of each row
maxZero = max(zeroArray,[],2);

figSelectedMolEye=figure('Name', matFileName + "_moleye_III_max_min");
set(gcf, 'Position',  [100, 100, 400, 400]);
xlabel("Time (s)", 'FontSize',14);
grid on;
ylabel("Particle numbers", 'FontSize',14);
set(gca, 'FontSize', 14)


hold on
plot(1:period,minOne(:,1), 'magenta','LineWidth',2);
plot(1:period,maxZero(:,1), 'Color',darkgreen,'LineWidth',2);
saveas(figSelectedMolEye,fullfile(moleyeFolder, figSelectedMolEye.Name + ".png"));



figSelectedMolEye=figure('Name', matFileName + "_moleye_IV_max_min_vs_all");
set(gcf, 'Position',  [100, 100, 400, 400]);

xlabel("Time (s)", 'FontSize',14);
grid on;
ylabel("Particle numbers", 'FontSize',14);
set(gca, 'FontSize', 14)

hold on
plot(1:period,minOne(:,1), 'magenta','LineWidth',2);
plot(1:period,maxZero(:,1), 'Color',darkgreen,'LineWidth',2);
plot(1:period, oneArray , 'Color', blue, 'LineWidth',1,'LineStyle','--');
plot(1:period, zeroArray , 'Color', red, 'LineWidth',1,'LineStyle','--');

saveas(figSelectedMolEye,fullfile(moleyeFolder, figSelectedMolEye.Name + ".png"));

%% Score calculation: Calculate the score and plot the area used to calculate the score between min and max plots
difference = minOne - maxZero;
gray = [.3 .3 .3];

figScore=figure('Name', matFileName + "_moleye_V_max_min_difference");
set(gcf, 'Position',  [100, 100, 400, 400]);

xlabel("Time (s)", 'FontSize',14);
grid on;
ylabel("Particle numbers", 'FontSize',14);
set(gca, 'FontSize', 14)

hold on
plot(1:period,minOne(:,1), 'magenta','LineWidth',2);
plot(1:period,maxZero(:,1), 'Color',[0 0.5 0],'LineWidth',2);

%please note that if the difference between minimum of ones and maximum of
%zeros are below zero, i dont sum that value up. then i multiplied with the
%deltaT, difference of seconds between 2 samples.
for i = 1:period
    if (difference(i) <= 0) 
        difference(i) = 0;
      %xline(i,maxZero(i),minOne(i),'--'); % Draw a vertical line for each signal (bit) duration
    else
      %xline(i,'--',20,40); % Draw a vertical line for each signal (bit) duration
      plot([i i],[maxZero(i) minOne(i)],'Color',gray);
    end
end

%figSelectedMolEyeWithDifference=figure('Name', matFileName + "_moleye_IV_selectedWithDifference");
%GMGMname=matFileName + "_moleye_IV_selectedWithDifference";
saveas(figScore,fullfile(moleyeFolder, figScore.Name + ".png"));

deltaT = time(2)-time(1);
score = sum(difference)*deltaT;
%GM: Changed the score to narrow down moleye so that lower symbol durations
%are favoured.
%score = sum(difference)/deltaT;

%Delta corresponds to signal duration. E.g.: (1872 * 13)/1000=24.336 --> If
%the symbol duration is 1872, the total simulation duration is 1872*13. All
%simulations are split into 1000 steps. Hence, DeltaT=24.336 The
%timearray's length is 1001 for 1000 deltaT steps. Each bit has 1001/13=77
%deltaT

%% The area is marked using all min and max lines
figScoreAllPlots=figure('Name', matFileName + "_moleye_VI_max_min_difference_allplots");
set(gcf, 'Position',  [100, 100, 400, 400]);

xlabel("Time (s)", 'FontSize',14);
grid on;
ylabel("Particle numbers", 'FontSize',14);
set(gca, 'FontSize', 14)

hold on
%plot(1:period,minOne(:,1), 'magenta','LineWidth',2);
%plot(1:period,maxZero(:,1), 'Color',[0 0.5 0],'LineWidth',2);

%please note that if the difference between minimum of ones and maximum of
%zeros are below zero, i dont sum that value up. then i multiplied with the
%deltaT, difference of seconds between 2 samples.
plot(1:period, oneArray , 'Color', blue, 'LineWidth',1);
plot(1:period, zeroArray , 'Color', red, 'LineWidth',1);

for i = 1:period
    if (difference(i) <= 0) 
        %disp('diff:')
        %difference(i)
        difference(i) = 0;
      %xline(i,maxZero(i),minOne(i),'--'); % Draw a vertical line for each signal (bit) duration
    else
      %xline(i,'--',20,40); % Draw a vertical line for each signal (bit) duration
      plot([i i],[maxZero(i) minOne(i)],'Color',gray);
      %i
      %maxZero(i)
      %minOne(i)
      %difference(i)
      %error ('gmgm');
    end
end
saveas(figScoreAllPlots,fullfile(moleyeFolder, figScoreAllPlots.Name + ".png"));


disp('Log - moleye - file: ' + matFileName + ", score:" + sprintf("%.000f",score) + ", deltaT:" + sprintf("%.3f",deltaT) + ", difference:" + sprintf("%.0f,",sum(difference)))

molEye = score;


%%%Alternative moleye scoring to incorporate multiple min(Ain_bitone) and
%%%max (Ain_bitzero):
% integratedOneArray=zeros(period,1);
% 
% integratedZeroArray=zeros(period,1);
% 
% numberOfOneBits=size(oneArray,2);
% numberOfZeroBits=size(zeroArray,2);
% 
% 
% 
% for i=1:period
%     row=oneArray(i,:);
%     integratedOneArray(i)=min(row);
% end
% 
% %numberOfDeltaSteps=ceil(period/10);
% % for (j=1:numberOfDeltaSteps)
% %     startIndex=(j-1) * 10 + 1;
% %     endIndex=j * 10;
% %     if (endIndex>period)
% %         endIndex=period;
% %     end
% %     deltaLength=endIndex-startIndex+1;
% %     deltaArray=zeros(deltaLength,numberOfOneBits);
% %     %for i = 1:numberOfOneBits
% %     for i = 1:3       
% %         currentBitArray=oneArray(:,i);
% %         deltaValues=currentBitArray(startIndex:endIndex);
% %         deltaArray(:,i)=deltaValues
% %     end
% % 
% %     minDelta = min(deltaArray,[],2)
% %     integratedOneArray(startIndex:endIndex)=minDelta
% % 
% %  %   error('gmgm_i');
% % end
% plot(1:period,integratedOneArray, 'Color',orange ,'LineWidth',2, "LineStyle", '--');
% 
% 
% 
% error ('gmgm')
