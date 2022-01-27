package asketch;

import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.ARROW;
import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.DOT;
import static asketch.alloy.etc.Operators.MINUS;
import static asketch.alloy.etc.Operators.PLUS;
import static asketch.alloy.etc.Operators.STAR;
import static asketch.alloy.etc.Operators.TILDE;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.initalizeModuloInput;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.isModuloInputPruned;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.updateState;
import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.util.AlloyUtil.computeSerialPath;
import static asketch.alloy.util.AlloyUtil.connectExprHolesWithSameExpressions;
import static asketch.alloy.util.AlloyUtil.createRelation;
import static asketch.alloy.util.AlloyUtil.createSpecialRelation;
import static asketch.alloy.util.AlloyUtil.isCommutative;
import static asketch.alloy.util.AlloyUtil.isDynamicPruned;
import static asketch.alloy.util.AlloyUtil.isStaticPruned;
import static asketch.alloy.util.AlloyUtil.isValidExpression;
import static asketch.alloy.util.AlloyUtil.opNum;
import static asketch.etc.Names.BASIC_PRUNING;
import static asketch.etc.Names.DOT_ALS;
import static asketch.etc.Names.DYNAMIC_PRUNING;
import static asketch.etc.Names.EQUIV_DIR_PATH;
import static asketch.etc.Names.EXPR_DIR_PATH;
import static asketch.etc.Names.MODULO;
import static asketch.etc.Names.MODULO_PRUNING;
import static asketch.etc.Names.STATIC_PRUNING;
import static asketch.opts.DefaultOptions.checkArity;
import static asketch.opts.DefaultOptions.logger;
import static asketch.util.FileUtil.createDirsIfNotExist;
import static asketch.util.Util.isValidPruningRule;
import static asketch.util.Util.printASketchGenUsage;
import static asketch.util.Util.printGeneratedExpressionsByDepth;
import static asketch.util.Util.serialize;

import asketch.alloy.ASketchParser;
import asketch.alloy.RelationAndVariableCollector;
import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import asketch.alloy.etc.Constants;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
import asketch.alloy.util.AlloyProgram;
import asketch.alloy.util.EquivalenceCheckFormula;
import asketch.alloy.util.SigCollector;
import asketch.opts.ASketchGenOpt;
import asketch.util.FileUtil;
import asketch.util.TextFileReader;
import asketch.util.TextFileWriter;
import asketch.util.Timer;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import parser.ast.nodes.ModelUnit;

public class ASketchGen {

