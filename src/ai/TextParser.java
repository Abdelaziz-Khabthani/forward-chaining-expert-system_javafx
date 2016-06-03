package ai;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

/**
 *
 * @author Abdelaziz Khabthani
 */
public final class TextParser {

    private TextParser() {
    }

    // But Syntax
    // cette methode verife aussi si le but apparait juste une et une seule fois
    // dans la partie droite de base des regles 
    //si l'utilisateur ecrit A cette fonction retourne A
    public static String parseBut(String text, ArrayList<HashMap<String, ArrayList<String>>> baseDesRegles) throws SyntaxException, ParseException {
        boolean found = false;

        if (!text.matches("([A-Z])")) {
            //check syntax.
            throw new SyntaxException("Erreure syntaxique (11), au niveau du but.");
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        for (HashMap<String, ArrayList<String>> regle : baseDesRegles) {
            ArrayList<String> conditions = regle.get("condition");
            ArrayList<String> resultas = regle.get("resultat");
            if (conditions.contains(text)) {
                throw new ParseException("Erreure semantique (21), Le but ne doit pas apraitre dans la partie gauche du base des regles.", 0);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (resultas.contains(text) && found) {
                throw new ParseException("Erreure semantique (22), Le but doit apraitre qu'une seule fois dans la partie droite.", 0);
            }
            if (resultas.contains(text) && !found) {
                found = true;
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        if (!found) {
            throw new ParseException("Erreure semantique (26), Le but saisi n'existe pas dans la partie droite du base des regles.", 0);
        }
        return text;
    }

    // Base des faits Syntax
    //si l'utilisateur ecrit A,E,R,Z,Y cette methde retourne une liste [A,E,R,Z,Y]
    public static ArrayList<String> parseFait(String text) throws SyntaxException, ParseException {
        ArrayList<String> resultList;
            if (!text.matches("(([A-Z])((\\,[A-Z])*))")) {
            //check syntax.
            resultList = null;
            throw new SyntaxException("Erreure syntaxique (12), au niveau du Base des faits.");
        } else {
            resultList = new ArrayList<>(Arrays.asList(text.split(",")));
            //check semantique (on cherche la redendance) ex: A,R,A,A
            Set<String> set = new HashSet<>(resultList);
            if (set.size() < resultList.size()) {
                resultList = null;
                throw new ParseException("Erreure sementatique (23), Il y a une redondance dans la base des faits \'...X,X...\'.", 0);
            }
        }
        return resultList;
    }

    /*
     * Base des regles Syntaxe
     * Si l'utilisateur tape:
     *      A ET B ET C ALORS D
     *      B ET C ALORS X
     *      F ALORS G
     * alors cette fonction retourne une liste des maps sous cette forme:
     *      [
     *          ["condition"=>["A", "B", "C"], "resultat"=>["D"]],
     *          ["condition"=>["B", "C"], "resultat"=>["X"]],
     *          ["condition"=>["F"], "resultat"=>["G"]]
     *      ]
     */
    public static ArrayList<HashMap<String, ArrayList<String>>> parseRegle(String text) throws SyntaxException, ParseException {
        ArrayList<HashMap<String, ArrayList<String>>> resultList = new ArrayList<HashMap<String, ArrayList<String>>>();

        HashMap<String, ArrayList<String>> tmpMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> tmpList = new ArrayList<String>();
        ArrayList<String> tmpListResultat = new ArrayList<String>();

        if (!text.matches("((([A-Z])((( ET )([A-Z]))*)( ALORS )([A-Z])((( ET )([A-Z]))*)(\\R)?)+)")) {
            //check syntax.
            resultList = null;
            throw new SyntaxException("Erreure syntaxique (13), au niveau du base des regles.");
        } else {
            String[] lines = text.split("\\R");
            // lire ligne par ligne
            for (String line : lines) {
                if (!line.equals("")) {
                    String[] parts = line.split(" ALORS ");

                    //conditions
                    // si on est dans ce cas A ALORS X
                    if (parts[0].length() == 1) {
                        tmpList.add(parts[0]);
                    } else {// si on est dans ce cas A ET B ET ... ALORS C;
                        String[] conditions = parts[0].split(" ET ");
                        for (String condition : conditions) {
                            tmpList.add(condition);
                        }
                    }

                    //Resultats
                    // si on est dans ce cas A ALORS X
                    if (parts[1].length() == 1) {
                        tmpListResultat.add(parts[1]);
                    } else {// si on est dans ce cas A ALORS C ET F ET...
                        String[] resultatts = parts[1].split(" ET ");
                        for (String resultatt : resultatts) {
                            tmpListResultat.add(resultatt);
                        }
                    }

                    tmpMap.put("condition", new ArrayList<String>(tmpList));
                    tmpMap.put("resultat", new ArrayList<String>(tmpListResultat));
                    resultList.add(new HashMap<>(tmpMap));
                    tmpList.clear();
                    tmpListResultat.clear();
                    tmpMap.clear();
                }
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////
        for (HashMap<String, ArrayList<String>> result : resultList) {
            ArrayList<String> conditions = result.get("condition");
            ArrayList<String> resultas = result.get("resultat");

            // Le cas du (A ALORS A)
            for (String condition : conditions) {
                if (resultas.contains(condition)) {
                    resultList = null;
                    throw new ParseException("Erreure sementique (27), Dans la base des regles il ya \'...X... ALORS ...X...\'.", 0);
                }
            }
            //Le cas du (A ALORS B ET B)
            Set<String> set = new HashSet<>(resultas);
            if (set.size() < resultas.size()) {
                resultList = null;
                throw new ParseException("Erreure sementatique (24), Dans la base des regles il y a \'... ALORS ...X ET X...\'.", 0);
            }
            //Le cas du (B ET B ALORS A)
            Set<String> setC = new HashSet<>(conditions);
            if (setC.size() < conditions.size()) {
                resultList = null;
                throw new ParseException("Erreure sementatique (25), Dans la base des regles il y a \'...X ET X...ALORS ...\'.", 0);
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////
        return resultList;
    }

    public static boolean chainageAvant(ArrayList<String> faits, String but, ArrayList<HashMap<String, ArrayList<String>>> regles) {
        while ((!faits.contains(but)) && (ReglesApplicables(faits, regles).size() > 0)) {
            ArrayList<HashMap<String, ArrayList<String>>> reglesApplicables = ReglesApplicables(faits, regles);
            for (HashMap<String, ArrayList<String>> regleApplicable : reglesApplicables) {
                ArrayList<String> resultats = regleApplicable.get("resultat");
                faits.addAll(resultats);
            }
            regles.removeAll(reglesApplicables);
            System.out.println("buts" + but);
            System.out.println("faits" + faits);
            System.out.println("regles" + regles);
        }
        return faits.contains(but);
    }

    /*
     * BR = [
     *          ["condition"=>["A", "B", "C"], "resultat"=>["D"]],
     *          ["condition"=>["B", "C"], "resultat"=>["X"]],
     *          ["condition"=>["F"], "resultat"=>["G"]]
     *      ] 
     * BF = [A,B,C]
     */
    private static ArrayList<HashMap<String, ArrayList<String>>> ReglesApplicables(ArrayList<String> faits, ArrayList<HashMap<String, ArrayList<String>>> regles) {
        ArrayList<HashMap<String, ArrayList<String>>> result = new ArrayList<HashMap<String, ArrayList<String>>>();

        for (HashMap<String, ArrayList<String>> regle : regles) {
            ArrayList<String> conditions = regle.get("condition");

            boolean test = true;
            for (String condition : conditions) {
                if (!faits.contains(condition)) {
                    test = false;
                    break;
                }
            }
            if (test) {
                result.add(regle);
            }
        }
        return result;
    }
}
