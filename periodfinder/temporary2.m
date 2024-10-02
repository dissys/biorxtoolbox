%% Parameters needed for most of the functions are defined here
set(0,'DefaultFigureVisible','on');
alpha = 0.15:0.05:0.6;
delay = 0:100:1000;
distance = [100,200,400, 800]
distance = [100,100,100, 100,100,100, 200, 200, 200, 200, 200, 200]
alpha = [2,2,2,3,3,3,2,2,2,3,3,3]
delay = [10, 20, 30, 10, 20, 30, 10, 20, 30,10, 20, 30]
values = [1,2,3,2,3,4,3,4,5,6,2,5]
%surf(distance,alpha,delay,values)
%scatter3(distance,alpha,delay,values)
surf(distance,alpha,delay,values)
% set(0,'DefaultFigureVisible','on');
% S = load(heatmapfile);
% data = S.result;
% x = S.alpha;
% y = S.delay;
% figure();
% h = heatmap(y,x,data);
% h.XLabel = "t_{shift}";
% h.YLabel = "α";
% colormap(flipud(jet))
% colorbar

%[X,Y] = meshgrid(-5:.5:5);
%Z = Y.*sin(X) - X.*cos(Y);
%s = surf(X,Y,Z,'FaceAlpha',0.5)


%[X,Y,Z] = peaks(25);
%CO(:,:,1) = zeros(25); % red
%CO(:,:,2) = ones(25).*linspace(0.5,0.6,25); % green
%CO(:,:,3) = ones(25).*linspace(0,1,25); % blue
%surf(X,Y,Z,CO)
