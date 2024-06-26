function saveRateFigure = saveRateFigure(figureParams, rate, name,I,I1, minSufficientTime,onebitPeriod,oneBitSeq)

if (figureParams.showFigures==1) % If showFigures is true
    %figure(figureParams.index)
    longName=strcat(figureParams.prefix,'_', int2str(figureParams.index), '_', name);
    f=figure (Name=longName);
    f.Visible='off';
    set(gcf,'position',[figureParams.x,figureParams.y,400,200]);
    plot(rate) ;
    titleText=strcat(figureParams.prefix,'-', int2str(figureParams.index), '-', strrep(name,'_','-'));
    figureParams.index=figureParams.index + 1;%Figure number
    figureParams.x= figureParams.x + 20;%X
    figureParams.y=figureParams.y + 20;%Y 
    imagePath=fullfile(figureParams.imageFolder, strcat(longName, ".png"));
    disp(imagePath);
    set(gca,'position',[0.07 0.16 0.88 0.75]);    
    gray = [.3 .3 .3];
    blue=[0, 0.4470, 0.7410];

    maxRate=max(rate);
    labelOffsetYAxis=maxRate/15;
    plot(rate,'Color',blue);
    %f=gcf;
    %f.Visible='on';
    hold on
    
    plot(I,0,'bx');
    
    
    %text(I-8,labelOffsetYAxis,'max(rate)');
    text(I-8,labelOffsetYAxis,'t_{Rm}', 'Color',gray,'FontSize',8);
       
    plot(I1,0,'bx');
    text(I1-8,labelOffsetYAxis,'t_{Am}','Color',gray,'FontSize',8);

    plot(minSufficientTime,0,'b*');
    text(minSufficientTime-20,labelOffsetYAxis,'t_{R0}','Color',gray,'FontSize',8);
    for i=1:strlength(oneBitSeq)        
        plot(onebitPeriod*i,0,'b.');
    end
    plot([onebitPeriod*2,minSufficientTime],[0,0],'red', 'LineWidth',1);

    title(titleText);
    saveas(gcf, imagePath);  
end

saveRateFigure=figureParams;