  public static void generateExpressionsForHoles(AlloyProgram alloyProgram, ASketchGenOpt opt,
      Map<String, String> inheritanceMap) {
    List<Hole> holes = alloyProgram.getHoles();
    // Quick union and find.  The indexes of holes with
    // the same scope will have the same value.
    int[] connectedHoles = connectExprHolesWithSameExpressions(holes);
    // Build a mapping from the first hole id that has
    // the unique basic candidate set.  Mainly for pretty
    // printing.
    Map<Integer, List<Integer>> connectMap = new HashMap<>();
    for (int i = 0; i < connectedHoles.length; i++) {
      if (connectedHoles[i] == -1) {
        continue;
      }
      connectMap.putIfAbsent(connectedHoles[i], new ArrayList<>());
      connectMap.get(connectedHoles[i]).add(i);
    }
    // Generate expressions for each hole and skip holes with
    // duplicated basic relations.
    for (int i = 0; i < holes.size(); i++) {
      if (connectedHoles[i] == -1 || connectedHoles[i] != i) {
        continue;
      }
      Hole hole = holes.get(i);
      E exprHole = (E) hole;
      List<Relation> basicRelations = new ArrayList<>(exprHole.getPrimaryRelations());
      // Compute the unique key for the context of this hole so that we can serialize
      // the result for later usage.  The order of basic relations follows the order
      // each relation is declared.
      String pathToSave = computeSerialPath(opt, basicRelations);
      // We sort the basicCands to check if two expression holes
      // can use the same expression function generated later.
      basicRelations.sort((o1, o2) -> {
        // We prioritize post state than pre state.
        // E.g. x' will appear before x.
        return -o1.getValue().compareTo(o2.getValue());
      });
      Timer timer = new Timer();
      timer.start();
      updateState(basicRelations,inheritanceMap);
      if (opt.getPruningRule() == MODULO_PRUNING) {
      for (int arity = 1; arity <= opt.getMaxArity(); arity++) {
          isModuloInputPruned(createSpecialRelation(Constants.NONE, arity));
          isModuloInputPruned(createSpecialRelation(Constants.UNIV, arity));
        }
        // Add identity relation.
        if (opt.getMaxArity() >= 2) {
          isModuloInputPruned(new Relation(Constants.IDEN, 1, 2));
        }
      }
      Map<Integer, Map<Integer, List<Relation>>> relations = generate(basicRelations, opt,
          inheritanceMap);
      timer.stop();
      
      HashMap<Integer, ArrayList<Relation>> exprByArity = new HashMap<Integer, ArrayList<Relation>>();
      
      for(Integer depth : relations.keySet()) {
    	  for(Integer arity : relations.get(depth).keySet()) {
    		  
    		  if(exprByArity.containsKey(arity)) {
    			  exprByArity.get(arity).addAll(relations.get(depth).get(arity));
    		  }
    		  else {
    			  exprByArity.put(arity, new ArrayList<Relation>());
    		  }
    	  }
      }
      
      
      // Compute the total number of expression for each arity across different depth.
      int[] exprNums = new int[opt.getMaxArity()];
      relations.entrySet().stream()
          .forEach(e1 -> e1.getValue().entrySet()
              .stream()
              .forEach(e2 -> exprNums[e2.getKey() - 1] += e2.getValue().size()));
      logger.info("==========");
      logger.info("Hole " + connectMap.get(i).stream()
          .map(holeId -> Integer.toString(holeId))
          .collect(Collectors.joining(",")));
      logger.info("Expression Number Sorted by Arity: " + Arrays.toString(exprNums));
      logger.info("Expression Generation Time: " + timer.getMilliSeconds());
      // Print generated expressions by depths and arities if in debugging mode.
      printGeneratedExpressionsByDepth(opt, relations);
      // Serialize generated expressions to local disk.
      serialize(relations, pathToSave);
      BufferedWriter writer;
      
      try {
  		writer = new BufferedWriter(new FileWriter(opt.getModelName() + "_exp_static.txt", false));
  		HashMap<Integer, ArrayList<Relation>> exprsByArity = new HashMap<Integer, ArrayList<Relation>>();
  		 for(Integer depth : relations.keySet()) {
  			 for(Integer arity : relations.get(depth).keySet()) {
  				if(exprsByArity.containsKey(arity)) {
  					exprsByArity.get(arity).addAll(relations.get(depth).get(arity));
  				}
  				else {
  					exprsByArity.put(arity, new ArrayList<Relation>());
  					exprsByArity.get(arity).addAll(relations.get(depth).get(arity));
  				}
  			 }
  		 }
  		 String newline = "";
  		 for(Integer arity : exprsByArity.keySet()) {
  			 for(Relation rel : exprsByArity.get(arity)) {
  				 if(rel.getValue().contains("_post") || (rel.getValue().contains("header") && rel.getValue().contains("_post")) || (rel.getValue().contains("link") && rel.getValue().contains("_post")) || (rel.getValue().contains("elem") && rel.getValue().contains("_post"))) {
  					 
  				 }
  				 else {
  					 writer.append( newline + rel.getValue() + "," + rel.getCost());
  				 newline = "\n";
  				 }
  			 }
  		 }
  	      
  	      writer.close();
  	      
  	    writer = new BufferedWriter(new FileWriter(opt.getModelName() + "_dynamic_results.txt", false));
  		writer.append("Expression Number Sorted by Arity: " + Arrays.toString(exprNums));
  		writer.append("\nExpression and Test Generation Time: " + timer.getMilliSeconds());
  		writer.close();
  	} catch (IOException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	}/* */
    }
  }

