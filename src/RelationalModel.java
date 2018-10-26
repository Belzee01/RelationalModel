import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RelationalModel {

    private List<String> attributes;
    private List<Pair<String[], String[]>> keyValues;

    public RelationalModel() {
        this.attributes = new ArrayList<>();
        this.keyValues = new ArrayList<>();
    }

    public void setAttributes(String[] atributes) {
        this.attributes = Arrays.asList(atributes);
    }

    @SuppressWarnings("unchecked")
    public void addFunctionalDependency(String[] args1, String[] args2) {
        this.keyValues.add(Pair.makePair(args1, args2));
    }

    public String[] calcClosure(String[] args) {
        List<String> current = new ArrayList<>(Arrays.asList(args));
        List<Pair<String[], String[]>> keyValuesCopy = new ArrayList<>(this.keyValues);

        for (int i = 0; i < current.size(); i++) {
            boolean changed = false;
            for (int j = 0; j < keyValuesCopy.size(); j++) {
                Pair<String[], String[]> currentPair = keyValuesCopy.get(j);

                List<String> relationKeyAsList = Arrays.asList(currentPair.getKey());
                List<String> relationValueAsList = Arrays.asList(currentPair.getValue());

                if (current.containsAll(relationKeyAsList)) {
                    if (!Arrays.equals(currentPair.getKey(), currentPair.getValue()) && !current.containsAll(relationValueAsList)) {
                        int index = current.indexOf(relationKeyAsList.get(relationKeyAsList.size() - 1));
                        current.addAll(index + 1, relationValueAsList);
                    }
                    changed = true;
                    keyValuesCopy.remove(currentPair);
                }
            }
            if (!changed)
                break;
        }

        return current.toArray(new String[0]);
    }

    public boolean isKey(String[] args, boolean minimal) {

        boolean isKey = false;
        List<String> closure = Arrays.asList(this.calcClosure(args));

        Collections.sort(this.attributes);
        Collections.sort(closure);

        if (this.attributes.equals(closure))
            isKey = true;

        if (isKey && minimal) {
            List<String> currentSet = Arrays.asList(args);
            for (Pair<String[], String[]> currentPair : this.keyValues) {
                List<String> keyValueCombined = new ArrayList<>(Arrays.asList(currentPair.getKey()));
                List<String> relationValueAsList = new ArrayList<>(Arrays.asList(currentPair.getValue()));

                keyValueCombined.addAll(relationValueAsList);
                List<String> keyValueCombinedAndReduced = keyValueCombined.stream().distinct().collect(Collectors.toList());

                if (keyValueCombinedAndReduced.size() > 1 && currentSet.containsAll(keyValueCombinedAndReduced))
                    return false;
            }
        }

        return isKey;
    }

    public static class Pair<Key, Value> {
        private Key key;
        private Value value;

        public static <Key, Value> Pair makePair(Key key, Value value) {
            return new Pair<>(key, value);
        }

        private Pair(Key key, Value value) {
            this.key = key;
            this.value = value;
        }

        public Key getKey() {
            return key;
        }

        public Value getValue() {
            return value;
        }
    }

    public static void main(String[] args) {

        RelationalModel relationalModel = new RelationalModel();

        String[] att = {"A", "B", "C", "D", "E", "F", "G"};

        relationalModel.setAttributes(att);

        relationalModel.addFunctionalDependency(new String[]{"G"}, new String[]{"B"});
        relationalModel.addFunctionalDependency(new String[]{"A"}, new String[]{"A"});
        relationalModel.addFunctionalDependency(new String[]{"D", "A"}, new String[]{"E"});


        relationalModel.addFunctionalDependency(new String[]{"A"}, new String[]{"F"});

        relationalModel.addFunctionalDependency(new String[]{"C"}, new String[]{"D"});
        relationalModel.addFunctionalDependency(new String[]{"A", "D"}, new String[]{"E"});
        relationalModel.addFunctionalDependency(new String[]{"F"}, new String[]{"G"});

        String[] base = {"A", "C"};
        System.out.println(Arrays.toString(relationalModel.calcClosure(base)));
        System.out.println(relationalModel.isKey(base, true));
    }
}