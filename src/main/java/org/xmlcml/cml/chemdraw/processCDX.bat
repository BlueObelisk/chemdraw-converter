@REM %1 is fileroot (e.g. paper, thesis, etc.)
@REM %2 is cdx or doc
@REM %3 is "scalefactor"???
@REM %4 is scalefactor

@set JUMBO_DIR=D:\wwmm\jumbo4.4
@set CDX_DIR=%JUMBO_DIR%\legacy\src\org\xmlcml\legacy\molecule\chemdraw
@set CDX_XSL=%CDX_DIR%\xsl

@REM set XERCES=C:\xml\xerces-2_0_1

@REM set classpath=.;%XERCES%\xercesImpl.jar;%XERCES%\xmlParserAPIs.jar;C:\xml\saxon\saxon7.jar
call %JUMBO_DIR%\setclasspath

java -mx500000000 org.xmlcml.legacy.chemdraw.ChemDrawDocumentImpl %1.%2

java net.sf.saxon.Transform -o %1.svgMenu.html %1.xml %CDX_XSL%/cdxml2foo.xsl id=%1 scalefactor=%4
java net.sf.saxon.Transform -o %1.0.cml %1.xml %CDX_XSL%/cdxml2foo.xsl outxml=CML scalefactor=%4
java net.sf.saxon.Transform -o %1.cml %1.0.cml %CDX_XSL%/cleanCml.xsl molid=%1
@REM note: Saxon MUST have -o flag
java net.sf.saxon.Transform -o junk.xml %1.cml %CDX_XSL%/splitmols.xsl infile=%1

java net.sf.saxon.Transform -o mollist.xhtml %1.xml %CDX_XSL%/makeMollist.xsl
@del junk.xml