  /**
   * This function implements an iterative way for expression generation, given the basic candidates
   * for each hole.
   *
   * @param basicCands represents the basic candidate expressions used to generate expression with
   * bigger depth
   */
  private static Map<Integer, Map<Integer, List<Relation>>> generate(List<Relation> basicCands,
      ASketchGenOpt opt, Map<String, String> inheritanceMap) {
    int startLevel = 0;
    if (opt.boundOnCost()) {
      startLevel = 1;
    }
    // cands: depth/cost -> arity -> candidate expressions.
    Map<Integer, Map<Integer, List<Relation>>> cands = new HashMap<>();
    // Initialize cands with depth 0 or cost 1 to maximum depth/cost.
    for (int depthOrCost = startLevel; depthOrCost <= opt.getMaxDepthOrCost(); depthOrCost++) {
      Map<Integer, List<Relation>> newRelationsByArity = new HashMap<>();
      for (int arity = 1; arity <= opt.getMaxArity(); arity++) {
        newRelationsByArity.putIfAbsent(arity, new ArrayList<>());
      }
      cands.putIfAbsent(depthOrCost, newRelationsByArity);
    }
    // If the arity of some basic relation is greater than the maximum arity,
    // then that relation is not used to generate expressions.  We also partition
    // the pre-state (e.g. elem) and post-state (e.g. elem') if necessary.
    Set<String> visitedRels = new HashSet<>();
    List<Relation> preStateRels = new ArrayList<>();
    for (Relation basicCand : basicCands) {
      int arity = basicCand.getArity();
      // Get relations with depth 0 or cost 1.
      Map<Integer, List<Relation>> relationsByArity = cands.get(startLevel);
      // Ignore relations with arity greater than maximum arity.
      if (relationsByArity.get(arity) == null) {
        continue;
      }
      String postStateRel = basicCand.getValue() + "'";
      // Seen post state before.
      if (!basicCand.getValue().contains("'") && visitedRels.contains(postStateRel)) {
        preStateRels.add(basicCand);
      }
      // Mark as seen.
      visitedRels.add(basicCand.getValue());
      relationsByArity.get(arity).add(basicCand);
      // Give Modulo checker the basic relations.
      if (opt.getPruningRule() == MODULO_PRUNING) {
        isModuloInputPruned(basicCand);
      }
    }
    // Iteratively generate expressions from depth 2 to the maximum depth.
    // For a given depth, generate expressions from arity 1 to maximum arity.
    for (int depthOrCost = startLevel + 1; depthOrCost <= opt.getMaxDepthOrCost(); depthOrCost++) {
      Map<Integer, List<Relation>> newRelationsByArity = cands.get(depthOrCost);
      // For depth k, we choose left cands and right cands from depth 0..k-1,
      // without duplicates to avoid redundant work.
      // For cost k, we choose left cands and right cands where
      // cost(left) + cost(op) + cost(right) = k.
      for (int leftDepthOrCost = startLevel; leftDepthOrCost < depthOrCost; leftDepthOrCost++) {
        for (int rightDepthOrCost = startLevel; rightDepthOrCost < depthOrCost;
            rightDepthOrCost++) {
          // If the combination is tried before, skip it.
          if ((opt.boundOnDepth() && leftDepthOrCost != depthOrCost - 1
              && rightDepthOrCost != depthOrCost - 1)
              || (opt.boundOnCost() && leftDepthOrCost + rightDepthOrCost >= depthOrCost)) {
            continue;
          }
          for (int arity = 1; arity <= opt.getMaxArity(); arity++) {
            List<Relation> newRelations = new ArrayList<>();
            // Generate sig operations with same arity.
            if (opt.getPruningRule() >= BASIC_PRUNING) {
              // Prune based on commutativity at the depth level.
              if (leftDepthOrCost <= rightDepthOrCost) {
                addExprs(newRelations, PLUS, depthOrCost, leftDepthOrCost,
                    cands.get(leftDepthOrCost).get(arity),
                    rightDepthOrCost, cands.get(rightDepthOrCost).get(arity), opt, preStateRels,
                    basicCands,
                    inheritanceMap);
                addExprs(newRelations, AMP, depthOrCost, leftDepthOrCost,
                    cands.get(leftDepthOrCost).get(arity),
                    rightDepthOrCost, cands.get(rightDepthOrCost).get(arity), opt, preStateRels,
                    basicCands,
                    inheritanceMap);
              }
            } else {
              addExprs(newRelations, PLUS, depthOrCost, leftDepthOrCost,
                  cands.get(leftDepthOrCost).get(arity),
                  rightDepthOrCost, cands.get(rightDepthOrCost).get(arity), opt, preStateRels,
                  basicCands,
                  inheritanceMap);
              addExprs(newRelations, AMP, depthOrCost, leftDepthOrCost,
                  cands.get(leftDepthOrCost).get(arity),
                  rightDepthOrCost, cands.get(rightDepthOrCost).get(arity), opt, preStateRels,
                  basicCands,
                  inheritanceMap);
            }
            addExprs(newRelations, MINUS, depthOrCost, leftDepthOrCost,
                cands.get(leftDepthOrCost).get(arity),
                rightDepthOrCost, cands.get(rightDepthOrCost).get(arity), opt, preStateRels,
                basicCands,
                inheritanceMap);

            // Cross product of all appropriate arity
            for (int leftArity = 1; leftArity < arity; leftArity++) {
              int rightArity = arity - leftArity;
              addExprs(newRelations, ARROW, depthOrCost, leftDepthOrCost,
                  cands.get(leftDepthOrCost).get(leftArity), rightDepthOrCost,
                  cands.get(rightDepthOrCost).get(rightArity), opt, preStateRels, basicCands,
                  inheritanceMap);
            }
            // Relational composition of all arities.
            for (int leftArity = Math.max(1, arity + 2 - opt.getMaxArity());
                leftArity <= Math.min(arity + 1, opt.getMaxArity()); leftArity++) {
              int rightArity = arity + 2 - leftArity;
              addExprs(newRelations, DOT, depthOrCost, leftDepthOrCost,
                  cands.get(leftDepthOrCost).get(leftArity), rightDepthOrCost,
                  cands.get(rightDepthOrCost).get(rightArity), opt, preStateRels, basicCands,
                  inheritanceMap);
            }
            newRelationsByArity.get(arity).addAll(newRelations);
          }
        }
      }
      if (opt.getMaxArity() >= 2) {
        for (int subDepthOrCost = startLevel; subDepthOrCost < depthOrCost; subDepthOrCost++) {
          if (opt.boundOnDepth() && subDepthOrCost + 1 != depthOrCost) {
            continue;
          }
          // Special expressions for binary relations.
          addExprs(newRelationsByArity.get(2), TILDE, depthOrCost, subDepthOrCost,
              cands.get(subDepthOrCost).get(2),
              opt,
              preStateRels);
          addExprs(newRelationsByArity.get(2), STAR, depthOrCost, subDepthOrCost,
              cands.get(subDepthOrCost).get(2),
              opt,
              preStateRels);
          addExprs(newRelationsByArity.get(2), CARET, depthOrCost, subDepthOrCost,
              cands.get(subDepthOrCost).get(2),
              opt,
              preStateRels);
        }
      }
      // Dynamic pruning.  Only relations with the same arity and type
      // can be equivalent.  Because we store candidates in separate
      // depth, we need to check equivalence for each depth.
      if (opt.getPruningRule() > STATIC_PRUNING) {
        // uniqueRelations contains semantically unique relations.
        Map<Integer, List<Relation>> uniqueRelationsByArity = new HashMap<>();
        // Read equivalence checking template
        CompModule module = compileAlloyModule(
            Paths.get(EQUIV_DIR_PATH, opt.getModelName() + DOT_ALS).toString());
        EquivalenceCheckFormula checkFormula = new EquivalenceCheckFormula(basicCands,
            inheritanceMap);
        for (int arity = 1; arity <= opt.getMaxArity(); arity++) {
          List<Relation> uniqueRelations = new ArrayList<>();
          // Only check a given arity if checkArity > 0.
          if (checkArity > 0 && arity != checkArity) {
            uniqueRelations.addAll(newRelationsByArity.get(arity));
          } else {
            for (Relation relation : newRelationsByArity.get(arity)) {
              // If the relation is semantically unique w.r.t. other relations,
              // then keep it.
              boolean isEquivalent;
              switch (opt.getPruningRule()) {
                case DYNAMIC_PRUNING:
                  isEquivalent = isDynamicPruned(module, checkFormula, relation,
                      cands, startLevel, depthOrCost, uniqueRelations);
                  break;
                case MODULO_PRUNING:
                  isEquivalent = isModuloInputPruned(relation);
                  break;
                default:
                  isEquivalent = false;
              }
              if (!isEquivalent) {
                uniqueRelations.add(relation);
              }
            }
          }
          uniqueRelationsByArity.put(arity, uniqueRelations);
        }
        newRelationsByArity = uniqueRelationsByArity;
      }
      cands.put(depthOrCost, newRelationsByArity);
    }
    // Add special relations like none and univ.
    for (int arity = 1; arity <= opt.getMaxArity(); arity++) {
      cands.get(startLevel).get(arity).add(createSpecialRelation(Constants.NONE, arity));
      cands.get(startLevel).get(arity).add(createSpecialRelation(Constants.UNIV, arity));
    }
    // Add identity relation.
    if (opt.getMaxArity() >= 2) {
      cands.get(startLevel).get(2).add(createSpecialRelation(Constants.IDEN, 1));
    }
    return cands;
  }

