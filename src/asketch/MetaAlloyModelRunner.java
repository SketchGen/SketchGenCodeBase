package asketch;

import static asketch.alloy.etc.Constants.SKETCH_COMMAND_NAME;
import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.util.AlloyUtil.extractHTML;
import static asketch.opts.DefaultOptions.logger;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is an auxiliary class that helps run Alloy model with better logging.  Mainly for comparing
 * all-in-one technique to the sub-model technique.
 */
public class MetaAlloyModelRunner {

  public static void main(String[] args) throws Err {
    String modelPath = Paths.get(args[0]).toString();
    CompModule module = compileAlloyModule(modelPath);
    Command runSketch = null;
    for (Command command : module.getAllCommands()) {
      String cmdName = extractHTML(command.getHTML(), Pattern.compile("</b> (.*?)$"));
      if (SKETCH_COMMAND_NAME.equals(cmdName)) {
        runSketch = command;
        break;
      }
    }
    assert runSketch != null;
    A4Reporter reporter = new A4Reporter() {
      @Override
      public void translate(String solver, int bitwidth, int maxseq, int skolemDepth,
          int symmetry) {
        super.translate(solver, bitwidth, maxseq, skolemDepth, skolemDepth);
        logger.info("Solver: " + solver);
      }

      @Override
      public void solve(int primaryVars, int totalVars, int clauses) {
        super.solve(primaryVars, totalVars, clauses);
        logger.info("Primary Vars: " + primaryVars);
        logger.info("Total Vars: " + totalVars);
        logger.info("Clauses: " + clauses);
      }

      @Override
      public void resultSAT(Object command, long solvingTime, Object solution) {
        super.resultSAT(command, solvingTime, solution);
        logger.info("Solving Time: " + solvingTime);
      }
    };
    A4Options options = new A4Options();
    A4Solution valuation = TranslateAlloyToKodkod
        .execute_command(reporter, module.getAllReachableSigs(), runSketch, options);
    if (valuation != null && valuation.satisfiable()) {
      logger.info("Solution Found:");
      String xml = valuation.toString();
      Matcher matcher = Pattern.compile(".*(R\\S+)=\\{(.*?)\\$\\d*\\}").matcher(xml);
      while (matcher.find()) {
        String resSigName = matcher.group(1);
        String sigName = matcher.group(2);
        logger.info(resSigName + ": " + sigName);
      }
    }
  }
}
