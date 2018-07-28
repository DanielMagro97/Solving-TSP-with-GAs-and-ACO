# Solving-TSP-with-GAs-and-ACO
ICS2207 - Machine Learning: Introduction to Classification, Search and Optimisation Assignment 2017/18

All Euclidean Symmetric TSP instances were obtained from http://elib.zib.de/pub/mp-testdata/tsp/tsplib/tsp/index.html

# How to Run the Program
In order to select which instances the program will find solutions for, simply download any
Symmetric instances from TSPLIB[14], and place them inside the TSPinstances folder. Some
instances have already been provided inside the folder, and a few more inside TSPinstanceslibrary.
Instances inside TSPinstances will be evaluated by the algorithms, whereas those in
TSPinstanceslibrary will not.
Simply double-click on run.bat and the program will perform both GA and ACO on every instance in
the TSPinstances folder. It will output the name of the algorithm (GA or ACO), the instance name,
the best path found, the length of the best path, and the time taken to find that path.
