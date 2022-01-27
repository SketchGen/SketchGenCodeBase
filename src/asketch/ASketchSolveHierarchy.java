package asketch;

import static asketch.ASketchSolveSubModels.generateFunDecl;
import static asketch.ASketchSolveSubModels.loadExpressionsOfHoles;
import static asketch.alloy.AlloyASTVisitor.constructPreds;
import static asketch.alloy.etc.Constants.ABSTRACT_BO;
import static asketch.alloy.etc.Constants.ABSTRACT_CO;
import static asketch.alloy.etc.Constants.ABSTRACT_LO;
import static asketch.alloy.etc.Constants.ABSTRACT_Q;
import static asketch.alloy.etc.Constants.ABSTRACT_UO;
import static asketch.alloy.etc.Constants.BO_FUN_NAME;
import static asketch.alloy.etc.Constants.CO_FUN_NAME;
import static asketch.alloy.etc.Constants.SKETCH_COMMAND_NAME;
import static asketch.alloy.etc.Constants.SLASH;
import static asketch.alloy.etc.Constants.UO_FUN_NAME;
import static asketch.alloy.util.AlloyUtil.cardOfSig;
import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.util.AlloyUtil.connectExprHolesWithSameExpressions;
import static asketch.alloy.util.AlloyUtil.extractHTML;
import static asketch.alloy.util.AlloyUtil.findSigsAndFields;
import static asketch.alloy.util.AlloyUtil.findSubnode;
import static asketch.etc.Names.DOT_ALS;
import static asketch.etc.Names.SOLVE_FILE_PATH;
import static asketch.etc.Names.TEST_DIR_PATH;
import static asketch.opts.DefaultOptions.logger;
import static asketch.opts.DefaultOptions.solvingScope;
import static asketch.util.FileUtil.createDirsIfNotExist;
import static asketch.util.StringUtil.afterSubstring;
import static asketch.util.Util.isValidPruningRule;
import static asketch.util.Util.printASketchGenUsage;
import static asketch.util.Util.printASketchSolveUsage;

import asketch.alloy.ASketchParser;
import asketch.alloy.RelationAndVariableCollector;
import asketch.alloy.cand.Relation;
import asketch.alloy.exception.AlloySyntaxErrorException;
import asketch.alloy.exception.TestValuationUnsatisfiableException;
import asketch.alloy.exception.UnsupportedHoleException;
import asketch.alloy.fragment.BO;
import asketch.alloy.fragment.CO;
import asketch.alloy.fragment.Hole;
import asketch.alloy.fragment.LO;
import asketch.alloy.fragment.Q;
import asketch.alloy.fragment.UO;
import asketch.alloy.util.AlloyProgram;
import asketch.alloy.util.TestTranslator;
import asketch.opts.ASketchSolveOpt;
import asketch.util.TextFileReader;
import asketch.util.TextFileWriter;
import asketch.util.Timer;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Browsable;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents the hierarchical technique for ASketchSolve.
 */
public class ASketchSolveHierarchy {

  public static void solveMetaModelForConcreteHoleValues(AlloyProgram alloyProgram,
      CompModule module, ASketchSolveOpt opt)
      throws TestValuationUnsatisfiableException, AlloySyntaxErrorException, UnsupportedHoleException {
    // Stores solution if there is any.
    String[] solution = new String[alloyProgram.getHoles().size()];
    // Construct the meta Alloy model that encodes many models
    // and save it to the disk.
    Map<String, String> mapToOriginalNames = constructMetaAlloyModelAndSaveIt(alloyProgram, module,
        opt);
    // Solving
    final String[] lastReport = new String[5];
    A4Reporter reporter = new A4Reporter() {
      @Override
      public void translate(String solver, int bitwidth, int maxseq, int skolemDepth,
          int symmetry) {
        super.translate(solver, bitwidth, maxseq, skolemDepth, skolemDepth);
        lastReport[0] = solver;
      }

      @Override
      public void solve(int primaryVars, int totalVars, int clauses) {
        super.solve(primaryVars, totalVars, clauses);
        lastReport[1] = String.valueOf(primaryVars);
        lastReport[2] = String.valueOf(totalVars);
        lastReport[3] = String.valueOf(clauses);
      }

      @Override
      public void resultSAT(Object command, long solvingTime, Object solution) {
        super.resultSAT(command, solvingTime, solution);
        lastReport[4] = String.valueOf(solvingTime);
      }
    };
    A4Options options = new A4Options();
    Timer timer = new Timer();
    timer.start();
    boolean foundSolution = findSolution(reporter, options, mapToOriginalNames, solution);
    timer.stop();
    // Print out the first solution.
    if (foundSolution) {
      logger.info("Solver: " + lastReport[0]);
      logger.info("Primary Vars: " + lastReport[1]);
      logger.info("Total Vars: " + lastReport[2]);
      logger.info("Clauses: " + lastReport[3]);
      logger.info("Solving Time: " + lastReport[4]);
      logger.info("First Solution Time: " + timer.getMilliSeconds());
      logger.info("Solution: " + String.join(", ", Arrays.asList(solution)));
    } else {
      logger.info("No solution is found.");
    }
  }

