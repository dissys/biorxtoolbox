function [rx_signal] = genThrDiffSignal(params, t)
%GENTHRDIFFSIGNAL Summary of this function goes here
%   Detailed explanation goes here
    d = params.d;
    Dico = params.Dico;
    rRx = params.rRx;
    amplitude = params.amplitude;
    
    rx_signal = (rRx/(d + rRx)) * erfc(d./sqrt(4*Dico*t)) * amplitude;
end

