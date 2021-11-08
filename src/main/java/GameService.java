import org.json.JSONArray;

public class GameService {

    public static int[] getWarriors() {
        int[] warriors = new int[8];
        for (int i = 0; i < warriors.length; i++) {
            warriors[i] = (int) (1 + Math.random() * warriors.length);
        }
        return warriors;
    }

    public static int[] parseString(String arr) {
        String[] items = arr.trim().split(" ");
        int[] result = new int[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                result[i] = Integer.parseInt(items[i].trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean isTurnCorrect(JSONArray jsonArray, int[] turn) {
        return false;
    }

}
