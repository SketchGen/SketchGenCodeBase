package asketch.alloy.moduloinputs;
import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

public class TestCase {
	/**Stores a test case's valuation as an A4Solution object -- already solved by the SAT solver**/
	public A4Solution valuation;
	/**Stores a test case's command**/
	String command;
	/**Stores test case's label**/
	String label;
	/**Stores the validity of a test case. An invalid test case invokes the negation of a predicate.**/
	public boolean valid;
	/**Unique number for test case look ups**/
	public int id;
	/**Total number of combos to explore to cover variable domains**/
	public int total_combos;
	/**Stores the value passed to parameter(s), if any**/
	ArrayList<String> param_values;
	/**Stores the label of the command's parameter(s), if any**/
	ArrayList<String> param_labels;
	/**Stores the different combos of the domaain(s)**/
	ArrayList<String> eval_domains;
	/**Stores the skolems to add to the CompModule for any evaluator calls.**/
	public HashMap<String, ExprVar> skolems;
	/**Stores the outer domain(s) that impact the expression**/
	ArrayList<Domain> domains;
	public String prettyPrintVal;
	
	public HashMap<String, String> prevEvals;
	public HashMap<String, String> reps;
	
	/**Build test case given: valuation and command**/
	public TestCase(A4Solution valuation, String command, boolean valid, int id, String label){
		this.valuation = valuation;
		this.command = command;
		this.valid = valid;
		this.id = id;
		this.label = label;
		param_values = new ArrayList<String>();
		param_labels = new ArrayList<String>();
		domains = new ArrayList<Domain>();
		eval_domains = new ArrayList<String>();
		skolems = new HashMap<String, ExprVar>();
		
		prevEvals = new HashMap<String, String>();
		reps = new HashMap<String, String>();
		
		/**Generate the skolems needed to evaluate expressions over this test valuation.**/
		for(ExprVar skolem : valuation.getAllSkolems()){ 
			for(int i = 0; i < param_labels.size(); i++){
				if(skolem.label.equals("$" + param_labels.get(i))){
					skolems.put(param_labels.get(i), skolem);  
				}
				else {
					skolems.put(skolem.label, skolem);
				}
			}
		}
	}
	
	public TestCase(A4Solution valuation, String command, boolean valid, int id, String label, ArrayList<String> plabels){
		this.valuation = valuation;
		this.command = command;
		this.valid = valid;
		this.id = id;
		this.label = label;
		param_values = new ArrayList<String>();
		param_labels = new ArrayList<String>();
		domains = new ArrayList<Domain>();
		eval_domains = new ArrayList<String>();
		skolems = new HashMap<String, ExprVar>();
		
		prevEvals = new HashMap<String, String>();
		reps = new HashMap<String, String>();
		
		for(String plabel : plabels) {
			param_labels.add(plabel);
		}
		
		for(String line : valuation.toString().split("\n")) {
			for(int i = 0; i < param_labels.size(); i++) {
				if(line.contains("skolem $" + param_labels.get(i))){
					param_values.add(line.substring(line.indexOf("{")+1, line.indexOf("}")).replaceAll("\\$", ""));
				}
			}
		}

		/**Generate the skolems needed to evaluate expressions over this test valuation.**/
		for(ExprVar skolem : valuation.getAllSkolems()){ 
			
			for(int i = 0; i < param_labels.size(); i++){
				if(skolem.label.equals("$" + param_labels.get(i))){
					skolems.put(param_labels.get(i), skolem);  
				
				}
				else {
					skolems.put(skolem.label, skolem);
				}
			}
		}
	}
	
	/**Build test case given: valuation, command, set of parameters**/
	public TestCase(A4Solution valuation, String command, boolean valid, int id, String label, ArrayList<String> param_labels, ArrayList<String> param_values){
		this.valuation = valuation;
		this.command = command;
		this.valid = valid;
		this.id = id;
		this.label = label;
		this.param_values = new ArrayList<String>();
		this.param_labels = new ArrayList<String>();
		for(String param : param_values){
			this.param_values.add("$" + label + "_" + param);
		}
		this.param_labels.addAll(param_labels);
		domains = new ArrayList<Domain>();
		eval_domains = new ArrayList<String>();
		skolems = new HashMap<String, ExprVar>();
		
		prevEvals = new HashMap<String, String>();
		reps = new HashMap<String, String>();
		
		/**Generate the skolems needed to evaluate expressions over this test valuation.**/
		for(ExprVar skolem : valuation.getAllSkolems()){ 
			for(int i = 0; i < param_values.size(); i++){
				if(skolem.label.equals("$" + label + "_" + param_values.get(i))){
					skolems.put(param_labels.get(i), skolem);  
				}
				else {
					skolems.put(skolem.label, skolem);
				}
			}
		}
	}
	
	/**Public methods to retrieve information**/
	public A4Solution getValuation() { return valuation; }
	public String getCommand() { return command; }
	public ArrayList<Domain> getDomains() { return domains; }
	public ArrayList<String> getEvalDomainStmts() { return eval_domains; }
	public ArrayList<String> getParamLabels() { return param_labels; }
	public ArrayList<String> getParamValues() { return param_values; }
	public String getParamX(int x) { return param_values.get(x); }
	public HashMap<String, ExprVar> getAllSkolems() { return skolems; }
	
