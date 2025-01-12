On Windows using MSYS2 compile with:

gfortran -o SMILESMeltingAndBoilingPoint.exe SMILESMeltingAndBoilingPoint.f

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