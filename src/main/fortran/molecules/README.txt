On Windows using MSYS2 compile with:

gfortran -o SMILESMeltingAndBoilingPoint.exe SMILESMeltingAndBoilingPoint.f
gfortran -o SMILESMutate.exe SMILESMutate.f

===================================================================

Here is a test for SMILESMutate====================================

./SMILESMutate.exe < example_mutate_input.txt

The contents of example_mutate_input.txt are:

131 246
13 3
C-C-N-C(=C)-O

which represents:

<random seed 1> <random seed 2>
<input string length> <[1-7] to determine mutation operator>
<input string>

Output is:

<mutated output string>

===================================================================

Here is a test for SMILESMeltingAndBoilingPoint====================

./SMILESMeltingAndBoilingPoint.exe < example_melting_boiling_point_input.txt

The contents of example_melting_boiling_point_input.txt are:

13 
C-C-N-C(=C)-O

which represents:

<input string length>
<input string>

Output is:

<melting point>
<boiling point>