	/**Adds a domain and gathers its specific information related to this test case.
	 * Size: how many elements in this domain given this test case's valuation
	 * Elements: what are the elements in this domain given this test case's valuation**/
	public void addDomain(String var, String domain, CompModule model) throws NumberFormatException, Err{
		/*Determine size of domain*/
		for(String label : skolems.keySet()){ model.addGlobal(label, skolems.get(label)); }	
		for(ExprVar atom : valuation.getAllAtoms()) { model.addGlobal(atom.label, atom); }
		int size = Integer.valueOf(valuation.eval(CompUtil.parseOneExpression_fromString(model, "#{" + domain + "}")).toString());

		/*Get each element of the domain*/
		ArrayList<String> values = new ArrayList<String>();
		if(size == 0){
			values.add(domain);
		}
		else{
			String evaluation = valuation.eval(CompUtil.parseOneExpression_fromString(model, domain )).toString();
			evaluation = evaluation.substring(1, evaluation.length() - 1); //remove leading and trailing {}
			String [] elements = evaluation.split(",");
			for(String element : elements) { 
				values.add(element); 
				
			}
		}
		
		/*Add the domain*/
		domains.add(new Domain(var, domain, values));
	}	
	
	public void establishDomains(){
		total_combos = 1;
		int [] changes = new int[domains.size()];
		for(int i = 0; i < domains.size(); i++){ 
			changes[i] = total_combos;
			total_combos *= domains.get(i).elements.size(); 
		}
		int [] val = new int [domains.size()];
		for(int i = 0; i < val.length; i++) { val[i] = 0; }

		/*Loop over all combinations, building the string to pass to the evaluator for each combo as needed*/
		for(int i = 0; i < total_combos; i++){	
			String let = "";
			for(int j = 0; j < val.length; j++){
				String var = domains.get(j).variable;
				String exprVar = domains.get(j).elements.get(val[j]);                      
				let += " let " + var + " = " + exprVar + " | "; 
				

			}

			eval_domains.add(let);
			
			/*Iterate to next combinations*/
			for(int j = 0; j < val.length; j++){
				if((i + 1) % changes[j] == 0 ){
					val[j] = val[j] + 1;
					if(val[j] == (domains.get(j).elements.size()))
						val[j] = 0;
				}
			}
		}
	}
	

public String generate(CompModule world, int predNum, int scope){
		
		//intial set up to create valuation
		prettyPrintVal = "";
		String [] lines = valuation.toString().split("\n");
		String sigValues = "";
		String noSig = "";
		String disjSig = "";
		String relValues = "";
		String noRel = "";
		String and = "";
		String command_set = "";
		
		ArrayList<String> sigsThatExtend = new ArrayList<String>();
		
		for(Sig sig : world.getAllSigs()){
			if(!sig.isTopLevel())
				sigsThatExtend.add(sig.label.replaceAll("this/", ""));
		}
		
		for(String line : lines){
			if(line.contains("this/")){
				if(line.contains("<:")){ //relation
					String [] temp = line.split("=");
					String relation = temp[0].substring(temp[0].indexOf(":")+1);
					if(temp[1].equals("{}")){
						noRel += and + "no " + relation;
					}
					else{
						String vals = temp[1];
						vals = vals.substring(1, vals.length()-1);
						vals = vals.replaceAll("\\$","");
						vals = vals.replaceAll(",", " +");
						relValues += and + relation + " = " + vals;
					}
				}
				else{ //sig
					String [] temp = line.split("=");
					String sig = temp[0].substring(5);
					if(temp[1].equals("{}")){
						noSig +=  "\n\t\tno " + sig;
					}
					else{
						String vals = temp[1];
						vals = vals.substring(1, vals.length()-1);
						vals = vals.replaceAll("\\$","");
						vals = vals.replaceAll(",", " +");
						sigValues += and + sig + " = " + vals;
						
						
						if(!sigsThatExtend.contains(sig)){
							String disjVals = temp[1];
							disjVals = disjVals.substring(1, disjVals.length()-1);
							disjVals = disjVals.replaceAll("\\$","");
							disjSig += " some disj " + disjVals + " : " + sig + " | ";
						}			
					}
				}
				and = "\n\t\t";
			}
		}
		String orders = "";
		for(String line : lines){
			if(line.contains("/Ord") && !line.contains("univ")){
				if(line.contains("First")){
					String [] temp = line.split("=");
					String first = temp[1];
					first = first.substring(1, first.length()-1);
					first = first.replaceAll("\\$","");
					first = first.replaceAll(",", " +");
					
					String [] getName = temp[0].split("<:");
					String [] futherGetName = getName[0].split("/");
					
					first = first.replaceAll(getName[0]+ "0\\-\\>", "");
					orders += and + futherGetName[0] + "/first = " + first;
				}
				else if(line.contains("Next")){
					String [] temp = line.split("=");
					String nexts = temp[1];
					nexts = nexts.substring(1, nexts.length()-1);
					nexts = nexts.replaceAll("\\$","");
					nexts = nexts.replaceAll(",", " +");
					
					String [] getName = temp[0].split("<:");
					String [] futherGetName = getName[0].split("/");
					
					nexts = nexts.replaceAll(getName[0]+ "0\\-\\>", "");
					orders += and + futherGetName[0] + "/next = " + nexts;
				}
			}
		}
		
		if(!valid) { command_set += "!"; }
		command_set += command + "[";
		String comma = "";
		for(String param : param_values) {
			command_set += comma + param;
			comma = ",";
		}
		command_set += "]";
	
		prettyPrintVal = "pred Test" + predNum + "{\n\t" + disjSig + " {\n\t\t" + sigValues + noSig + relValues + noRel + orders + "\n\t\t" + command_set + "\n\t}\n}";
		prettyPrintVal += "\n\nrun Test" + predNum + " for " + scope;
		return prettyPrintVal;
	}
	
}
