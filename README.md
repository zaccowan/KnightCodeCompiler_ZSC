# KnightCodeCombpiler

This is my final compiler project for CS322. The language,KnightCode, is a toy language with some basic features and essential features like sequential, branch, and repetition statements. It allows for INTEGER and STRING types.
<br/>
<br/>
For more information, see the syntax section for insight into the full extent of language features.


# How To Use

1. Generate ANTLR files used by compiler.
   ```
    ant build-grammar
    ```
2. Compile the grammar files and compiler files.
   ```
   ant compile-grammar
   ```
   ```
   ant compile
   ```

3. Run the compiler specifing a <program>.kc file to compile. Note, the default output location is "output/".
   ```
   java compiler/kcc tests/<file_name>.kc [output/<file_output_name>]
   ```
# Syntax of a KnightCode (.KC) file.

 - DataTypes
    - INTEGER – one or more digits
    - STRING – one or more characters including letters, numbers, and (:)
- Arithmetic Operators
  - +, -, *, /
- Relational Operators
  - <, >, <> (not equal) and = (equal to)
- Assignment Operator
  - :=
- Valid Statements
  - READ Identifier – get data from the user
  - PRINT Identifier – print data to the screen (does not support concatenation)
  - SET Identifier = Expression – set the value of a variable
  - IF Condition THEN Statement(s) ELSE Statement(s) ENDIF - traditional if structure (ELSE could be optional)
  - WHILE Condition DO Statement(s) ENDWHILE - traditional while structure
- Comments
  - #Single line comments (no multi-line comments supported)
- Keywords
  - BEGIN
  - ENDIF
  - SET
  - THEN
  - DECLARE
  - ENDWHILE
  - PROGRAM
  - DO
  - IF
  - READ
  - WHILE
  - ELSE
  - INTEGER
  - STRING
  - PRINT
  - END

# Example KnightCode Program
```
PROGRAM Program1

DECLARE
	INTEGER x
	INTEGER y
	INTEGER z

BEGIN

SET x := 10
SET y := 12
SET z := x + y

PRINT z

END
```