  public static boolean findSolution(A4Reporter reporter, A4Options options,
      Map<String, String> mapToOriginalRelationNames, String[] solution) {
    CompModule metaModel = compileAlloyModule(SOLVE_FILE_PATH);
    Command runSketch = null;
    for (Command command : metaModel.getAllCommands()) {
      String cmdName = extractHTML(command.getHTML(), Pattern.compile("</b> (.*?)$"));
      if (SKETCH_COMMAND_NAME.equals(cmdName)) {
        runSketch = command;
        break;
      }
    }
    assert runSketch != null;
    try {
      A4Solution valuation = TranslateAlloyToKodkod
          .execute_command(reporter, metaModel.getAllReachableSigs(), runSketch, options);
      if (valuation != null && valuation.satisfiable()) {
        String xml = valuation.toString();
        Matcher matcher = Pattern.compile("R.*(\\d+)=\\{(.*?)\\$\\d*\\}").matcher(xml);
        while (matcher.find()) {
          int holeId = Integer.valueOf(matcher.group(1));
          String sigName = matcher.group(2);
          solution[holeId] = mapToOriginalRelationNames.get(sigName);
        }
        return true;
      }
    } catch (Err err) {
      err.printStackTrace();
    }
    return false;
  }

  public static Map<String, String> constructMetaAlloyModelAndSaveIt(AlloyProgram alloyProgram,
      CompModule module, ASketchSolveOpt opt)
      throws TestValuationUnsatisfiableException, AlloySyntaxErrorException, UnsupportedHoleException {
    List<Hole> holes = alloyProgram.getHoles();
    // Find signatures and fields as they are declared.
    List<Relation> sigsAndFields = findSigsAndFields(module);
    // Quick union and find.  The indexes of holes with
    // the same scope will have the same value.
    int[] connectedHoles = connectExprHolesWithSameExpressions(holes);
    // Meta model
    StringBuilder metaModel = new StringBuilder();
    // Construct sig for meta model.
    metaModel.append(constructSigsAndRun(module));
    // Construct pred for meta model.
    metaModel.append(constructPreds(alloyProgram, module));
    // Map from the sig names back to the original operators or expressions.
    Map<String, String> mapToOriginalNames = new HashMap<>();
    // Generate operator sigs and functions on demand.
    metaModel.append(generateOperatorSigsAndFunDecl(alloyProgram, mapToOriginalNames));
    // Load expressions generated for each hole.  Only the first
    // hole with the unique set of basic relations gets assigned.
    loadExpressionsOfHoles(alloyProgram, opt, connectedHoles);
    // Construct function declarations for expression holes.
    // Append them to the meta Alloy model
    String funDecl = generateFunDecl(alloyProgram, connectedHoles, sigsAndFields,
        mapToOriginalNames);
    metaModel.append(funDecl);
    // Add test facts translated from test predicates.
    // TODO(kaiyuanw): This is a hacking way to reuse tests for previous runs.
    String modelName = opt.getModelName();
    modelName =
        modelName.contains("_") ? modelName.substring(0, modelName.indexOf("_")) : modelName;
    String testPath = Paths.get(TEST_DIR_PATH, modelName + DOT_ALS).toString();
    String testFacts = TestTranslator.translate(testPath, opt.getTestNum());
    metaModel.append(testFacts);
    // Save the meta model to disk so we can use Alloy analyzer to parse it.
    TextFileWriter.writeText(metaModel.toString(), SOLVE_FILE_PATH, false);
    // Return a mapping to the original relation names.
    return mapToOriginalNames;
  }

