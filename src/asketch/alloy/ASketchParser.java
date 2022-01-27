package asketch.alloy;

import static asketch.alloy.etc.Constants.UNIV;
import static asketch.util.StringUtil.beforeSubstring;

import asketch.alloy.cand.Relation;
import asketch.alloy.exception.UnsupportedHoleException;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
import asketch.alloy.util.AlloyProgram;
import java.util.Arrays;
import java.util.List;

public class ASketchParser {

  public static AlloyProgram parse(String modelText) throws UnsupportedHoleException {
    AlloyProgram program = new AlloyProgram();
    String[] lines = modelText.split("\n");
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].trim().contains("//")) {
        lines[i] = beforeSubstring(lines[i], "//", false);
      }
      if (lines[i].trim().contains("--")) {
        lines[i] = beforeSubstring(lines[i], "--", false);
      }
      int begin = 0;
      int lastEnd = 0;
      while (begin < lines[i].length()) {
        if (lines[i].charAt(begin) == '\\') {
          int end = begin + 1;
          while (end < lines[i].length()) {
            if (lines[i].charAt(end) == '\\') {
              program.addFragment(lines[i].substring(lastEnd, begin), i);
              program.addHole(lines[i].substring(begin, end + 1), i, begin, end + 1);
              lastEnd = end + 1;
              begin = end + 1;
              break;
            } else {
              end += 1;
            }
          }
        }
        begin += 1;
      }
      program.addFragment(lines[i].substring(lastEnd), i);
    }
    // Add candidate sigs to each expression hole
    List<Relation> primSigs = Arrays
        .asList(new Relation(UNIV, 1), new Relation("(" + UNIV + "->" + UNIV + ")", 2),
            new Relation("(" + UNIV + "->" + UNIV + "->" + UNIV + ")", 3), new Relation("0", 1));
    for (Hole hole : program.getHoles()) {
      if (!(hole instanceof E)) {
        continue;
      }
      E exprHole = (E) hole;
      exprHole.addRelations(primSigs);
    }
    return program;
  }

//  private static List<Relation> extractPrimSig(String modelText) {
//    List<Relation> primSigs = new ArrayList<Relation>();
//    for (String line : modelText.split("\n")) {
//      extractPrimSigByLine(line, primSigs);
//    }
//    return primSigs;
//  }
//
//  /**
//   * At this point we only care about signatures that make the program compile.  We don't care if
//   * the sig is abstract or the multiplicity of the sig.
//   */
//  private static void extractPrimSigByLine(String line, List<Relation> result) {
//    Pattern p = Pattern.compile(".*sig\\s+(\\w+)\\s*\\{?.*");
//    Matcher m = p.matcher(line);
//    if (m.matches()) {
//      String sig = m.group(1);
//      sig = afterSubstring(sig, SLASH, true);
//      // All sigs should be of arity 1
//      result.add(new Relation(sig, 1));
//    }
//  }
}
