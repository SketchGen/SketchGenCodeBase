package asketch.evaluation;

import static asketch.opts.DefaultOptions.logger;

import asketch.alloy.ASketchParser;
import asketch.alloy.RelationAndVariableCollector;
import asketch.alloy.exception.AlloySyntaxErrorException;
import asketch.alloy.exception.UnsupportedHoleException;
import asketch.alloy.fragment.E;
import asketch.alloy.util.AlloyProgram;
import asketch.util.TextFileReader;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import java.util.HashMap;
import java.util.Map;
import parser.ast.nodes.Assertion;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.Body;
import parser.ast.nodes.CallExpr;
import parser.ast.nodes.CallFormula;
import parser.ast.nodes.Check;
import parser.ast.nodes.ConstExpr;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.FieldDecl;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.Function;
import parser.ast.nodes.ITEExpr;
import parser.ast.nodes.ITEFormula;
import parser.ast.nodes.LetExpr;
import parser.ast.nodes.ListExpr;
import parser.ast.nodes.ListFormula;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.ModuleDecl;
import parser.ast.nodes.OpenDecl;
import parser.ast.nodes.ParamDecl;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.QtExpr;
import parser.ast.nodes.QtFormula;
import parser.ast.nodes.Run;
import parser.ast.nodes.SigDecl;
import parser.ast.nodes.SigExpr;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.VarDecl;
import parser.ast.nodes.VarExpr;
import parser.ast.visitor.VoidVisitorAdapter;
import parser.etc.Context;
import parser.util.AlloyUtil;
import parser.util.FileUtil;

public class ModelInfoCollector {

  public static void collectModelInfo(String modelPath, String sketchPath)
      throws Err, UnsupportedHoleException, AlloySyntaxErrorException {
    CompModule module = AlloyUtil.compileAlloyModule(modelPath);
    assert module != null;
    ModelUnit mu = new ModelUnit(null, module);
    NodeCountingVisitor ncv = new NodeCountingVisitor();
    mu.accept(ncv, null);
    NodeCounter nc = ncv.getNodeCounter();
    logger.info("Total AST Num: " + nc.getTotalNum());
    logger.info("Total Sig Num: " + nc.getSigNum());
    logger.info("Total Rel Num: " + nc.getRelNum());

    String modelText = TextFileReader.readText(sketchPath);
    AlloyProgram alloyProgram = ASketchParser.parse(modelText);
    RelationAndVariableCollector.collect(alloyProgram, null);
    // Assume only one expression hole
    alloyProgram.getHoles().stream()
        .filter(hole -> hole instanceof E)
        .forEach(exprHole ->
            logger.info("Total Var Num: " + exprHole.getPrimaryRelations().size()));

    Command runEmpty = new Command(false, 3, 3, 3, module.getAllReachableFacts());
    A4Reporter reporter = new A4Reporter() {
      @Override
      public void solve(int primaryVars, int totalVars, int clauses) {
        super.solve(primaryVars, totalVars, clauses);
        logger.info("Primary Var Num: " + primaryVars);
      }
    };
    A4Options options = new A4Options();
    TranslateAlloyToKodkod
        .execute_command(reporter, module.getAllReachableSigs(), runEmpty, options);
  }

  public static void main(String... args)
      throws Err, UnsupportedHoleException, AlloySyntaxErrorException {
//    args = new String[]{"sketch/models/complete/addr.als", "sketch/models/gen/addr.als"};
    if (args.length != 2) {
      logger.error("Wrong number of arguments: " + args.length);
      logger.info("Model Info Collector takes as input a concrete model path and its sketch path.");
      return;
    }
    String modelPath = args[0];
    String sketchPath = args[1];
    if (!FileUtil.fileExists(modelPath)) {
      Context.logger.error("Cannot find model path at " + modelPath);
    }
    if (!FileUtil.fileExists(sketchPath)) {
      Context.logger.error("Cannot find sketch path at " + sketchPath);
    }
    collectModelInfo(modelPath, sketchPath);
  }
}

class NodeCounter {

  // Total number of AST nodes.
  private int totalNum;
  // Total number of signatures.
  private int sigNum;
  // Total number of relations.
  private int relNum;

  public NodeCounter() {
    this.totalNum = 0;
    this.sigNum = 0;
    this.relNum = 0;
  }

  public void incTotalNum() {
    totalNum++;
  }

  public void incSigNum() {
    sigNum++;
  }

  public void incRelNum() {
    relNum++;
  }

  public int getTotalNum() {
    return totalNum;
  }

  public int getSigNum() {
    return sigNum;
  }

  public int getRelNum() {
    return relNum;
  }
}

class NodeCountingVisitor extends VoidVisitorAdapter<Object> {

  private NodeCounter nodeCounter;

  public NodeCountingVisitor() {
    this.nodeCounter = new NodeCounter();
  }

  public NodeCounter getNodeCounter() {
    return nodeCounter;
  }

  public void visit(ModelUnit n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(ModuleDecl n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(OpenDecl n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(SigDecl n, Object arg) {
    nodeCounter.incTotalNum();
    nodeCounter.incSigNum();
    super.visit(n, arg);
  }

  public void visit(FieldDecl n, Object arg) {
    nodeCounter.incTotalNum();
    nodeCounter.incRelNum();
    super.visit(n, arg);
  }

  public void visit(ParamDecl n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(VarDecl n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(ExprOrFormula n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(SigExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(FieldExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(VarExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(UnaryExpr n, Object arg) {
    // Only count meaningful AST Node
    if (n.getOp() != UnaryExpr.UnaryOp.NOOP) {
      nodeCounter.incTotalNum();
    }
    super.visit(n, arg);
  }

  public void visit(UnaryFormula n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(BinaryExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(BinaryFormula n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(ListExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(ListFormula n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(CallExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(CallFormula n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(QtExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(QtFormula n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(ITEExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(ITEFormula n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(LetExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(ConstExpr n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(Body n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(Predicate n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(Function n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(Fact n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(Assertion n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(Run n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }

  public void visit(Check n, Object arg) {
    nodeCounter.incTotalNum();
    super.visit(n, arg);
  }
}