  /**
   * Generate expression for binary operators.  E.g. +,&,-,->,.. Note that leftOperand and
   * rightOperand may be at different depths.
   */
  private static void addExprs(List<Relation> relations, Candidate op, int depthOrCost,
      int leftDepthOrCost, List<Relation> leftOperands,
      int rightDepthOrCost, List<Relation> rightOperands,
      ASketchGenOpt opt, List<Relation> preStateRels, List<Relation> basicCands,
      Map<String, String> inheritanceMap) {
    int newExpressionCost = leftDepthOrCost + op.getCost() + rightDepthOrCost;
    if (opt.boundOnCost() && newExpressionCost != depthOrCost) {
      return;
    }
    // Prune based on commutativity at the expression level.
    int trick = isCommutative(op)
        && leftDepthOrCost == rightDepthOrCost
        && opt.getPruningRule() >= BASIC_PRUNING ? 1 : 0;
    for (int i1 = 0; i1 < leftOperands.size() - trick; i1++) {
      Relation leftOperand = leftOperands.get(i1);
      int leftOpNum = opNum(leftOperand);
      // Stop generating expressions if number of operators in left operand
      // is greater than or equal to the maximum operator number.
      if (leftOpNum >= opt.getMaxOpNum()) {
        continue;
      }
      for (int i2 = i1 * trick + trick; i2 < rightOperands.size(); i2++) {
        Relation rightOperand = rightOperands.get(i2);
        int rightOpNum = opNum(rightOperand);
        // Stop generating expressions if number of operators in both left
        // operand and right operand is greater than or equal to the maximum
        // operator number.
        if (leftOpNum + rightOpNum >= opt.getMaxOpNum()) {
          continue;
        }
        if (isValidExpression(op, leftOperand, rightOperand, inheritanceMap)
            && !isStaticPruned(opt, preStateRels, basicCands, op, leftOperand, leftDepthOrCost,
            rightOperand, rightDepthOrCost, inheritanceMap)) {
          relations.add(
              createRelation(newExpressionCost, op, leftOperand, rightOperand, inheritanceMap));
        }
      }
    }
  }

