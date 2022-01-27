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
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.compareExpr;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.getExprValueTestGen;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.isModuloInputPruned;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.getEquivClasses;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.moduloInputRepresentation;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.reinitalizeModuloInput;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.model;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.var_decls;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.cmd;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.updateState;
import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.moduloinputs.ModuloEquivalenceChecker.getTestSuite;
import static asketch.alloy.util.AlloyUtil.computeSerialPath;
import static asketch.alloy.util.AlloyUtil.connectExprHolesWithSameExpressions;
import static asketch.alloy.util.AlloyUtil.createRelation;
import static asketch.alloy.util.AlloyUtil.createSpecialRelation;
import static asketch.alloy.util.AlloyUtil.isCommutative;
import static asketch.alloy.util.AlloyUtil.isDynamicPruned;
import static asketch.alloy.util.AlloyUtil.isStaticPruned;
import static asketch.alloy.util.AlloyUtil.isValidExpression;
import static asketch.alloy.util.AlloyUtil.areEquivalent;
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
import static asketch.opts.DefaultOptions.maxDynamicPruningScope;
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
import asketch.alloy.moduloinputs.Expression;
import asketch.alloy.moduloinputs.ModuloEquivalenceChecker;
import asketch.alloy.moduloinputs.TestCase;
import asketch.alloy.util.AlloyProgram;
import asketch.alloy.util.EquivalenceCheckFormula;
import asketch.alloy.util.SigCollector;
import asketch.opts.ASketchGenOpt;
import asketch.util.FileUtil;
import asketch.util.TextFileReader;
import asketch.util.TextFileWriter;
import asketch.util.Timer;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
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
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import parser.ast.nodes.ModelUnit;

public class TestGen {
	
	static String oracle_check;	
	static ArrayList<TestCase> new_tests = new ArrayList<TestCase>();
	static TreeSet<Expression> truly_unique = new TreeSet<Expression>();
	static ArrayList<String> param_labels = new ArrayList<String>();	
	static int test_size = 0;
	
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

      Map<Integer, Map<Integer, List<Relation>>> relations = generate(basicRelations, opt, inheritanceMap);

      for(Integer depth : relations.keySet()) {
    	  for(Integer arity : relations.get(depth).keySet()) {
    		  for(Relation rel : relations.get(depth).get(arity)) {
    			  isModuloInputPruned(rel);
    		  }
    	  }
      } 
      
      RexGenTestGen(basicRelations, opt,  inheritanceMap);
      timer.stop();
        relations.clear();
     BufferedWriter writer;
      for(Expression exp : truly_unique) {
    		if(relations.containsKey(exp.rel.getCost())) {
    			if(relations.get(exp.rel.getCost()).containsKey(exp.rel.getArity())) {
    				relations.get(exp.rel.getCost()).get(exp.rel.getArity()).add(exp.rel);
    			}
    			else {
    				relations.get(exp.rel.getCost()).put(exp.rel.getArity(), new ArrayList<Relation>());
    				relations.get(exp.rel.getCost()).get(exp.rel.getArity()).add(exp.rel);
    			}
    		}
    		else {
    			relations.put(exp.rel.getCost(), new HashMap<Integer, List<Relation>>());
    			relations.get(exp.rel.getCost()).put(exp.rel.getArity(), new ArrayList<Relation>());
				relations.get(exp.rel.getCost()).get(exp.rel.getArity()).add(exp.rel);
    		}
      }
      
	try {
		writer = new BufferedWriter(new FileWriter(opt.getModelName() + "_start" + test_size + "_new_tests.als", false));
		 for(TestCase test : new_tests) {
	    	  writer.append(test.prettyPrintVal + "\n\n");
	      }
	      
	      writer.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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
      logger.info("Expression and Test Generation Time: " + timer.getMilliSeconds());
      logger.info("Number of New Tests Generated: " + new_tests.size());
      // Print generated expressions by depths and arities if in debugging mode.
      printGeneratedExpressionsByDepth(opt, relations);
      // Serialize generated expressions to local disk.
     
     try {
    		writer = new BufferedWriter(new FileWriter(opt.getModelName() + "_exp.txt", false));
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
    				 writer.append( newline + rel.getValue() + "," + rel.getCost());
    				 newline = "\n";
    			 }
    		 }
    	      
