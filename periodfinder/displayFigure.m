function displayFigure = displayFigure(figureParams, data, name)

if (figureParams.showFigures==1) % If showFigures is true
    %figure(figureParams.index)
    longName=strcat(figureParams.prefix,'_', int2str(figureParams.index), '_', name);
    f=figure (Name=longName);
    f.Visible='off';
    set(gcf,'position',[figureParams.x,figureParams.y,400,200]);
    %set(gca, 'FontSize', 14)    
    plot(data) ;
    title(strcat(figureParams.prefix,'-', int2str(figureParams.index), '-', strrep(name,'_','-')));    
    figureParams.index=figureParams.index + 1;%Figure number
    figureParams.x= figureParams.x + 20;%X
    figureParams.y=figureParams.y + 20;%Y 
    imagePath=fullfile(figureParams.imageFolder, strcat(longName, ".png"));
    disp(imagePath);
    set(gca,'position',[0.07 0.16 0.88 0.75]);    
    saveas(gcf, imagePath);  
end

displayFigure=figureParams;