  /**
   * Generate expression for unary operators.  E.g. ~,^,*.
   */
  private static void addExprs(List<Relation> relations, Candidate op, int depthOrCost,
      int subDepthOrCost, List<Relation> operands, ASketchGenOpt opt, List<Relation> preStateRels) {
    int newExpressionCost = subDepthOrCost + op.getCost();
    if (opt.boundOnCost() && newExpressionCost != depthOrCost) {
      return;
    }
    for (Relation operand : operands) {
      int opNum = opNum(operand);
      if (opNum >= opt.getMaxOpNum()) {
        continue;
      }
      if (isValidExpression(op, operand) && !isStaticPruned(opt, preStateRels, op, operand)) {
        relations.add(createRelation(newExpressionCost, op, operand));
      }
    }
  }

  /**
   * Only add signature declarations to the equivalence template.
   */
  private static void buildEquivTemplate(CompModule module, AlloyProgram program,
      ASketchGenOpt opt) {
    ModelUnit mu = new ModelUnit(null, module);
//    StringBuilder template = new StringBuilder();
//    Set<RowSpan> rowSpans = new HashSet<>();
//    Browsable sigs = findSubnode(module, "sig");
//    if (sigs != null) {
//      // Signature declarations should be separated by newlines.
//      // For example, sig A {
//      //              } sig B {
//      //              }
//      //              ^
//      // The above example results in error parsing to "sig A {\n} sig B {" and "} sig B {\n}".
//      // But sig A,B,C extends O {...} is allowed.
//      for (int i = 0; i < sigs.getSubnodes().size(); i++) {
//        Sig sig = (Sig) sigs.getSubnodes().get(i);
//        RowSpan sigRowSpan = new RowSpan(sig.pos().y - 1, sig.pos().y2 - 1);
//        rowSpans.add(sigRowSpan);
//      }
//      for (RowSpan rowSpan : rowSpans) {
//        template.append(program.getLines(rowSpan));
//      }
//    }
    // We should not add facts in the template since there could be holes in facts.

    TextFileWriter.writeText(mu.accept(new SigCollector(), null),
        Paths.get(EQUIV_DIR_PATH, opt.getModelName() + DOT_ALS).toString(), false);
  }

