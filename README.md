# Period Finder
Period Finder is a simulation tool that is written in MATLAB to find suitable molecular communication parameters and identify the best possible communication scenarios. PeriodFinder implements the cellular pre-equaliser approach to minimise ISI. This cellular pre-equaliser uses two type-A and type-B input signals. While the former is the actual data carrier, the latter is used to remove the heavy tail of the former. Period Finder optimises the pre- equaliser's parameters, including symbol duration, the delay between type-A and type-B signals, and the input signals' amplitudes. A biological use case in the form of a cellular receiver is designed as a genetic circuit using the MolSim7 java project and is used as another Period Finder parameter.

PeriodFinder is a set of reusable Matlab functions. Use main.m to change simulation parameters and customise communication scenarios.

Some of these functions are summarised below:

* main: The main file and executes the simulation workflow. It also includes system parameters such as the total number of molecules, the ratio of molelecules and the delay between them. 

* generateAll: Simulates all communication scenarios for given parameter vectors.

* generateOne: Simulates a single communication scenario. Includes diffusion specific parameters.

* allPeriods: Infers optimum symbol durations.

* myheatmap: Generates a heatmap for optimised symbol durations.

* plotter: Creates a plot using COPASI simulation results for a single scenario.

* generateNice: Simulates the computationally identified potential scenarios based on symbol durations.

* nicePlotter: Plots the signals from the generateNice process.

* MolEyeScore: Calculates the mol-eye scores and sorts the scenarios.

# MolSim7
MolSim is a Java tool that converts the external type-A and type-B signals to internal cellular signals and creates the corresponding computational model.


## Dependencies
MolSim7 relies on COPASI Java bindings, which are included in this project under "/periodfinder/dependencies." 

If you are using a Mac computer, you may face the "library load disallowed by system policy" issue. Please follow the steps below in this situation:

* Check if the COPASI binding is signed.

  ``` $codesign -vvvv libCopasiJava.jnilib```

* If it is not signed, check if the file is in MacOS quarantine. The output may include com.apple.quarantine.

  ```$xattr libCopasiJava.jnilib```

* If the file is in quarantine, remove it from the MacOS quarantine.

   ```$xattr -d com.apple.quarantine  libCopasiJava.jnilib```
 
 * Double check the quarantine list to ensure it is not listed anymore.
   ```$xattr libCopasiJava.jnilib```


# Genetic circuit design and models
The genetic circuit design and sequences used in this project are available as a Synthetic Biology Open Language (SBOL) version 2 file. The file can be accessed from ```biorxtoolbox/molsimv7/abcomm.sbol```.

Each communication scenario has its own computational model in the form of the Systems Biology Markup Language (SBML) Level 3 files. These models are located in directories specific for communication scenarious under the ```biorxtoolbox/output/testData``` folder.




