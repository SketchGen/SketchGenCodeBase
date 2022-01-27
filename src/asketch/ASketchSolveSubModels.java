package asketch;

import static asketch.alloy.etc.Constants.ABSTRACT_E;
import static asketch.alloy.etc.Constants.EXPR_FUN_NAME;
import static asketch.alloy.etc.Constants.NONE;
import static asketch.alloy.etc.Constants.RESULT_E;
import static asketch.alloy.etc.Constants.SKETCH_COMMAND_NAME;
import static asketch.alloy.etc.Constants.SLASH;
import static asketch.alloy.etc.Constants.UNIV;
import static asketch.alloy.util.AlloyUtil.cardOfSig;
import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.util.AlloyUtil.computeSerialPath;
import static asketch.alloy.util.AlloyUtil.connectExprHolesWithSameExpressions;
import static asketch.alloy.util.AlloyUtil.convertToRelations;
import static asketch.alloy.util.AlloyUtil.extractHTML;
import static asketch.alloy.util.AlloyUtil.findSigsAndFields;
import static asketch.alloy.util.AlloyUtil.findSubnode;
import static asketch.alloy.util.AlloyUtil.findSubnodes;
import static asketch.alloy.util.AlloyUtil.generateRelationNameInMetaModel;
import static asketch.alloy.util.AlloyUtil.getAllRelationsByArity;
import static asketch.etc.Names.DOT_ALS;
import static asketch.etc.Names.MODEL_DIR_PATH;
import static asketch.etc.Names.SOLVE_FILE_PATH;
import static asketch.etc.Names.TEST_DIR_PATH;
import static asketch.opts.DefaultOptions.logger;
import static asketch.opts.DefaultOptions.solveArity;
import static asketch.opts.DefaultOptions.solvingScope;
import static asketch.util.FileUtil.createDirsIfNotExist;
import static asketch.util.StringUtil.afterSubstring;
import static asketch.util.Util.deserialize;
import static asketch.util.Util.isValidPruningRule;
import static asketch.util.Util.printASketchGenUsage;
import static asketch.util.Util.printASketchSolveUsage;