  private static String constructSigsAndRun(CompModule module) {
    // Construct signature declarations.
    StringBuilder sigDeclAndRun = new StringBuilder();
    Browsable sigParent = findSubnode(module, "sig");
    if (sigParent != null) {
      List<? extends Browsable> sigs = sigParent.getSubnodes();
      for (int i = 0; i < sigs.size(); i++) {
        Sig sig = (Sig) sigs.get(i);
        sigDeclAndRun.append(sig.isAbstract == null ? "" : "abstract ");
        String mult = cardOfSig(sig);
        if (mult.equals("set")) {
          mult = "";
        } else {
          mult += " ";
        }
        sigDeclAndRun.append(mult).append("sig ")
            .append(afterSubstring(sig.toString(), SLASH, true)).append(" {}\n\n");
      }
    }
    sigDeclAndRun.append(SKETCH_COMMAND_NAME).append(": run {} for ").append(solvingScope)
        .append("\n\n");
    return sigDeclAndRun.toString();
  }

  private static String generateOperatorSigsAndFunDecl(AlloyProgram alloyProgram,
      Map<String, String> mapToOriginalOperatorNames) {
    StringBuilder operatorSigsAndFunDecl = new StringBuilder();
    Set<Class> visitedOperatorType = new HashSet<>();
    for (Hole hole : alloyProgram.getHoles()) {
      if (hole instanceof Q) {
        if (visitedOperatorType.add(hole.getClass())) {
          String all = ABSTRACT_Q + "_All";
          String no = ABSTRACT_Q + "_No";
          String some = ABSTRACT_Q + "_Some";
          String lone = ABSTRACT_Q + "_Lone";
          String one = ABSTRACT_Q + "_One";
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_Q + " {}\n")
              .append("one sig " + all + ", " + no + ", " + some + ", " + lone + ", " + one
                  + " extends " + ABSTRACT_Q + " {}\n");
          mapToOriginalOperatorNames.put(all, "all");
          mapToOriginalOperatorNames.put(no, "no");
          mapToOriginalOperatorNames.put(some, "some");
          mapToOriginalOperatorNames.put(lone, "lone");
          mapToOriginalOperatorNames.put(one, "one");
          // Quantifier function declaration must be generated on the fly.
        }
      } else if (hole instanceof CO) {
        if (visitedOperatorType.add(hole.getClass())) {
          String eq = ABSTRACT_CO + "_Eq";
          String in = ABSTRACT_CO + "_In";
          String neq = ABSTRACT_CO + "_NEq";
          String nin = ABSTRACT_CO + "_NIn";
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_CO + " {}\n")
              .append(
                  "one sig " + eq + ", " + in + ", " + neq + ", " + nin + " extends " + ABSTRACT_CO
                      + " {}\n")
              .append("pred " + CO_FUN_NAME + "(h: " + ABSTRACT_CO + ", e1, e2: set univ) {\n" +
                  "  h = " + eq + " => e1 = e2\n" +
                  "  h = " + in + " => e1 in e2\n" +
                  "  h = " + neq + " => e1 != e2\n" +
                  "  h = " + nin + " => e1 !in e2\n" +
                  "}\n\n");
          mapToOriginalOperatorNames.put(eq, "=");
          mapToOriginalOperatorNames.put(in, "in");
          mapToOriginalOperatorNames.put(neq, "!=");
          mapToOriginalOperatorNames.put(nin, "!in");
        }
      } else if (hole instanceof UO) {
        if (visitedOperatorType.add(hole.getClass())) {
          String no = ABSTRACT_UO + "_No";
          String some = ABSTRACT_UO + "_Some";
          String lone = ABSTRACT_UO + "_Lone";
          String one = ABSTRACT_UO + "_One";
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_UO + " {}\n")
              .append("one sig " + no + ", " + some + ", " + lone + ", " + one + " extends "
                  + ABSTRACT_UO + " {}\n")
              .append("pred " + UO_FUN_NAME + "(h: " + ABSTRACT_UO + ", e: set univ) {\n" +
                  "  h = " + no + " => no e\n" +
                  "  h = " + some + " => some e \n" +
                  "  h = " + lone + " => lone e\n" +
                  "  h = " + one + " => one e\n" +
                  "}\n\n");
          mapToOriginalOperatorNames.put(no, "no");
          mapToOriginalOperatorNames.put(some, "some");
          mapToOriginalOperatorNames.put(lone, "lone");
          mapToOriginalOperatorNames.put(one, "one");
        }
      } else if (hole instanceof BO) {
        if (visitedOperatorType.add(hole.getClass())) {
          String amp = ABSTRACT_BO + "_Intersect";
          String plus = ABSTRACT_BO + "_Union";
          String minus = ABSTRACT_BO + "_Diff";
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_BO + " {}\n")
              .append("one sig " + amp + ", " + plus + ", " + minus + " extends " + ABSTRACT_BO
                  + " {}\n")
              .append(
                  "fun " + BO_FUN_NAME + "(h: " + ABSTRACT_BO + ", e1, e2: set univ): set univ {\n"
                      +
                      "  h = " + amp + " => e1 & e2 else\n" +
                      "  h = " + plus + " => e1 + e2 else\n" +
                      "  h = " + minus + " => e1 - e2 else none\n" +
                      "}\n\n");
          mapToOriginalOperatorNames.put(amp, "&");
          mapToOriginalOperatorNames.put(plus, "+");
          mapToOriginalOperatorNames.put(minus, "-");
        }
      } else if (hole instanceof LO) {
        if (visitedOperatorType.add(hole.getClass())) {
          String and = ABSTRACT_LO + "_And";
          String or = ABSTRACT_LO + "_Or";
          String biImply = ABSTRACT_LO + "_BiImply";
          String imply = ABSTRACT_LO + "_Imply";
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_LO + " {}\n")
              .append("one sig " + and + ", " + or + ", " + biImply + ", " + imply
                  + " extends " + ABSTRACT_LO + " {}\n");
          mapToOriginalOperatorNames.put(and, "&&");
          mapToOriginalOperatorNames.put(or, "||");
          mapToOriginalOperatorNames.put(biImply, "<=>");
          mapToOriginalOperatorNames.put(imply, "=>");
          // Logical operator function declaration must be generated on the fly.
        }
      } else {
        // TODO(kaiyuan): Implement other types of holes
      }
    }
    return operatorSigsAndFunDecl.toString();
  }

  public static void main(String[] args)
      throws UnsupportedHoleException, AlloySyntaxErrorException, TestValuationUnsatisfiableException {
    args = new String[]{"sketch/models/demo_5.als", "dynamic", "16"};
    if (args.length != 3) {
      logger.error("Wrong number of arguments: " + args.length);
      printASketchSolveUsage();
      return;
    }
    String modelPath = args[0];
    String pruningRule = args[1];
    if (!isValidPruningRule(pruningRule)) {
      logger.error("Invalid pruning rule: " + pruningRule);
      printASketchGenUsage();
      return;
    }
    int testNum = Integer.valueOf(args[2]);
    ASketchSolveOpt opt = new ASketchSolveOpt(modelPath, pruningRule, testNum);
    // Check if mandatory directories and files exist, and create them if not.
    createDirsIfNotExist();
    logger.info("Arguments: " + String.join(", ", args));
    // Ignore dijkstra sig State
//        blacklist.ignore("State");
    // Read Alloy model with holes as string
    String modelText = TextFileReader.readText(modelPath);
    // Parse Alloy program to detect holes.
    AlloyProgram alloyProgram = ASketchParser.parse(modelText);
    // Find relations and variables for each hole.
    CompModule module = RelationAndVariableCollector.collect(alloyProgram);
    // Compute column span for each hole.
    alloyProgram.computeHoleColSpan();
    // Generate meta Alloy model and solve it.
    solveMetaModelForConcreteHoleValues(alloyProgram, module, opt);
  }
}
