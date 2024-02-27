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
function genOne = generateOne(a,d,M,signalDuration,bitSeqStr, TEST_DATA_FOLDER,PLOT_FOLDER,showFigures,TMP_FOLDER)
genAll = false;
counter = 0;
 
            %%% 
            % * calculating current IPTG and aTc molecule numbers and ssigning other parameters
            IPTG_params.amplitude = M*(1-a);
            aTc_params.amplitude = M*a;
            signal_duration = signalDuration;
            signal_shift_bw_A_B = d;
            %Creates a nx1 array:
            % E.g.: 0010111100101 --> [0;0;1;0;1;1;1;1;0;0;1;0;1]
            bit_sequence = transpose(num2str(bitSeqStr) - '0');

            %%%
            % * directory name for the new folder to be generated
            dirName= getName(IPTG_params.amplitude, aTc_params.amplitude, signalDuration, d, bitSeqStr)
            matFileName = strcat(dirName,".mat")
            disp('Log - Directory name: ' + dirName)
            disp('Log - Matlab file name: ' + matFileName)

            
            mkdir(char(strcat(TEST_DATA_FOLDER,dirName)));
            imageFolder=strcat(TMP_FOLDER,dirName);
            
            if (showFigures==1)            
                mkdir(char(imageFolder));
            end

            IPTG_params.Dico = 600;
            IPTG_params.d = 200;
            IPTG_params.rRx = 2;            
            aTc_params.Dico = 870;
            aTc_params.d = 200;
            aTc_params.rRx = 2;            
            sampling_rate = 40;            
            total_time = signal_duration * length(bit_sequence);
            
            COPASI_PARAMS = [signal_duration; signal_shift_bw_A_B;total_time; sampling_rate];
            %%% 
            % * saving the current parameters in the <dirname> directory            
            
            csvwrite(strcat(TEST_DATA_FOLDER, dirName ,"/COPASI_PARAMS.csv"),COPASI_PARAMS);
            csvwrite(strcat(TEST_DATA_FOLDER, dirName,"/bit_sequence.csv"),bit_sequence);
            
            %figureParameters= [showFigures,1,100,100]
            figureParameters.showFigures=showFigures;
            figureParameters.index=1;
            figureParameters.x=100;
            figureParameters.y=100;
            figureParameters.prefix='IPTG';
            figureParameters.imageFolder=imageFolder;

            %Signal A (IPTG) processing:            
            figureParameters=generateInputSignal(IPTG_params, strcat(TEST_DATA_FOLDER, dirName, "/IPTG_vals.csv"), bit_sequence, signal_duration, total_time, sampling_rate, figureParameters);
            disp('Log  - IPTG data are saved!')
                        
            figureParameters.prefix='ATC';
            figureParameters.index=1;            
            %%Signal B (aTc) processing:
            generateInputSignal(aTc_params, strcat(TEST_DATA_FOLDER, dirName, "/ARA_vals.csv"), bit_sequence, signal_duration, total_time, sampling_rate, figureParameters);
            disp('Log  - ARA data are saved!')
            %error('gmgm')

          
            %GM: vpr2.5 javaCommand=strcat('java -cp ./dependencies/molsim_jars/mol6new4.jar:./dependencies/super-csv/*:./dependencies/COPASI-4.23.184-Java-Bindings-Darwin/*:./dependencies/molsim_jars/* -Djava.library.path=".:./dependencies/COPASI-4.23.184-Java-Bindings-Darwin" dissys.keele.ac.uk.App'," ",strcat(TEST_DATA_FOLDER, dirName));
            javaCommand=strcat('java -cp ./dependencies/molsim_jars/molsim7.jar:./dependencies/super-csv/*:./dependencies/COPASI-4.23.184-Java-Bindings-Darwin/*:./dependencies/molsim_jars/* -Djava.library.path=".:./dependencies/COPASI-4.23.184-Java-Bindings-Darwin" dissys.keele.ac.uk.App'," ",strcat(TEST_DATA_FOLDER, dirName));
            javaCommand

            status = system(char(javaCommand));
            status           
            disp('Log - VPR has been run!')
            disp('Log - Status(O: Success, 1: Error):' + sprintf("%.0f",status))
                  
            signalData = csvread(strcat(TEST_DATA_FOLDER,dirName,'/timeCourseResult.csv'));
            %Split the total time 1000 to create 1000 sec ranges. 
            % E.g.: To split the total 100 seconds by 10 second intervals: 
            % 1:10:95: -->  1    11    21    31    41    51    61    71    81    91
            t = 0:(total_time/1000):total_time;            
            
            mkdir(PLOT_FOLDER);        
            save(strcat(PLOT_FOLDER, matFileName),'t', 'signalData');                      
        
genOne = matFileName;
