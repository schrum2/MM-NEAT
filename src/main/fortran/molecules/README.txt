On Windows using MSYS2 compile with:

gfortran -o SMILESMeltingPointFitness.exe SMILESMeltingPointFitness.f
gfortran -o SMILESMutate.exe SMILESMutate.f

===================================================================

Here is a test for SMILESMutate====================================

./SMILESMutate.exe < example_mutate_input.txt

The contents of example_mutate_input.txt are:

131 246
13 3
C-C-N-C(=C)-O

which represents:

<random seed> <???>
<input string length> <[1-7] to determine mutation operator>
<input string>

Output is:

<output string length>
<mutated output string>

===================================================================

Here is a test for SMILESMeltingPointFitness=======================