    	      writer.close();
    	      
      		writer = new BufferedWriter(new FileWriter(opt.getModelName() + "_start" + test_size + "_results.txt", false));
      		writer.append("Expression Number Sorted by Arity: " + Arrays.toString(exprNums));
      		writer.append("\nExpression and Test Generation Time: " + timer.getMilliSeconds());
      		writer.append("\nNumber of New Tests Generated: " + new_tests.size());
      		writer.close();

    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}/* */
      
    }
  }
  
  /**
   * This function implements a new test generation technique which uses the difference in dynamic
   * and modulo pruning to generate a strong test suite for ASketch purposes. 
   * TODO: update to get relation storage
   */
  private static void RexGenTestGen(List<Relation> basicCands, ASketchGenOpt opt, Map<String, String> inheritanceMap) {
	  /**The initial test suite**/
	  ArrayList<TestCase> initial_suite = getTestSuite();
	  test_size = initial_suite.size();
	  /**The equivalence classes found by modulo pruning**/
	  HashMap<Integer, HashMap<Expression, ArrayList<Expression>>> equivByArity = getEquivClasses();
	  CompModule module = compileAlloyModule(   Paths.get(EQUIV_DIR_PATH, opt.getModelName() + DOT_ALS).toString());
	   
	  /**Check if the modulo equivalence classes are truly equivalent
	   * Use this check to generate a test suite that is robust for sketching**/
	  for(Integer arity : equivByArity.keySet()) {
		  /**Initial unique expressions by arity**/
		  ArrayList<Expression> check =  new ArrayList<>(equivByArity.get(arity).keySet()); 

		  int index = 0;
		  while(index < check.size()) {
			  /**End condition check**/
			  /**Get the expression that represents this equivalence class*/
			  Expression class_rep = check.get(index);
			  truly_unique.add(class_rep);

			  /**Get all the expression wrapped up in the relevant equivalence class*/
			  ArrayList<Expression> equiv_class = new ArrayList<Expression>();
			  equiv_class.addAll(equivByArity.get(arity).get(class_rep));
			  
			  /**Moved to a new equivalence class so skip dynamic evaluation**/
			  HashSet<Expression> skip = new HashSet<Expression>();

			  for(int idx_expr = 0; idx_expr < equiv_class.size(); idx_expr++) {
				  Expression current = equiv_class.get(idx_expr);

				  if(!skip.contains(current)){
					  try {
					    	/**Dynamicly check expressions**/
					    	A4Options options = new A4Options();
					        EquivalenceCheckFormula checkFormula = new EquivalenceCheckFormula(basicCands,inheritanceMap);
	        			    String negatedFormulaString = checkFormula.buildNegatedFormula(current.rel, class_rep.rel);
					    	Expr negatedFormula = CompUtil.parseOneExpression_fromString(module, negatedFormulaString);					      
					    	int scope = maxDynamicPruningScope;
					        Command checkCommand = new Command(true, scope, -1, -1, negatedFormula);
					        A4Solution sol = TranslateAlloyToKodkod.execute_command(A4Reporter.NOP, module.getAllReachableSigs(), checkCommand,options);
					       
					        /**If satisfiable, then this is not equivalent*/
					        if (sol.satisfiable()) {
					        	//Create a new test
					        	TestCase test_case = new TestCase(sol,cmd.func,true,initial_suite.size() + new_tests.size() + 1, "", param_labels);	    
					        	 for (String label : test_case.skolems.keySet()) {
					       	      module.addGlobal(label, test_case.skolems.get(label));
					       	    }
					        	Expr oracle_expr = CompUtil.parseOneExpression_fromString(module, oracle_check);					      
							    boolean valid = (boolean) sol.eval(oracle_expr);
					        	test_case.valid = valid;
					        	new_tests.add(test_case);
						        reinitalizeModuloInput(test_case, module, basicCands, inheritanceMap); 

					        	//Update list of equivalence classes to check
								check.add(current);
						        equivByArity.get(arity).put(current, new ArrayList<Expression>());
						        
						        //Reform equivalence class due to new test
						        //Gather all expressions after the current one - "idx_expr" - to see if they are in a different equivalence class
						        ArrayList<Expression> temp_exprs = new ArrayList<Expression>();
						        temp_exprs.addAll(equivByArity.get(arity).get(class_rep));
						        
						        //Reset the equivalence class
						        equivByArity.get(arity).get(class_rep).clear();

						        //Set up all the possible equivalence classes 
						        ArrayList<Expression> unique_classes = new ArrayList<Expression>();
						        unique_classes.add(class_rep);
						        unique_classes.add(current);

						        for(Expression expr : temp_exprs) {
						        	//Is it the same as one of the class options over the test case?
						        	boolean unique = true;
						        	String result1 = getExprValueTestGen(expr, test_case);
						        	for(int i = 0; i < unique_classes.size(); i++) {						        		
						        		String result2 = getExprValueTestGen(unique_classes.get(i), test_case);
						        		if(result1.equals(result2)) {
						        			unique = false;
						        			equivByArity.get(arity).get(unique_classes.get(i)).add(expr);
						        			if(i > 0) //if not original class, skip over
						        				skip.add(expr);
						        			break;
						        		}
						        	}
						        	//This expression differs from class rep and all others in unique classes
						        	if(unique) {
										check.add(expr);
								        equivByArity.get(arity).put(expr, new ArrayList<Expression>());
						        		unique_classes.add(expr);
					        			skip.add(expr);
						        	}
						        }
						        
						        
					    		//Check if this test case impact the other equiv classes too
						        //This follows the same logic as above, without the skip
						        //This also needs to make sure the test cases are applied across all other arities
						        //Which also follows the same logic as above, but without addding to the to be checked list
						       for(Integer a : equivByArity.keySet()) {
						    	   //Apply impact of new test to current arity 
						    	   if(a == arity) {
								        int size = check.size();
								        for(int idx = index + 1; idx < size; idx++) {
								        	temp_exprs = new ArrayList<Expression>();
									        temp_exprs.addAll(equivByArity.get(arity).get(check.get(idx)));
									        equivByArity.get(arity).get(check.get(idx)).clear();

									        unique_classes = new ArrayList<Expression>();
									        unique_classes.add(check.get(idx));
									        for(Expression expr : temp_exprs) {
									        	boolean unique = true;
									        	String result1 = getExprValueTestGen(expr, test_case);
									        	for(int i = 0; i < unique_classes.size(); i++) {						        		
									        		String result2 = getExprValueTestGen(unique_classes.get(i), test_case);
									        		if(result1.equals(result2)) {
									        			unique = false;
									        			equivByArity.get(arity).get(unique_classes.get(i)).add(expr);
									        			break;
									        		}
									        	}

									        	if(unique) {
													check.add(expr);
											        equivByArity.get(arity).put(expr, new ArrayList<Expression>());
									        		unique_classes.add(expr);
									        	}
									        }
									        
								        }
						    	   }
						    	   else if(a > arity) { //Apply to all unchecked arities 
						        		ArrayList<Expression> classes = new ArrayList<>(equivByArity.get(a).keySet());
						        		int size = equivByArity.get(a).keySet().size();
						        		for(int idx = 0; idx < size; idx++) {

								        	temp_exprs = new ArrayList<Expression>();
									        temp_exprs.addAll(equivByArity.get(a).get(classes.get(idx)));
									        equivByArity.get(a).get(classes.get(idx)).clear();

									        unique_classes = new ArrayList<Expression>();
									        unique_classes.add(classes.get(idx));
									        for(Expression expr : temp_exprs) {
									        	boolean unique = true;
									        	String result1 = getExprValueTestGen(expr, test_case);
									        	for(int i = 0; i < unique_classes.size(); i++) {						        		
									        		String result2 = getExprValueTestGen(unique_classes.get(i), test_case);
									        		if(result1.equals(result2)) {
									        			unique = false;
									        			equivByArity.get(a).get(unique_classes.get(i)).add(expr);
									        			break;
									        		}
									        	}

									        	if(unique) {
											        equivByArity.get(a).put(expr, new ArrayList<Expression>());
									        		unique_classes.add(expr);
									        	}
									        }
								        }
						        	}
						        }							        
					    	}
					      
					    } catch (Err err) {
					      err.printStackTrace();
					    }	
				  }				    
			  }
			  index++;
		  }
	  }
	  
	  //Prep test cases for printing
	  for(int i = 0; i < new_tests.size(); i++) {
		new_tests.get(i).generate(module, new_tests.get(i).id, maxDynamicPruningScope);
	  }
  }

  /**
   * This function implements an iterative way for expression generation, given the basic candidates
   * for each hole.
//   *
   * @param basicCands represents the basic candidate expressions used to generate expression with
   * bigger depth
   */
  private static Map<Integer, Map<Integer, List<Relation>>> generate(List<Relation> basicCands,
      ASketchGenOpt opt, Map<String, String> inheritanceMap) {
    int startLevel = 0;
    if (opt.boundOnCost()) {
      startLevel = 1;
    }
    int num_exp = 0;
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
        num_exp++;
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
    	  num_exp++;
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
            	num_exp++;
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
	  
	//To run your own model, set up the following
	//args parameters:
	// args[0] = location of model
	//args[1] = max cost of expression
	//args[2] = max arity of expression
	//args[3] = max operators
	//args[4] = location of starting test suite
	//args[5] = scope
	//Additional information to provide
	//oracle_check stores the oracle string to automatically label generated test cases
	//param_labels is a list that stores any parameters to the test case command (see bempl model for example usage)
	  
	//The output is stored in the following files:
	//[modelname]_startX_new_tests.als -- stores all generated tests
	//[modelname]_exp.txt -- stores the list of generated expressions
	//[modelname]_startX_results.txt -- stores performance information about the execution
	  
	/**To run example models, the below can  be uncommented and executed**/
	  
	/**bempl**/
	/*
	oracle_check = "r in p.owns.opened_by";
	param_labels.add("p"); param_labels.add("r");
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/bempl.als", "5", "2", "10", "tests_testgen1/bempl.als", "5"};

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/bempl.als", "5", "2", "10", "tests_testgen5/bempl.als", "5"};

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/bempl.als", "5", "2", "10", "tests_testgen10/bempl.als", "5"};
	*/	
	  
	/**bt**/
	/*
	oracle_check = "all n: Node { n in BinaryTree.root.*(left + right)  => {n !in n.^(left + right) and no n.left & n.right and lone n.~(left + right)}}";
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/bt.als", "7", "2", "10", "tests_testgen1/bt.als", "3"}; 

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/bt.als", "7", "2", "10", "tests_testgen5/bt.als", "3"}; 

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/bt.als", "7", "2", "10", "tests_testgen10/bt.als", "3"}; 
	*/
	  
	/**contains**/
	/*
	oracle_check = " e in l.header.*link.elem";
	param_labels.add("l"); param_labels.add("e");		
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/contains.als", "7", "2", "10", "tests_testgen1/contains.als", "3"};

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/contains.als", "7", "2", "10", "tests_testgen5/contains.als", "3"};

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/contains.als", "7", "2", "10", "tests_testgen10/contains.als", "3"};
	*/

	/**deadlock hole domain 1**/
	/*
	oracle_check = "{some Process} \n {some s: State | all p: Process | some p.(s.waits)}";
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/deadlock.als", "6", "2", "10", "tests_testgen1/deadlock.als", "3"};

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/deadlock.als", "6", "2", "10", "tests_testgen5/deadlock.als", "3"};

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/deadlock.als", "6", "2", "10", "tests_testgen10/deadlock.als", "3"};
	*/

	/**deadlock hole domain 2**/
	/*
	oracle_check = "{some Process} and {some s: State | all p: Process | some p.(s.waits)}";
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/deadlock2.als", "6", "3", "10", "tests_testgen1/deadlock.als", "3"};

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/deadlock2.als", "6", "3", "10", "tests_testgen5/deadlock.als", "3"};

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/deadlock2.als", "6", "3", "10", "tests_testgen10/deadlock.als", "3"};
	 */	
	
	/**dll**/
	/*
	oracle_check = "all n: Node | some n.nxt => n.elem <= n.nxt.elem";
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/dll.als", "5", "2", "10", "tests_testgen1/dll.als", "3"};

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/dll.als", "5", "2", "10", "tests_testgen5/dll.als", "3"};

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/dll.als", "5", "2", "10", "tests_testgen10/dll.als", "3"};
	*/	
	  
	/**fsm**/
	/*
	oracle_check = "all n: State | n in FSM.start.*transition and FSM.stop in n.*transition"; 
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/fsm.als", "6", "2", "10", "tests_testgen1/fsm.als", "3"};

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/fsm.als", "6", "2", "10", "tests_testgen5/fsm.als", "3"};

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/fsm.als", "6", "2", "10", "tests_testgen10/fsm.als", "3"};
	*/
	
	/**grade**/
	/*
	param_labels.add("s"); param_labels.add("a");
	oracle_check = "{s in a.associated_with.assistant_for} and  {s !in a.assigned_to}";
	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/grade.als", "5", "2", "10", "tests_testgen1/grade.als", "4"};

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/grade.als", "5", "2", "10", "tests_testgen5/grade.als", "4"};

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/grade.als", "5", "2", "10", "tests_testgen10/grade.als", "4"};
	 */  
	
	/**graph**/
	/*
	oracle_check = " all n1: Node | all n2 : Node | n1 != n2 => n1 in n2.^neighbors "; 
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/graph.als", "5", "2", "10", "tests_testgen1/graph.als", "3"}; 

	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/graph.als", "5", "2", "10", "tests_testgen5/graph.als", "3"}; 

	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/graph.als", "5", "2", "10", "tests_testgen10/graph.als", "3"}; 
	*/
	
	/**remove**/
	/*
	oracle_check = "l.header.*link.elem - e = l.h_post.*l_post.e_post";
	param_labels.add("l"); param_labels.add("e");
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/remove.als", "8", "2", "10", "tests_testgen1/remove.als", "3"};
	 
	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/remove.als", "8", "2", "10", "tests_testgen5/remove.als", "3"};	
  
	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/remove.als", "8", "2", "10", "tests_testgen10/remove.als", "3"};	
	*/
	  
	/**sll**/
	/*
	oracle_check = "all n : Node | n in List.header.*link => n !in n.^link"; 
	//Start 1 Configuration
	args = new String[]{"sketch/models/testgen/sll.als", "6", "2", "10", "tests_testgen1/sll.als", "3"};
	 
	//Start 5 Configuration
	args = new String[]{"sketch/models/testgen/sll.als", "6", "2", "10", "tests_testgen5/sll.als", "3"};
	
	//Start 10 Configuration
	args = new String[]{"sketch/models/testgen/sll.als", "6", "2", "10", "tests_testgen10/sll.als", "3"};
	*/

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
    String pruningRule = "static";
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

      testPath = args[4];
      scope = Integer.parseInt(args[5]);

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
    //Set up for SketchGen with Modulo run
    buildEquivTemplate(module, alloyProgram, opt);
    initalizeModuloInput(modelPath, testPath, scope);
    // Generate expressions.
    generateExpressionsForHoles(alloyProgram, opt, inheritanceMap);
  }
}