import asketch.alloy.ASketchParser;
import asketch.alloy.RelationAndVariableCollector;
import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import asketch.alloy.cand.Type;
import asketch.alloy.etc.RowSpan;
import asketch.alloy.exception.AlloySyntaxErrorException;
import asketch.alloy.exception.TestValuationUnsatisfiableException;
import asketch.alloy.exception.UnsupportedHoleException;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
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
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ASketchSolveSubModels {

  public static void solveMetaModelForConcreteHoleValues(AlloyProgram alloyProgram,
      CompModule module, ASketchSolveOpt opt)
      throws TestValuationUnsatisfiableException, AlloySyntaxErrorException, UnsupportedHoleException {
    // Stores solution if there is any.
    String[] solution = new String[alloyProgram.getHoles().size()];
    // Construct the meta Alloy model that encodes many models
    // and save it to the disk.
    Map<String, String> mapToOriginalRelationNames = constructMetaAlloyModelAndSaveIt(alloyProgram,
        module, opt);
    // Read meta Alloy model as AlloyProgram and iterate over
    // all combinations of operator values.
    String metaModelText = TextFileReader.readText(SOLVE_FILE_PATH);
    // Parse Alloy program to detect holes.
    AlloyProgram metaAlloyProgram = ASketchParser.parse(metaModelText);
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
    boolean foundSolution = sketchHolesAndFindSolution(metaAlloyProgram, 0, reporter, options,
        mapToOriginalRelationNames, solution);
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

  private static boolean sketchHolesAndFindSolution(AlloyProgram metaAlloyProgram, int depth,
      A4Reporter reporter, A4Options options, Map<String, String> mapToOriginalRelationNames,
      String[] solution) {
    if (depth == metaAlloyProgram.getHoles().size()) {
      String program = metaAlloyProgram.toString();
      TextFileWriter.writeText(program, SOLVE_FILE_PATH, false);
      CompModule module = compileAlloyModule(SOLVE_FILE_PATH);
      Command runSketch = null;
      for (Command command : module.getAllCommands()) {
        String cmdName = extractHTML(command.getHTML(), Pattern.compile("</b> (.*?)$"));
        if (SKETCH_COMMAND_NAME.equals(cmdName)) {
          runSketch = command;
          break;
        }
      }
      assert runSketch != null;
      try {
        A4Solution valuation = TranslateAlloyToKodkod
            .execute_command(reporter, module.getAllReachableSigs(), runSketch, options);
        if (valuation != null && valuation.satisfiable()) {
          String xml = valuation.toString();
          Matcher matcher = Pattern.compile(RESULT_E + "(\\d+)=\\{(.*?)\\$\\d*\\}").matcher(xml);
          while (matcher.find()) {
            int holeId = Integer.valueOf(matcher.group(1));
            String sigName = matcher.group(2);
            solution[holeId] = mapToOriginalRelationNames.get(sigName);
          }
          Iterator<Hole> iter = metaAlloyProgram.getHoles().iterator();
          for (int i = 0; i < solution.length; i++) {
            if (solution[i] != null) {
              continue;
            }
            solution[i] = iter.next().getContent();
          }
          return true;
        }
      } catch (Err err) {
        err.printStackTrace();
      }
      return false;
    }
    Hole hole = metaAlloyProgram.getHoles().get(depth);
    for (Candidate cand : hole.getCands()) {
      hole.setContent(cand.getValue());
      boolean isCompiled = sketchHolesAndFindSolution(metaAlloyProgram, depth + 1, reporter,
          options, mapToOriginalRelationNames, solution);
      if (isCompiled) {
        return true;
      }
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
    // Construct part of the meta Alloy model.
    StringBuilder metaModel = constructSigsAndPredsAndRun(alloyProgram, module, connectedHoles,
        sigsAndFields);
    // Load expressions generated for each hole.  Only the first
    // hole with the unique set of basic relations gets assigned.
    loadExpressionsOfHoles(alloyProgram, opt, connectedHoles);
    // Map from the sig names back to the old relation names.
    Map<String, String> mapToOriginalRelationNames = new HashMap<>();
    // Construct function declarations for expression holes.
    // Append them to the meta Alloy model
    String funDecl = generateFunDecl(alloyProgram, connectedHoles, sigsAndFields,
        mapToOriginalRelationNames);
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
    return mapToOriginalRelationNames;
  }

  public static void loadExpressionsOfHoles(AlloyProgram alloyProgram, ASketchSolveOpt opt,
      int[] connectedHoles) {
    List<Hole> holes = alloyProgram.getHoles();
    for (int i = 0; i < holes.size(); i++) {
      if (connectedHoles[i] == -1 || connectedHoles[i] != i) {
        continue;
      }
      Hole hole = holes.get(i);
      E exprHole = (E) hole;
      // Deprecated in the new evaluation settings.
      List<Relation> basicRelations = new ArrayList<>(exprHole.getPrimaryRelations());
      // Compute the path to load expressions generated by ASketchGen.
      // The expressions should be saved previously in the default
      // location with the same name computed as follows.
      String pathToLoad = computeSerialPath(opt, basicRelations);
      // Deserialize expressions
      Object obj = deserialize(pathToLoad);
      if (obj == null) {
        logger.info("Cannot find serialized expressions at " + pathToLoad);
        logger.info("You should run ASketchGen before you run ASketchSolveSubModels");
        return;
      }
      Map<Integer, Map<Integer, List<Relation>>> relationsByDepthAndArity = (Map<Integer, Map<Integer, List<Relation>>>) obj;
      // Print relations if in debugging mode.
//      printGeneratedExpressionsByDepth(opt, relationsByDepthAndArity);
      // Flatten relations
      List<Relation> relations = getAllRelationsByArity(relationsByDepthAndArity, solveArity);
      // Add relations to holes with unique scope.
      exprHole.removeAllCands();
      exprHole.addRelations(relations);
    }
  }

  /**
   * Construct meta Alloy model that encodes many Alloy models, then invoke SAT solver to find
   * concrete values for holes.
   */
  private static StringBuilder constructSigsAndPredsAndRun(AlloyProgram alloyProgram,
      CompModule module, int[] connectedHoles, List<Relation> sigsAndFields) {
    // Construct signature declarations.
    StringBuilder sigsAndPredsAndRun = new StringBuilder();
    Browsable sigParent = findSubnode(module, "sig");
    if (sigParent != null) {
      List<? extends Browsable> sigs = sigParent.getSubnodes();
      for (int i = 0; i < sigs.size(); i++) {
        Sig sig = (Sig) sigs.get(i);
        sigsAndPredsAndRun.append(sig.isAbstract == null ? "" : "abstract ");
        String mult = cardOfSig(sig);
        if (mult.equals("set")) {
          mult = "";
        } else {
          mult += " ";
        }
        sigsAndPredsAndRun.append(mult).append("sig ")
            .append(afterSubstring(sig.toString(), SLASH, true)).append(" {}\n\n");
      }
    }
    // Ignore all facts including sig facts or normal facts because
    // all tests should satisfy all facts.

    // Construct predicate declarations.
    Browsable preds = findSubnode(module, "pred");
    if (preds != null) {
      for (int i = 0; i < preds.getSubnodes().size(); i++) {
        Func pred = (Func) preds.getSubnodes().get(i);
        RowSpan predRowSpan = new RowSpan(pred.pos().y - 1, pred.pos().y2 - 1);
        replaceExprHolesWithFunCalls(alloyProgram, predRowSpan, connectedHoles);
        // Create predicate.
        // Find predicate name and append it.
        String predName = afterSubstring(
            extractHTML(pred.getHTML(), Pattern.compile("</b> (.*?)$")), SLASH, true);
        String predBody = afterSubstring(alloyProgram.getLines(predRowSpan), "{", false);
        sigsAndPredsAndRun.append("pred ").append(predName).append("(");
        // Find parameters and append them.
        String prefix = "";
        for (Browsable para : findSubnodes(pred, "parameter")) {
          String paraName = extractHTML(para.getHTML(), Pattern.compile("</b> (.*?) <i>"));
          String paraType = afterSubstring(
              extractHTML(para.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>")), SLASH, true);
          sigsAndPredsAndRun.append(prefix).append(paraName).append(": ").append(paraType);
          prefix = ", ";
        }
        // Append new parameters to the predicate.
        for (Relation relation : sigsAndFields) {
          // Ignore relations other than sigs
          List<String> types = relation.getTypes().stream()
              .map(Type::getGenType).collect(Collectors.toList());
          if (types.size() == 1) { // Signature
            String sigName = relation.getValue();
            String newSigName = sigName + "s";
            String sigType = types.get(0);
            sigsAndPredsAndRun.append(prefix).append(newSigName).append(":").append(sigType);
            prefix = ", ";
            // Since we rename sigs, we need to replace all occurrence
            // in pred body with new names.
            predBody = predBody.replaceAll(sigName, newSigName);
          } else { // Fields
            String fieldName = relation.getValue();
            String fieldType = String.join("->", types);
            sigsAndPredsAndRun.append(prefix).append(fieldName).append(":").append(fieldType);
            prefix = ", ";
          }
        }
        sigsAndPredsAndRun.append(")");
        sigsAndPredsAndRun.append(" {").append(predBody).append("\n");
      }
    }
    sigsAndPredsAndRun.append(SKETCH_COMMAND_NAME).append(": run {} for ").append(solvingScope)
        .append("\n\n");
    return sigsAndPredsAndRun;
  }

  private static void replaceExprHolesWithFunCalls(AlloyProgram alloyProgram, RowSpan rowSpan,
      int[] connectedHoles) {
    List<Hole> holes = alloyProgram.getHoles();
    for (int holeId = 0; holeId < holes.size(); holeId++) {
      // Ignore operator holes.
      if (connectedHoles[holeId] == -1) {
        continue;
      }
      Hole hole = holes.get(holeId);
      if (!rowSpan.containsLine(hole.getLineNumber())) {
        continue;
      }
      hole.setContent(generateFunCall(holeId, connectedHoles, hole.getPrimaryRelations()));
    }
  }

  private static String generateFunCall(int holeId, int[] connectedHoles,
      List<Relation> basicRelations) {
    // Find basic relations
    StringBuilder call = new StringBuilder();
    // Create unique function name.
    call.append(EXPR_FUN_NAME).append(connectedHoles[holeId]);
    // Create argument list; the first argument is the
    // special token which represents an expression.
    call.append("[").append(RESULT_E).append(holeId);
    // Create the rest of arguments; the order follows
    // the order sigs and fields are declared.
    for (Relation basicRelation : basicRelations) {
      String relationName = basicRelation.getValue();
      call.append(", ").append(relationName);
      // Here we don't append s after signatures because later we will
      // replace all sigs with new names.
    }
    call.append("]");
    return call.toString();
  }

  public static String generateFunDecl(AlloyProgram alloyProgram, int[] connectedHoles,
      List<Relation> sigsAndFields, Map<String, String> mapToOriginalRelationNames) {
    StringBuilder sigDecl = new StringBuilder();
    StringBuilder funDecl = new StringBuilder();
    List<Hole> holes = alloyProgram.getHoles();
    for (int i = 0; i < holes.size(); i++) {
      if (connectedHoles[i] == -1) {
        continue;
      }
      // Generate special sig to store solving result.
      sigDecl.append("one sig ")
          .append(RESULT_E).append(i)
          .append(" in ").append(ABSTRACT_E).append(connectedHoles[i])
          .append(" {}\n\n");
      if (connectedHoles[i] != i) {
        continue;
      }
      Hole hole = holes.get(i);
      E exprHole = (E) hole;
      List<Relation> basicRelations = exprHole.getPrimaryRelations();
      // Create abstract sig.
      String abstractSigName = ABSTRACT_E + i;
      sigDecl.append("abstract sig ").append(abstractSigName).append(" {}\n");
      // Create function parameter list.
      funDecl.append("fun ").append(EXPR_FUN_NAME).append(i)
          .append("[h: ").append(abstractSigName);
      for (Relation basicRelation : basicRelations) {
        String relationName = basicRelation.getValue();
        List<String> types = basicRelation.getTypes().stream()
            .map(Type::getGenType).collect(Collectors.toList());
        if (types.size() == 1) { // Sigs.
          if (relationName.equals(types.get(0))) {
            relationName = relationName + "s";
          }
          String relationType = types.get(0);
          String relationCard = basicRelation.getCards().get(0);
          funDecl.append(", ").append(relationName).append(": ").append(relationCard).append(" ")
              .append(relationType);
        } else { // Parameters, variables and fields.
          String relationType = String.join("->", types);
          funDecl.append(", ").append(relationName).append(": ").append(relationType);
        }
      }
      funDecl.append("]: ").append(UNIV).append(" {\n");
      // Create sub sigs and function body
      sigDecl.append("one sig ");
      String sigPrefix = "";
      String funPrefix = "";
      StringBuilder closingParentheses = new StringBuilder();
      List<Relation> cands = convertToRelations(exprHole.getCands());
      for (int cnt = 0; cnt < cands.size(); cnt++) {
        String subSigName = abstractSigName + "N" + cnt;
        String relationName = cands.get(cnt).getValue();
        String newRelationName = generateRelationNameInMetaModel(relationName, sigsAndFields);
        mapToOriginalRelationNames.put(subSigName, relationName);
        sigDecl.append(sigPrefix).append(subSigName);
        sigPrefix = ", ";
        funDecl.append(funPrefix).append("  (h = ").append(subSigName)
            .append(" => ").append(newRelationName)
            .append(" else");
        funPrefix = "\n";
        closingParentheses.append(")");
      }
      sigDecl.append(" extends ").append(abstractSigName).append("{}\n");
      funDecl.append(" ").append(NONE).append(closingParentheses.toString()).append("\n");
      funDecl.append("}\n\n");
    }
    return sigDecl.toString() + funDecl.toString();
  }

  /**
   * Run ASketchSolveSubModels to solve for sketches.
   *
   * @param args includes model name, test name, number of tests
   */
  public static void main(String[] args)
      throws UnsupportedHoleException, AlloySyntaxErrorException, TestValuationUnsatisfiableException {
//        args = new String[]{"remove_1", "dynamic", "2"};
    if (args.length != 3) {
      logger.error("Wrong number of arguments: " + args.length);
      printASketchSolveUsage();
      return;
    }
    String modelName = args[0];
    String pruningRule = args[1];
    if (!isValidPruningRule(pruningRule)) {
      logger.error("Invalid pruning rule: " + pruningRule);
      printASketchGenUsage();
      return;
    }
    int testNum = Integer.valueOf(args[2]);
    ASketchSolveOpt opt = new ASketchSolveOpt(modelName, pruningRule, testNum);
    // Check if mandatory directories and files exist, and create them if not.
    createDirsIfNotExist();
    logger.info("Arguments: " + String.join(", ", args));
    // Ignore dijkstra sig State
//        blacklist.ignore("State");
    // Read Alloy model with holes as string
    String modelPath = Paths.get(MODEL_DIR_PATH, opt.getModelName() + DOT_ALS).toString();
    String modelText = TextFileReader.readText(modelPath);
    // Parse Alloy program to detect holes.
    AlloyProgram alloyProgram = ASketchParser.parse(modelText);
    // Find relations and variables for each hole.
    CompModule module = RelationAndVariableCollector.collect(alloyProgram);
    // Reset the content of each hole because we want
    // to reuse the meta model generated for each SAT
    // solving call.
    alloyProgram.getHoles().stream().forEach(Hole::resetContent);
    // Solve for sketches
    solveMetaModelForConcreteHoleValues(alloyProgram, module, opt);
  }
}
