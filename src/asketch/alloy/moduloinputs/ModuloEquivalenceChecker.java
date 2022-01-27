package asketch.alloy.moduloinputs;

import asketch.alloy.cand.Relation;
import asketch.alloy.util.EquivalenceCheckFormula;
import asketch.opts.ASketchGenOpt;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprBinary;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprCall;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprITE;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprLet;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprList;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprQt;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprUnary;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ModuloEquivalenceChecker {
  /**
   * Maps: function name to name of all of its parameter labels
   **/
  static HashMap<String, ArrayList<String>> func_to_labels;
  /**
   * Stores most recently explored command
   **/
  public static Command cmd;
  /**
   * The module containing the model
   **/
  public static CompModule model;
  /**
   * Stores the test suite
   **/
  static ArrayList<TestCase> test_suite;
  /**
   * Maps arity to expression equivalence classes
   **/
  static HashMap<Integer, HashMap<Expression, ArrayList<Expression>>> exprEquivClasses;
  
  /**
   * Maps arity to expression equivalence classes
   **/
  static HashMap<Integer, HashMap<Relation, ArrayList<Relation>>> relEquivClasses;
  /**
   * Maps funcs to the domains (variable declarations) within them
   **/
  public static HashMap<String, HashMap<String, String>> var_decls;
  
  static ArrayList<TestCase> testgen_testsuite = new ArrayList<TestCase>();
  
  static List<Relation> basicCands;
  
  static Map<String, String> inheritanceMap;
  
  static TreeSet<Expression> encountered = new TreeSet<Expression>();

/**Modulo input methods**/


  /**
   * Determines if a new expression, check, is equivalent to an already found expression Any two
   * expressions are considered equivalent if they evaluated to the same value over all test cases.
   **/
  public static Expression equivViaTestCases(Set<Expression> exprs, Expression check,
      ArrayList<TestCase> test_suite, CompModule world) throws Err {
    /**Update the model to the current Alloy model**/
    model = world;

    /**Loop over all remaining combinations**/
    for (Expression expr : exprs) { //Search the space of all previously found non-equivalent expressions
      for (int t = 0; t < test_suite.size(); t++) {
    	if (!compareExpr(check, expr, test_suite.get(t))) {
          break;
        }
        if (t == (test_suite.size() - 1)) {
          return expr;
        }
      }
    }
    return check;
  }
  
  public static Expression equivViaTestCasesTestGen(ArrayList<Expression> exprs, Expression check,
	      ArrayList<TestCase> test_suite, CompModule world) throws Err {
	    /**Update the model to the current Alloy model**/
	    model = world;

	    /**Loop over all remaining combinations**/
	    for (Expression expr : exprs) { //Search the space of all previously found non-equivalent expressions
	      for (int t = 0; t < test_suite.size(); t++) {
	        if (!compareExprTestGen(check, expr, test_suite.get(t))) {
	          break;
	        }
	        if (t == (test_suite.size() - 1)) {
	          return expr;
	        }
	      }
	    }
	    return check;
	  }
  /**
   * Compare the value of two expressions over a test case.
   **/
  public static boolean compareExpr(Expression expr1, Expression expr2, TestCase test) throws Err {
	  
	HashMap<String, ExprVar> skolems = test.getAllSkolems();
	for (String label : skolems.keySet()) {
		model.addGlobal(label, skolems.get(label));
	}
	for (ExprVar atom : test.valuation.getAllAtoms()) {
		model.addGlobal(atom.label, atom);
	}
	  
    String expr1Result = expr1.evaluations[test.id];
    if (expr1Result == null) {
      expr1Result = getExprValue(expr1, test);
    }

    String expr2Result = expr2.evaluations[test.id];
    if (expr2Result == null) {
      expr2Result = getExprValue(expr2, test);
    }

	return expr1Result.equals(expr2Result);
  }
  
  public static boolean compareExprTestGen(Expression expr1, Expression expr2, TestCase test) throws Err {  
	   EquivalenceCheckFormula checkFormula = new EquivalenceCheckFormula(basicCands,inheritanceMap);
		   String formula = checkFormula.buildNegatedFormula(expr1.rel, expr2.rel);
		 //    String formula = "!(all n: one Node | { " + expr1.value + " = " + expr2.value + "})";
		  String repeat = test.getValuation().eval(CompUtil.parseOneExpression_fromString(model,formula)).toString();
		  
		  if(repeat.equals("true")) { return false; }
		  else { return true; }
}
	  

 public static String getRep(Expression expr, TestCase test) throws Err {
    String rep = "";
    String temp;
    for (Expression sub : expr.subExprs) {
      temp = test.prevEvals.get(sub.value);
      if (temp == null) {
        getExprValue(sub, test);
        temp = test.prevEvals.get(sub.value);
      }
      rep += temp;
      rep += sub.op;
    }
    rep += expr.op;
    return rep;
  }

  /**
   * Get the value of an expression over a test case
   **/
  public static String getExprValue(Expression expr, TestCase test) throws Err {
    String evaluation = "";
    
    /**Add valuation's skolems to the evaluator**/
    HashMap<String, ExprVar> skolems = test.getAllSkolems();
    for (String label : skolems.keySet()) {
      model.addGlobal(label, skolems.get(label));
    }
    for (ExprVar atom : test.valuation.getAllAtoms()) {
      model.addGlobal(atom.label, atom);
    }

      ArrayList<String> concrete_domain = test.getEvalDomainStmts();
      for (int i = 0; i < test.total_combos; i++) {
        evaluation += test.getValuation().eval(
            CompUtil.parseOneExpression_fromString(model, concrete_domain.get(i) + expr.value))
            .toString();
      }
      expr.evaluations[test.id] = evaluation;
      return evaluation;
  }
  
  

  public static String getExprValueTestGen(Expression expr, TestCase test) throws Err {
    String evaluation = "";

    /**Add valuation's skolems to the evaluator**/
    HashMap<String, ExprVar> skolems = test.getAllSkolems();
    for (String label : skolems.keySet()) {
      model.addGlobal(label, skolems.get(label));
    }
    for (ExprVar atom : test.valuation.getAllAtoms()) {
      model.addGlobal(atom.label, atom);
    }

    /**Then, proceed in two fashions:
     * If no variable, just get result with direct evaluator call
     * If there is a variable, then get the combination of all possible values**/
      ArrayList<String> concrete_domain = test.getEvalDomainStmts();
      for (int i = 0; i < test.total_combos; i++) {
        evaluation += test.getValuation().eval(
            CompUtil.parseOneExpression_fromString(model, concrete_domain.get(i) + expr.value))
            .toString();

      }
      return evaluation;
  }
  
  public static void reinitalizeModuloInput(TestCase test, CompModule world, List<Relation> basic,  Map<String, String> map) {
	  for (String var : var_decls.get(cmd.func).keySet()) {
      try {
          test.addDomain(var, var_decls.get(cmd.func).get(var), world);
      } catch (NumberFormatException | Err e) {
			// TODO Auto-generated catch block
              e.printStackTrace();
          }
	  }
	  test.establishDomains();
	  testgen_testsuite.add(test);	  
	  model = world;
	  basicCands = basic;
	  inheritanceMap = map;
  }
  
  public static void updateState(List<Relation> basic,  Map<String, String> map) {
	  basicCands = basic;
	  inheritanceMap = map; 
  }

  public static void initalizeModuloInput(String model_path, String test_path, int scope)
      throws Exception {
    exprEquivClasses = new HashMap<>();

    /**Required Alloy variables**/
    A4Reporter rep = new A4Reporter() {
      @Override
      public void warning(ErrorWarning msg) {
      }
    };
    A4Options options = new A4Options();
    options.solver = A4Options.SatSolver.SAT4J;

    String write_model = "";
    /**If the test suite is in a separate file, append the test suite to the model.**/
    if (!test_path.equals(model_path)) {
      write_model += "\n\n//test suite appened\n";
      for (String line : Files.readAllLines(Paths.get(test_path))) {
        write_model += line + "\n";
      }
    }

    /**Write everything to temporary model.**/
    PrintWriter writer = new PrintWriter(new FileOutputStream(new File("model.als")));
    writer.println(write_model);
    writer.close();
    String [] text = write_model.split("\n");

    /**Store the parsed Alloy model.**/
    model = CompUtil.parseEverything_fromFile(rep, null, "model.als");
    /**Stores the test suite that will get parsed in.**/
    test_suite = new ArrayList<>();
    /**Stores a mapping from func names to the variables defined within them.**/
    var_decls = new HashMap<>();

    /**Build up mapping from function names to the names of the parameters of that function.**/
    func_to_labels = new HashMap<>();
    for (Func func : model.getAllFunc()) {
      String name = func.label.substring(func.label.indexOf("/") + 1);
      if (!name.toLowerCase().startsWith("test")) {
        var_decls.put(name, new HashMap<>());
        findDomains(func.getBody(), name, text);
        ArrayList<String> param_labels = new ArrayList<>();
        for (ExprVar param : func.params()) {
          param_labels.add(param.label);
        }
        func_to_labels.put(name, param_labels);
      }
    }

    /**Execute the test suite, collect valuations for equivalence checks.**/
    int test_id = 0;
    for (Func func : model.getAllFunc()) {
      String name = func.label.substring(func.label.indexOf("/") + 1);
      if (name.toLowerCase().startsWith("test")) {
        Expr invokeTest = CompUtil.parseOneExpression_fromString(model, name + "[]");
        edu.mit.csail.sdg.alloy4compiler.ast.Command runTest = new edu.mit.csail.sdg.alloy4compiler.ast.Command(
            false, scope, -1, -1, invokeTest);
        A4Solution testResult = TranslateAlloyToKodkod
            .execute_commandFromBook(rep, model.getAllReachableSigs(), runTest, options);
        if (!testResult.satisfiable()) {
          //Not a passing test. Throw an error
          throw new Exception("All tests should pass but test \"" + name + "\" fails.");
        }
        findCmd(func.getBody(), false);
        TestCase test = new TestCase(testResult, cmd.func, cmd.valid, test_id, name,
            cmd.param_labels, cmd.param_values);

        for (String var : var_decls.get(cmd.func).keySet()) {
          test.addDomain(var, var_decls.get(cmd.func).get(var), model);
        }
        test.establishDomains();
        test_suite.add(test);
        test_id++;
      }
    }

    /**Clean up created files.**/
    File file = new File("model.als");
    file.delete();
  }
  
	public static void findDomains(Expr e, String func, String [] text){
		if(e instanceof ExprBinary){
			ExprBinary binExp = (ExprBinary)e;
			if(!(binExp.op == ExprBinary.Op.JOIN)){
				findDomains(binExp.left, func, text);
				findDomains(binExp.right, func, text);
			}
		}
		else if(e instanceof ExprQt){
			ExprQt quantFormula = (ExprQt) e;
			for(int i = 0; i < quantFormula.decls.size(); i++){
				String temp =  getText(quantFormula.decls.get(i).expr, text);
				for(ExprHasName s : quantFormula.decls.get(i).names){
					var_decls.get(func).put(s.label, temp); 
				}
			}
			findDomains(quantFormula.sub, func, text);
		}
		else if(e instanceof ExprUnary){
			ExprUnary unExp = (ExprUnary)e;
			findDomains(unExp.sub, func, text);
		}
		else if(e instanceof ExprList){
			ExprList listExp = (ExprList)e;
			for(Expr e1 : listExp.args){ 
				findDomains(e1, func, text); 
			}
		}
		else if (e instanceof ExprITE){
			ExprITE exITE = (ExprITE)e;
			findDomains(exITE.cond, func, text);
			findDomains(exITE.left, func, text);
			findDomains(exITE.right, func, text);
		}
		else if (e instanceof ExprLet){
			ExprLet exLet = (ExprLet)e;
			var_decls.get(func).put(exLet.var.label, exLet.expr.toString()); 
			findDomains(exLet, func, text);
		}
	}

  /**
   * Takes the Expr object depicting a test case's content and parses out command information - is
   * the command valid - what function is invoked - what parameters, if any, are used in that
   * command.
   **/
  public static void findCmd(Expr e, boolean negated) {
    if (e instanceof ExprBinary) {
      ExprBinary binExp = (ExprBinary) e;
      if (!(binExp.op == ExprBinary.Op.JOIN)) {
        findCmd(binExp.left, negated);
        findCmd(binExp.right, negated);
      }
    } else if (e instanceof ExprQt) {
      ExprQt quantFormula = (ExprQt) e;
      findCmd(quantFormula.sub, negated);
    } else if (e instanceof ExprUnary) {
      ExprUnary unExp = (ExprUnary) e;
      if (unExp.op == ExprUnary.Op.NOT) {
        negated = true;
      }
      findCmd(unExp.sub, negated);
    } else if (e instanceof ExprList) {
      ExprList listExp = (ExprList) e;
      for (Expr e1 : listExp.args) {
        findCmd(e1, negated);
      }
    } else if (e instanceof ExprCall) {
      ExprCall callExp = (ExprCall) e;
      String func_name = callExp.fun.label.substring(callExp.fun.label.indexOf("/") + 1);
      ArrayList<String> param_values = new ArrayList<>();
      for (Expr value : callExp.args) {
        String param_value = value.toString();
        param_value = param_value.substring(param_value.lastIndexOf("/") + 1);
        param_values.add(param_value);
      }
      cmd = new Command(func_name, !negated, func_to_labels.get(func_name), param_values);
    } else if (e instanceof ExprITE) {
      ExprITE exITE = (ExprITE) e;
      findCmd(exITE.cond, negated);
      findCmd(exITE.left, negated);
      findCmd(exITE.right, negated);
    } else if (e instanceof ExprLet) {
      ExprLet exLet = (ExprLet) e;
      findCmd(exLet, negated);
    }
  }
  /**Helper class: get text from model**/
public static String getText(Expr exp, String text []){
		int start = exp.span().x;
		int end = exp.span().x2;
		int lineStart = exp.span().y;
		int lineEnd = exp.span().y2;
		String name = "";
		if(lineStart == lineEnd){
			
			String temp = text[lineStart -1] + " ";
			name = temp.substring(start-1,end);
			if(temp.charAt(end) == ']') { name += "]"; }
			if(temp.charAt(end) == ')') { name += ")"; }
			if(name.trim().endsWith("{")) { name = name.substring(0, name.length()-1); }
		}
		else{
			name = text[lineStart -1].substring(start-1) + "\n";
			for(int i = lineStart; i < lineEnd-1; i++){	
				name += text[i] + "\n";
			}
			name += text[lineEnd-1].substring(0, end);
		}
		name = name.replaceAll("\t", "");
		if(name.startsWith("pred")){
			name = name.substring(name.indexOf("{"));
		}
		
		return name;
	}

  public static boolean isModuloInputPruned(Relation rel) {
    /**Read in the expressions that have previously been generated.**/
    boolean pruned = true;

    if((rel.getValue().contains("header") && rel.getValue().contains("_post")) || (rel.getValue().contains("link") && rel.getValue().contains("_post")) || (rel.getValue().contains("elem") && rel.getValue().contains("_post")))
    	return true;
    /**Build Expression object from Relation object**/
    for (String label : test_suite.get(0).skolems.keySet()) {
      model.addGlobal(label, test_suite.get(0).skolems.get(label));
    }
    boolean has_var = true;

    Expression check = new Expression(rel.getValue(), test_suite.size(), has_var,
        rel.getOp().getValue(), rel.getArity(), rel);

    /**Handle first encounter of this arity.**/
    if (!exprEquivClasses.containsKey(rel.getArity())) {
      exprEquivClasses.put(rel.getArity(), new HashMap<>());
      exprEquivClasses.get(rel.getArity()).put(check, new ArrayList<>());
      return false;
    }

    /**Check expression over exisiting pool of unique expressions**/
    try {
      Expression equivClass = equivViaTestCases(exprEquivClasses.get(check.arity).keySet(), check,
          test_suite, model);
      if (!exprEquivClasses.get(check.arity).containsKey(equivClass)) {
        exprEquivClasses.get(check.arity).put(equivClass, new ArrayList<>());
        return false;
      }
      exprEquivClasses.get(check.arity).get(equivClass).add(check);
    } catch (Err err) {
      err.printStackTrace();
    }
    return true;
  }
  

  public static Expression moduloInputRepresentation(Expression exp, ArrayList<Expression> equiv_classes) {
   Expression rep = null;

    /**Check expression over exisiting pool of unique expressions**/
    try {
      return equivViaTestCasesTestGen(equiv_classes, exp, testgen_testsuite, model);
    } catch (Err err) {
      err.printStackTrace();
    }
    return rep;
  }
  
  public static ArrayList<TestCase> getTestSuite(){
	  return test_suite;
  }
  
  public static HashMap<Integer, HashMap<Expression, ArrayList<Expression>>> getEquivClasses(){
	  return exprEquivClasses;
  }
  
  public static HashMap<Integer, HashMap<Relation, ArrayList<Relation>>> getRelationEquivClasses(){
	  return relEquivClasses;
  }
}
