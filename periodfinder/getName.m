%Generates the file name without its extension. The name can also be for a
%directory.
function getName = getName(A_amplitude,B_amplitude, signalDuration,signal_shift_bw_A_B, bitSeqStr)
    countAStr = int2str(A_amplitude);
    countBStr = int2str(B_amplitude);
    tSignalStr = int2str(signalDuration);
    shiftStr = int2str(signal_shift_bw_A_B);    
    name = strcat("A",countAStr,"_B", countBStr,"_K_TS",tSignalStr, "_Seq", bitSeqStr, "_TShift", shiftStr);           
getName = name;
