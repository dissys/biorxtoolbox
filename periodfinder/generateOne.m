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

function genOne = generateOne(a,d,M,signalDuration,bitSeqStr, TEST_DATA_FOLDER,PLOT_FOLDER)
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
                        
            t = 1:total_time;
            t_travel = 30;
            sig_rx = genThrDiffSignal(IPTG_params, t);            
            sig_rx_pdf = sig_rx - [0 sig_rx(1:end-1)];        
            
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
            
            main_signal = sum(sig_rx_pdf_sq);                       
                        
            sequence_signal = bit_sequence(1) * main_signal;

            x = length(bit_sequence);
            
            for i = 2:x
                sequence_signal = [sequence_signal; ...
                    [zeros(1,(i-1)*signal_duration), bit_sequence(i) * main_signal(1:end-(i-1)*signal_duration)]];
            end
            
            sequence_signal_total = sum(sequence_signal);
            
          
            moving_average = 1/sampling_rate*ones(sampling_rate,1);
            
            out = filter(moving_average,1,sequence_signal_total);       
            
            x = stem(t(sampling_rate:sampling_rate:end), out(sampling_rate:sampling_rate:end));
            x.Visible = 'off';
            set(findall(gca, 'Type', 'Line'),'LineWidth',2);
            csvwrite(strcat(TEST_DATA_FOLDER, dirName, "/IPTG_vals.csv"),floor(x.YData));   
            disp('Log  - IPTG data are saved!')
           
            %Signal B (Ara) processing: TODO Use a function rather than
            %repeating the same code.
            sig_rx = genThrDiffSignal(aTc_params, t);
            sig_rx_pdf = sig_rx - [0 sig_rx(1:end-1)];            
            t_shift = 30;
            
            sig_rx_pdf_sq = [sig_rx_pdf; ...
                [zeros(1,t_shift), sig_rx_pdf(1:end-t_shift)]; ...
                [zeros(1,2*t_shift), sig_rx_pdf(1:end-2*t_shift)]; ...
                [zeros(1,3*t_shift), sig_rx_pdf(1:end-3*t_shift)]; ...
                [zeros(1,4*t_shift), sig_rx_pdf(1:end-4*t_shift)]; ...
                ];            
            main_signal = sum(sig_rx_pdf_sq);            
            sequence_signal = bit_sequence(1) * main_signal;
            x = length(bit_sequence);
            
            for i = 2:x
                sequence_signal = [sequence_signal; ...
                    [zeros(1,(i-1)*signal_duration), bit_sequence(i) * main_signal(1:end-(i-1)*signal_duration)]];
            end
            
            sequence_signal_total = sum(sequence_signal);
            moving_average = 1/sampling_rate*ones(sampling_rate,1);
            out = filter(moving_average,1,sequence_signal_total);
            x = stem(t(sampling_rate:sampling_rate:end), out(sampling_rate:sampling_rate:end));
            x.Visible = 'off';
            set(findall(gca, 'Type', 'Line'),'LineWidth',2);
            csvwrite(char(strcat(TEST_DATA_FOLDER, dirName ,"/ARA_vals.csv")),floor(x.YData));     
           
            disp('Log  - ARA data are saved!')
                        
            status = system(char(strcat('java -cp ./dependencies/molsim_jars/mol6new4.jar:./dependencies/super-csv/*:./dependencies/COPASI-4.23.184-Java-Bindings-Darwin/*:./dependencies/molsim_jars/* -Djava.library.path=".:./dependencies/COPASI-4.23.184-Java-Bindings-Darwin" dissys.keele.ac.uk.App'," ",strcat(TEST_DATA_FOLDER, dirName))));
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
