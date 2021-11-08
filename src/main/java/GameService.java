import org.json.JSONArray;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Integer> arr1 = new ArrayList<>();
        List<Integer> arr2 = Arrays.stream(turn).boxed().collect(Collectors.toList());
        jsonArray.forEach(js->arr1.add((Integer)js));
        arr1.sort(Comparator.comparingInt(Integer::intValue));
        arr2.sort(Comparator.comparingInt(Integer::intValue));
        int i=0;
        for (Integer arr : arr1){
            if(!arr.equals(arr2.get(i))){
                return false;
            }
            i++;
        }
        return true;
    }

    public static String getWinner(int[] clientArr, int[] serverArr) {
        int clientScore = 0;
        int serverScore = 0;
        for(int i=0;i<clientArr.length;i++){
            if (clientArr[i] > serverArr[i]) {
                clientScore++;
            } else {
                serverScore++;
            }
        }
        String result="";
        if(clientScore>serverScore)
            result="Player 2 win! ";
        else if(serverScore>clientScore)
            result="Player 1 win!";
        else result="Dead heat";
            return result;
    }
}
