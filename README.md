# SketchGenCodeBase

This repo contains the source code for `SketchGen<sup>2</sup>` which creates expressions and test suites for use with ASketch. This repository is based on an Eclipse workspace, but can be compiled and executed within any Java-based set up.

# Requirements:

* Supported Operating Systems
  - Linux (64 bit)
  - Mac OS X (64 bit)

* Dependencies
  - [Alloy 4.2]: Must be in the classpath.  `SketchGen<sup>2</sup>` comes with Alloy4.2 under `libs/alloy.jar`.
  - [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html): Must be installed and accessible from `PATH`.  JVM should support 32
    bit native library if you use minisat solver.
  - [Alloy Parser]: A parser that is able to process Alloy model sketches with holes. `SketchGen<sup>2</sup>` comes with Alloy Parser under `libs/aparser.jar`.
  - [Commons Library]: `SketchGen<sup>2</sup>` comes with commons library under `libs/commonc-cli-1.4.jar`.
  - [Antlr]: `SketchGen<sup>2</sup>` comes with Antlr version 4.7.2 under `libs/antlr-4.7.2-complete.jar`.
  
# Quick Start:

## Example Models
A series of example models and starting test suites can be found in the following:
* [sketch/models/testgens] - contains the models with hole locations to flag what domains to genreate expressions for within each model.
* [tests_testgenX] - these three folders contain different sized starting test suites that can be used to initate `SketchGen<sup>2</sup>`

## Running
* The main class is `TestGen.java` which can be found in the `scr/asketch// package.
