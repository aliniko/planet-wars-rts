#!/bin/bash
#PBS -N apptainer_build
#PBS -l select=1:ncpus=4:mem=8gb
#PBS -l walltime=00:30:00
#PBS -j oe

cd $PBS_O_WORKDIR

module load apptainer  # if needed; omit if already available

apptainer build planetwars.sif planetwars.def
