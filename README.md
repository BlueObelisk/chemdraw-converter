# Chemdraw-Converter
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.blueobelisk/chemdraw-converter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.blueobelisk/chemdraw-converter)
[![Build Status](https://github.com/BlueObelisk/chemdraw-converter/actions/workflows/maven.yml/badge.svg)](https://github.com/BlueObelisk/chemdraw-converter/actions/workflows/maven.yml)
[![codecov](https://codecov.io/gh/BlueObelisk/chemdraw-converter/branch/main/graph/badge.svg?token=E1NGWVWL04)](https://codecov.io/gh/BlueObelisk/chemdraw-converter)

This distribution contains a library to read (and partially write)
Chemdraw (CDX) files into/from CMLDOM. It is based on the web-published
definition of CDX files on the cambridgesoft.com site and honours
many of the components of a CDX file. It does not support Chemdraw-specific
elements such as display types, fonts, etc.

The code is made available under the Apache License, Version 2.0,
<https://www.apache.org/licenses/LICENSE-2.0>.

Peter Murray-Rust,
www.xml-cml.org, 2020

The current distribution is a library, not an application and will require a
CMLDOM for integration into a system. See http://www.xml-cml.org and
http://www-pmr.ch.cam.ac.uk/ and https://github.com/BlueObelisk
