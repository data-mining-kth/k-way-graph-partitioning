#!/bin/bash

# run the graph partitioning
# echo -e "Usage:\n1. delta (e.g. 0.8)\n2. temp (e.g. 1.0)\n3. alpha (e.g. 2.0)\n4. graph (e.g. add20)"
delta="$1"
temp="$2"
alpha="$3"
graph_name="$4"

echo "delta $delta"
echo "temp $temp"
echo "alpha $alpha"

echo "Running graph partitions....."
./run.sh -delta $delta -alpha $alpha -temp $temp -graph graphs/$graph_name.graph

echo "Making the plot....."
#./plot.sh "output/'$graph_name'.graph_NS_HYBRID_GICP_ROUND_ROBIN_T_'$temp'_D_'$delta'_RNSS_3_URSS_6_A_'$alpha'_R_1000"

printf -v filename "output/${graph_name}.graph_NS_HYBRID_GICP_ROUND_ROBIN_T_${temp}_D_${delta}_RNSS_3_URSS_6_A_${alpha}_R_1000.txt"
printf -v plotname "plots/graph_${graph_name}_D${delta}_T${temp}_A${alpha}.png"
echo "plotname: $plotname"
echo "filename: $filename"

gnuplot -e "plotname='$plotname'; filename='$filename'" my_graph.gnuplot

echo "Opening plot....."
open $plotname