  /**
   * Run ASketchGen to generate expressions for holes.
   *
   * @param args includes model_name, depth, arity
   */
  public static void main(String[] args)
      throws Exception {
	  /**Generate static pruned expressions**/
	  //args = new String[]{"sketch/models/testgen/bempl.als", "5", "2", "10", "static"}; 
	  //args = new String[]{"sketch/models/testgen/bt.als", "7", "2", "10", "static"}; 
	  //args = new String[]{"sketch/models/testgen/contains.als", "7", "2", "10", "static"}; 
	  //args = new String[]{"sketch/models/testgen/coloredTree.als", "5", "2", "10", "static"}; 
	  //args = new String[]{"sketch/models/testgen/dll.als", "5", "2", "10", "static"}; 
	  //args = new String[]{"sketch/models/testgen/deadlock.als", "6", "2", "10", "static"};
	  //args = new String[]{"sketch/models/testgen/deadlock2.als", "6", "3", "10", "static"};
	  //args = new String[]{"sketch/models/testgen/grade.als", "5", "2", "10", "static"};
	  //args = new String[]{"sketch/models/testgen/sll.als", "6", "2", "10", "static"};
	  //args = new String[]{"sketch/models/testgen/remove.als", "8", "2", "10", "static"};
	  //args = new String[]{"sketch/models/testgen/fsm.als", "6", "2", "10", "static"};

    /**Generate modulo pruned expressions using robust test suite*/
	//args = new String[]{"sketch/models/testgen/arr.als", "7", "2", "10", "modulo", "tests_testgen/arr.als", "3"};
    //args = new String[]{"sketch/models/testgen/bt.als", "7", "2", "10", "modulo", "tests_testgen/bt.als", "3"}; 
    //args = new String[]{"sketch/models/testgen/contains.als", "7", "2", "10", "modulo", "tests_testgen/contains.als", "3"}; 
    //args = new String[]{"sketch/models/testgen/coloredTree.als", "4", "2", "10", "modulo", "tests_testgen/ctree.als", "3"}; 
    //args = new String[]{"sketch/models/testgen/dll.als", "5", "2", "10", "modulo", "tests_testgen/dll.als", "3"}; 
    //args = new String[]{"sketch/models/testgen/deadlock.als", "6", "2", "10", "modulo", "tests_testgen/deadlock.als", "3"};
    //args = new String[]{"sketch/models/testgen/deadlock2.als", "6", "3", "10", "modulo", "tests_testgen/deadlock.als", "3"};
    //args = new String[]{"sketch/models/testgen/grade.als", "5", "2", "10", "modulo", "tests_testgen/grade.als", "4"};
    //args = new String[]{"sketch/models/testgen/sll.als", "6", "2", "10", "modulo", "tests_testgen/sll.als", "3"};
    //args = new String[]{"sketch/models/testgen/bempl.als", "5", "2", "10", "modulo", "tests_testgen/bempl.als", "5"};
    //args = new String[]{"sketch/models/testgen/remove.als", "8", "2", "10", "modulo", "tests_testgen/remove.als", "3"};
    
    /**Generate dynamic pruned expressions**/
	//args = new String[]{"sketch/models/testgen/arr.als", "5", "3", "10", "dynamic"};
    //args = new String[]{"sketch/models/testgen/bt.als", "7", "2", "10", "dynamic"}; 
    //args = new String[]{"sketch/models/testgen/contains.als", "7", "2", "10", "dynamic"}; 
    //args = new String[]{"sketch/models/testgen/coloredTree.als", "5", "2", "10", "dynamic"}; 
    //args = new String[]{"sketch/models/testgen/dll.als", "5", "2", "10", "dynamic"}; 
    //args = new String[]{"sketch/models/testgen/deadlock.als", "6", "2", "10", "dynamic"};
    //args = new String[]{"sketch/models/testgen/deadlock2.als", "6", "3", "10", "dynamic"};
    //args = new String[]{"sketch/models/testgen/grade.als", "5", "2", "10", "dynamic"};
    //args = new String[]{"sketch/models/testgen/sll.als", "6", "2", "10", "dynamic"};
    //args = new String[]{"sketch/models/testgen/bempl.als", "5", "2", "10", "dynamic"};
    //args = new String[]{"sketch/models/testgen/remove.als", "8", "2", "10", "dynamic"};
	//args = new String[]{"sketch/models/testgen/fsm.als", "6", "2", "10", "dynamic"};
	  
    if (args.length < 5 && args.length > 7) {
      logger.error("Wrong number of arguments: " + args.length);
      printASketchGenUsage();
      return;
    }
    String modelPath = args[0];
    int maxDepth = Integer.parseInt(args[1]);
    int maxArity = Integer.parseInt(args[2]);
    // maxOpNum is at most 2^maxArity - 1
    int maxOpNum = Integer.parseInt(args[3]);
    String pruningRule = args[4];
    String testPath = null;
    int scope = -1;
    if (!isValidPruningRule(pruningRule)) {
      logger.error("Invalid pruning rule: " + pruningRule);
      printASketchGenUsage();
      return;
    }
    if (!FileUtil.fileExists(modelPath)) {
      logger.error("Cannot find model at " + modelPath);
      printASketchGenUsage();
      return;
    }
    if (pruningRule.equals(MODULO)) {
      if (args.length != 7) {
        logger.error("You must provide a test suite path and scope for " + MODULO + " pruning.");
        printASketchGenUsage();
        return;
      }
      testPath = args[5];
      scope = Integer.parseInt(args[6]);
    }
    ASketchGenOpt opt
        = new ASketchGenOpt(modelPath, maxDepth, maxArity, maxOpNum, pruningRule);
    // Check if mandatory directories and files exist, and create them if not.
    createDirsIfNotExist(EQUIV_DIR_PATH, EXPR_DIR_PATH);
    // Read Alloy model with holes as string
    String modelText = TextFileReader.readText(modelPath);
    // Parse Alloy program to detect holes.
    AlloyProgram alloyProgram = ASketchParser.parse(modelText);
    // Keep signature inheritance map.
    Map<String, String> inheritanceMap = new HashMap<>();
    // Find relations and variables for each hole.
    CompModule module = RelationAndVariableCollector.collect(alloyProgram, inheritanceMap);
//    inheritanceMap.entrySet().stream().forEach(entry->System.out.println(entry.getKey() + "," + entry.getValue()));
//    alloyProgram.getHoles().stream().forEach(
//        hole -> hole.getPrimaryRelations().stream()
//            .forEach(relation -> System.out.println(relation.getValue() + ": " + relation.getTypes().toString()))
//    );
    // Build equivalent expressions template.
    if (opt.getPruningRule() == DYNAMIC_PRUNING) {
      buildEquivTemplate(module, alloyProgram, opt);
    }
    if (opt.getPruningRule() == MODULO_PRUNING) {
      initalizeModuloInput(modelPath, testPath, scope);
      // Add special relations like none and univ.
      for (int arity = 1; arity <= opt.getMaxArity(); arity++) {
        isModuloInputPruned(createSpecialRelation(Constants.NONE, arity));
        isModuloInputPruned(createSpecialRelation(Constants.UNIV, arity));
      }
      // Add identity relation.
      if (opt.getMaxArity() >= 2) {
        isModuloInputPruned(new Relation(Constants.IDEN, 1, 2));
      }/**/
    }
    // Generate expressions.
    generateExpressionsForHoles(alloyProgram, opt, inheritanceMap);
  }
}
